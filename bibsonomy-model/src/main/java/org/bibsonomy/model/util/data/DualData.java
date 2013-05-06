package org.bibsonomy.model.util.data;

import java.io.InputStream;

/**
 * @author jensi
 * @version $Id$
 */
public interface DualData extends Data {
	/**
	 * @return {@link InputStream} of the second data
	 */
	public InputStream getInputStream2();
}
