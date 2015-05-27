package sk.umb.projekty.ardbot.particle_filter;

import java.util.HashMap;
import java.util.Map;

/**
 * Reprezentacia vzdialenosti (meranych senzormi, ako aj vypocitanych pre kazdu casticu).
 * 
 */
public class DistanceMeasurements {

	// vzdialenosti
	private Map<Senzor, Float> distances = new HashMap<Senzor, Float>();

	/**
	 * Pocet vzdialenosti.
	 * 
	 * @return
	 */
	public static Senzor[] getSenzors() {
		return Senzor.values();
	}

	/**
	 * Nastavenie hodnoty vzdialenosti.
	 * 
	 * @param i
	 * @param pi
	 */
	public void setDistance(Senzor s, float f) {
		distances.put(s, f);
	}

	/**
	 * Citanie hodnoty vzdialenosti.
	 * 
	 * @param i
	 * @return
	 */
	public float getDistance(Senzor s) {
		return distances.get(s);
	}

	/**
	 * Vypis vzdialenosti.
	 */
	public void showReadings() {
		for (Senzor s : Senzor.values()) {
			System.out.println("Reading " + s + " = " + distances.get(s));
		}
	}

	/**
	 * Overenie kompletnosti meranych vzdialenosti.
	 * 
	 * @return
	 */
	public boolean incomplete() {
		for (Senzor s : Senzor.values()) {
			if (distances.get(s)==null) {
				return true;
			}
		}
		return false;
	}

	public enum Senzor {
		LEFT, FRONT, RIGHT, BACK;
	}

}
