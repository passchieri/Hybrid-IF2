package quadtree;

/**
 * Simple representation of a latitude/longitude value.
 * 
 * @author Igor Passchier
 *
 */
class LatLon {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(lat);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LatLon other = (LatLon) obj;
		if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
			return false;
		if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
			return false;
		return true;
	}

	final double lat;
	final double lon;

	public LatLon(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;
	}

	public MercatorPoint asMercatorPoint() {
		return Mercator.latLonToMercator(this);
	}

	@Override
	public String toString() {
		return "(" + lat + ", " + lon + ")";
	}

	public Tile getContainingTile(int zoom) {
		return asMercatorPoint().getContainingTile(zoom);
	}

}