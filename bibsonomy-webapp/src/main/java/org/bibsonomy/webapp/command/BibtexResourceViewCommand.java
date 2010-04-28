package org.bibsonomy.webapp.command;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexResourceViewCommand extends TagResourceViewCommand {
	
	/** the intrahash of a publication **/
	private String requBibtex = "";
	
	/** the title of a publication **/
	private String title = "";
	
	/** the group whode resources are requested*/
	private ConceptsCommand concepts = new ConceptsCommand();
	
	/**
	 * @return the hash of a bibtex
	 */
	public String getRequBibtex(){
		return this.requBibtex;
	}
	
	/**
	 * @param requBibtex the requBibtex to set
	 */
	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return this.title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the concepts
	 */
	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	/**
	 * @param concepts the concepts to set
	 */
	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}
}
