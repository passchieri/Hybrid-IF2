package quadtree;

/**
 * Utility class to perform calculations to and from Mercator projection.
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 * *
 */
public class Mercator {
	public static final double EARTH_RADIUS = 6378137; // earth radius in m
	public static final double MERCATOR_SIZE = 2 * Math.PI * EARTH_RADIUS; // width of the mercator map, in m

	public static final double X0 = -0.5 * MERCATOR_SIZE; // left side of the map, in m
	public static final double Y0 = X0; // top side of the map, in m

	public static MercatorPoint latLonToMercator(double lat, double lon) {
		return latLonToMercator(new LatLon(lat, lon));
	}

	public static MercatorPoint latLonToMercator(LatLon ll) {
		double mx = ll.lon * MERCATOR_SIZE / 360.;
		double my = Math.log(Math.tan((Math.PI / 4. + ll.lat / 180. * Math.PI / 2.))) * MERCATOR_SIZE / (2. * Math.PI);
		return new MercatorPoint(mx, my);
	}

	public static LatLon mercatorToLatLon(double x, double y) {
		return mercatorToLatLon(new MercatorPoint(x, y));
	}

	public static LatLon mercatorToLatLon(MercatorPoint m) {
		double lon = m.x / (MERCATOR_SIZE / 360.);
		double lat = m.y / MERCATOR_SIZE / 360. * 2. * Math.PI;
		lat = Math.exp(lat);
		lat = Math.atan(lat) - Math.PI / 4.;
		lat = lat * 180. / Math.PI * 2;
		return new LatLon(lat, lon);
	}
}
