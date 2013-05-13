package org.bibsonomy.model.util;

import java.util.Collection;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;

/**
 * Generic class for Import formats
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface BibTexReader {
	/**
	 * @param importRes {@link ImportResource} to read from
	 * @return {@link Collection} of read {@link BibTex}s
	 */
	public Collection<ImportResource> read(ImportResource importRes);
}
