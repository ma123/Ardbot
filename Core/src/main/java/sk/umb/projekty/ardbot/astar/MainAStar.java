package sk.umb.projekty.ardbot.astar;

import sk.umb.projekty.ardbot.world.Map;
import sk.umb.projekty.ardbot.world.RobotDestination;

public class MainAStar {
	private Map map;
	private static Path robotPath;
	private int xRobot, yRobot;
	
	public MainAStar(Map map, int xRobot, int yRobot) {
		this.map = map;
		this.xRobot = xRobot;
		this.yRobot = yRobot;

	    AreaMap mapArea = new AreaMap(((int)this.map.getBoundingRect().getH() + 1), ((int)this.map.getBoundingRect().getW() + 1), this.map.createObstacleMap());
	    AStarHeuristic heuristic = new ClosestHeuristic();
	    AStar pathFinder = new AStar(mapArea, heuristic);
	    try {
	    	 robotPath = pathFinder.calcShortestPath(this.yRobot, this.xRobot, RobotDestination.getyPozCm(), RobotDestination.getxPozCm());    	 
		} catch (Exception e) {
		}
	}

	public static Path getRobotPatch() {
		return robotPath;
	}
}
