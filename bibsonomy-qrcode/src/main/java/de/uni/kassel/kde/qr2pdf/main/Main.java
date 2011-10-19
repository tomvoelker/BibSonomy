package de.uni.kassel.kde.qr2pdf.main;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import de.uni.kassel.kde.qr2pdf.util.Point;

public class Main {

	private static final int BLACK = 0xFF000000;
	private static final int WHITE = 0xFFFFFFFF;

	
	public static void main(String[] args) throws IOException, COSVisitorException, WriterException
	{
		Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);

		PDDocument doc = null;
		long loadAndConvertTime = 0;
		long bestPointTime = 0;
		long qrcodetime = 0;
		long pdfmaniptime = 0;
		
		
		try
		{			
			
			InputStream in = new BufferedInputStream(new FileInputStream(new File("/home/philipp/Downloads/adobe.pdf")), 1024);
			
			loadAndConvertTime = System.currentTimeMillis();
			
			doc = PDDocument.load(in, true);
			
			PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get( 0 );
			
			BufferedImage convertToImage = page.convertToImage();
			
			loadAndConvertTime = System.currentTimeMillis() - loadAndConvertTime;
			
			System.out.println("Load and convert time: " + loadAndConvertTime/1000.0 + "s");
			
			bestPointTime = System.currentTimeMillis();
			
			Point bestPoint = getFreeSquares(convertToImage, WHITE, 200, 500);
			
			bestPointTime = System.currentTimeMillis() - bestPointTime;
			
			System.out.println("Find best Point time: " + bestPointTime/1000.0 + "s");
			
			int size = bestPoint.getSize()/2;
			
			int posx = bestPoint.getX()/2;
			int posy = (int) (page.getMediaBox().getHeight() - bestPoint.getY()/2);
			
			qrcodetime = System.currentTimeMillis();
			
			QRCodeWriter writer = new QRCodeWriter();
			BitMatrix matrix = writer.encode("guck mal was ich kann", BarcodeFormat.QR_CODE, size, size);
			
			int width = matrix.getWidth();
		    int height = matrix.getHeight();
		    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		    for (int x = 0; x < width; x++) {
		      for (int y = 0; y < height; y++) {
		        image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
		      }
		    }
		    
		    qrcodetime = System.currentTimeMillis() - qrcodetime;
			
		    System.out.println("QR-Code write time: " + qrcodetime/1000.0 + "s");
		    
		    pdfmaniptime = System.currentTimeMillis();
		    
		    PDXObjectImage ximage = new PDJpeg(doc, image);
		    
		    ximage.setHeight(size);
		    ximage.setWidth(size);
			
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
			
			contentStream.drawImage( ximage, (float)posx, (float)posy);
					 				 	
		 	contentStream.close();
		 	doc.save( "/home/philipp/Desktop/test.pdf" );
		 	
		 	pdfmaniptime = System.currentTimeMillis() - pdfmaniptime;

		 	System.out.println("PDF manipulate and save time: " + pdfmaniptime/1000.0 + "s");
		}
		finally
		{
			if(doc != null)
			{
				doc.close();
				System.out.println("Total Time: " + loadAndConvertTime/1000.0 + "s + " + bestPointTime/1000.0 + "s + "
						+ qrcodetime/1000.0 + "s + " + pdfmaniptime/1000.0 + "s = "
						+ (loadAndConvertTime + bestPointTime + qrcodetime + pdfmaniptime)/1000.0 + "s" );
			}
		}
	}


	private static Point getFreeSquares(
			BufferedImage convertToImage, int checkColor, int thresholdMin, int thresholdMax) {
		
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
					
					if(counts[i][j] > thresholdMin && counts[i][j] < thresholdMax)
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
