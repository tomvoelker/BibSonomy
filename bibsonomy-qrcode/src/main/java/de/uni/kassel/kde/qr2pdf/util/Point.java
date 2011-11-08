package de.uni.kassel.kde.qr2pdf.util;

public class Point {

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
	
}
