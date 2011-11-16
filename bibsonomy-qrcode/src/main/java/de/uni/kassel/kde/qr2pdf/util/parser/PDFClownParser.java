package de.uni.kassel.kde.qr2pdf.util.parser;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.pdfclown.documents.Document;
import org.pdfclown.documents.contents.composition.PrimitiveComposer;
import org.pdfclown.documents.contents.entities.Image;
import org.pdfclown.documents.contents.xObjects.XObject;
import org.pdfclown.files.File;
import org.pdfclown.files.SerializationModeEnum;
import org.pdfclown.tools.PageStamper;

import de.uni.kassel.kde.qr2pdf.util.Point;
import de.uni.kassel.kde.qr2pdf.util.QRCodeCreator;
import de.uni.kassel.kde.qr2pdf.util.SquareFinder;
import de.uni.kassel.kde.qr2pdf.util.converter.Converter;
import de.uni.kassel.kde.qr2pdf.util.converter.GhostScriptConverter;

public class PDFClownParser extends Parser {

	@Override
	public void parse(String inFile, String outFile) throws Exception {
		// TODO Auto-generated method stub
		
		File file = new File(inFile);
		Document doc = file.getDocument();
		
		
		PageStamper stamper = new PageStamper(doc.getPages().get(0));
		
		Converter converter = new GhostScriptConverter();

		BufferedImage img = converter.convertToImage(inFile);

		Point bestPoint = SquareFinder.getFreeSquare(img, SquareFinder.WHITE, 50, 100);

		int size = bestPoint.getSize();

		int posx = bestPoint.getX();
		int posy = bestPoint.getY();
		
		BufferedImage qrCode = QRCodeCreator.createQRCode(size);
		
		java.io.File output = new java.io.File("src/main/resources/tmp.jpg");
		ImageIO.write(qrCode, "jpg", output);
		
		PrimitiveComposer foreground = stamper.getForeground();
		
		Image image2 = Image.get("src/main/resources/tmp.jpg");
		
//		if(null == image2)
//		{
//			System.out.println("blabla");
//		}
		
		//Not working
		XObject xObject = image2.toXObject(doc);
		foreground.showXObject(xObject, new java.awt.Point(posx, posy));
		
		doc.getFile().save(new java.io.File(outFile), SerializationModeEnum.Standard);
		
	}
}
