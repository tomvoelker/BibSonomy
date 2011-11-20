package de.uni.kassel.kde.qr2pdf.util.converter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import de.intarsys.cwt.awt.environment.CwtAwtGraphicsContext;
import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSError;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.CSWarning;
import de.intarsys.pdf.content.ICSExceptionHandler;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
import de.intarsys.tools.locator.FileLocator;

public class JPodConverter extends Converter {

	@Override
	public BufferedImage convertToImage(String fileName) throws Exception 
	{

		PDDocument createFromLocator = PDDocument.createFromLocator(new FileLocator(fileName));
		
		PDPage pageAt = createFromLocator.getPageTree().getPageAt(0);
		
		BufferedImage renderPage = renderPage(pageAt, 2);
		
		//ImageIO.write(renderPage, "png", new File("src/main/resources/test.png"));
		
		return renderPage;
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
