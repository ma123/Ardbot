package sk.kamil.gui.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import sk.umb.projekty.ardbot.world.Line;

public class LineGraphics extends Line {

	public LineGraphics(float x1, float y1, float x2, float y2) {
		super(x1, y1, x2, y2);
	}

	// vykreslenie ciar
	public void paint(Graphics2D g2d) {
		Line2D line = new Line2D.Float(x1, y1, x2,y2);
		g2d.draw(line);
	}
}
