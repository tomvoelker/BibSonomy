/**
 *
 *  BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util;

/**
 * class to represent found free square point.
 * contains additional size of found free square.
 * 
 * @author pbu
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

