package Parallel;

import java.awt.geom.Point2D;
import java.util.Random;
import java.util.Vector;

/**
 * This class represents a perfectly circular body that have properties such
 * as a position, velocity, force, mass, and diameter.
 * @author Aaron & Addison
 *
 */
public class BodyP {
	private Point2D pos, vel;
	private Vector<Point2D> force;
	private double radius;
	
	public BodyP(int bounds, int radius, int numWorkers){
		int maxpos = bounds, minpos = -1*bounds;
		int minvel = -5, maxvel = 5;
		Random randy = new Random();

		this.pos = new Point2D.Double(minpos + (maxpos - minpos) * randy.nextDouble(), minpos + (maxpos - minpos) * randy.nextDouble());	
		this.vel = new Point2D.Double(minvel + (maxvel - minvel) * randy.nextDouble(), minvel + (maxvel - minvel) * randy.nextDouble());		

		//this.vel = new Point2D.Double(0.0, 0.0);

		this.force = new Vector<Point2D>();
		for (int i = 0; i < numWorkers; i++) {
			this.force.add(new Point2D.Double(0.0, 0.0));
		}
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
	
	public Point2D getForce(int i) {
		return force.get(i);
	}

	public Point2D getForceSum() {
		double x = 0.0, y = 0.0;
		for (Point2D p : force) {
			x += p.getX();
			y += p.getY();
		}
		return new Point2D.Double(x,y);
	}

	public void setForce(int i, double x, double y) {
		this.force.get(i).setLocation(x, y);
	}

	public double getRadius() {
		return radius;
	}	
	
	public String toString(){
		return "position = " + pos.toString() + "; velocity = " + vel.toString() + "; force = " + force.toString();
	}

	public boolean collidedWith(BodyP that) {
		return Math.abs(this.pos.distance(that.getPos())) <= radius*2;
	}

	public void calculateCollision(BodyP that) {
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
