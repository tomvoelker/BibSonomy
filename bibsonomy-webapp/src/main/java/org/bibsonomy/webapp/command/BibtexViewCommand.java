package org.bibsonomy.webapp.command;


/**
 * @author jensi
  */
public interface BibtexViewCommand extends PublicationViewCommand {

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
