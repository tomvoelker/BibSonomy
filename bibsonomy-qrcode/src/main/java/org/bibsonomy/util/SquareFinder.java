package org.bibsonomy.util;

import java.awt.image.BufferedImage;

public class SquareFinder {

	public static final int BLACK = 0xFF000000;
	public static final int WHITE = 0xFFFFFFFF;
	
	public static Point getFreeSquare(BufferedImage convertedPage, int checkColor) 
	{
		
		Point maxPoint = new Point(0, 0, 0);
		
		int nrOfRows = convertedPage.getHeight();
		int nrOfCols = convertedPage.getWidth();
		
		int[][] counts = new int[nrOfRows][nrOfCols];
		
		for(int i = 0; i < nrOfRows; i++) {
			for(int j = nrOfCols-1; j >= 0; j--) {	
				if (convertedPage.getRGB(j, i) == checkColor) {					
					if((i > 0) && (j < (nrOfCols-1))) {
						counts[i][j] = (1 + Math.min(counts[i][j+1],Math.min(counts[i-1][j], counts[i-1][j+1])));
					}
					
					else {
						counts[i][j] = 1;
					}
					
					Point p = new Point(j, i, counts[i][j]);
					
					if(p.compareTo(maxPoint) > 0) {
						maxPoint.setX(p.getX());
						maxPoint.setY(p.getY());
						maxPoint.setSize(p.getSize());
					}
				}
			}
		}
		
		return maxPoint;
	}
	
}
