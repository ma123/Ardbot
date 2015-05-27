package sk.umb.projekty.ardbot;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Vector;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.bluetooth.RemoteDevice;

import sk.umb.projekty.ardbot.bt.BTConnection;
import sk.umb.projekty.ardbot.bt.ProtocolId;
import sk.umb.projekty.ardbot.bt.RemoteDeviceDiscovery;
import sk.umb.projekty.ardbot.bt.RemoteDeviceServicesSearch;
import sk.umb.projekty.ardbot.bt.RemoteDeviceServicesSearch.BTService;
import sk.umb.projekty.ardbot.robot.Robot;
import sk.umb.projekty.ardbot.robot.RobotConnection;

/**
 * Bluetooth.
 * 
 * sudo apt-get install libbluetooth-dev
 *
 * http://www.jsr82.com/jsr-82-sample-spp-server-and-client/
 */
public class Hlavna {
	public static Logger logger = Logger.getLogger(Hlavna.class.getPackage().getName());

	private static final String CONST_ROBOT_DEVICE_NAME = "HC-06";
	static {
		try {
			URL url = ClassLoader.getSystemResource("logging.properties");
			LogManager.getLogManager().readConfiguration(url.openStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		// ziskaj adresu pripojenia
		String btsUrl;
		if (args.length>0) {
			// btspp://301412041124:1;authenticate=false;encrypt=false;master=false
			btsUrl = args[0];
		} else {
			btsUrl = getRFCOMMUrl();
		}
		if (btsUrl==null) {
			return;
		}
		// pripoj sa k robotovi a sprav jednoduchu komunikaciu
		RobotConnection con = new BTConnection(btsUrl);
		Robot r = new Robot(con);
		try {
			r.connect();
			//r.commTest();
			//r.execute(new Led(true));
			//Thread.sleep(1000);
			//r.execute(new Led(false));
			// citaj ultrasonicke senzory
			//int[] pi = (int[])r.execute(new ReadUltrasonicSensors());
			//logger.fine("hodnoty ultrasonic senzorov: " + Arrays.toString(pi));
			// odskusaj motory
			//r.execute(new SetMotor(Motor.NO1, Direction.FORWARD, (byte)100));
			//Thread.sleep(1000);
			//r.execute(new SetMotor(Motor.NO1, Direction.FORWARD, (byte)0));
		} finally {
			r.disconnect();
		}
	}

	public static String getRFCOMMUrl() throws InterruptedException, IOException {
		// najdi bluetooth zariadenie robota
		RemoteDeviceDiscovery remoteDeviceDiscovery = new RemoteDeviceDiscovery();
		Vector<RemoteDevice> devices = remoteDeviceDiscovery.getDevices();
		logger.fine("pocet najdenych zariadeni: " + devices.size());
		RemoteDevice robot = null;
		StringBuffer sb = new StringBuffer();
		for (RemoteDevice btDevice : devices) {
			String n = btDevice.getFriendlyName(false);
			sb.append("," + n);
			if (CONST_ROBOT_DEVICE_NAME.equals(n)) {
				robot = btDevice;
				break;
			}
		}
		if (robot==null) {
			logger.severe("robot " + CONST_ROBOT_DEVICE_NAME + " nebol najdeny!");
			logger.severe("viditelne zariadenia: " + (sb.length()>0?sb.toString().substring(1):""));
			return null;
		}
		logger.fine("robot bol najdeny");
		logger.fine("  nazov: " + robot.getFriendlyName(false));
		logger.fine("  adresa: " + robot.getBluetoothAddress());
		// najdi RFCOMM sluzbu
		RemoteDeviceServicesSearch ss = new RemoteDeviceServicesSearch();
		List<BTService> services = ss.getBluetoothDeviceServices(robot, new ProtocolId[]{ProtocolId.RFCOMM});
		if (services.size()!=1) {
			logger.severe("sluzba " + ProtocolId.RFCOMM.name() + " nebola najdena!");
			return null;
		}
		BTService bts = services.get(0);
		logger.fine("sluzba " + ProtocolId.RFCOMM.name() + " bola najdena");
		logger.fine("  nazov: " + bts.name);
		logger.fine("  url: " + bts.url);
		return bts.url;
	}

}
