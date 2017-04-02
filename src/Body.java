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
		int minvel = -5, maxvel = 5;
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

	public boolean collidedWith(Body that) {
		return Math.abs(this.pos.distance(that.getPos())) <= radius*2;
	}

	public void calculateCollision(Body that) {
		double 	v1x = this.vel.getX(), 	
				v1y = this.vel.getY(),
				x1 = this.pos.getX(),
				y1 = this.pos.getY(),
				v2x = that.getVel().getX(),
				v2y = that.getVel().getY(), 
				x2 = that.getPos().getX(),
				y2 = that.getPos().getY();
		
		double v1fx = v2x * Math.pow(x2 - x1, 2);
		v1fx += v2y * (x2 - x1) * (y2 - y1);
		v1fx += v1x * Math.pow(y2 - y1, 2);
		v1fx -= v1y * (x2 - x1) * (y2 - y1);
		v1fx /= Math.pow(x2 - x1, 2) + Math.pow(y2 - y1,  2);
		
		double v1fy = v2x * (x2 - x1) * (y2 - y1);
		v1fy += v2y * Math.pow(y2 - y1, 2);
		v1fy -= v1x * (y2 - y1) * (x2 - x1);
		v1fy += v1y * Math.pow(x2 - x1, 2);
		v1fy /= Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2);
		
		double v2fx = v1x * Math.pow(x2 - x1, 2);
		v2fx += v1y * (x2 - x1) * (y2 - y1);
		v2fx += v2x * Math.pow(y2 - y1, 2);
		v2fx -= v2y * (x2 - x1) * (y2 - y1);
		v2fx /= Math.pow(x2 - x1,  2) + Math.pow(y2 - y1, 2);
		
		double v2fy = v1x * (x2 - x1) * (y2 - y1);
		v2fy += v1y * Math.pow(y2 - y1,  2);
		v2fy -= v2x * (y2 - y1) * (x2 - x1);
		v2fy += v2y * Math.pow(x2 - x1, 2);
		v2fy /= Math.pow(x2 - x1,  2) + Math.pow(y2 - y1, 2);
		
		this.setVel(v1fx,  v1fy);
		that.setVel(v2fx, v2fy);
	}

}
