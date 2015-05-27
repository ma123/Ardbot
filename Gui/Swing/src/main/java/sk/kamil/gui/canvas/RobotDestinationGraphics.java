package sk.kamil.gui.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import sk.umb.projekty.ardbot.world.RobotDestination;

public class RobotDestinationGraphics extends RobotDestination {
	private static final float DESTINATION_SIZE = 20;
	
	public RobotDestinationGraphics(float xPoz, float yPoz, float rw, float rh) {
		super(xPoz, yPoz);
		super.setxPoz(xPoz);
		super.setyPoz(yPoz);
		super.setRw(rw);
		super.setRh(rh);
	}

    // vykreslenie cielovej destinacia
    public void paint(Graphics2D g2d) {
    	Ellipse2D.Double circle = new Ellipse2D.Double(xPoz- 5, yPoz-5, DESTINATION_SIZE, DESTINATION_SIZE);
    	g2d.fill(circle);
	}
}
