package org.bibsonomy.model.util.data;

import java.io.InputStream;
import java.io.Reader;

/**
 * @author jensi
 * @version $Id$
 */
public interface DualData extends Data {
	/**
	 * @return {@link InputStream} of the second data
	 */
	public InputStream getInputStream2();
	
	/**
	 * @return {@link Reader} with characters interpreted with the data's internal encoding
	 */
	public Reader getReader2();
	
	/**
	 * @return the second data as a {@link Data} Object
	 */
	public Data getData2();
}
