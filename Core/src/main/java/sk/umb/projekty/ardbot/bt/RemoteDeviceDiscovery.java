package sk.umb.projekty.ardbot.bt;

import java.util.Vector;
import java.util.logging.Logger;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;

/**
 * Vyhladavanie BlueTooth zariadeni.
 * 
 * @author mvagac
 *
 */
public class RemoteDeviceDiscovery {

	private static Logger logger = Logger.getLogger(RemoteDeviceDiscovery.class.getPackage().getName());

	/**
	 * Sprav zoznam viditelnych BlueTooth zariadeni.
	 * 
	 * @return
	 * @throws BluetoothStateException
	 * @throws InterruptedException
	 */
	public Vector<RemoteDevice> getDevices() throws BluetoothStateException, InterruptedException {
		logger.fine("vyhladavam zariadenia");
		final Vector<RemoteDevice> ret = new Vector<RemoteDevice>();
		final Object inquiryCompletedEvent = new Object();

		DiscoveryListener listener = new DiscoveryListener() {
			public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
				// pridaj najdene zariadenie do vysledneho zoznamu
				ret.addElement(btDevice);
			}
			public void inquiryCompleted(int discType) {
				// inquiry skoncilo - odblokuj to cakanie
				synchronized (inquiryCompletedEvent) {
					inquiryCompletedEvent.notifyAll();
				}
			}
			public void serviceSearchCompleted(int transID, int respCode) {
			}
			public void servicesDiscovered(int transID,
					ServiceRecord[] servRecord) {
			}
		};

		synchronized (inquiryCompletedEvent) {
			// spusti hladanie zariadeni
			boolean started = LocalDevice.getLocalDevice().getDiscoveryAgent().startInquiry(DiscoveryAgent.GIAC, listener);
			if (started) {
				logger.fine("cakam na koniec inquiry...");
				// cakaj, kym inquiry neskonci
				inquiryCompletedEvent.wait();
			}
		}

		return ret;
	}

}
