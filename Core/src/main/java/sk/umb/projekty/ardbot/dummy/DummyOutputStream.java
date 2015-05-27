package sk.umb.projekty.ardbot.dummy;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class DummyOutputStream extends OutputStream {

	private static Logger logger = Logger.getLogger(DummyOutputStream.class.getPackage().getName());

	private DummyRobot robot;

	private int lastCommand = -1;
	private StringBuffer request;

	public DummyOutputStream(DummyRobot robot) {
		this.robot = robot;
	}

	@Override
	public void write(int b) throws IOException {
		logger.fine("dostal som: " + b);
		if (lastCommand==-1) {
			lastCommand = b;
			request = new StringBuffer();
		} else {
			request.append((char)b);
		}
		
		if (lastCommand=='L') {
		    robot.ledCommand(request.toString());
		} else if (lastCommand=='K') {
			robot.compassCommand(request.toString());
			lastCommand = -1;
		} else if (lastCommand=='U') {
			robot.ultrasonicSensorsCommand(request.toString());
			lastCommand = -1;
		} else if (lastCommand=='M') {
			if (request.indexOf(".") == -1) {
				return;	
			}	
			robot.moveCommand(request.toString());
			request = null;
			lastCommand = -1;
		} else if (lastCommand=='R') {
			if (request.indexOf(".") == -1) {
				return;	
			}	
			robot.rotateCommand(request.toString());
			request = null;
			lastCommand = -1;
		}
	}

}
