import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class NBodyParallel {
	private int barrierStages;
	public static final double G = 6.67 * Math.pow(10, -11);
	public static final int MASS = 10000000;
	public static final double DT = 5;
	private int numBodies;
	private int bodyRadius;
	private ArrayList<Body> oldbodies;
	private ArrayList<Body> newbodies;
	private final int dimension = 600;
	private Semaphore[][] semaphores = null;
	
	public NBodyParallel(int numBodies, int bodyRadius) {
		StdDraw.setCanvasSize(dimension, dimension);
		StdDraw.setXscale(-(dimension/2),(dimension/2)); 
        StdDraw.setYscale(-(dimension/2), (dimension/2));
        this.numBodies = numBodies;
        this.bodyRadius = bodyRadius;
        oldbodies = new ArrayList<>();
		newbodies = new ArrayList<>();
		for (int i = 0; i < numBodies; i++){
			Body b = new Body(dimension/2, bodyRadius);
			oldbodies.add(b);
			newbodies.add(b);
		}
		draw();
	}
	
	public static void main (String [] args){
		if (args.length < 4){
			System.out.println("NBodyParallel numWorkers numBodies bodyRadius numSteps");
			System.exit(1);
		}
		
		int numWorkers = Integer.parseInt(args[0]);
		int numBodies = Integer.parseInt(args[1]);
		int bodyRadius = Integer.parseInt(args[2]);
		int numSteps = Integer.parseInt(args[3]);
		
		NBodyParallel model = new NBodyParallel(numBodies, bodyRadius);
		
		model.barrierStages = (int) Math.ceil(Math.log10(numWorkers) / Math.log10(2));
		Thread workers[] = new Thread[numWorkers];
		// Initialize the semaphores
		model.semaphores = new Semaphore[model.barrierStages][numWorkers];
		for (int i = 0; i < model.barrierStages; ++i) {
			for (int j = 0; j < numWorkers; ++j) {
				model.semaphores[i][j] = new Semaphore(0);
			}
		}
		
		// Create new worker threads with their own slice of the grid to compute
		int step = numBodies / numWorkers;
		Range[] ranges = new Range[numWorkers];
		for (int i = 0; i < numWorkers; ++i) {
			ranges[i] = new Range();
		}
		ranges[0].startIndex = 1;
		ranges[0].endIndex = step - 1;	
		int i;
		for (i = 0; i < numWorkers; ++i) {
			// Update the range
			if (i > 0) {
				ranges[i].startIndex = ranges[i-1].endIndex + 1;
				ranges[i].endIndex = ranges[i-1].endIndex + step;
			}
			if (i == numWorkers - 1) {
				ranges[i].endIndex = numBodies;
			}
			// Create the thread and start running it
			workers[i] = new Thread(new Worker(ranges[i], i, numSteps, model.oldbodies, model.newbodies, model.semaphores));
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
	}
	
	
	private static class Worker implements Runnable {
		private int startIndex, endIndex, id, numSteps;
		private ArrayList<Body> oldbodies, newbodies;
		private Semaphore[][] semaphores;
		
		public Worker(Range range, int id, int numSteps, ArrayList<Body> oldbodies, ArrayList<Body> newbodies, Semaphore[][] semaphores) {
			this.startIndex = range.startIndex;
			this.endIndex = range.endIndex;
			this.id = id;
			this.numSteps = numSteps;
			this.oldbodies = oldbodies;
			this.newbodies = newbodies;
			this.semaphores = semaphores;
		}

		public void run() {
			
		}
		
		private void calculateForces(){
			double dist, mag;
			Point2D dir, newforce;
			Body body1, body2;
			Point2D pos1, pos2;
			Point2D force1, force2;
			
			for (int i = startIndex; i < endIndex-1; i++){
				body1 = oldbodies.get(i);
				body2 = oldbodies.get(i+1);
				
				pos1 = body1.getPos();
				pos2 = body2.getPos();
				
				force1 = body1.getForce();
				force2 = body2.getForce();
				
				dist = pos1.distance(pos2); //not sure about this line pos1-pos2 or pos2-pos1
				mag = (G*MASS*MASS)/ (dist*dist);
				
				dir = new Point2D.Double(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY());
				newforce = new Point2D.Double((mag*dir.getX())/dist, (mag*dir.getY())/dist);
				
				newbodies.get(i).setForce(force1.getX() + newforce.getX(), force1.getY() + newforce.getY());
				newbodies.get(i+1).setForce(force2.getX() - newforce.getX(), force2.getY() - newforce.getY());	
			}	
			oldbodies.clear();
			oldbodies.addAll(newbodies);		
		}
		
		private void moveBodies(){
			Point2D dv, dp, force, velocity, position;
			Body body;
			
			for (int i = startIndex; i < endIndex; i++){
				body = oldbodies.get(i);
				force = body.getForce();
				velocity = body.getVel();
				position = body.getPos();
				dv = new Point2D.Double(force.getX()/MASS * DT, force.getY()/MASS * DT);
				dp = new Point2D.Double(   (velocity.getX() + dv.getX()/2) * DT, 
											(velocity.getY() + dv.getY()/2) * DT);
				
				body.setVel(velocity.getX() + dv.getX(), velocity.getY() + dv.getY());
				body.setPos(position.getX() + dp.getX(), position.getY() + dp.getY());
				body.setForce(0.0,  0.0);
			}
			
		}
		
	}
	
	public void draw() {
		StdDraw.clear();
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);	

		for (Body body : newbodies) {
			Point2D pos = body.getPos();
			StdDraw.filledCircle(pos.getX(), pos.getY(), body.getRadius());
		}
		StdDraw.show();
	}
	
	/**
	 * Simple class to hold the range of which bodies a worker should update.
	 */
	private static class Range {
		public int startIndex, endIndex;
	}
}
