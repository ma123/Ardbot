package sk.umb.projekty.ardbot.particle_filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.umb.projekty.ardbot.world.Map;
import sk.umb.projekty.ardbot.world.Point;
import sk.umb.projekty.ardbot.world.Rectangle;

/**
 * Casticovy filter.
 * 
 */
public class ParticleSet {

	// minimalna vzdialenost castice od steny
	private static final int BORDER = 5;
	private static final int LOCALIZATION_AREA = 15; 
	// castice
	protected List<Particle> particles = new ArrayList<Particle>(); 
	private float oldPoseX;
	private float oldPoseY;

	// svet
	protected Map map;

	/**
	 * Vytvorenie casticoveho filtra s danym poctom castic. Castice su vygenerovane v rozsahu danom svetom.
	 * 
	 * @param noParticles
	 * @param map
	 */
	public ParticleSet(int noParticles, Map map) {
		this.map = map;
		
		for (int i=0; i<noParticles; i++) {
			Particle newParticle = generateParticle();
		    particles.add(newParticle);
	    }
		recalculateDistances();
	}

    // generovanie castic
	private Particle generateParticle() {
		float x, y, angle;
		Rectangle bound = map.getBoundingRect();
		Rectangle innerRect = new Rectangle(bound.getX()+BORDER, bound.getY()+BORDER, 
				bound.getW()-BORDER*2, bound.getH()-BORDER*2);

		// generujeme nahodne hodnoty x,y vo vnutri mapy
		for(;;) {
			x = innerRect.getX() + (((float) Math.random()) * innerRect.getW());
			y = innerRect.getY() + (((float) Math.random()) * innerRect.getH());
			
			Point p = new Point(x, y);
			
			if (map.inside(p)) {
				break;
			}
		}

		// nahodne urcenie uhla castice
		angle = ((float) Math.random()) * 360;
		// vytvor casticu
		return createParticle(x,y,angle);
	}

	protected Particle createParticle(float x, float y, float angle) {
		return new Particle(map, new Pose(x, y, angle));
	}

	/**
	 * Posunutie vsetkych castic na zaklade pohybu robota.
	 * 
	 * @param angle
	 * @param distance
	 */
	public void applyMove(float angle, float distance) {
		for(int i=0; i<particles.size(); i++) {
			particles.get(i).move(angle, distance);
		}
	}

	/**
	 * Vypocet vzdialenosti vsetkych castic od prekazok na mape.
	 */
	public void recalculateDistances() {
		for(int i=0; i<particles.size(); i++) {
			particles.get(i).recalculateDistances();
		}
	}

	/**
	 * Vypocet vahy vsetkych castic vzhladom na realne merania.
	 * 
	 * @param rr
	 */
	public void calculateWeights(DistanceMeasurements rr) {
		for(int i=0; i<particles.size(); i++) {
			particles.get(i).calculateWeight(rr);
		}
	}

	/**
	 * Prevzorkovanie podla vahy castic.
	 */
	public void resample() {
		List<Particle> oldParticles = new ArrayList<Particle>(); 

		//vytvorenie kopii castic
		for (int i=0; i<particles.size(); i++) {
		   oldParticles.add(particles.get(i));
	    }
	
		// vyberame nahodne cislo a casticu s váhou vacsou alebo rovnou kym nemame kompletnu sadu castic
		int count = 0;	
		while (count < particles.size()) {
			float rand = (float) Math.random();
			for (int i=0; i<particles.size() && count<particles.size(); i++) {
				if (oldParticles.get(i).getWeight() >= rand) {
					Pose oldPose = oldParticles.get(i).getPose();
					Particle newParticle = createParticle(oldPose.getX(), oldPose.getY(), oldPose.getAngle());
					particles.set(count, newParticle);
					particles.get(count++).setWeight(0);
				}
			}
		}
	}
	
	public boolean isRobotLocalized() {
		int counter = 1;
		Pose oldPose = particles.get(0).getPose();
		oldPoseX = oldPose.getX();
		oldPoseY = oldPose.getY();
		
		for(int i=1; i<particles.size(); i++) {
			Pose pose = particles.get(i).getPose();
			float poseX = pose.getX();
			float poseY = pose.getY();
			
			if(((oldPoseX - LOCALIZATION_AREA) <= poseX) && ((oldPoseX + LOCALIZATION_AREA) >= poseX)) {
				if(((oldPoseY - LOCALIZATION_AREA) <= poseY) && ((oldPoseY + LOCALIZATION_AREA) >= poseY)) {
					counter++;
				}			
			}
		}
		
		if(counter == particles.size()) {    // ak sa pocet v oblasti rovna celkovemu poctu tak sprav aritmeticky priemer
			for(int i=0; i<particles.size(); i++) {  
				Pose pose = particles.get(i).getPose();
				oldPoseX = oldPoseX + pose.getX();
				oldPoseY = oldPoseY + pose.getY();
			}
			oldPoseX = oldPoseX / particles.size();
			oldPoseY = oldPoseY / particles.size();
			
			return true;
		}
		return false;
	}
	
	public List<Particle> getParticleList() {
		return particles;
	}

	public float getOldPoseX() {
		return oldPoseX;
	}

	public float getOldPoseY() {
		return oldPoseY;
	}
	
	public void clearParticleArray() {
		particles.clear();
	}
}
