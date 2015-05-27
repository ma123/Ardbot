package sk.umb.projekty.ardbot.world;

import java.util.ArrayList;
import java.util.List;

import sk.umb.projekty.ardbot.particle_filter.Pose;

/**
 * Mapa svetu, v ktorom sa robot pohybuje.
 *
 */
public class Map {

	protected List<Line> lines = new ArrayList<Line>();

	private Rectangle boundingRect;
	private final static int OBSTACLE_MAP = 1;

	/**
	 * Pridaj ciaru definujucu svet. Automaticky sa prepocita boundingRect.
	 * 
	 * @param l
	 */
	public void addLine(Line l) {
		lines.add(l);
		// vypocitaj bounding rect
		float x, y, w, h;
		if (l.getX1() < l.getX2()) {
			x = l.getX1();
			w = l.getX2() - l.getX1();
		} else {
			x = l.getX2();
			w = l.getX1() - l.getX2();
		}
		if (l.getY1() < l.getY2()) {
			y = l.getY1();
			h = l.getY2() - l.getY1();
		} else {
			y = l.getY2();
			h = l.getY1() - l.getY2();
		}
		if (boundingRect==null) {
			boundingRect = new Rectangle(x, y, w, h);
		} else {
			if (x < boundingRect.getX()) {
				boundingRect.setX(x);
			}
			if (y < boundingRect.getY()) {
				boundingRect.setY(y);
			}
			if (w > boundingRect.getW()) {
				boundingRect.setW(w);
			}
			if (h > boundingRect.getH()) {
				boundingRect.setH(h);
			}
		}
	}

	/**
	 * Vypocet vzdialenosti danej pozicie od steny.
	 * 
	 * @param pose
	 * @return
	 */
	public float range(Pose pose) {
		Line l = pose.getRangeLine();
		Line rl = null;

		for (int i=0; i<lines.size(); i++) {
			Point p = lines.get(i).intersectsAt(l);
			if (p == null) {
				// nepretinaju sa
				continue;
			}
			Line tl = new Line(pose.getX(), pose.getY(), p.getX(), p.getY());
			if (rl == null || tl.lengthLine() < rl.lengthLine()) {
				rl = tl;
			}
		}
		return (rl == null ? -1 : rl.lengthLine());
	}

	/**
	 * Kontrola, ci je dany bod v mape.
	 * 
	 * @param p
	 * @return
	 */
	public boolean inside(Point p) {
		if (p.getX() < boundingRect.getX() || p.getY() < boundingRect.getY()) return false;
		if (p.getX() > boundingRect.getX() + boundingRect.getW() || 
		    p.getY() > boundingRect.getY() + boundingRect.getH()) return false;

		// Create a line from the point to the left
		Line l = new Line(p.getX(), p.getY(), p.getX() - boundingRect.getW(), p.getY());

		// Count intersections
		int count= 0;		
		for(int i=0; i<lines.size(); i++ ) {
			if (lines.get(i).intersectsAt(l) != null) {
				count++;
			}
		}
        // We are inside if the number of intersections is odd
		return count%2 == 1;
	}
	
	public int[][] createObstacleMap() {
		int mapWidth = (int)boundingRect.getW()+1;
		int mapHeight = (int)boundingRect.getH()+1;
		
		int[][] obstacleMap = new int[mapHeight][mapWidth];
		
		for(int i= 0; i < mapHeight; i++ ) {
			for(int j= 0; j < mapWidth; j++ ) {
				obstacleMap[i][j] = 0;
			}
		}
		
		for(int i=0; i<lines.size(); i++ ) {
			int x1 = (int)lines.get(i).getX1();
			int y1 = (int)lines.get(i).getY1();
			int x2 = (int)lines.get(i).getX2();
			int y2 = (int)lines.get(i).getY2();
			
	           if(x1 == x2) { // vertikalne ciary
	        	   if(y1 < y2) {
	        		   for(int m = y1; m <= y2; m++) {
		        		   obstacleMap[m][x1] = 1;
		        	   }    	       
	        	   } else {
	        		   for(int m = y2; m <= y1; m++) {
	        			   obstacleMap[m][x1] = 1;
	        		   }
	        	   }
	           } 
	           
	           if(y1 == y2) { // horizontalne ciary
	        	   if(x1 < x2) {
	        		   for(int m = x1; m <= x2; m++) {
	        			   obstacleMap[y1][m] = 1;
	        		   }
	        	   } else {
	        		   for(int m = x2; m <= x1; m++) {
	        			   obstacleMap[y1][m] = 1;
	        		   }
	        	   }
	           }
			}
		
		/*for(int i = 0; i < mapHeight; i++) {
			for(int j = 0; j < mapWidth; j++) {
				System.out.print(obstacleMap[i][j]);
			}
			System.out.println();
		}*/
		return obstacleMap;
	}
	
	public void clearMapList() {
		lines.clear();
	}
	
	public Rectangle getBoundingRect() {
		return boundingRect;
	}

}
