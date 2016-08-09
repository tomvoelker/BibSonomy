/**
 * BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import java.awt.image.BufferedImage;

/**
 * class to find free space in pdf page searches for pixels with
 * checkcolor as color value
 * 
 * @author pbu
 */
public class SquareFinder {

	/**
	 * constant for black color
	 */
	public static final int BLACK = 0xFF000000;
	
	/**
	 * constant for white color
	 */
	public static final int WHITE = 0xFFFFFFFF;

	/**
	 * method to get free square in pdf page
	 * 
	 * algorithm starts from top right
	 * algorithm searches for free space in the top half of the page
	 * algorithm takes the actual pixel and checks if the desired color is met
	 * if not 0 is assigned
	 * if color is met and it is an edge cell it assign 1
	 * for all other cells it checks the east, north and north-east cells for the value
	 * minimum of these values + 1 is assigned
	 * 
	 * |?|?|
	 * |x|?|
	 * 
	 * global maximum is stored
	 * 
	 * this way a square is formed every iteration
	 * 
	 * @param convertedPage the page to search in image form
	 * @param checkColor the color to search for
	 * @return a point where the qr code can be placed
	 */
	public static Point getFreeSquare(BufferedImage convertedPage, int checkColor) {

		Point maxPoint = new Point(0, 0, 0);

		/*
		 * search upper half
		 */
		int nrOfRows = convertedPage.getHeight() / 2;
		int nrOfCols = convertedPage.getWidth();

		/*
		 * array to store free space info
		 */
		int[][] counts = new int[nrOfRows][nrOfCols];

		for(int i = 0; i < nrOfRows; i++) {
			for(int j = nrOfCols-1; j >= nrOfCols / 10; j--) {	
				/*
				 * check for color
				 */
				if (convertedPage.getRGB(j, i) == checkColor) {					
					
					/*
					 * cell is not an edge cell
					 */
					if((i > 0) && (j < (nrOfCols-1))) {
						counts[i][j] = (1 + Math.min(counts[i][j+1],Math.min(counts[i-1][j], counts[i-1][j+1])));
					}

					/*
					 * cell is edge cell
					 */
					else {
						counts[i][j] = 1;
					}

					/*
					 * create point info
					 */
					Point p = new Point(j, i, counts[i][j]);

					/*
					 * compare to max and reassign max value if necessary
					 */
					if(p.compareTo(maxPoint) > 0) {
						maxPoint.setX(p.getX());
						maxPoint.setY(p.getY());
						maxPoint.setSize(p.getSize());
					}
				}
			}
		}

		/*
		 * return max value
		 */
		return maxPoint;
	}
}
