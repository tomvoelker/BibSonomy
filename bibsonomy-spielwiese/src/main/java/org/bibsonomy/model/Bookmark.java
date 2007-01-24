package org.bibsonomy.model;


/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.model.Resource} like all resources in BibSonomy.
 * 
 * @author Christian Schenk
 */
public class Bookmark extends Resource {

	
	private String description;
	private String extended;
	private String urlHash;
	
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExtended() {
		return this.extended;
	}

	public void setExtended(String extended) {
		this.extended = extended;
	}

	

    
	public String getUrlHash() {
		return this.urlHash;
	}

	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	
	public static String hash (String url) { // exists, so that everybody can calculate Hashes of an URL
		return hash(url);
	}

	
}