package sk.umb.projekty.ardbot.navigate;

import java.io.IOException;

import sk.umb.projekty.ardbot.astar.MainAStar;
import sk.umb.projekty.ardbot.astar.Path;
import sk.umb.projekty.ardbot.particle_filter.ParticleSet;
import sk.umb.projekty.ardbot.robot.Robot;
import sk.umb.projekty.ardbot.robot.command.Move;
import sk.umb.projekty.ardbot.robot.command.Move.Direction;
import sk.umb.projekty.ardbot.robot.command.Move.WaitType;
import sk.umb.projekty.ardbot.robot.command.ReadCompass;
import sk.umb.projekty.ardbot.robot.command.Rotate;

public class RobotNavigate {
    private int xRobot, yRobot;
    private int compassValue;
    private ParticleSet particles;
    private Robot robot;
    private DetermineAngle determineAngle;
    private final int DIVIDE_PATH = 5;
    
    public RobotNavigate(int xRobot, int yRobot, Robot robot, ParticleSet particles) throws IOException {
        this.xRobot = xRobot;
        this.yRobot = yRobot;
        this.particles = particles;
        this.robot = robot;
        
        determinePath();
    }

	private void determinePath() throws IOException {
    	Path robotPath = MainAStar.getRobotPatch();
    	
		if(robotPath != null) {		
			int robotPathLength = robotPath.getLength();
	    	int robotPlanY = robotPath.getX((int)robotPathLength / DIVIDE_PATH);
	    	int robotPlanX = robotPath.getY((int)robotPathLength / DIVIDE_PATH);
	    	
	    	determineAngle = new DetermineAngle(this.xRobot, this.yRobot, robotPlanX, robotPlanY);
	   
			compassValue = (Integer)this.robot.execute(new ReadCompass());
	    	
	    	int rotateAngle = ((360 + (int)determineAngle.getAngle()) - compassValue) % 360;
	    	Rotate rotate = new Rotate(rotateAngle);
			this.robot.execute(rotate);
				
			this.particles.applyMove(rotateAngle, 0);
			
			Move move = new Move(Direction.BACKWARD, (byte)230, Direction.BACKWARD, (byte)230, WaitType.TIME, 1000);
			int[] pi = null;
			pi = (int[])this.robot.execute(move);

			float d = pi[0] * (21.24f/20f);
			this.particles.applyMove(0, d);
		}
	}
}