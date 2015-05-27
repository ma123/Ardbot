package sk.umb.projekty.ardbot.particle_filter;

import java.util.Random;

import sk.umb.projekty.ardbot.world.Line;
import sk.umb.projekty.ardbot.world.Point;

/**
 * (Mozne) umiestnenie robota - dane poziciou a otocenim.
 *
 */
public class Pose {

	// maximalna vzdialenost v svete
	private static final float MAXIMUM_RANGE = 250f;

	protected float x, y, angle;
	
	public Pose(float x, float y, float angle) {
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public Point getPoint() {
		return new Point(x,y);
	}

	public float getAngle() {
		return angle;
	}

	/**
	 * Vytvor verziu umiestnenia otocenu o dany uhol.
	 * 
	 * @param angle
	 * @return
	 */
	public Pose getRotatedCopy(float angleRot) {
		Pose ret = new Pose(x, y, angle);
		ret.angle += angleRot;
		ret.angle %= 360;
		return ret;
	}

	/**
	 * Velmi dlha ciara v smere (a z miesta) tejto pose.
	 * 
	 * @return
	 */
	public Line getRangeLine() {
		return new Line(x, y, x + MAXIMUM_RANGE * (float) Math.cos(Math.toRadians(angle)),
				y + MAXIMUM_RANGE * (float) Math.sin(Math.toRadians(angle)));
	}

	/**
	 * Posun tuto poziciu a otocenie o nahodny sum.
	 */
	public void addNoise() {
		Random rand = new Random();
		this.angle += (float) 4*rand.nextGaussian();
		this.x += (float) rand.nextGaussian();
		this.y += (float) rand.nextGaussian();
	}

	/**
	 * Aplikuj pohyb a otocenie na tuto poziciu.
	 * 
	 * @param move
	 */
	public void move(float angle, float distance) {
		this.x += (distance * ((float) Math.cos(Math.toRadians(this.angle))));
		this.y += (distance * ((float) Math.sin(Math.toRadians(this.angle))));
		this.angle += angle;
		this.angle = this.angle % 360;
	}

}
