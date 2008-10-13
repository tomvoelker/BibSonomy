package org.bibsonomy.webapp.command;

/**
 * @author mwa
 * @version $Id$
 */
public class BibtexResourceViewCommand extends SimpleResourceViewCommand{
	
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
	
	public void setRequBibtex(String requBibtex) {
		this.requBibtex = requBibtex;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ConceptsCommand getConcepts() {
		return this.concepts;
	}

	public void setConcepts(ConceptsCommand concepts) {
		this.concepts = concepts;
	}

}
