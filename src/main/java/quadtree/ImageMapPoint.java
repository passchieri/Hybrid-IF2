package quadtree;

/**
 * Representation of a point in an image of a map. It should be possible to
 * remove the whole conversion to image points, as it has no effect on the
 * conversion bewteen lat/lon and tiles.
 * 
 * @author Igor
 *
 */
class ImageMapPoint {
	final double x;
	final double y;
	final int zoom;

	private final int tileSize = 256;

	public ImageMapPoint(int zoom, double x, double y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}

	public ImageMapPoint(int zoom, MercatorPoint m) {
		this.zoom = zoom;
		double res = resolution();
		x = (m.x - Mercator.X0) / res;
		y = (m.y - Mercator.Y0) / res;
	}

	private double resolution() {
		int NoOfPix = tileSize * (1 << zoom);
		return Mercator.MERCATOR_SIZE / NoOfPix;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + " (" + zoom + ")]";
	}

	public Tile getContainingTile() {
		int tx = (int) (Math.ceil(x / (1. * tileSize)) - 1);
		int ty = (int) (Math.ceil(y / (1. * tileSize)) - 1);
		return new Tile(zoom, tx, ty);
	}

	public MercatorPoint asMercatorPoint() {
		double mx = x * resolution() + Mercator.X0;
		double my = y * resolution() + Mercator.Y0;
		return new MercatorPoint(mx, my);
	}
}