import java.awt.Point;

/**
 * This class represents a perfectly circular body that have properties such
 * as a position, velocity, force, mass, and diameter.
 * @author Aaron & Addison
 *
 */
public class Body {
	private Point pos, vel, force;
	private double diameter;
	
	public Body(Point pos, Point vel, Point force, double diameter) {
		this.setPos(pos);
		this.setVel(vel);
		this.setForce(force);
		this.diameter = diameter;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}

	public Point getVel() {
		return vel;
	}

	public void setVel(Point vel) {
		this.vel = vel;
	}

	public Point getForce() {
		return force;
	}

	public void setForce(Point force) {
		this.force = force;
	}

	public double getDiameter() {
		return diameter;
	}	

}
