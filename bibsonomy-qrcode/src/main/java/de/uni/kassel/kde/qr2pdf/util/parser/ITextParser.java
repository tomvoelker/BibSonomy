package de.uni.kassel.kde.qr2pdf.util.parser;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import de.uni.kassel.kde.qr2pdf.util.Point;
import de.uni.kassel.kde.qr2pdf.util.QRCodeCreator;
import de.uni.kassel.kde.qr2pdf.util.SquareFinder;
import de.uni.kassel.kde.qr2pdf.util.converter.Converter;
import de.uni.kassel.kde.qr2pdf.util.converter.GhostScriptConverter;

public class ITextParser extends Parser{

	public void parse(String inFile, String outFile) throws Exception
	{
		InputStream in = new FileInputStream(new File(inFile));
		PdfReader reader = new PdfReader(in);
		
		Document doc = new Document(PageSize.A4);
		
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(outFile));
		
		doc.open();
		
		Converter converter = new GhostScriptConverter();

		BufferedImage img = converter.convertToImage(inFile);

		Point bestPoint = SquareFinder.getFreeSquare(img, SquareFinder.WHITE, 50, 100);

		int size = bestPoint.getSize();

		int posx = bestPoint.getX();
		int posy = (int)doc.getPageSize().getHeight() - bestPoint.getY() - 1;
		
		BufferedImage qrCode = QRCodeCreator.createQRCode(size);
		
		PdfImportedPage page = null;
		PdfContentByte canvas = writer.getDirectContent();
		
		//BufferedImage image = QRCodeCreator.createQRCode(25);
		
		for(int i = 1; i <= reader.getNumberOfPages(); i++)
		{
			if(i == 1)
			{
				page = writer.getImportedPage(reader, i);
				//System.out.println(img.toString());
				canvas.addTemplate(page, 0.0f, 0.0f);
				Image instance = Image.getInstance(writer,qrCode,1.0f);
				instance.setAbsolutePosition((float) posx, (float) posy);
				//instance.scaleAbsoluteHeight(size);
				//instance.scaleAbsoluteWidth(size);
				canvas.addImage(instance);
			}
			
			else
			{
				page = writer.getImportedPage(reader, i);
				canvas.addTemplate(page, 1f, 0, 0, 1, 0, 0);
			}
			
			doc.newPage();
		}
		
		doc.close();
	}
}
