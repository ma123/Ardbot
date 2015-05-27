package sk.kamil.gui.control;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;

import sk.kamil.gui.log.LogPanel;
import sk.umb.projekty.ardbot.robot.Robot;
import sk.umb.projekty.ardbot.robot.command.Move;
import sk.umb.projekty.ardbot.robot.command.Move.Direction;
import sk.umb.projekty.ardbot.robot.command.Move.WaitType;
import sk.umb.projekty.ardbot.robot.command.Rotate;

public class RobotControlPanel extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1802195878912156058L;

	private Robot robot;
	private List<HistoryItem> movementHistory = new ArrayList<HistoryItem>();
	private JPanel canvas;
	private LogPanel logPanel;

	private JButton upBtn, downBtn, leftBtn, rightBtn, stopBtn;

	public RobotControlPanel(Robot robot, JPanel canvas) {
		this.robot = robot;
		this.canvas = canvas;
    	this.setLayout(new BorderLayout());
    	upBtn = new JButton("↑"); // hore tlacitko
    	this.add(upBtn, BorderLayout.NORTH);	
		upBtn.addActionListener(this);
    	downBtn = new JButton("↓"); // dole tlacitko
    	this.add(downBtn, BorderLayout.SOUTH);
    	downBtn.addActionListener(this);
    	leftBtn = new JButton("←"); // vlavo tlacitko
    	this.add(leftBtn, BorderLayout.WEST);
    	leftBtn.addActionListener(this);
    	rightBtn = new JButton("→"); // vpravo tlacitko
    	this.add(rightBtn, BorderLayout.EAST);
    	rightBtn.addActionListener(this);
    	stopBtn = new JButton("Stop"); // stop tlacitko
    	this.add(stopBtn, BorderLayout.CENTER);
    	stopBtn.addActionListener(this);
    }
    
	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == upBtn) {	
			try {
 				Move m = new Move(Direction.FORWARD, (byte)230, Direction.FORWARD, (byte)230, WaitType.TIME, 1000);
				int[] pi = (int[])robot.execute(m);
				movementHistory.add(new MoveItem(pi[0], pi[1]));	
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == downBtn) {		
			try {
 				Move m = new Move(Direction.BACKWARD, (byte)230, Direction.BACKWARD, (byte)230, WaitType.TIME, 1000);
				int[] pi = (int[])robot.execute(m);
				movementHistory.add(new MoveItem(pi[0], pi[1]));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == leftBtn) {
			try {
// 				Move m = new Move(Direction.BACKWARD, (byte)255, Direction.FORWARD, (byte)255, WaitType.TIME, 200);
 				Rotate m = new Rotate(315);
				robot.execute(m);
				movementHistory.add(new RotateItem(315));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == rightBtn) {
			try {
 				//Move m = new Move(Direction.FORWARD, (byte)255, Direction.BACKWARD, (byte)255, WaitType.TIME, 200);
 				Rotate m = new Rotate(45);
				robot.execute(m);
				movementHistory.add(new RotateItem(45));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == stopBtn) {
			try {
 				Move m = new Move(Direction.RELEASE, (byte)0, Direction.RELEASE, (byte)0, WaitType.TIME, 1000);
				robot.execute(m);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		canvas.repaint();
	}
	
	

	public List<HistoryItem> getMovementHistory() {
		return movementHistory;
	}

	public void clearMovementHistory() {
		movementHistory.clear();
	}

	class HistoryItem { }
	class MoveItem extends HistoryItem {
		int left;
		int right;
		MoveItem(int left, int right) {
			this.left = left;
			this.right = right;
		}
	}
	class RotateItem extends HistoryItem {
		int angle;
		RotateItem(int angle) {
			this.angle = angle;
		}
	}

}
