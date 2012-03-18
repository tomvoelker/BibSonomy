package org.bibsonomy.util;

/**
 * class to represent found free square.
 * contains additional size of found free square.
 * 
 * @author pbu
 *
 */
public class Point implements Comparable<Point>{

	private int x;
	private int y;
	private int size;

	public Point(int x, int y, int size)
	{
		this.x = x;
		this.y = y;
		this.size = size;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public String toString()
	{
		return "This point: " + this.getX() + "/" + this.getY() + "\tSize: " + this.getSize();
	}

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
