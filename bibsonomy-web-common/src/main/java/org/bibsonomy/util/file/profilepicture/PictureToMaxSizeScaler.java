/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util.file.profilepicture;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * @author wla
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
