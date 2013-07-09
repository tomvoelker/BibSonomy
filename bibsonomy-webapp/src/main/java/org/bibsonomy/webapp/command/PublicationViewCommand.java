package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author jensi
 * @version $Id$
 */
public interface PublicationViewCommand {
	/**
	 * @return the bibtex ListView
	 */
	public ListCommand<Post<BibTex>> getBibtex();
	
	/**
	 * @return The requested format.
	 * 
	 */
	public String getFormat();

	/** @return whether the result should be presented as a download */
	public boolean isDownload();

}
