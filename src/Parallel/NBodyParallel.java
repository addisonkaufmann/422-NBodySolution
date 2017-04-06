package Parallel;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

import org.apache.commons.cli.*;


/**
 * This program models n bodies gravitation in parallel. It 
 * reads parameters, creates the bodies (either randomly or
 * optionally by input), and calculates their new 
 * forces and positions for each time step. The work is
 * split in stripes so each thread has several bodies to
 * perform calculations for, spread out in the array list.
 * It optionally draws each time step to stddraw window
 * and prints results to a file.
 * 
 * @author Addison Kaufmann, Aaron Woodward
 *
 */
public class NBodyParallel implements Observer {
	private int barrierStages, numWorkers;
	public final double G = 6.67 * Math.pow(10, -11);
	public final int MASS = 10000000;
	public static double DT = .5;
	private int numBodies;
	private int bodyRadius;
	private Vector<BodyP> oldbodies;
	private Vector<BodyP> newbodies;
	private static int dimension = 600;
	private BodyP[][] bodies;
	private Semaphore[][] semaphores = null;
	private CyclicBarrier cyclicBarrier;
	private long barrierSec = 0, barrierNano = 0;
	private int numCollisions = 0;
	private static int seed;
	private static boolean hasSeed = false;
	private static boolean gui = false, cb = false;

	
	public NBodyParallel(int numBodies, int bodyRadius, int numWorkers, int barrierStages) {
		if (gui){
			StdDraw.enableDoubleBuffering();
			StdDraw.setCanvasSize(dimension, dimension);
			StdDraw.setXscale(-(dimension/2),(dimension/2)); 
	        StdDraw.setYscale(-(dimension/2), (dimension/2));
	        StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		}

        this.numBodies = numBodies;
        this.bodyRadius = bodyRadius;
        this.numWorkers = numWorkers;
        this.barrierStages = barrierStages;
        oldbodies = new Vector<>(numBodies);
		newbodies = new Vector<>(numBodies);
		Random randy = null;
		if (hasSeed){
			randy = new Random(seed);
		}

		
		bodies = new BodyP[numWorkers][numBodies];
		for (int i = 0; i < numWorkers; i++) {
			for (int j = 0; j < numBodies; j++) {
				if (hasSeed){
					bodies[i][j] = new BodyP(dimension/2, bodyRadius, randy);
				} else {
					bodies[i][j]  = new BodyP(dimension/2, bodyRadius);
				}
			}
		}
		
		if (gui){
			draw();
		}
	}
	
