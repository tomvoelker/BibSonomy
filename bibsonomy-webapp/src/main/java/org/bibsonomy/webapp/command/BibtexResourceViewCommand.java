package org.bibsonomy.webapp.command;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.Document;


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
	
	private List<Document> documents;

	/**
	 * additional metadata for bibtex resource
	 * 
	 * additionalMetadataMap
	 *  {
	 *  	DDC=[010, 050, 420, 422, 334, 233], 
	 *  	post.resource.openaccess.additionalfields.additionaltitle=[FoB], 
	 *  	post.resource.openaccess.additionalfields.phdreferee2=[Petra Musterfrau], 
	 *  	post.resource.openaccess.additionalfields.phdreferee=[Peter Mustermann], 
	 *  	ACM=[C.2.2], 
	 *  	JEL=[K12], 
	 *  	post.resource.openaccess.additionalfields.sponsor=[DFG, etc..], 
	 *  	post.resource.openaccess.additionalfields.phdoralexam=[17.08.2020], 
	 *  	post.resource.openaccess.additionalfields.institution=[Uni KS tEST ]
	 *  }
	 */
	private Map<String, List<String>> additionalMetadata;

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

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	/**
	 * @return the documents
	 */
	public List<Document> getDocuments() {
		return documents;
	}

	/**
	 * @return the map of additionalMetadata
	 */
	public Map<String, List<String>> getAdditionalMetadata() {
		return this.additionalMetadata;
	}

	/**
	 * @param additionalMetadata the map of additionalMetadata to set
	 */
	public void setAdditionalMetadata(Map<String, List<String>> additionalMetadata) {
		this.additionalMetadata = additionalMetadata;
	}
}
