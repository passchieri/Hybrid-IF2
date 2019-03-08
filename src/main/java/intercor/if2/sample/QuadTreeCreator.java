package intercor.if2.sample;

import java.io.PrintStream;

import quadtree.LatLon;
import quadtree.QuadTreeConverter;
import quadtree.Tile;

/**
 * This is a simple example program that takes 3 arguments (latitude, longitude,
 * zoomlevel) and an optional fourth argument (seperator). It will output the
 * tile, and the corners of the tile.
 * 
 * @author Igor Passchier
 *
 */

public class QuadTreeCreator {

	public static void main(String[] args) {

		if (args.length < 3) {
			usage(System.err);
			System.exit(1);
		}
		double lat = Double.parseDouble(args[0]);
		double lon = Double.parseDouble(args[1]);
		int zoom = Integer.parseInt(args[2]);
		String quadTree = QuadTreeConverter.getQuadTree(zoom, lat, lon, (args.length > 3 ? args[3] : ""));
//		LatLon point = new LatLon(lat, lon);
		String line = String.format("[%8.8f, %8.8f] at %d: %s", lat, lon, zoom, quadTree);
		System.out.println(line);
		Tile tile = QuadTreeConverter.latLonToTile(zoom, lat, lon);
		LatLon ll = tile.getLowerLeftCorner();
		LatLon ur = tile.getUpperRightCorner();
		System.out.println("corners: " + ll + ", " + ur);

	}

	private static void usage(PrintStream stream) {
		stream.println("<program name> <latitude> <longitude> <zoomlevel>");
	}

}
