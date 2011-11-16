package de.uni.kassel.kde.qr2pdf.util.parser;

import java.awt.image.BufferedImage;

import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.platform.cwt.image.awt.ImageConverterAwt2Pdf;
import de.intarsys.tools.locator.FileLocator;
import de.uni.kassel.kde.qr2pdf.util.Point;
import de.uni.kassel.kde.qr2pdf.util.QRCodeCreator;
import de.uni.kassel.kde.qr2pdf.util.SquareFinder;
import de.uni.kassel.kde.qr2pdf.util.converter.Converter;
import de.uni.kassel.kde.qr2pdf.util.converter.GhostScriptConverter;


public class JPodParser extends Parser {

	@Override
	public void parse(String inFile, String outFile) throws Exception {
		// TODO Auto-generated method stub
		Converter converter = new GhostScriptConverter();

		BufferedImage img = converter.convertToImage(inFile);

		Point bestPoint = SquareFinder.getFreeSquare(img, SquareFinder.WHITE, 50, 100);

		int size = bestPoint.getSize();

		int posx = bestPoint.getX();
		
		BufferedImage qrCode = QRCodeCreator.createQRCode(size);
		
		PDDocument createFromLocator = PDDocument.createFromLocator(new FileLocator(inFile));
		
		PDPage pageAt = createFromLocator.getPageTree().getPageAt(0);
		
		int posy = (int)pageAt.getCropBox().getHeight() - bestPoint.getY() - 1;
		
		ImageConverterAwt2Pdf converter2 = new ImageConverterAwt2Pdf(qrCode);
		PDImage pdImage = converter2.getPDImage();
		
		//
		// open a device to the page content stream
		CSCreator creator = CSCreator.createFromProvider(pageAt);

		creator.saveState();
		creator.transform(size, 0, 0, size, posx, posy);
		creator.doXObject(null, pdImage);
		creator.restoreState();

		// don't forget to flush the content.
		creator.close();
		
		FileLocator locator = new FileLocator(outFile);
		createFromLocator.save(locator);
		
	}

}
