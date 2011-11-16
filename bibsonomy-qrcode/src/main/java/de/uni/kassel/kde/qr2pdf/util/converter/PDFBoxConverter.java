package de.uni.kassel.kde.qr2pdf.util.converter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class PDFBoxConverter extends Converter{
	
	public BufferedImage convertToImage(String fileName) throws Exception
	{
		InputStream in = new FileInputStream(new File(fileName));
		
		PDDocument doc = PDDocument.load(in);
						
		PDPage page = (PDPage)doc.getDocumentCatalog().getAllPages().get( 0 );
			
		BufferedImage convertToImage = page.convertToImage();	
		
		return convertToImage;
	}
	
}
