package sk.kamil.gui.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;

import sk.kamil.gui.canvas.CanvasPanel;
import sk.kamil.gui.canvas.LineGraphics;
import sk.kamil.gui.canvas.MapWorld;
import sk.kamil.gui.canvas.ParticleSetGraphics;
import sk.kamil.gui.control.RobotControlPanel.HistoryItem;
import sk.kamil.gui.control.RobotControlPanel.MoveItem;
import sk.kamil.gui.control.RobotControlPanel.RotateItem;
import sk.kamil.gui.log.LogPanel;
import sk.umb.projekty.ardbot.Hlavna;
import sk.umb.projekty.ardbot.astar.MainAStar;
import sk.umb.projekty.ardbot.bt.BTConnection;
import sk.umb.projekty.ardbot.dummy.DummyConnection;
import sk.umb.projekty.ardbot.dummy.DummyInputStream;
import sk.umb.projekty.ardbot.navigate.RobotNavigate;
import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements;
import sk.umb.projekty.ardbot.particle_filter.DistanceMeasurements.Senzor;
import sk.umb.projekty.ardbot.robot.Robot;
import sk.umb.projekty.ardbot.robot.RobotConnection;
import sk.umb.projekty.ardbot.robot.command.Led;
import sk.umb.projekty.ardbot.robot.command.ReadCompass;
import sk.umb.projekty.ardbot.robot.command.ReadUltrasonicSensors;

/**
 * @author 7KQR2
 */
public class MainPanel extends JPanel {//implements ActionListener {
	
	private static final long serialVersionUID = 3927558685105594394L;

	private String[] args;
	private RobotConnection con;
	private Robot robot;
	private int xRobot;
	private int yRobot;
	private CanvasPanel canvasPanel;

	private String[] mapArrayStrings = { "Map01", "Map02", "Map03", "Map04", "Map05"};
	private String[] numberParticleArrayStrings = { "100", "1000", "10000"};
	private String mapPath = "Map03";
	private int numberOfParticle = 1000;
	private MapWorld map;
	private ParticleSetGraphics particles;

	private RobotControlPanel robotControlPanel;
	private LogPanel logPanel;
	
	private DummyInputStream dummyDis;
	private String btOrDummy = "B";
	private static boolean drawBoolRobot = false;
	private static boolean drawBoolAStar = false;

	public MainPanel(String[] args) {
		this.args = args;
		this.setPreferredSize(new Dimension(1200, 700));
		this.setLayout(new BorderLayout());
		this.setBackground(new Color(112, 181, 155));
		
		buildWorld(mapPath);
		initPanelComponent();
	}

	private void buildWorld(String mapPath) {
		map = new MapWorld();
		readMapFromFile(mapPath);	
		particles = new ParticleSetGraphics(numberOfParticle, map);
	}

