package org.bibsonomy.model.util;

import java.util.Collection;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.Data;

/**
 * Generic class for Import formats
 * 
 * @author Jens Illig
 * @version $Id$
 */
public interface BibTexReader {
	/**
	 * @param data {@link Data} to read from
	 * @return {@link Collection} of read {@link BibTex}s
	 */
	public Collection<ImportResource> read(Data data);
}
