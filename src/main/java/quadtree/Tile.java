package quadtree;

/**
 * Simple representation of a tile of a map. A tile has an x and y value, and
 * always relates to a zoom level. The number is equal to the tile numbering
 * implemented in globalmaptiles.py.
 * 
 * @author Igor Passchier
 * @copyright (c) Tass International BV
 * *
 */
class Tile {
	final int x;
	final int y;
	final int zoom;

	public Tile(int zoom, int x, int y) {
		this.zoom = zoom;
		this.x = x;
		this.y = y;
	}
	
	public Tile(String quadtree) {
		this(quadtree,"");
	}
	
	public Tile(String quadtree,String sep) {
		String qt=quadtree.replace(sep, "");
		if (!qt.matches("^[0123]*$")) throw new RuntimeException(quadtree+ " not a valid quadtree path");
		this.zoom=qt.length();
		int x=0;int y=0;
		for(char c : qt.toCharArray()) {
			x=x<<1;
			y=y<<1;
			switch(c) {
				case '0':
					y+=1;
					break;
				case '1':
					x+=1;
					y+=1;
					break;
				case '2':
					break;
				case '3':
					x+=1;
					break;
				default:
					throw new RuntimeException();
			}		
		}
		this.x=x;this.y=y;
	}

	public String getQuadTree(String seperator) {
		StringBuffer qt = new StringBuffer("");
		int ty = ((1 << zoom) - 1) - y;
		for (int i = zoom; i > 0; i--) {
			int digit = 0;
			int mask = 1 << (i - 1);
			if ((x & mask) != 0) {
				digit += 1;
			}
			if ((ty & mask) != 0) {
				digit += 2;
			}
			qt.append( seperator + digit);
		}
		return qt.toString();
	}

	public String getQuadTree() {
		return getQuadTree("");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + zoom;
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
		Tile other = (Tile) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (zoom != other.zoom)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "<" + x + ", " + y + " (" + zoom + ")>";
	}
	

}