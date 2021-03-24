package org.bibsonomy.search.es.client;

/**
 * information about the document to delete
 * @author dzo
 */
public class DeleteData extends AbstractData {

	private String id;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
