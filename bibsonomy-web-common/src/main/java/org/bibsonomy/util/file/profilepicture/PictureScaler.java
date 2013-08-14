package org.bibsonomy.util.file.profilepicture;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.IOException;

/**
 * @author dzo
 * @version $Id$
 */
public interface PictureScaler {
	
	/**
	 * scales the picture
	 * 
	 * @param image
	 * @return the scaled picture
	 * @throws IOException
	 */
	public RenderedImage scalePicture(final Image image) throws IOException;
}
