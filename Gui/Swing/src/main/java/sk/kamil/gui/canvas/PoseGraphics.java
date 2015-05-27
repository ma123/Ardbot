package sk.kamil.gui.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import sk.umb.projekty.ardbot.particle_filter.Pose;
import sk.umb.projekty.ardbot.world.Line;

public class PoseGraphics extends Pose {

	private static final float ARROW_LENGTH = 2f;

	public PoseGraphics(float x, float y, float angle) {
		super(x, y, angle);
	}

	private Line getArrowLine() {
		return new Line(x, y, x + ARROW_LENGTH * (float) Math.cos(Math.toRadians(angle)),
				y + ARROW_LENGTH * (float) Math.sin(Math.toRadians(angle)));
	}
	
	// malovanie castice
	public void paint(Graphics2D g2d, float rw, float rh) {
		Ellipse2D c = new Ellipse2D.Float(x*rw-5, y*rh-5, 10, 10);
		Line rl = getArrowLine();
		Line2D l2d = new Line2D.Float(rl.getX1()*rw, rl.getY1()*rh, rl.getX2()*rw, rl.getY2()*rh);
		g2d.draw(l2d);
		g2d.fill(c);
	}
	
}
