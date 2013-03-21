/**
 *
 *  BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util;

import java.awt.image.BufferedImage;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * class uses zxing qr code library to generate a qr code 
 * based on the encodee. a preferred size is of the resulting image
 * is also set.
 * 
 * @author pbu
 * @version $Id$
 */
public class QRCodeCreator {
	
	/**
	 * method takes encodee and preferred size and generates
	 * the corresponding qr code
	 * 
	 * @param encodee the URL to encode
	 * @param size preferred size of the resulting image
	 * @return an image representation of the generated qr code
	 * @throws WriterException if qr code could not be generated
	 */
	public static BufferedImage createQRCode(String encodee, int size) throws WriterException {
		
		/*
		 * get write and encode encodee to bit matrix
		 */
		QRCodeWriter writer = new QRCodeWriter();
		BitMatrix matrix = writer.encode(encodee, BarcodeFormat.QR_CODE, size, size);
		
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		/*
		 * convert bit matrix to buffered image
		 */
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, matrix.get(x, y) ? SquareFinder.BLACK : SquareFinder.WHITE);
			}
		}
		
		return image;
	}

}
