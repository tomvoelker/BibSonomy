package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author jensi
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

	/**
	 * @return true iff fields with automatically imported dummy values (noauthor/noyear) for required fields should be skipped during rendering (or alternatively replaced by more readable values such as N.N. by some views)
	 */
	public boolean isSkipDummyValues();
}
