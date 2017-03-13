import java.awt.Point;
import java.util.ArrayList;

public class NBodySequential {
	public final double G = 6.67 * Math.pow(10, -11);
	public final int mass = 5;
	private int numBodies;
	private int bodyRadius;
	private int numSteps;
	private ArrayList<Body> oldbodies;
	private ArrayList<Body> newbodies;
	private final int height = 250, width = 250;
	
	public void main (String [] args){
		if (args.length < 4){
			System.out.println("NBodySequential numWorkers numBodies bodyRadius numSteps");
			System.exit(1);
		}
		
		//int numWorkers = Integer.parseInt(args[0]);
		numBodies = Integer.parseInt(args[1]);
		bodyRadius = Integer.parseInt(args[2]);
		numSteps = Integer.parseInt(args[3]);
		
		oldbodies = new ArrayList<>();
		System.out.println("ehllo");
		
	}

	public void calculateForces(){
		double dist, mag, newforcex, newforcey;
		Point dir;
		Body body1, body2;
		Point pos1, pos2;
		Point force1, force2;
		
		for (int i = 0; i < numBodies-1; i++){
			body1 = oldbodies.get(i);
			body2 = oldbodies.get(i+1);
			
			pos1 = body1.getPos();
			pos2 = body2.getPos();
			
			force1 = body1.getForce();
			force2 = body2.getForce();
			
			dist = pos1.distance(pos2);
			mag = (G*mass*mass)/ (dist*dist);
			dir = new Point(pos2.x - pos1.x, pos2.y - pos1.y);
			
			newforcex = (mag*dir.x)/dist;
			newforcey = (mag*dir.y)/dist;
			
			body1.setForce(force1.x + newforcex, force1.y + newforcey);
			body2.setForce(force2.x - newforcex, force2.y - newforcey);
			
		}
		
	}
	
	public void draw() {
		StdDraw.clear(StdDraw.BLACK);
		StdDraw.setPenColor(StdDraw.BOOK_BLUE);
		
		StdDraw.setCanvasSize(height, width);
		StdDraw.setXscale(-(width/2),(width/2)); 
        StdDraw.setYscale(-(height/2), (height/2));
		for (Body body : newbodies) {
			Point pos = body.getPos();
			StdDraw.filledCircle(pos.x, pos.y, body.getDiameter()/2);
		}
		StdDraw.show();
	}
	
}
