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
}
