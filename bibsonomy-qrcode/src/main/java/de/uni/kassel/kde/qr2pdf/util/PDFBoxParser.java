package de.uni.kassel.kde.qr2pdf.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import com.google.zxing.WriterException;

public class PDFBoxParser {

	long loadTime = 0;
	long convertTime = 0;
	long bestPointTime = 0;
	long qrcodetime = 0;
	long pdfmaniptime = 0;
	
	public PDDocument parse(String fileName) throws IOException									 
	{
		PDDocument doc = null;
		
		InputStream in = new FileInputStream(new File(fileName));
			
		loadTime = System.currentTimeMillis();
		
		doc = PDDocument.load(in);
			
		loadTime = System.currentTimeMillis() - loadTime;
			
		System.out.println("Load time: " + loadTime/1000.0 + "s");
		
		return doc;
	}
	
	public void manipulatePDF(PDDocument doc) throws IOException, WriterException, COSVisitorException
	{
		try
		{
			convertTime = System.currentTimeMillis();
			
			PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get( 0 );
			
			BufferedImage convertToImage = page.convertToImage();
			
			convertTime = System.currentTimeMillis() - convertTime;
			
			System.out.println("Convert time: " + convertTime/1000.0 + "s");
			
			bestPointTime = System.currentTimeMillis();
			
			Point bestPoint = SquareFinder.getFreeSquare(convertToImage, SquareFinder.WHITE, 50, 100);
			
			bestPointTime = System.currentTimeMillis() - bestPointTime;
			System.out.println(bestPoint.toString());
			System.out.println("Find best Point time: " + bestPointTime/1000.0 + "s");
			
			int size = bestPoint.getSize()/2;
			
			int posx = bestPoint.getX()/2;
			int posy = (int) (page.getMediaBox().getHeight() - bestPoint.getY()/2);
			
			qrcodetime = System.currentTimeMillis();
			
			BufferedImage image = QRCodeCreator.createQRCode(size);
		    
		    qrcodetime = System.currentTimeMillis() - qrcodetime;
			
		    System.out.println("QR-Code write time: " + qrcodetime/1000.0 + "s");
		    
		    pdfmaniptime = System.currentTimeMillis();
		    
		    PDXObjectImage ximage = new PDJpeg(doc, image);
		    
		    ximage.setHeight(size);
		    ximage.setWidth(size);
			
			PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
			
			contentStream.drawImage( ximage, (float)posx, (float)posy);
					 				 	
		 	contentStream.close();
		 	doc.save( "/home/philipp/Dokumente/pdftest/out/pdfbox.pdf" );
		 	
		 	pdfmaniptime = System.currentTimeMillis() - pdfmaniptime;
		
		 	System.out.println("PDF manipulate and save time: " + pdfmaniptime/1000.0 + "s");
		}
		
		finally
		{
			if(doc != null)
			{
				doc.close();
				System.out.println("Total Time: " + loadTime/1000.0 + "s + " + convertTime/1000.0 + "s + " + bestPointTime/1000.0 + "s + "
						+ qrcodetime/1000.0 + "s + " + pdfmaniptime/1000.0 + "s = "
						+ (loadTime + convertTime + bestPointTime + qrcodetime + pdfmaniptime)/1000.0 + "s" );
			}
		}
	}
	
}
