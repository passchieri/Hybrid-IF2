package quadtree;
/**
 * @author Igor Passchier
 * @copyright (c) Tass International BV 
 *
 */

import static org.junit.Assert.*;

import org.junit.Test;

public class QuadTreeTest {

	@Test
	public void testLatLonToMercatorPoint() {
		LatLon ll = new LatLon(51.46769695622339, 5.625);
		MercatorPoint mp = ll.asMercatorPoint();
		assertEquals(626172.1357121639, mp.x, 1E-3);
		assertEquals(6704444.624949383, mp.y, 1E-3);

		LatLon ll2 = mp.asLatLon();
		assertEquals(ll, ll2);
	}

	@Test
	public void testMercatorPointToImagePoint() {
		LatLon ll = new LatLon(51.46769695622339, 5.625);
		MercatorPoint mp = ll.asMercatorPoint();
		ImageMapPoint imp = mp.asImagePoint(16);
		assertEquals(imp.x, 8650752.0, 1.);
		assertEquals(imp.y, 1.1195392000000002E7, 1.);
		MercatorPoint mp2 = imp.asMercatorPoint();
		assertEquals(mp.x, mp2.x, 1E-3);
		assertEquals(mp.y, mp2.y, 1E-3);
	}

	@Test
	public void testImagePointToTile() {
		ImageMapPoint mp = (new LatLon(51.46769695622339, 5.625)).asMercatorPoint().asImagePoint(16);
		Tile t = mp.getContainingTile();
		Tile expect = new Tile(16, 33791, 43732);
		assertEquals(expect, t);
	}

	@Test
	public void testCorners() {
		Tile t = new LatLon(51.46769695622339, 5.625).getContainingTile(16);
		t = new LatLon(55, 25).getContainingTile(16);
		LatLon ulc = t.getUpperLeftCorner();
		assertEquals(ulc.lat, 54.99967515853579, 1E-6);
		assertEquals(ulc.lon, 24.99938964843749, 1E-6);

		ulc = t.getUpperRightCorner();
		assertEquals(ulc.lat, 54.99967515853579, 1E-6);
		assertEquals(ulc.lon, 25.004882812499986, 1E-6);

		ulc = t.getLowerLeftCorner();
		assertEquals(ulc.lat, 55.0028258097932, 1E-6);
		assertEquals(ulc.lon, 24.99938964843749, 1E-6);

		ulc = t.getLowerRightCorner();
		assertEquals(ulc.lat, 55.0028258097932, 1E-6);
		assertEquals(ulc.lon, 25.004882812499986, 1E-6);
	}

	@Test
	public void testTileToQuadTree() {
		Tile t = new Tile(16, 33791, 43732);
		String qt = t.getQuadTree("/");
		String expect = "/1/2/0/2/0/2/1/3/1/1/3/1/3/1/3/3";
		assertEquals(expect, qt);
		Tile t2 = new Tile(qt, "/");
		assertEquals(t, t2);
		qt = t.getQuadTree("");
		expect = "1202021311313133";
		assertEquals(expect, qt);
		t2 = new Tile(qt);
		assertEquals(t, t2);

	}
}
