package sk.kamil.gui.canvas;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import sk.umb.projekty.ardbot.world.Map;

public class MapWorld extends Map {

    // kreslenie mapy
	public void paint(Graphics2D g2d, float rw, float rh) {
		for(int i=0;i<lines.size();i++) {
			Line2D line = new Line2D.Float(lines.get(i).getX1()*rw, lines.get(i).getY1()*rh, lines.get(i).getX2()*rw, lines.get(i).getY2()*rh);
			g2d.draw(line);
		}
	}

}