	private void initPanelComponent() {
		JPanel topPanel = new JPanel(); // hornyPanel JPanel
		topPanel.setLayout(new FlowLayout());
		topPanel.setBorder(new LineBorder(new Color(190, 190, 190), 2));
		this.add(topPanel, BorderLayout.NORTH);
		
		JPanel commandPanel = new JPanel();
		commandPanel.setLayout(new BoxLayout(commandPanel, BoxLayout.PAGE_AXIS));
		topPanel.add(commandPanel);
		
		JPanel selectPanel = new JPanel();
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.PAGE_AXIS));
		topPanel.add(selectPanel);

		canvasPanel = new CanvasPanel(map, particles); // canvas JPanel
		canvasPanel.setBackground(new Color(41, 161, 118));
		topPanel.setBorder(new LineBorder(new Color(190, 190, 190), 2));
		this.add(canvasPanel, BorderLayout.CENTER);

		logPanel = new LogPanel(robot); // logovaci panel
		this.add(logPanel, BorderLayout.EAST);

		JComboBox mapList = new JComboBox(mapArrayStrings);
		mapList.setSelectedIndex(2);
		mapList.addActionListener( new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 JComboBox cb = (JComboBox)e.getSource();				
			     mapPath = (String)cb.getSelectedItem();
			     
			     readMapFromFile(mapPath);	
			     repaint();
			 }
	    });
		selectPanel.add(mapList);
		
		JComboBox numberParticleList = new JComboBox(numberParticleArrayStrings);
		numberParticleList.setSelectedIndex(2);
		numberParticleList.addActionListener( new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
				 JComboBox cb = (JComboBox)e.getSource();				
			     numberOfParticle = Integer.valueOf((String)cb.getSelectedItem());
			     
			   	 particles.clearParticleArray();
				 particles = new ParticleSetGraphics(numberOfParticle, map);
		 		 canvasPanel.setParticleGraphics(particles);
		 		 repaint();
			 }
	    });
		selectPanel.add(numberParticleList);
		
		JRadioButton btRadioBtn = new JRadioButton("BT",true);
		btRadioBtn.setActionCommand("B");
		JRadioButton dummyRadioBtn = new JRadioButton("Dummy", false);
		dummyRadioBtn.setActionCommand("D");
		final ButtonGroup group = new ButtonGroup();
	    group.add(btRadioBtn);
	    group.add(dummyRadioBtn);
		
		btRadioBtn.addActionListener(new ActionListener() {
			 @Override
			 public void actionPerformed(ActionEvent e) {
			 	 btOrDummy = e.getActionCommand();
			 }
		});
	    commandPanel.add(btRadioBtn);
	        
	    dummyRadioBtn.addActionListener(new ActionListener() {
		 	 @Override
		 	 public void actionPerformed(ActionEvent e) {
		 		 btOrDummy = e.getActionCommand();
		 	}
		});
	    dummyRadioBtn.setSelected(false);
	    commandPanel.add(dummyRadioBtn);
	    
		JButton restartBtn = new JButton("Restart");
		restartBtn.addActionListener(new ActionListener() {
		 	 @Override
		 	 public void actionPerformed(ActionEvent e) {
		 		if(robot != null) {
		 		  try {
					    robot.disconnect();
					    logPanel.sendMessageArduino("Robot disconnect");
					    drawBoolRobot = false;
				  } catch (IOException e1) {
					    e1.printStackTrace();
				  }
		 		}
		 			
		 		particles.clearParticleArray();
				particles = new ParticleSetGraphics(numberOfParticle, map);
		 		canvasPanel.setParticleGraphics(particles);
				repaint();
		 	}
		});
		commandPanel.add(restartBtn);
		
		JButton connectBtn = new JButton("Connect");
		connectBtn.addActionListener(new ActionListener() {
		 	 @Override
		 	 public void actionPerformed(ActionEvent e) {
		 		  if(btOrDummy.equals("B")) {
		 			 connectToArduino(); // pripojenie
		 		  }
		 		  else {
		 			 connectToDummy();
		 			 drawBoolRobot = true;
		 		  }
		 		  repaint();
		 	}
		});
		topPanel.add(connectBtn);
		
		JButton removeBtn = new JButton("Disconnect");
		  removeBtn.addActionListener(new ActionListener() {
		 	   @Override
		 	   public void actionPerformed(ActionEvent e) {
		 			 try {
						  robot.disconnect();
						  logPanel.sendMessageArduino("Robot disconnect");
						  drawBoolRobot = false;
						  drawBoolAStar = false;
					  } catch (IOException e1) {
						  e1.printStackTrace();
					  }
		 			  repaint();
		 	   }
		  });
		topPanel.add(removeBtn);
		
		JButton ledBtn = new JButton("Led");  // nefunguje pri realnom robotovi
	    ledBtn.addActionListener(new ActionListener() {
	 			@Override
	 			public void actionPerformed(ActionEvent e) {
	 				if(con != null) {
	 				  if(btOrDummy.equals("B")) {
	 					 try {
							   robot.execute(new Led(true));			
							   try {
								   Thread.sleep(1000);
							   } catch (InterruptedException e1) {
								   e1.printStackTrace();
							   }
			 				   robot.execute(new Led(false));
			 				   
						    } catch (IOException e1) {
							   e1.printStackTrace();
						    }
	 		 		      }
	 				 logPanel.sendMessageArduino("Blink");
	 		 		 repaint();
	 				}
	 				else {
	 					JOptionPane.showMessageDialog(null, "Pripojte robota!!!");
	 				}
	 		     }
	 	     });
	    topPanel.add(ledBtn);
	    
	    JButton compassBtn = new JButton("Compass");
	    compassBtn.addActionListener(new ActionListener() {
	 			@Override
	 			public void actionPerformed(ActionEvent e) {
	 				if(con != null) {
	 				   try {
	 					  int compassValue = (Integer)robot.execute(new ReadCompass());
						  logPanel.sendMessageArduino("[" + String.valueOf(compassValue) + "]");
					   } catch (IOException e1) {
						  e1.printStackTrace();
					   }
	 				} else {
	 					JOptionPane.showMessageDialog(null, "Pripojte robota!!!");
	 				}
	 			}
	 	     });
	    topPanel.add(compassBtn);
		  
	    JButton readBtn = new JButton("Read");
	    readBtn.addActionListener(new ActionListener() {
	 			@Override
	 			public void actionPerformed(ActionEvent e) {	
	 				if(con != null) {
	 					 DistanceMeasurements readings = getReadings();
		 				 readings.showReadings();
		 			} else {
		 					JOptionPane.showMessageDialog(null, "Pripojte robota!!!");
		 			} 			  
	 			}
	 	     });
	    topPanel.add(readBtn);	    
	    
		JButton particlesBtn = new JButton("Particles");
	    particlesBtn.addActionListener(new ActionListener() {
	 			@Override
	 			public void actionPerformed(ActionEvent e) {
	 				if(con != null) {
	 					  processParticles();
		 			} else {
		 					JOptionPane.showMessageDialog(null, "Pripojte robota!!!");
		 			} 
	 			}
	 	     });
	    topPanel.add(particlesBtn);
	    
	    robotControlPanel = new RobotControlPanel(robot, canvasPanel);
		topPanel.add(robotControlPanel);
		
		JButton aStarNavigateBtn = new JButton("A* navigate");
		aStarNavigateBtn.addActionListener(new ActionListener() {
		    @Override
		  	public void actionPerformed(ActionEvent e) {
		    	robotNavigate();
		  	}
		});
		topPanel.add(aStarNavigateBtn);
	}
	
	private void readMapFromFile(String pathFile) {
		map.clearMapList();
    	InputStream input = this.getClass().getResourceAsStream("/maps/" + pathFile + ".txt");
    	InputStreamReader isr = new InputStreamReader(input);
        BufferedReader reader = new BufferedReader(isr);
        String line = null;
        try {
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				float[] mapCoord = new float[parts.length];
				for(int i = 0; i < parts.length; i++) {
					mapCoord[i] = Float.valueOf(parts[i]);
				}
				map.addLine(new LineGraphics(mapCoord[0], mapCoord[1], mapCoord[2], mapCoord[3]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}     
    }
	
	// pripojenie ku arduinu cez bluetooth
	private void connectToArduino() {
		String btsUrl = null;
		if (args.length>0) {
			// btspp://301412041124:1;authenticate=false;encrypt=false;master=false
			btsUrl = args[0];
		} else {
			try {
				btsUrl = Hlavna.getRFCOMMUrl();
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (btsUrl==null) {
			return;
		}
		// pripoj sa k robotovi a sprav jednoduchu komunikaciu
		con = new BTConnection(btsUrl);
		robot = new Robot(con);
		robotControlPanel.setRobot(robot);
	    try {
			robot.connect();
			logPanel.sendMessageArduino("Robot connect");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	// pripojenie ku dummy robotovi
	public void connectToDummy() {
		con = new DummyConnection(map);
		robot = new Robot(con);
		robotControlPanel.setRobot(robot);
	    try {
			robot.connect();
			logPanel.sendMessageArduino("Dummy connect");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	dummyDis = (DummyInputStream) con.getInputStream();
    	String response = dummyDis.getResponse();
    	String[] parts = response.split(",");
		
    	logPanel.sendMessageArduino("X:" + parts[0] + " Y:" + parts[1] + " θ:" + parts[2]);
	}

    // ziskanie dat z robota
	private DistanceMeasurements getReadings() {
	    int[] pi = null;
		try {
			// citaj vzdialenosti (v cm)
			pi = (int[])robot.execute(new ReadUltrasonicSensors());
			logPanel.sendMessageArduino(Arrays.toString(pi));
		} catch (IOException e) {
				e.printStackTrace();
		}

		DistanceMeasurements readings = new DistanceMeasurements();
		readings.setDistance(Senzor.LEFT, pi[0]);
		readings.setDistance(Senzor.FRONT, pi[1]);
		readings.setDistance(Senzor.RIGHT, pi[2]);
		readings.setDistance(Senzor.BACK, pi[3]);
		
		return readings;
   	}

	private void processParticles() {
		// sprav pohyb castic podla poslednych pohybov robota (nameranych)
		List<HistoryItem> h = robotControlPanel.getMovementHistory();
		for (HistoryItem hi : h) {
			if (hi instanceof MoveItem) {
				MoveItem mi = (MoveItem)hi;
				// v mi je pocet dielikov, o kolko sa to posunulo. 20dielikov = 21.24cm
				float d = mi.left * (21.24f/20f);
				particles.applyMove(0, d);
				logPanel.sendMessageArduino("Move " + "L:" + mi.left + " R:" + mi.right);
			}
			if (hi instanceof RotateItem) {
				RotateItem ri = (RotateItem)hi;
				particles.applyMove(ri.angle, 0);
				logPanel.sendMessageArduino("Rotate [" + ri.angle + "]");
			}
		}
		
		robotControlPanel.clearMovementHistory();

		DistanceMeasurements readings = getReadings();
		// ignorujeme ak niektore meranie je mimo rozsahu
		if (!readings.incomplete()) {
			particles.recalculateDistances();				// vypocitaju sa vzdialenosti castic od okrajov mapy
			particles.calculateWeights(readings);
			particles.resample();			
		}

		if(particles.isRobotLocalized()) {
			logPanel.sendMessageArduino("Robot is localized");
			xRobot = (int) particles.getOldPoseX();
			yRobot = (int) particles.getOldPoseY();		
			logPanel.sendMessageArduino("X: " + xRobot + ", Y: " + yRobot);
		} else {
			logPanel.sendMessageArduino("Robot is not localized");
		}
//		float forwardRange = readings.getRange(1);				
		// nepohybuje sa ak je pred stenou
//		if (forwardRange > 0 && move.distance + ParticleSet.BORDER + ROBOT_SIZE > forwardRange) {
//			move.distance = 0f; // nastavíme vzdialenost na nulu
//		}
		repaint();
	}
	
	public void robotNavigate() {
		MainAStar mainAStar = new MainAStar(map, xRobot, yRobot);
    	drawBoolAStar = true;
		try {
			RobotNavigate robotNavigate = new RobotNavigate(xRobot, yRobot, robot, particles);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		DistanceMeasurements readings = getReadings();
		// ignorujeme ak niektore meranie je mimo rozsahu
		if (!readings.incomplete()) {
			particles.recalculateDistances();				// vypocitaju sa vzdialenosti castic od okrajov mapy
			particles.calculateWeights(readings);
			particles.resample();			
		}

		if(particles.isRobotLocalized()) {
			logPanel.sendMessageArduino("Robot is localized");
			xRobot = (int) particles.getOldPoseX();
			yRobot = (int) particles.getOldPoseY();
			logPanel.sendMessageArduino("X: " + xRobot + ", Y: " + yRobot);
		} else {
			logPanel.sendMessageArduino("Robot is not localized");
		}
	
	    repaint();
	}
	
	public static boolean getDrawBoolRobot() {
		return drawBoolRobot;
	}
	
	public static boolean getDrawBoolAStar() {
		return drawBoolAStar;
	}
}
