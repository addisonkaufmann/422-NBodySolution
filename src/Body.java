import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * This class represents a perfectly circular body that have properties such
 * as a position, velocity, force, mass, and diameter.
 * @author Aaron & Addison
 *
 */
public class Body {
	private Point2D pos, vel, force;
	private double radius;
	
	public Body(Point pos, Point vel, Point force, double radius) {
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

}
