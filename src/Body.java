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
		this.pos = pos;
		this.vel = vel;
		this.force = force;
		this.diameter = diameter;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(int x, int y) {
		this.pos = new Point(x,y);
	}

	public Point getVel() {
		return vel;
	}

	public void setVel(int x, int y) {
		this.vel = new Point(x,y);
	}

	public Point getForce() {
		return force;
	}

	public void setForce(int x, int y) {
		this.force = new Point(x,y);
	}

	public double getDiameter() {
		return diameter;
	}	

}
