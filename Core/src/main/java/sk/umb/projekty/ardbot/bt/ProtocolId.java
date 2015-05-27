package sk.umb.projekty.ardbot.bt;

import javax.bluetooth.UUID;

public enum ProtocolId {

	// https://www.bluetooth.org/en-us/specification/assigned-numbers/service-discovery

	SDP(new UUID(0x0001), "Bluetooth Core Specification"),
	UDP(new UUID(0x0002), "[NO USE BY PROFILES]"),
	RFCOMM(new UUID(0x0003), "RFCOMM with TS 07.10"),
	TCP(new UUID(0x0004), "[NO USE BY PROFILES]"),
	TCS_BIN(new UUID(0x0005), "Telephony Control Specification / TCS Binary [DEPRECATED]"),
	TCS_AT(new UUID(0x0006), "[NO USE BY PROFILES]"),
	ATT(new UUID(0x0007), "Attribute Protocol"),
	OBEX(new UUID(0x0008), "IrDA Interoperability"),
	IP(new UUID(0x0009), "[NO USE BY PROFILES]"),
	FTP(new UUID(0x000A), "[NO USE BY PROFILES]"),
	HTTP(new UUID(0x000C), "[NO USE BY PROFILES]"),
	WSP(new UUID(0x000E), "[NO USE BY PROFILES]"),
	BNEP(new UUID(0x000F), "Bluetooth Network Encapsulation Protocol (BNEP)"),
	UPNP(new UUID(0x0010), "Extended Service Discovery Profile (ESDP) [DEPRECATED]"),
	HIDP(new UUID(0x0011), "Human Interface Device Profile (HID)"),
	HardcopyControlChannel(new UUID(0x0012), "Hardcopy Cable Replacement Profile (HCRP)"),
	HardcopyDataChannel(new UUID(0x0014), "See Hardcopy Cable Replacement Profile (HCRP)"),
	HardcopyNotification(new UUID(0x0016), "Hardcopy Cable Replacement Profile (HCRP)"),
	AVCTP(new UUID(0x0017), "Audio/Video Control Transport Protocol (AVCTP)"),
	AVDTP(new UUID(0x0019), "Audio/Video Distribution Transport Protocol (AVDTP)"),
	CMTP(new UUID(0x001B), "Common ISDN Access Profile (CIP) [DEPRECATED]"),
	MCAPControlChannel(new UUID(0x001E), "Multi-Channel Adaptation Protocol (MCAP)"),
	MCAPDataChannel(new UUID(0x001F), "Multi-Channel Adaptation Protocol (MCAP)"),
	L2CAP(new UUID(0x0100), "Bluetooth Core Specification");

	private final UUID uuid;

	private final String description;

	ProtocolId(UUID uuid, final String description) {
		this.uuid = uuid;
		this.description = description;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getDescription() {
		return this.description;
	}

}
