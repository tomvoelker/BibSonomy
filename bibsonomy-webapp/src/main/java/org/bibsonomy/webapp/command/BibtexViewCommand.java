package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author jensi
 * @version $Id$
 */
public interface BibtexViewCommand {

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
	 * @return how to render person names in bibtex export last (true <=> person names in "First Last" order)
	 */
	public boolean isFirstLastNames();

	/**
	 * @return bibtexkey stuff
	 */
	public boolean isGeneratedBibtexKeys();

	/**
	 * @return name of a spring-registered urlGenerator (for customized biburl fields from vufind)
	 */
	public String getUrlGenerator();
	
}
