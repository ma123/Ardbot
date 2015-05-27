package sk.umb.projekty.ardbot.bt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import sk.umb.projekty.ardbot.robot.RobotConnection;

public class BTConnection implements RobotConnection {

	private static Logger logger = Logger.getLogger(BTConnection.class.getPackage().getName());

	private String btsUrl;

	private StreamConnection con = null;
	private InputStream is = null;
	private OutputStream os = null;

	public BTConnection(String btsUrl) {
		this.btsUrl = btsUrl;
	}

	@Override
	public void open() throws IOException {
//		if (!robot.isAuthenticated()) {
//			String PIN = "1234";
//			boolean paired = RemoteDeviceHelper.authenticate(robot, PIN);
//			logger.fine("autentifikacia: " + paired);
//		}
		logger.fine("pripajam sa ku " + btsUrl);
		con = (StreamConnection)Connector.open(btsUrl);
		logger.fine("ok..");
		is = con.openInputStream();
		os = con.openOutputStream();
	}

	@Override
	public InputStream getInputStream() {
		return is;
	}

	@Override
	public OutputStream getOutputStream() {
		return os;
	}

	@Override
	public void close() throws IOException {
		if (con!=null) {
			con.close();
		}
		if (is!=null) {
			is.close();
		}
		if (os!=null) {
			os.close();
		}
	}

}
