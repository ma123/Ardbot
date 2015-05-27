package sk.umb.projekty.ardbot.dummy;

import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements;
import sk.umb.projekty.ardbot.particle_filter.Pose;
import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements.Senzor;
import sk.umb.projekty.ardbot.world.Map;
import sk.umb.projekty.ardbot.world.Point;
import sk.umb.projekty.ardbot.world.Rectangle;

/**
 * Fiktivny robot v definovanej mape. Pri vytvoreni instancie sa robot umiestni niekde na mape. Na zaklade prikazov, ktore potom dostava vzdy spravne
 * reaguje a spravne odpoveda (t.j. pri prikaze o posunutie sa na mape posunie, pri prikaze o nacitani hodnot ultrasonic senzorov vrati presne vypocitane
 * vzdialenosti od stien.
 * 
 * @author mvagac
 *
 */
public class DummyRobot {

	private DummyInputStream dis;
	private DistanceMeasurements distances = new DistanceMeasurements();
	private static final int BORDER = 5;
	private Map map;
	private static Pose pose;

	public DummyRobot(DummyInputStream dis, Map map) {
		this.dis = dis;
		this.map = map;
		generateDummyPose();
	}

	private void generateDummyPose() {
		float x, y, angle;
		Rectangle bound = map.getBoundingRect();
		Rectangle innerRect = new Rectangle(bound.getX()+BORDER, bound.getY()+BORDER, 
				bound.getW()-BORDER*2, bound.getH()-BORDER*2);

		for(;;) {
			x = innerRect.getX() + (((float) Math.random()) * innerRect.getW());
			y = innerRect.getY() + (((float) Math.random()) * innerRect.getH());
			Point p = new Point(x, y);
			
			if (map.inside(p)) {
				break;
			}
		}

		// nahodne urcenie uhla dummyRobota
		angle = ((float) Math.random()) * 360;
		// vytvor dummyPose
	    pose = new Pose(x, y, angle);
	    dis.setResponse((int)x + "," + (int)y + "," + (int)angle);
	}
	
	public void ledCommand(String request) {
	}

	// vrati hodnotu kompasu dummyPose
	public void compassCommand(String request) {
		dis.setResponse(String.valueOf((int)pose.getAngle()));
	}

	// vzpocita vzdialenosti od stien a vrati 4 hodnoty senzorov
	public void ultrasonicSensorsCommand(String request) {
		distances.setDistance(Senzor.LEFT, map.range(pose.getRotatedCopy(-90)));
		distances.setDistance(Senzor.FRONT, map.range(pose));
		distances.setDistance(Senzor.RIGHT, map.range(pose.getRotatedCopy(90)));
		distances.setDistance(Senzor.BACK, map.range(pose.getRotatedCopy(180)));
		
		String s = (int)distances.getDistance(Senzor.LEFT) + "\n" + (int)distances.getDistance(Senzor.FRONT) + "\n"
		+ (int)distances.getDistance(Senzor.RIGHT) + "\n" + (int)distances.getDistance(Senzor.BACK);
		dis.setResponse(s);
	}

	public void moveCommand(String request) {
	    final int leftWheel = 5;
	    final int rightWheel = 5;
		// v mi je pocet dielikov, o kolko sa to posunulo. 20dielikov = 21.24cm	    
		float d = leftWheel * (21.24f/20f);
		pose.move(0, d);
		String s = leftWheel + "\n" + rightWheel;
		dis.setResponse(s);
	}

	public void rotateCommand(String request) {		
	    float rotateAngle = Float.valueOf(request.replace(".", ""));
		pose.move(rotateAngle, 0);
		dis.setResponse(String.valueOf(rotateAngle));
	}

	public static Pose getPose() {
		return pose;
	}
}
