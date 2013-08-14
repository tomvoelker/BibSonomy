package org.bibsonomy.util.file.profilepicture;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * @author wla
 * @version $Id$
 */
public class PictureToMaxSizeScaler implements PictureScaler {

	private int sizeOfLargestSide;

	/**
	 * @param sizeOfLargestSide
	 */
	public PictureToMaxSizeScaler(int sizeOfLargestSide) {
		this.sizeOfLargestSide = sizeOfLargestSide;
	}

	@Override
	public RenderedImage scalePicture(Image image) throws IOException {
		final Image scaledImage;
		final int width = image.getWidth(null);
		final int height = image.getHeight(null);
		if (height > sizeOfLargestSide || width > sizeOfLargestSide) {
			/*
			 * convert picture to the standard size with fixed aspect ratio
			 */
			if (width > height) {
				/*
				 *  _________        ____
				 * |         | ---> |____|
				 * |_________|
				 * 
				 */
				scaledImage = image.getScaledInstance(sizeOfLargestSide, -1, Image.SCALE_SMOOTH);
			} else {
				/*
			 	*  ____        __
			 	* |    | ---> |  |
			 	* |    |      |__|
			 	* |    |
			 	* |____|
			 	*/
				scaledImage = image.getScaledInstance(-1, sizeOfLargestSide, Image.SCALE_SMOOTH);
			}
		} else {
			scaledImage = image;
		}

		/*
		 * create new BufferedImage with converted picture
		 */
		final BufferedImage outImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
		final Graphics g = outImage.getGraphics();
		g.drawImage(scaledImage, 0, 0, null);
		g.dispose();

		return outImage;
	}

}
