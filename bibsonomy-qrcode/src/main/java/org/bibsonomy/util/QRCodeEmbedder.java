/**
 * BibSonomy-QRCode - Embbeding QR Codes in PDFs in Bibsonomy
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/**
 * class to embed qr code into existing pdf document.
 * conversion can at most take 5 seconds to complete and the minimum
 * size of the qr code has to be 30 pixels so that it is readable by webcams.
 * conversion an manipulation is computed in this thread so that the main thread
 * can monitor it.
 * 
 * @author pbu
 */
public class QRCodeEmbedder implements Callable<String> {
	
	/**
	 * scale factor for the picture to render from the first pdf page
	 */
	private static final int SCALE_FACTOR = 1;

	/**
	 * maximum wait time -> here 5 seconds
	 */
	public static final int WAIT_TIME = 5000;
	
	/**
	 * qr code has to be at least 30x30 pixels in size
	 */
	public static final int MINIMUM_SIZE = 30;

	/**
	 * the input path of the pdf document
	 */
	private String inFile;
	
	/**
	 * the output path of the document output = input + .qr
	 */
	private String outFile;
	
	/**
	 * the URL to encode
	 */
	private String encodee;

	/**
	 * the x coordinate of the QR code
	 */
	private float x;

	/**
	 * the y coordinate of the QR code
	 */
	private float y;

	/**
	 * the size of the QR code
	 */
	private int size;

	/**
	 * 
	 * @param inFile input path
	 * @param encodee URL to encode
	 */
	public QRCodeEmbedder(final String inFile, final String encodee) {
		this.setInFile(inFile);
		this.setOutFile(inFile.concat(".qr"));
		this.setEncodee(encodee);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public String call() throws Exception {
		try {
			/*
			 * avoid recalculation when the converted file already exists
			 */
			if (new File(this.outFile).exists()) {
				return this.outFile;
			}

			try (final PDDocument document = PDDocument.load(new File(this.getInFile()))) {
				if (document.getNumberOfPages() == 0) {
					throw new Exception("PDF has no pages");
				}

				final int firstPageIndex = 0;
				final PDPage firstPage = document.getPage(firstPageIndex);
				final BufferedImage renderedPage = renderPage(document, firstPageIndex, SCALE_FACTOR);
				final Point freeSquare = SquareFinder.getFreeSquare(renderedPage, SquareFinder.WHITE);

				final PDRectangle cropBox = firstPage.getCropBox();
				final float imageToPdfScaleX = renderedPage.getWidth() / cropBox.getWidth();
				final float imageToPdfScaleY = renderedPage.getHeight() / cropBox.getHeight();

				this.setX(cropBox.getLowerLeftX() + (freeSquare.getX() / imageToPdfScaleX));
				this.setY(cropBox.getLowerLeftY() + cropBox.getHeight() - (freeSquare.getY() / imageToPdfScaleY));
				this.setSize(Math.round(freeSquare.getSize() / imageToPdfScaleX));

				if (this.getSize() <= MINIMUM_SIZE) {
					throw new Exception("Could not find enough white space for QR code");
				}

				final BufferedImage qrCode = QRCodeCreator.createQRCode(this.encodee, this.getSize());
				final PDImageXObject pdImage = LosslessFactory.createFromImage(document, qrCode);

				try (final PDPageContentStream contentStream =
						new PDPageContentStream(document, firstPage, AppendMode.APPEND, true, true)) {
					contentStream.drawImage(pdImage, this.getX(), this.getY(), this.getSize(), this.getSize());
				}

				document.save(this.outFile);
			}

			/*
			 * return path to manipulated file
			 */
			return this.outFile;

		} catch (final Throwable e) {
			/*
			 * if we get here something went wrong during conversion/manipulation.
			 * therefore output file can already exist an be corrupt so we have to delete it.
			 */
			new File(this.getOutFile()).delete();
			throw new Exception(e);
		}

	}

	/**
	 * method to render pdf page to buffered image
	 * 
	 * @param document the owning document
	 * @param pageIndex index of the page to render
	 * @param scale scale factor of image
	 * @return the converted image
	 * @throws Exception if page could not be converted
	 */
	private BufferedImage renderPage(final PDDocument document, final int pageIndex, final int scale) throws Exception {
		final PDFRenderer renderer = new PDFRenderer(document);
		return renderer.renderImage(pageIndex, scale, ImageType.GRAY);
	}

	/**
	 * @return the inFile
	 */
	public String getInFile() {
		return inFile;
	}

	/**
	 * @param inFile the inFile to set
	 */
	public void setInFile(final String inFile) {
		this.inFile = inFile;
	}

	/**
	 * @return the outFile
	 */
	public String getOutFile() {
		return outFile;
	}

	/**
	 * @param outFile the outFile to set
	 */
	public void setOutFile(final String outFile) {
		this.outFile = outFile;
	}

	/**
	 * @return the encodee
	 */
	public String getEncodee() {
		return encodee;
	}

	/**
	 * @param encodee the encodee to set
	 */
	public void setEncodee(final String encodee) {
		this.encodee = encodee;
	}

	/**
	 * @return the x coordinate of the QR code
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x the x coordinate to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y coordinate of the QR code
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y the y coordinate to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the size of the QR code
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
}
