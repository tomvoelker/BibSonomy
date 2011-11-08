package de.uni.kassel.kde.qr2pdf.main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.pdfbox.exceptions.COSVisitorException;
import org.apache.pdfbox.pdmodel.PDDocument;

import com.google.zxing.WriterException;
import com.itextpdf.text.DocumentException;

import de.uni.kassel.kde.qr2pdf.util.ITextParser;
import de.uni.kassel.kde.qr2pdf.util.PDFBoxParser;

public class Main {

	

	
	public static void main(String[] args) throws IOException, COSVisitorException, WriterException, DocumentException
	{
		Logger.getLogger("org.apache.pdfbox").setLevel(Level.OFF);

		//PDFFile file = PDFRendererParser.parse("/home/philipp/Dokumente/pdftest/in/1.pdf");
		//PDFRendererParser.manipulatePDF(file);
		
		PDFBoxParser parser = new PDFBoxParser();
		PDDocument doc = parser.parse("/home/philipp/Dokumente/pdftest/in/2.pdf");
		parser.manipulatePDF(doc);
		
		ITextParser.manipulatePDF("/home/philipp/Dokumente/pdftest/in/2.pdf");
	}
}
