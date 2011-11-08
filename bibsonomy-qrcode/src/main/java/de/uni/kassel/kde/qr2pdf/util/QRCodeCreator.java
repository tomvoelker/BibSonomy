package de.uni.kassel.kde.qr2pdf.util;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeCreator {
	
	public static BufferedImage createQRCode(int size) throws WriterException {
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix matrix = writer.encode("guck mal was ich kann", BarcodeFormat.QR_CODE, size, size);
		
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
		  for (int y = 0; y < height; y++) {
		    image.setRGB(x, y, matrix.get(x, y) ? SquareFinder.BLACK : SquareFinder.WHITE);
		  }
		}
		return image;
	}

}
