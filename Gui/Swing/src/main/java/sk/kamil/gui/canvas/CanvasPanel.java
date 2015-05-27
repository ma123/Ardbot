package sk.kamil.gui.canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import sk.kamil.gui.control.MainPanel;
import sk.umb.projekty.ardbot.astar.MainAStar;
import sk.umb.projekty.ardbot.astar.Path;
import sk.umb.projekty.ardbot.dummy.DummyRobot;
import sk.umb.projekty.ardbot.particle_filter.Pose;

public class CanvasPanel extends JPanel implements MouseListener {

	private static final long serialVersionUID = 3430140619003955271L;

	private MapWorld map;
	private static ParticleSetGraphics particles;
	private RobotDestinationGraphics robotDestinationGraphics;
	private PoseGraphics robotPoseGraphics;
	private AstarGraphics astarGraphics = new AstarGraphics();
	private static float xClick, yClick = 4;
	
	public CanvasPanel(MapWorld map, ParticleSetGraphics particles) {
		super();
		this.map = map;
		this.particles = particles;
		addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();
		float rw = (float)size.getWidth() / map.getBoundingRect().getW();
		float rh = (float)size.getHeight() / map.getBoundingRect().getH();

		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(Color.black);
		map.paint(g2d, rw, rh);
		g2d.setColor(Color.green);
		particles.paint(g2d, rw, rh);
		
		robotDestinationGraphics = new RobotDestinationGraphics(xClick , yClick, rw, rh);
		g2d.setColor(Color.red);
		robotDestinationGraphics.paint(g2d);
		
		if(MainPanel.getDrawBoolRobot()) {
			Pose pose = DummyRobot.getPose();
			robotPoseGraphics = new PoseGraphics(pose.getX(), pose.getY(), pose.getAngle());
			g2d.setColor(Color.blue);
			robotPoseGraphics.paint(g2d, rw, rh);
		}	
		
		if(MainPanel.getDrawBoolAStar()) {
			Path robotPath = MainAStar.getRobotPatch();
			if(robotPath != null) {
				g2d.setColor(Color.orange);
			    for(int i = 0; i < robotPath.getLength(); i++) {
			    	if((i % 5) == 0) {
			    		astarGraphics.paint(g2d, robotPath.getY(i), robotPath.getX(i), rw, rh);
			    	}
			    }	
			}
		}
		g2d.dispose();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		xClick =e.getX();
	    yClick=e.getY();
	    repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	public static float getxClick() {
		return xClick;
	}

	public static float getyClick() {
		return yClick;
	}
	
	public static void setParticleGraphics(ParticleSetGraphics p) {
		particles = p;
	}
}
