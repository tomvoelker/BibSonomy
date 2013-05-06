package org.bibsonomy.model.util.data;

import java.io.InputStream;

/**
 * @author jensi
 * @version $Id$
 */
public interface Data {
	/**
	 * @return mimetype string of the data
	 */
	public String getMimeType();
	
	/**
	 * @return {@link InputStream} with raw bytes
	 */
	public InputStream getInputStream();
}
