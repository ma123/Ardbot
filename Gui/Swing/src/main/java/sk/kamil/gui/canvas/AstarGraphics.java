package sk.kamil.gui.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class AstarGraphics {
	private static final int PATH_SIZE = 10;

	public void paint(Graphics2D g2d, float x, float y,float rw, float rh) {
		Ellipse2D c = new Ellipse2D.Float(x*rw, y*rh, PATH_SIZE, PATH_SIZE);
		g2d.fill(c);
	}
}
