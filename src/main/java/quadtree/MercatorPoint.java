package quadtree;

/**
 * Simple representation of a point in Mercator space.
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 *
 */
public class MercatorPoint {
	final double x;
	final double y;
	ImageMapPoint imp = null;

	public MercatorPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	public ImageMapPoint asImagePoint(int zoom) {
		if (imp == null)
			imp = new ImageMapPoint(zoom, this);
		return imp;
	}
	
	public LatLon asLatLon() {
		return Mercator.mercatorToLatLon(this);
	}

	public Tile getContainingTile(int zoom) {
		return this.asImagePoint(zoom).getContainingTile();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
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
		MercatorPoint other = (MercatorPoint) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}
	
	
}
