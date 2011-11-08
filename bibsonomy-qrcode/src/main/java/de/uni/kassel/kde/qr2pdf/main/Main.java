package de.uni.kassel.kde.qr2pdf.main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;

import de.uni.kassel.kde.qr2pdf.util.ITextParser;
import de.uni.kassel.kde.qr2pdf.util.MyLogger;
import de.uni.kassel.kde.qr2pdf.util.PDFBoxParser;

public class Main {

	

	
	public static void main(String[] args) throws IOException
	{
		
		MyLogger logger = null;
		

		logger = new MyLogger();
		
		Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);
		
		//PDFFile file = PDFRendererParser.parse("/home/philipp/Dokumente/pdftest/in/1.pdf");
		//PDFRendererParser.manipulatePDF(file);
		
		for(int i = 1; i <= 10; i++)
		{
			try
			{
				PDFBoxParser parser = new PDFBoxParser();
				PDDocument doc = parser.parse("/home/philipp/Dokumente/pdftest/in/" + String.valueOf(i) + ".pdf", logger);
				parser.manipulatePDF(doc, i, logger);
				
				ITextParser.manipulatePDF("/home/philipp/Dokumente/pdftest/in/" + String.valueOf(i) + ".pdf", i, logger);
			}	
			catch(Exception e)
			{
				logger.getOut().println("-----------------------------------------");
				e.printStackTrace(logger.getOut());
				
				logger.getOut().println("-----------------------------------------");
				logger.getOut().println();
				logger.getOut().println();
			}
		}
		
		logger.getOut().close();		
		
	}
}
