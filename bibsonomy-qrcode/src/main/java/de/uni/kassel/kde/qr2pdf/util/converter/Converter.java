package de.uni.kassel.kde.qr2pdf.util.converter;

import java.awt.image.BufferedImage;

public abstract class Converter {

	public abstract BufferedImage convertToImage(String fileName) throws Exception;
	
}
