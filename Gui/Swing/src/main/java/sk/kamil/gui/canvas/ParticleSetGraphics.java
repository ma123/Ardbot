package sk.kamil.gui.canvas;

import java.awt.Graphics2D;

import sk.umb.projekty.ardbot.particle_filter.Particle;
import sk.umb.projekty.ardbot.particle_filter.ParticleSet;
import sk.umb.projekty.ardbot.particle_filter.Pose;
import sk.umb.projekty.ardbot.world.Map;

public class ParticleSetGraphics extends ParticleSet {

	public ParticleSetGraphics(int noParticles, Map map) {
		super(noParticles, map);
	}

	public void paint(Graphics2D g2d, float rw, float rh) {
		for (int i = 0; i < particles.size(); i++) {
			((PoseGraphics) particles.get(i).getPose()).paint(g2d, rw, rh);
		}
	}

	protected Particle createParticle(float x, float y, float angle) {
		return new Particle(map, new PoseGraphics(x, y, angle));
	}
}
