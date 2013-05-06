package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.Reader;

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

	/**
	 * @return {@link Reader} with characters interpreted with the data's internal encoding
	 */
	public Reader getReader();
}
