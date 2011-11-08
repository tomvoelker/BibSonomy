package de.uni.kassel.kde.qr2pdf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.zxing.WriterException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

public class ITextParser {

	public static void manipulatePDF(String fileName) throws IOException, DocumentException, WriterException
	{//587/25
		
		long currentMillis = System.currentTimeMillis();
		
		InputStream in = new FileInputStream(new File(fileName));
		PdfReader reader = new PdfReader(in);
		
		Document doc = new Document(PageSize.A4);
		
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("/home/philipp/Dokumente/pdftest/out/itext.pdf"));
		
		doc.open();
		
		PdfImportedPage page = null;
		PdfContentByte canvas = writer.getDirectContent();
		
		//BufferedImage image = QRCodeCreator.createQRCode(25);
		
		for(int i = 1; i <= reader.getNumberOfPages(); i++)
		{
			if(i == 1)
			{
				page = writer.getImportedPage(reader, i);
				//System.out.println(img.toString());
				canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);
			}
			
			else
			{
				page = writer.getImportedPage(reader, i);
				canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);
			}
			
			doc.newPage();
		}
		
		doc.close();
		
		currentMillis = System.currentTimeMillis() - currentMillis;
		
		System.out.println("Zeit ITextPDF: " + currentMillis/1000.0 + "s");
		
	}
}
