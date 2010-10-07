package org.bibsonomy.util.upload;

import java.io.BufferedInputStream;

/**
 * @author  Christian Kramer
 * @version $Id$
 */
public interface FileDownloadInterface {

	/**
	 * @return bis
	 */
	public abstract BufferedInputStream getBuf();
}