package de.uni.kassel.kde.qr2pdf.util;

import java.awt.image.BufferedImage;

public class SquareFinder {

	public static final int BLACK = 0xFF000000;
	public static final int WHITE = 0xFFFFFFFF;
	public static final int RED = 0xFFFF0000;
	
	public static Point getFreeSquare(
			BufferedImage convertToImage, int checkColor, int thresholdMin, int thresholdMax) 
	{
		
		int nrOfRows = convertToImage.getHeight();
		int nrOfCols = convertToImage.getWidth();
		
		int[][] counts = new int[nrOfRows][nrOfCols];
		
		for(int i = 0; i < nrOfRows; i++)
		{
			for(int j = nrOfCols-1; j >= 0; j--)
			{	
				if (convertToImage.getRGB(j, i) == checkColor)
				{
					Point p = new Point(0, 0, 0);
					
					if((i > 0) && (j < (nrOfCols-1)))
					{						
						counts[i][j] = (1 + Math.min(counts[i][j+1],Math.min(counts[i-1][j], counts[i-1][j+1])));
					}
					
					else
					{
						counts[i][j] = 1;
					}
					
					if(counts[i][j] >= thresholdMin && counts[i][j] <= thresholdMax)
					{
						p.setX(j);
						p.setY(i);
						p.setSize(counts[i][j]);
						
						return p;
					}
				}
			}
		}
		
		return null;
	}
	
}
