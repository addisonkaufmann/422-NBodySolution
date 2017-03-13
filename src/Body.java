import java.awt.Point;

/**
 * This class represents a perfectly circular body that have properties such
 * as a position, velocity, force, mass, and diameter.
 * @author Aaron & Addison
 *
 */
public class Body {
	private Point pos, vel, force;
	private double mass, diameter;
	
	public Body(Point pos, Point vel, Point force, double mass, double diameter) {
		this.setPos(pos);
		this.setVel(vel);
		this.setForce(force);
		this.setMass(mass);
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

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public double getDiameter() {
		return diameter;
	}	

}
