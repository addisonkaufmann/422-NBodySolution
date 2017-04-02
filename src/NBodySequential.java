import java.awt.geom.Point2D;
import java.util.ArrayList;

public class NBodySequential {
	public final double G = 6.67 * Math.pow(10, -11);
	public final int MASS = 100000;
	public final double DT = 5;
	private int numBodies;
	private int bodyRadius;
	private ArrayList<Body> oldbodies;
	private ArrayList<Body> newbodies;
	private final int dimension = 600;
	
	public static void main (String [] arg){
		String [] args = {"0", "10", "10", "500"};
		if (args.length < 4){
			System.out.println("NBodySequential numWorkers numBodies bodyRadius numSteps");
			System.exit(1);
		}
		
		int numBodies = Integer.parseInt(args[1]);
		int bodyRadius = Integer.parseInt(args[2]);
		int numSteps = Integer.parseInt(args[3]);
		NBodySequential n = new NBodySequential(numBodies, bodyRadius);
		for (int i = 0 ; i < numSteps; i++){
			System.out.println(i);
			//System.out.println(n.toString());

			try{Thread.sleep(50);}catch(InterruptedException e){System.out.println(e);}  
			n.calculateForces();
			n.moveBodies();
			n.draw();
			
		}

	}
	public NBodySequential(int numBodies, int bodyRadius){
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
		System.out.println("ehllo");

	}

	public void calculateForces(){
		double dist, mag;
		Point2D dir, newforce;
		Body body1, body2;
		Point2D pos1, pos2;
		Point2D force1, force2;
		
		for (int i = 0; i < numBodies-1; i++){
			body1 = oldbodies.get(i);
			for (int j = i+1; j < numBodies; j++) {
				body2 = oldbodies.get(j);
				pos1 = body1.getPos();
				pos2 = body2.getPos();
				
				force1 = body1.getForce();
				force2 = body2.getForce();
				
				dist = pos1.distance(pos2); //not sure about this line pos1-pos2 or pos2-pos1
				if (dist == 0) {
					System.err.println("Divide by zero.");
				}
				mag = (G*MASS*MASS)/ (dist*dist);
				
				dir = new Point2D.Double(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY());
				newforce = new Point2D.Double((mag*dir.getX())/dist, (mag*dir.getY())/dist);
				
				newbodies.get(i).setForce(force1.getX() + newforce.getX(), force1.getY() + newforce.getY());
				newbodies.get(j).setForce(force2.getX() - newforce.getX(), force2.getY() - newforce.getY());	
			}
		}	
		oldbodies.clear();
		oldbodies.addAll(newbodies);		
	}
	
	public void moveBodies(){
		Point2D dv, dp, force, velocity, position;
		Body body;
		
		for (int i = 0; i < numBodies; i++){
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
	
	public void draw() {
		StdDraw.clear();
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		

		for (Body body : newbodies) {
			Point2D pos = body.getPos();
			StdDraw.filledCircle(pos.getX(), pos.getY(), body.getRadius());
		}
		StdDraw.show();
	}
	
	public String toString(){
		StringBuffer s = new StringBuffer("");
		for (Body b: oldbodies){
			s.append(b.toString() + "\n");
		}
		return s.toString();
	}
	
}
