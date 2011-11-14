package de.uni.kassel.kde.qr2pdf.main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.sun.pdfview.PDFFile;

import de.uni.kassel.kde.qr2pdf.util.ITextParser;
import de.uni.kassel.kde.qr2pdf.util.MyLogger;
import de.uni.kassel.kde.qr2pdf.util.PDFBoxParser;
import de.uni.kassel.kde.qr2pdf.util.PDFRendererParser;

public class Main {

	

	
	public static void main(String[] args) throws IOException
	{
		
		MyLogger logger = null;
		

		logger = new MyLogger();
		
		Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);
		
		for(int i = 1; i <= 10; i++)
		{
			try
			{
				logger.getOut().println("----------------PDFBox----------------");
				logger.getOut().println();
				PDFBoxParser parser = new PDFBoxParser();
				PDDocument doc = parser.parse("src/main/resources/in/" + String.valueOf(i) + ".pdf", logger);
				parser.manipulatePDF(doc, i, logger);
				logger.getOut().println();
				logger.getOut().println("----------------PDFBox----------------");
				logger.getOut().println();
				logger.getOut().println();
			}	
			catch(Exception e)
			{
				logger.getOut().println("-----------------------------------------");
				e.printStackTrace(logger.getOut());
				logger.getOut().println("-----------------------------------------");
				logger.getOut().println();
				logger.getOut().println("----------------PDFBox----------------");
				logger.getOut().println();
				logger.getOut().println();
			}
			
			try
			{
				logger.getOut().println("----------------IText----------------");
				logger.getOut().println();
				ITextParser parser2 = new ITextParser();
				parser2.manipulatePDF("src/main/resources/in/" + String.valueOf(i) + ".pdf", i, logger);
				logger.getOut().println();
				logger.getOut().println("----------------IText----------------");
				logger.getOut().println();
				logger.getOut().println();
			}	
			catch(Exception e)
			{
				logger.getOut().println("-----------------------------------------");
				e.printStackTrace(logger.getOut());
				logger.getOut().println("-----------------------------------------");
				logger.getOut().println();
				logger.getOut().println("----------------IText----------------");
				logger.getOut().println();
				logger.getOut().println();
			}
			
			try
			{
				logger.getOut().println("----------------PDFRenderer----------------");
				logger.getOut().println();
				PDFRendererParser parser3 = new PDFRendererParser();
				PDFFile file = parser3.parse("src/main/resources/in/" + String.valueOf(i) + ".pdf", logger);
				parser3.manipulatePDF(file, i, logger);
				logger.getOut().println();
				logger.getOut().println("----------------PDFRenderer----------------");
				logger.getOut().println();
				logger.getOut().println();
			}	
			catch(Exception e)
			{
				logger.getOut().println("-----------------------------------------");
				e.printStackTrace(logger.getOut());
				logger.getOut().println("-----------------------------------------");
				logger.getOut().println();
				logger.getOut().println("----------------PDFRenderer----------------");
				logger.getOut().println();
				logger.getOut().println();
			}
		}
		
		logger.getOut().close();		
		
	}
}
