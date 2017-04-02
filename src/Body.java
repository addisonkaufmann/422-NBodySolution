import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

/**
 * This class represents a perfectly circular body that have properties such
 * as a position, velocity, force, mass, and diameter.
 * @author Aaron & Addison
 *
 */
public class Body {
	private Point2D pos, vel, force;
	private double radius;
	
	public Body(int bounds, int radius){
		int maxpos = bounds, minpos = -1*bounds;
		int minvel = -1, maxvel = 1;
		Random randy = new Random();

		this.pos = new Point2D.Double(minpos + (maxpos - minpos) * randy.nextDouble(), minpos + (maxpos - minpos) * randy.nextDouble());	
		this.vel = new Point2D.Double(minvel + (maxvel - minvel) * randy.nextDouble(), minvel + (maxvel - minvel) * randy.nextDouble());
		//this.vel = new Point2D.Double(0.0, 0.0);

		this.force = new Point2D.Double(0.0, 0.0);
		this.radius = radius;
	}
	
	public Body(Point2D pos, Point2D vel, Point2D force, double radius) {
		this.pos = pos;
		this.vel = vel;
		this.force = force;
		this.radius = radius;
	}

	public Point2D getPos() {
		return pos;
	}

	public void setPos(double x, double y) {
		this.pos.setLocation(x, y);
	}

	public Point2D getVel() {
		return vel;
	}

	public void setVel(double x, double y) {
		this.vel.setLocation(x, y);
		
	}

	public Point2D getForce() {
		return force;
	}

	public void setForce(double x, double y) {
		this.force.setLocation(x, y);
	}

	public double getRadius() {
		return radius;
	}	
	
	public String toString(){
		return "position = " + pos.toString() + "; velocity = " + vel.toString() + "; force = " + force.toString();
	}

}
