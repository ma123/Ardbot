package sk.umb.projekty.ardbot.world;

/**
 * Ciara tvoriaca svet. Suradnice a rozmery su v cm.
 */
public class Line {

	protected float x1, y1, x2, y2;
	
	public Line(float x1, float y1, float x2, float y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public float getX1() {
		return x1;
	}

	public float getY1() {
		return y1;
	}

	public float getX2() {
		return x2;
	}

	public float getY2() {
		return y2;
	}

	/**
	 * Vypocet priesecniku dvoch ciar.
	 * 
	 * @param l
	 * @return
	 */
	public Point intersectsAt(Line l) {	
		float x, y;
		if (y2 == y1 && l.y2 == l.y1) return null; // parallel
		if (x2 == x1 && l.x2 == l.x1) return null; // parallel	
		
		if (x1 == x2 && l.y1 == l.y2) {
			x = x1;
			y = l.y2;
		} else if (y1 == y2 && l.x1 == l.x2) {
			x = l.x1;
			y = y2;
		} else if (y2 == y1 || l.y2 == l.y1) {
			float a1 = (y2 - y1) / (x2 - x1);
			float b1 = y1 - a1*x1;

			float a2 = (l.y2 - l.y1) / (l.x2 - l.x1);
			float b2 = l.y1 - a2*l.x1;
			
			if (a1 == a2) return null; //parallel

			x = (b2 - b1) / (a1 - a2);
			y = a1 * x + b1; 

		} else {
			float a1 = (x2 - x1) / (y2 - y1);
			float b1 = x1 - a1*y1;

			float a2 = (l.x2 - l.x1) / (l.y2 - l.y1);
			float b2 = l.x1 - a2*l.y1;
			
			if (a1 == a2) return null; //parallel

			y = (b2 - b1) / (a1 - a2);
			x = a1 * y + b1; 
		}
		
		if (x1 <= x2) {
			if (x < x1 || x > x2) return null;
		} else {
			if (x < x2 || x > x1) return null;
		}
		if (y1 <= y2) {
			if (y < y1 || y > y2) return null;
		} else {
			if (y < y2 || y > y1) return null;
		}
		if (l.x1 <= l.x2) {
			if (x < l.x1 || x > l.x2) return null;
		} else {
			if (x < l.x2 || x > l.x1) return null;
		}
		if (l.y1 <= l.y2) {
			if (y < l.y1 || y > l.y2) return null;
		} else {
			if (y < l.y2 || y > l.y1) return null;
		}
		return new Point(x, y);
	}

	/**
	 * Vypocet dlzky ciary.
	 * 
	 * @return
	 */
	public float lengthLine() {
		return (float) Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
	}

}
