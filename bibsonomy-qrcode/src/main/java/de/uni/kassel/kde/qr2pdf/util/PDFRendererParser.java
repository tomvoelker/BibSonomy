package de.uni.kassel.kde.qr2pdf.util;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PDFRendererParser {

	private long readTime = 0;
	private long convertTime = 0;
	private long findTime = 0;

	public PDFFile parse(String fileName, MyLogger logger) throws IOException {

		readTime = System.currentTimeMillis();
		
		File file = new File(fileName);
		java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r");
		
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		
		PDFFile pdffile = new PDFFile(buf);
		
		readTime = System.currentTimeMillis() - readTime;
		
		logger.getOut().println("PDF Read Time: " + readTime/1000.0 + "s");
		
		return pdffile;

	}
	
	public void manipulatePDF(PDFFile pdffile, int number, MyLogger logger)
	{
		
		convertTime = System.currentTimeMillis();
		// draw the first page to an image
		PDFPage page = pdffile.getPage(0);
		
		//get the width and height for the doc at the default zoom 
		Rectangle rect = new Rectangle(0,0,
		                (int)page.getBBox().getWidth(),
		                (int)page.getBBox().getHeight());

		//generate the image
		Image img = page.getImage(
		                rect.width, rect.height, //width & height
		                rect, // clip rect
		                null, // null for the ImageObserver
		                true, // fill background with white
		                true  // block until drawing is done
		                );
		
		BufferedImage bufImg = new BufferedImage(rect.width, rect.height, 8);
		
		bufImg.getGraphics().drawImage(img, 0, 0, null);
		
		convertTime = System.currentTimeMillis() - convertTime;
		
		logger.getOut().println("Convert Time: " + convertTime/1000.0 + "s");
		
		findTime = System.currentTimeMillis();
		
		Point bestPoint = SquareFinder.getFreeSquare(bufImg, SquareFinder.WHITE, 100, 200);
		
//		for(int i = 0; i< bestPoint.getSize(); i++)
//		{
//			for(int j = 0; j < bestPoint.getSize(); j++)
//			{
//				bufImg.setRGB(bestPoint.getX() + i, bestPoint.getY() + j - bestPoint.getSize() + 1, SquareFinder.RED);
//			}
//		}
		
		findTime = System.currentTimeMillis() - findTime;
		
		logger.getOut().println("Find Point Time: " + findTime/1000.0 + "s");
		
		logger.getOut().println("Total Time: " + readTime/1000.0 + "s + " + convertTime/1000.0 + "s + " + findTime/1000.0 + "s = "
				+ (readTime + convertTime + findTime)/1000.0 + "s" );
		
		if(null != bestPoint)
		{
			logger.getOut().println();
			logger.getOut().println(bestPoint.toString());
		}
		
	}
	
}
