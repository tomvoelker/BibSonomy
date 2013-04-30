package org.bibsonomy.model.util;

import java.io.InputStream;
import java.util.Collection;

import org.bibsonomy.model.BibTex;

/**
 * Generic class for Import formats
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface BibTexReader {
	/**
	 * @param is {@link InputStream} to read from
	 * @return {@link Collection} of read {@link BibTex}s
	 */
	public Collection<BibTex> read(InputStream is);
}
