package quadtree;

/**
 * Basic utility to calculate Tile numbers and quadtree paths, based on a
 * latitude and longitude value. This class could be extended with additional
 * function to e.g. determine the optimum zoom level to cover a specific area.
 * 
 * This and other classes in this package are inspired on a python
 * implementation, globalmaptiles.py, developed by Klokan Petr Pridal, klokan at
 * klokan dot cz, http://www.klokan.cz/projects/gdal2tiles/
 * 
 * Please note, that quadtrees are based on tiles of a mercator projection. Not
 * every location on earth can be represented (roughly -85<lat<85 is allowed).
 * The behaviour is undefined outside this area.
 * 
 * @author Igor Passchier
 *
 */
public class QuadTreeConverter {

	/**
	 * Determine the Tile that contains a specific lat/lon location, at a specific
	 * zoom level
	 * 
	 * @param zoom
	 * @param lat
	 * @param lon
	 * @return
	 */
	public static Tile latLonToTile(int zoom, double lat, double lon) {
		LatLon ll = new LatLon(lat, lon);
		return ll.getContainingTile(zoom);
	}

	/**
	 * Determine the QuadTreePath of a specific lat/lon location, at a specific zoom
	 * level. The resulting string is exactly zoom bytes long.
	 * 
	 * @param zoom
	 *            Map zoom level
	 * @param lat
	 *            Latitude, in degrees
	 * @param lon
	 *            Longitude, in degrees
	 * @return
	 */
	public static String getQuadTree(int zoom, double lat, double lon) {
		LatLon ll = new LatLon(lat, lon);
		return ll.getContainingTile(zoom).getQuadTree();
	}

	/**
	 * Determine the QuadTreePath of a specific lat/lon location, at a specific zoom
	 * level. Every digit, also the first one, is preceded by a seperator. Length of
	 * the resulting string is exactly 2 * zoom bytes long.
	 * 
	 * @param zoom
	 *            Map zoom level
	 * @param lat
	 *            Latitude, in degrees
	 * @param lon
	 *            Longitude, in degrees
	 * @param seperator
	 *            inserted before every digit in the quadtree path.
	 * @return
	 */
	public static String getQuadTree(int zoom, double lat, double lon, String seperator) {
		LatLon ll = new LatLon(lat, lon);
		return ll.getContainingTile(zoom).getQuadTree(seperator);
	}

	/**
	 * Main method containing some simple tests.
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		String qt = QuadTreeConverter.getQuadTree(16, 51.46769695622339, 5.625);
		if (qt.equals("1202021311313133")) {
			System.out.println("Quattree correct: " + qt);
		} else {
			System.out.println("Quattree incorrect: " + qt + ", should be " + "1202021311313133");
		}
		qt = QuadTreeConverter.getQuadTree(16, 51.46769695622339, 5.625, "/");
		if (qt.equals("/1/2/0/2/0/2/1/3/1/1/3/1/3/1/3/3")) {
			System.out.println("Quattree correct: " + qt);
		} else {
			System.out.println("Quattree incorrect: " + qt + ", should be " + "/1/2/0/2/0/2/1/3/1/1/3/1/3/1/3/3");
		}
	}
}
