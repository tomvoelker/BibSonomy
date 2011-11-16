package de.uni.kassel.kde.qr2pdf.util.converter;

import java.awt.image.BufferedImage;

import org.icepdf.core.pobjects.Document;
import org.icepdf.core.pobjects.Page;
import org.icepdf.core.util.GraphicsRenderingHints;

public class IcePDFConverter extends Converter{

	public BufferedImage convertToImage(String fileName) throws Exception
	{
		Document doc = new Document();
		
		float scale = 1.0f;
        float rotation = 0f;

		doc.setFile(fileName);
		BufferedImage image = (BufferedImage) doc.getPageImage(0,
				GraphicsRenderingHints.SCREEN,
				Page.BOUNDARY_CROPBOX, 
				rotation, 
				scale);
			
		return image;
	}

}
