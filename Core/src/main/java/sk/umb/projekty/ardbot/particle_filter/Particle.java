package sk.umb.projekty.ardbot.particle_filter;

import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements.Senzor;
import sk.umb.projekty.ardbot.world.Map;

/**
 * Castica casticoveho filtra.
 *
 */
public class Particle {

	// umiestnenie a natocenie
	private Pose pose;

	// vaha
	private float weight;

	// vzdialenosti castice od krajov
	private DistanceMeasurements distances = new DistanceMeasurements();

	// svet
	private Map map;

	public Particle(Map map, Pose pose) {
		this.map = map;
		this.pose = pose;
		this.weight = 0;
	}

	public Pose getPose() {
		return pose;
	}
	
	public void setPose(Pose pose) {
		this.pose = pose;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	/**
	 * Vypocet vzdialenosti castice od prekazok na mape.
	 * 
	 * @param map
	 */
	public void recalculateDistances() {		
		distances.setDistance(Senzor.LEFT, map.range(pose.getRotatedCopy(-90)));
		distances.setDistance(Senzor.FRONT, map.range(pose));
		distances.setDistance(Senzor.RIGHT, map.range(pose.getRotatedCopy(90)));
		distances.setDistance(Senzor.BACK, map.range(pose.getRotatedCopy(180)));
	}

	/**
	 * Vypocet vahy castice vzhladom na realne merania.
	 * 
	 * @param realMeasurements
	 */
	public void calculateWeight(DistanceMeasurements realMeasurements) {
		weight = 1;
		for (Senzor s : DistanceMeasurements.getSenzors()) {
			float myReading = distances.getDistance(s);
			float robotReading = realMeasurements.getDistance(s);
			float diff = Math.abs((robotReading - myReading) / 5);
			float inverse = 1/(diff+1);
			weight *= inverse;
		}
		
	}

	/**
	 * Posunutie castice na zaklade pohybu robota.
	 * 
	 * @param angle
	 * @param distance
	 */
	public void move(float angle, float distance) {
		pose.addNoise();
		pose.move(angle, distance);
	}

}
