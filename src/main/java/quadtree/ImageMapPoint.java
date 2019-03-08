package quadtree;

/**
 * Representation of a point in an image of a map. It should be possible to
 * remove the whole conversion to image points, as it has no effect on the
 * conversion bewteen lat/lon and tiles.
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV *
 */
class ImageMapPoint {
	final double x;
	final double y;
	final int zoom;

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

	/*
	 * Distance on the mercator map per pixel.
	 */
	private double resolution() {
		int NoOfPix = Mercator.TILE_SIZE * (1 << zoom);
		return Mercator.MERCATOR_SIZE / NoOfPix;
	}

	@Override
	public String toString() {
		return String.format("[%8.8f, %8.8f (%d, args)]",x,y,zoom);
	}

	public Tile getContainingTile() {
		int tx = (int) (Math.ceil(x / (1. * Mercator.TILE_SIZE)) - 1);
		int ty = (int) (Math.ceil(y / (1. * Mercator.TILE_SIZE)) - 1);
		return new Tile(zoom, tx, ty);
	}

	public MercatorPoint asMercatorPoint() {
		double mx = x * resolution() + Mercator.X0;
		double my = y * resolution() + Mercator.Y0;
		return new MercatorPoint(mx, my);
	}
}