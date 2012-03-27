package org.bibsonomy.util;

/**
 * class to represent found free square point.
 * contains additional size of found free square.
 * 
 * @author pbu
 * @version $Id$
 */
public class Point implements Comparable<Point> {

	/**
	 * x coordinate of found free square
	 */
	private int x;
	
	/**
	 * y coordinate of found free square
	 */
	private int y;
	
	/**
	 * size of found free square
	 */
	private int size;

	/**
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param size size of found free square
	 */
	public Point(int x, int y, int size) {
		this.setX(x);
		this.setY(y);
		this.setSize(size);
	}

	/**
	 * 
	 * @return x coordinate of found free square
	 */
	public int getX() {
		return x;
	}

	/**
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * 
	 * @return y coordinate of found free square
	 */
	public int getY() {
		return y;
	}

	/**
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * 
	 * @return size of found free square
	 */
	public int getSize() {
		return size;
	}

	/**
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Point p) {
		
		if(this.getSize() == p.getSize()) {
			return 0;
		}
		
		else if(this.getSize() > p.getSize()) {
			return 1;
		}
		
		else {
			return -1;
		}		
	}
	
}

