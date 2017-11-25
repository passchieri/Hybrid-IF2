package quadtree;

/**
 * Simple representation of a point in Mercator space.
 * 
 * @author Igor Passchier
 *
 */
public class MercatorPoint {
	final double x;
	final double y;

	public MercatorPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	public ImageMapPoint asImagePoint(int zoom) {
		return new ImageMapPoint(zoom, this);
	}

	public Tile getContainingTile(int zoom) {
		return this.asImagePoint(zoom).getContainingTile();
	}
}
