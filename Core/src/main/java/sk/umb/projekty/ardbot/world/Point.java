package sk.umb.projekty.ardbot.world;

/**
 * Bod v svete. Suradnice su v cm.
 *
 */
public class Point {

	private float x, y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}	

}
