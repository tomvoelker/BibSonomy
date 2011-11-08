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

	public static PDFFile parse(String fileName) throws IOException {
		
		long currentTime = System.currentTimeMillis();
		
		File file = new File(fileName);
		java.io.RandomAccessFile raf = new java.io.RandomAccessFile(file, "r");
		
		FileChannel channel = raf.getChannel();
		ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
		
		PDFFile pdffile = new PDFFile(buf);
		
		currentTime = System.currentTimeMillis() - currentTime;
		
		//ImageIO.write(bufImg, "jpg", new File("/home/philipp/Desktop/test.jpg"));
		
		System.out.println("Time: " + currentTime/1000.0 + "s");
		
		return pdffile;

	}
	
	public static void manipulatePDF(PDFFile pdffile)
	{
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
				
				Point bestPoint = SquareFinder.getFreeSquare(bufImg, SquareFinder.WHITE, 100, 200);
				
				for(int i = 0; i< bestPoint.getSize(); i++)
				{
					for(int j = 0; j < bestPoint.getSize(); j++)
					{
						bufImg.setRGB(bestPoint.getX() + i, bestPoint.getY() + j - bestPoint.getSize() + 1, SquareFinder.RED);
					}
				}
				
				System.out.println(bestPoint.toString());
	}
	
}
