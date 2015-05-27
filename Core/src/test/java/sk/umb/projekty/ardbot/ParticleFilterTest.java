package sk.umb.projekty.ardbot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sk.umb.projekty.ardbot.particle_filter.ParticleSet;
import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements;
import sk.umb.projekty.ardbot.world.Line;
import sk.umb.projekty.ardbot.world.Map;

public class ParticleFilterTest {

	@Test
	public void testParticleFiltra() {
		// priprav mapu (v cm)
		Map map = new Map();
		map.addLine(new Line(0, 0, 150, 0));
		map.addLine(new Line(0, 0, 0, 100));
		map.addLine(new Line(0, 100, 100, 100));
		map.addLine(new Line(150, 0, 150, 150));
		map.addLine(new Line(100, 100, 100, 150));
		map.addLine(new Line(100, 150, 150, 150));
    	assertEquals(150f, map.getBoundingRect().getW());
    	assertEquals(150f, map.getBoundingRect().getH());

    	// generuj castice
		ParticleSet particles = new ParticleSet(1000, map);

		// simulacia pohybu robota a citania senzorov
		DistanceMeasurements measurements;
		// krok 1 - hodnoty zo senzorov
		measurements = new DistanceMeasurements();
		//TODO new float[]{108f, 85f, 56f}
		particles.calculateWeights(measurements);
		particles.resample();
		// krok 2 - posun dopredu a otoc o 45 stupnov
		particles.applyMove(-45, 20);
		particles.recalculateDistances();
		// krok 2 - hodnoty zo senzorov
		measurements = new DistanceMeasurements();
		//TODO new float[]{55f, 60f, 40f}
		particles.calculateWeights(measurements);
		particles.resample();
		// krok 3 - posun dopredu
		particles.applyMove(0, 20);
		particles.recalculateDistances();
		// krok 3 - hodnoty zo senzorov
		measurements = new DistanceMeasurements();
		//TODO new float[]{35f, 60f, 90f}
		particles.calculateWeights(measurements);
		particles.resample();
		//TODO pozicia je 115,60
	}

}
