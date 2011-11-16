package de.uni.kassel.kde.qr2pdf.util.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import de.uni.kassel.kde.qr2pdf.util.Point;
import de.uni.kassel.kde.qr2pdf.util.QRCodeCreator;
import de.uni.kassel.kde.qr2pdf.util.SquareFinder;
import de.uni.kassel.kde.qr2pdf.util.converter.Converter;
import de.uni.kassel.kde.qr2pdf.util.converter.GhostScriptConverter;

public class PDFBoxParser extends Parser{
	
	public void parse(String inFile, String outFile) throws Exception									 
	{
		Converter converter = new GhostScriptConverter();
		InputStream in = new FileInputStream(new File(inFile));
		PDDocument doc = PDDocument.load(in);
		PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get( 0 );
		
		BufferedImage convertToImage = converter.convertToImage(inFile);
				
		Point bestPoint = SquareFinder.getFreeSquare(convertToImage, SquareFinder.WHITE, 50, 100);
		
		int size = bestPoint.getSize();
		
		int posx = bestPoint.getX();
		int posy = (int)page.getMediaBox().getHeight() - bestPoint.getY() - 1;
		
		BufferedImage image = QRCodeCreator.createQRCode(size);
	    	    
	    PDXObjectImage ximage = new PDJpeg(doc, image);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true);
		
		contentStream.drawImage( ximage, (float)posx, (float)posy);
		//contentStream.drawXObject(ximage, (float)posx, (float)posy, size, size);
				 				 	
	 	contentStream.close();
	 	
	 	doc.save(outFile);
	 		 	
	 	doc.close();
	}
	
}
