package org.bibsonomy.model;

/**
 * @author philipp
 * @version $Id$
 */
public class PublicationClassification {

	final String id;
	
	final String description;

	/**
	 * @param s
	 * @param description2
	 */
	public PublicationClassification(String s, String description2) {
		this.id = s;
		this.description = description2;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
}
