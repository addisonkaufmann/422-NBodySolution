import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class NBodySequential {
	public final double G = 6.67 * Math.pow(10, -11);
	public final int mass = 5;
	private int numBodies;
	private int bodyRadius;
	private ArrayList<Body> oldbodies;
	private ArrayList<Body> newbodies;
	private final int dimension = 600;
	
	public static void main (String [] arg){
		String [] args = {"0", "10", "5", "6"};
		if (args.length < 4){
			System.out.println("NBodySequential numWorkers numBodies bodyRadius numSteps");
			System.exit(1);
		}
		
		int numBodies = Integer.parseInt(args[1]);
		int bodyRadius = Integer.parseInt(args[2]);
		int numSteps = Integer.parseInt(args[3]);
		
		NBodySequential n = new NBodySequential(numBodies, bodyRadius);
		for (int i = 0 ; i < numSteps; i++){
			//n.update();
//			n.draw();
		}

	}
	public NBodySequential(int numBodies, int bodyRadius){
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
			body2 = oldbodies.get(i+1);
			
			pos1 = body1.getPos();
			pos2 = body2.getPos();
			
			force1 = body1.getForce();
			force2 = body2.getForce();
			
			dist = pos1.distance(pos2);
			mag = (G*mass*mass)/ (dist*dist);
			
			dir = new Point2D.Double(pos2.getX() - pos1.getX(), pos2.getY() - pos1.getY());
			newforce = new Point2D.Double((mag*dir.getX())/dist, (mag*dir.getY())/dist);
			
			body1.setForce(force1.getX() + newforce.getX(), force1.getY() + newforce.getY());
			body2.setForce(force2.getX() - newforce.getX(), force2.getY() - newforce.getY());
			
		}
		
	}
	
	public void draw() {
		StdDraw.clear(StdDraw.BLACK);
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		
		StdDraw.setCanvasSize(dimension, dimension);
		StdDraw.setXscale(-(dimension/2),(dimension/2)); 
        StdDraw.setYscale(-(dimension/2), (dimension/2));
		for (Body body : newbodies) {
			Point2D pos = body.getPos();
			StdDraw.filledCircle(pos.getX(), pos.getY(), body.getRadius());
		}
		StdDraw.show();
	}
	
}
