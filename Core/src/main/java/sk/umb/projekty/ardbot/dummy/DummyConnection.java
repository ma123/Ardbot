package sk.umb.projekty.ardbot.dummy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import sk.umb.projekty.ardbot.robot.RobotConnection;
import sk.umb.projekty.ardbot.world.Map;

public class DummyConnection implements RobotConnection {

	private static Logger logger = Logger.getLogger(DummyConnection.class.getPackage().getName());

	private Map map;

	private DummyRobot robot;

	private DummyInputStream dis;
	private DummyOutputStream dos;

	public DummyConnection(Map map) {
		logger.fine("vytvaram dummy pripojenie");
		this.map = map;
	}

	@Override
	public void open() throws IOException {
		dis = new DummyInputStream();
		robot = new DummyRobot(dis, map);
		dos = new DummyOutputStream(robot);
	}

	@Override
	public InputStream getInputStream() {
		return dis;
	}

	@Override
	public OutputStream getOutputStream() {
		return dos;
	}
	@Override
	public void close() throws IOException {
		dis.close();
		dos.close();
	}

}
