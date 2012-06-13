package org.bibsonomy.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.Callable;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDForm;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDResources;
import de.intarsys.pdf.platform.cwt.image.awt.ImageConverterAwt2Pdf;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
import de.intarsys.pdf.tools.kernel.PDFGeometryTools;
import de.intarsys.tools.locator.FileLocator;

/**
 * class to embed qr code into existing pdf document.
 * conversion can at most take 5 seconds to complete and the minimum
 * size of the qr code has to be 30 pixels so that it is readable by webcams.
 * conversion an manipulation is computed in this thread so that the main thread
 * can monitor it.
 * 
 * @author pbu
 * @version $Id$
 */
public class QRCodeEmbedder implements Callable<String> {
	
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
			 * check if file already exists
			 */
			if (new File(this.outFile).createNewFile()) {
				
				/*
				 * read input file and get first page
				 */
				final PDDocument createFromLocator = PDDocument.createFromLocator(new FileLocator(this.getInFile()));
				final PDPage pageAt = createFromLocator.getPageTree().getFirstPage();

				/*
				 * convert to image
				 */
				final BufferedImage renderPage = renderPage(pageAt, 1);
				

				if (renderPage != null) {

					/*
					 * find coordinates to put qr code to
					 */
					final Point freeSquare = SquareFinder.getFreeSquare(renderPage, SquareFinder.WHITE);

					final float x = freeSquare.getX();
					final float y = (float) pageAt.getCropBox().toNormalizedRectangle().getHeight() - freeSquare.getY();
					final int size = freeSquare.getSize();
					
					if (size > MINIMUM_SIZE) {		
						
						/*
						 * generate qr code
						 */
						final BufferedImage qrCode = QRCodeCreator.createQRCode(this.encodee, size);

						/*
						 * convert qr code image to internal pdf representation
						 */
						final ImageConverterAwt2Pdf converter2 = new ImageConverterAwt2Pdf(qrCode);
						final PDImage pdImage = converter2.getPDImage();

						/*
						 * get pdf page coordinate system offset and correct it
						 */
						final AffineTransform pageTx = new AffineTransform();
						PDFGeometryTools.adjustTransform(pageTx, pageAt);

						/*
						 * this is a workaround because the library is buggy.
						 * one has to create a new overlay of the first page and
						 * create a new contentstream on the existing page. this means
						 * everything but links etc. are deleted from the first page
						 * and added back via the overlay. finally the qr code is placed.
						 * this is necessary because else image positioning and scaling 
						 * is incorrect.
						 */
						final PDForm form = (PDForm) PDForm.META.createNew();
						final CSContent content = pageAt.getContentStream();

						if(pageAt.getResources() != null) {
							final COSObject cosResourcesCopy = pageAt.getResources().cosGetObject().copyDeep();
							final PDResources pdResourcesCopy = (PDResources) PDResources.META.createFromCos(cosResourcesCopy);
							form.setResources(pdResourcesCopy);
						}

						form.setBytes(content.toByteArray());
						form.setBoundingBox(pageAt.getCropBox().copy());

						/*
						 * open device to content stream
						 */
						final CSCreator creator = CSCreator.createNew(pageAt);

						creator.saveState();

						/*
						 * apply form
						 */
						creator.doXObject(null, form);

						final float newSize = size - (float) pageTx.getScaleX();
						final float newX = x - (float) pageTx.getTranslateX();
						final float newY = y - (float) pageTx.getTranslateY();

						/*
						 * apply qr code image
						 */
						creator.transform(newSize, 0, 0, newSize, newX, newY);
						creator.doXObject(null, pdImage);

						/*
						 * flush content
						 */
						creator.close();

					} else {	
						/*
						 * if minimum requirements are not met throw exception
						 */
						throw new Exception();
					}
				}

				/*
				 * save manipulated pdf to disk
				 */
				createFromLocator.save(new FileLocator(this.outFile));
				createFromLocator.close();
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
			throw new Exception();
		}

	}

	/**
	 * method to render pdf page to buffered image
	 * 
	 * @param page the page to render
	 * @param scale scale factor of image
	 * @return the converted image
	 * @throws CSException if page could not be converted
	 */
	private BufferedImage renderPage(final PDPage page, final int scale) throws CSException {
		
		/*
		 * get page dimensions
		 */
		final Rectangle2D rect = page.getCropBox().toNormalizedRectangle();
		
		BufferedImage image = null;
		IGraphicsContext graphics = null;
		
		try {
			
			/*
			 * create scaled buffered image with gray scale color space
			 * this way we can eliminate searching failures
			 */
			image = new BufferedImage( (int) (rect.getWidth() * scale),
									   (int) (rect.getHeight() * scale),
									   BufferedImage.TYPE_BYTE_GRAY);
			
			/*
			 * get graphics from scaled image
			 */
			final Graphics2D g2 = (Graphics2D) image.getGraphics();
			
			graphics = new CwtAwtGraphicsContext(g2);
			
			/*
			 * setup affine transform and background color
			 */
			final AffineTransform imgTransform = graphics.getTransform();
			imgTransform.scale(scale, -scale);
			imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
			graphics.setTransform(imgTransform);
			graphics.setBackgroundColor(Color.WHITE);
			graphics.fill(rect);
			
			/*
			 * get content stream of pdf page
			 */
			final CSContent content = page.getContentStream();
			
			if (content != null) {
				
				/*
				 * render pdf page
				 */
				final CSPlatformRenderer renderer = new CSPlatformRenderer(null, graphics);
				renderer.process(content, page.getResources());
			}   
			
			/*
			 * return rendered image
			 */
			return image;
			
		} finally {
			
			/*
			 * close resources
			 */
			if (graphics != null) {
				graphics.dispose();
			}
		}
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
		
}

