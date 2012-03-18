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
import de.intarsys.pdf.content.CSError;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.CSWarning;
import de.intarsys.pdf.content.ICSExceptionHandler;
import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.platform.cwt.image.awt.ImageConverterAwt2Pdf;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
import de.intarsys.tools.locator.FileLocator;

public class QRCodeEmbedder implements Callable<String>{
	
	public static final int WAIT_TIME = 5000;
	
	private String inFile;
	private String outFile;
	private String encodee;

	public QRCodeEmbedder(String inFile, String encodee)
	{
		this.inFile = inFile;
		this.outFile = inFile.concat(".qr");
		this.encodee = encodee;
		
	}
	
	@Override
	public String call() throws Exception
	{
		try
		{
			if(!(new File(this.outFile).exists())) {
				PDDocument createFromLocator = PDDocument.createFromLocator(new FileLocator(this.inFile));
				
				PDPage pageAt = createFromLocator.getPageTree().getPageAt(0);
				
				BufferedImage renderPage = renderPage(pageAt, 1);
				
				if(renderPage != null)
				{
					Point freeSquare = SquareFinder.getFreeSquare(renderPage, SquareFinder.WHITE);
							
					int x = freeSquare.getX();
					int y = (int)pageAt.getCropBox().getHeight() - freeSquare.getY();
					int size = freeSquare.getSize();
					
					if(size > 0)
					{
						BufferedImage qrCode = QRCodeCreator.createQRCode(this.encodee, size);
						
						ImageConverterAwt2Pdf converter2 = new ImageConverterAwt2Pdf(qrCode);
						PDImage pdImage = converter2.getPDImage();
						
						// open a device to the page content stream
						CSCreator creator = CSCreator.createFromProvider(pageAt);
						
						creator.saveState();
						creator.transform(size, 0, 0, size, x, y);
						creator.doXObject(null, pdImage);
						creator.restoreState();
				
						// don't forget to flush the content.
						creator.close();
					}
				}
				
				createFromLocator.save(new FileLocator(this.outFile));
				createFromLocator.close();
			}
			
			return this.outFile;
			
		} catch (Throwable e) {
			throw new Exception();
		}
		
	}
	
	private BufferedImage renderPage(final PDPage page, int scale) throws CSException
	{
        Rectangle2D rect = page.getCropBox().toNormalizedRectangle();
        BufferedImage image = null;
        IGraphicsContext graphics = null;
        try {
                image = new BufferedImage(
                        (int) (rect.getWidth() * scale),
                        (int) (rect.getHeight()* scale),
                        BufferedImage.TYPE_INT_RGB
                );
                Graphics2D g2 = (Graphics2D) image.getGraphics();
                graphics = new CwtAwtGraphicsContext(g2);
                // setup user space
                AffineTransform imgTransform = graphics.getTransform();
                imgTransform.scale(scale, -scale);
                imgTransform.translate(-rect.getMinX(), -rect.getMaxY());
                graphics.setTransform(imgTransform);
                graphics.setBackgroundColor(Color.WHITE);
                graphics.fill(rect);
                CSContent content = page.getContentStream();
                if (content != null) {
                        CSPlatformRenderer renderer = new CSPlatformRenderer(null, graphics);
                        renderer.setExceptionHandler(new ICSExceptionHandler() {
                                public void error(CSError error) throws CSException {
                                        // ignore
                                }
                                public void warning(CSWarning warning) throws CSException {
                                        // ignore
                                }
                        });
                        renderer.process(content, page.getResources());
                }
                return image;
        } finally {
                if (graphics != null) {
                        graphics.dispose();
                }
        }
	}
	
}
