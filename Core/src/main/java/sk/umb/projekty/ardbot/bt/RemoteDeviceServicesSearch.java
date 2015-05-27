package sk.umb.projekty.ardbot.bt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;

/**
 * Vyhladavanie BlueTooth sluzieb pre dane zariadenie.
 * 
 * @author mvagac
 *
 */
public class RemoteDeviceServicesSearch {

	private static Logger logger = Logger.getLogger(RemoteDeviceServicesSearch.class.getPackage().getName());

	private int URL_ATTRIBUTE = 0x0100;

	/**
	 * Na danom zariadeni hladaj dane sluzby.
	 * 
	 * @param btDevice
	 * @param services
	 * @return
	 * @throws BluetoothStateException
	 * @throws InterruptedException
	 */
	public List<BTService> getBluetoothDeviceServices(RemoteDevice btDevice, ProtocolId[] services) throws BluetoothStateException, InterruptedException {
		logger.fine("vyhladavam sluzby " + Arrays.toString(services));
		final List<BTService> ret = new ArrayList<BTService>();
		final Object serviceSearchCompletedEvent = new Object();

		DiscoveryListener listener = new DiscoveryListener() {
			public void deviceDiscovered(RemoteDevice btDevice,
					DeviceClass cod) {
			}
			public void inquiryCompleted(int discType) {
			}
			public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
				for (int i = 0; i < servRecord.length; i++) {
//					RemoteDevice rd = servRecord[i].getHostDevice();
					// ziskaj url BlueTooth zariadenia
					String url = servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false);
					if (url == null) {
						logger.warning("??? url nezname");
						continue;
					}
					BTService bs = new BTService();
					bs.url = url;
					// nacitaj atributy sluzby
//for (int attrId : servRecord[i].getAttributeIDs()) {
//	System.out.println(attrId + ": " + servRecord[i].getAttributeValue(attrId));
//}
					DataElement serviceName = servRecord[i].getAttributeValue(URL_ATTRIBUTE);
					if (serviceName != null) {
						bs.name = String.valueOf(serviceName.getValue());
					} else {
						bs.name = "Uknown service";
					}
					ret.add(bs);
				}
			}
			public void serviceSearchCompleted(int transID, int respCode) {
				// hladanie skoncilo - odblokuj to cakanie
				synchronized (serviceSearchCompletedEvent) {
					serviceSearchCompletedEvent.notifyAll();
				}
			}
		};

		synchronized (serviceSearchCompletedEvent) {
			// spusti hladanie sluzieb
			UUID[] searchUuidSet = new UUID[services.length];
			for (int i = 0; i < services.length; i++) {
				searchUuidSet[i] = services[i].getUuid();
			}
			int[] attrIDs = new int[] { URL_ATTRIBUTE };
			LocalDevice.getLocalDevice().getDiscoveryAgent().searchServices(attrIDs, searchUuidSet, btDevice, listener);
			// cakaj, kym neskonci hladanie
			serviceSearchCompletedEvent.wait();
		}

		return ret;
	}

	public class BTService {
		public String name;
		public String url;
	}

}
