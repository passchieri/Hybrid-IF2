package intercor.if2.client;

import quadtree.QuadTreeConverter;

/**
 * A simeple data structure to represent the metadata of a message
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 *
 */
public class Datum {
	double lat; // latitude of the data
	double lon; // longitude of the data
	int zoom; // zoom level at which the data should be published
	String messageType; // message type, In this example always DENM
	String messageVersion; // message version. The current DENM version is 1.2.1, encoded as 1_2_1
	String provider; // identifier of the organisation that publishes the message
	String subtype; // subtype of the message. In case of DENMs, we use the causecode

	public Datum(double lat, double lon, int zoom, String messageType, String messageVersion, String provider,
			String subtype) {
		super();
		this.lat = lat;
		this.lon = lon;
		this.zoom = zoom;
		this.messageType = messageType;
		this.messageVersion = messageVersion;
		this.provider = provider;
		this.subtype = subtype;
	}

	public String getRoutingKey() {
		// none of the strings in the routing key should contain ".". This should be
		// checked
		// No extra "." after the subtype, as the quadtree starts with a "."
		return messageType + "." + messageVersion + "." + provider + "." + subtype
				+ QuadTreeConverter.getQuadTree(zoom, lat, lon, ".");
	}

	@Override
	public String toString() {
		return "Datum [lat=" + lat + ", lon=" + lon + ", zoom=" + zoom + ", key=" + getRoutingKey() + "]";
	}
}