	public static void main (String [] arg){
		String [] args = {"2", "50", "10", "1000", "-g", "-s", "20"};
		if (args.length < 4){
			System.out.println("NBodyParallel numWorkers numBodies bodyRadius numSteps");
			System.exit(1);
		}
		
		checkInput(args);
		int numWorkers = Integer.parseInt(args[0]);
		int numBodies = Integer.parseInt(args[1]);
		int bodyRadius = Integer.parseInt(args[2]);
		int numSteps = Integer.parseInt(args[3]);
		
		int barrierStages = (int) Math.ceil(Math.log10(numWorkers) / Math.log10(2));
		
		NBodyParallel model = new NBodyParallel(numBodies, bodyRadius, numWorkers, barrierStages);

		Thread workers[] = new Thread[numWorkers];
		// Initialize the semaphores
		if (!cb) {
			model.semaphores = new Semaphore[barrierStages][numWorkers];
			for (int i = 0; i < barrierStages; ++i) {
				for (int j = 0; j < numWorkers; ++j) {
					model.semaphores[i][j] = new Semaphore(0);
				}
			}
		}
		else {
			model.cyclicBarrier = new CyclicBarrier(numWorkers);
		}
		
		// Begin time analysis
		Instant start = Instant.now();
		
		// Create new worker threads
		int i;
		for (i = 0; i < numWorkers; ++i) {
			// Create the thread and start running it
			Worker worker = model.new Worker(i, numSteps, model.oldbodies, model.newbodies, model.semaphores, numBodies);
			worker.addObserver(model);
			workers[i] = new Thread(worker);
			workers[i].start();
		}
		
		// Wait for all threads to finish
		for (i = 0; i < numWorkers; ++i) {
			try {
				workers[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		// End time analysis
		Instant end = Instant.now();
		
		// Print results to file
		try {
			boolean exists = false;
			File f = new File("NBodyResultsParallel.csv");
			if(f.exists() && !f.isDirectory()) { 
				exists = true;
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter("NBodyResultsParallel.csv", true));
			if (!exists) {
				bw.write("Runtime, TotalTimeInBarrier\n");
			}
			Double nanos = ((double)model.barrierNano/1000000000);
			String runtime = Duration.between(start, end).toString();
			bw.write(runtime.substring(2, runtime.length()-1) + "," + model.barrierSec + "." + nanos.toString().substring(2) );
			System.out.println("Computation time: " + runtime.substring(2, runtime.length()-1) + " seconds");
			System.out.println("Number of collisions: " + model.numCollisions);
			bw.newLine();
			bw.flush();
			bw.close();
			
			 PrintWriter writer = new PrintWriter("NBodyParallelFinalPositions.txt", "UTF-8");
			 for (BodyP body : model.newbodies) {
				 writer.println("Pos: " + body.getPos().toString());
				 writer.println("Vel: " + body.getVel().toString());
			 }
			 writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	/**
	 * This worker class does the calculations to simulate the movement of bodies with gravitational forces.
	 * Threads handle the calculations in stripes. For example, given 4 threads (0,1,2,3) and 8 bodies (o):
	 * 
	 * 0 1 2 3 0 1 2 3 
	 * o o o o o o o o
	 * 
	 * where threads handle the bodies below their number.
	 * @author Aaron Woodward & Addison Kaufmann
	 *
	 */
	public class Worker extends Observable implements Runnable {
		private int id, numSteps;
		
		public Worker(int id, int numSteps, Vector<BodyP> oldbodies, Vector<BodyP> newbodies, Semaphore[][] semaphores, int numBodies) {
			this.id = id;
			this.numSteps = numSteps;
		}

		public void run() {
			for (int i = 0; i < numSteps; ++i) {
				calculateForces();
				
				barrier();
				
				moveBodies();
				
				barrier();
				
				if (id == 0) {
					oldbodies.clear();
					oldbodies.addAll(newbodies);
				}
				
				barrier();
				
				if (id == 0) {
					//adjustCollisions();
				}
					
				//barrier();

				if (id == 0 && gui){
					this.setChanged();
					this.notifyObservers(); // Redraw
				}
			}
			
		}
		
		/**
		 * Calculates the force of every body on this thread's bodies (bodies are in stripes).
		 */
		public void calculateForces(){
			double dist, mag;
			Point2D dir, newforce;
			BodyP body1, body2;
			Point2D pos1, pos2;
			Point2D force1, force2;
			
			for (int i = id; i < numBodies; i+=numWorkers){
				for (int j = i+1; j < numBodies; j++) {
					body1 = bodies[id][i];
					body2 = bodies[id][j];
					pos1 = body1.getPos();
					pos2 = body2.getPos();
					
					force1 = body1.getForce();
					force2 = body2.getForce();
					
					dist = pos1.distance(pos2);
					if (dist == 0) {
						System.err.println("Divide by zero.");
					}
					mag = (G*MASS*MASS)/ (dist*dist);
					
					dir = new Point2D.Double(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY());
					newforce = new Point2D.Double((mag*dir.getX())/dist, (mag*dir.getY())/dist);
					
					bodies[id][i].setForce(force1.getX() + newforce.getX(), force1.getY() + newforce.getY());
					bodies[id][j].setForce(force2.getX() - newforce.getX(), force2.getY() - newforce.getY());	
				}
			}	
		}
		
		/**
		 * Updates one thread's bodies' velocities & positions (bodies are in stripes) 
		 * based upon their newly calculated forces.
		 */
		public void moveBodies(){
			Point2D dv, dp, force, velocity, position;
			BodyP body = null;
			force = new Point2D.Double();
			force.setLocation(0.0, 0.0);
			for (int i = id; i < numBodies; i+=numWorkers){
				for (int k = 0; k < numWorkers; k++) {
					body = bodies[k][i];
					Point2D bForce = body.getForce();
					force.setLocation(force.getX() + bForce.getX(), force.getY() + bForce.getY());
					body.setForce(0.0, 0.0);
				}
				velocity = body.getVel();
				position = body.getPos();
				dv = new Point2D.Double(force.getX()/MASS * DT, force.getY()/MASS * DT);
				dp = new Point2D.Double(   (velocity.getX() + dv.getX()/2) * DT, 
											(velocity.getY() + dv.getY()/2) * DT);
				
				for (int k = 0; k < numWorkers; k++) {
					bodies[k][i].setVel(velocity.getX() + dv.getX(), velocity.getY() + dv.getY());
					bodies[k][i].setPos(position.getX() + dp.getX(), position.getY() + dp.getY());
					bodies[k][i].setForce( 0.0,  0.0);
				}
			}
		}
		
		/**
		 * Checks for collisions and updates the velocities of any collided bodies.
		 */
		public void adjustCollisions() {
			for (int i = 0; i < numBodies-1; i++){
				for (int j = i + 1 ; j < numBodies; j++){
					if (oldbodies.get(i).collidedWith(oldbodies.get(j))){
						numCollisions++;
						newbodies.get(i).calculateCollision(newbodies.get(j), oldbodies.get(i), oldbodies.get(j));
					}
				}
			}
			oldbodies.clear();
			oldbodies.addAll(newbodies);
		}
		
		/**
		 * Used to block threads until all threads have reached the same point.
		 * Uses a custom dissemination barrier unless the -cb flag is added as an 
		 * argument, in which case Java's CyclicBarrier class is used instead.
		 */
		private void barrier() {
			Instant start = null, end;
			if (id == 0) start = Instant.now();
			
			if (!cb) {
				dissemBarrier(id, semaphores, barrierStages, numWorkers);
			}
			else {
				try {
					cyclicBarrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
			
			if (id == 0) {
				end = Instant.now();
				barrierSec += Duration.between(start, end).getSeconds();
				barrierNano += Duration.between(start, end).getNano();
			}
		}
		
	}
	
	/**
	 * A dissemination barrier that uses semaphores to wait until all threads arrive before releasing them.
	 * @param id of the worker.
	 * @param semaphores the array of semaphores for each stage/worker.
	 * @param numStages
	 * @param numThreads
	 */
	private static void dissemBarrier(int id, Semaphore[][] semaphores, int numStages, int numThreads) {
		int stage = 0;
		int next = 1;
		while (stage < numStages) {
			semaphores[stage][id].release(); // V(self) . . . I've arrived!
			try {
				semaphores[stage][(id + next) % (numThreads)].acquire(); // P(next thread) . . . wait for next thread to arrive
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			next *= 2;
			stage++;
		}
	}
	
	public void draw() {
		StdDraw.clear();
		for (int i = 0; i < numBodies; ++i) {
			Point2D pos = bodies[0][i].getPos();
			StdDraw.filledCircle(pos.getX(), pos.getY(), bodies[0][i].getRadius());
		}
		StdDraw.show();
	}

	public void update(Observable o, Object arg) {
		draw();
	}
	
	/**
	 * Parses the arguments and sets respective flags appropriately.
	 * @param args
	 */
	private static void checkInput(String[] args) {
		if (args.length < 4){
			System.out.println("NBodySequential numWorkers numBodies bodyRadius numSteps [-g] [-s x] [-dt x] [-d x]");
			System.exit(1);
		} 

        Options ops = new Options();
        ops.addOption("g", "gui", false, "Display the gui");
        ops.addOption("s", "seed", true, "Set a seed for the random bodies");
        ops.addOption("d", "dimension", true, "Specify a window size");
        ops.addOption("dt", "timedelta", true, "Specify a time delta");
        ops.addOption("cb", "CyclicBarrier", false, "Use CyclicBarrier instead of dissemination");
        
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
        	cmd = parser.parse( ops, args);
		} catch (ParseException e) {
			System.out.println("NBodySequential numWorkers numBodies bodyRadius numSteps [-g] [-s x] [-dt x] [-d x]");
			System.exit(1);
		}
        
        if (cmd.hasOption("g")){
        	gui = true;
        }
        if (cmd.hasOption("s")){
        	hasSeed = true;
        	seed = Integer.parseInt(cmd.getOptionValue("s"));
        }
        if (cmd.hasOption("d")){
        	dimension = Integer.parseInt(cmd.getOptionValue("d")); 
        }
        if (cmd.hasOption("dt")){
        	DT = Double.parseDouble(cmd.getOptionValue("dt"));
        }      
        if (cmd.hasOption("cb")){
        	cb = true;
        } 
	}
}
