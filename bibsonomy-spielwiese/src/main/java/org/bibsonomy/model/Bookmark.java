package org.bibsonomy.model;

import java.util.List;

//import resources.Resource;

/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.model.Resource} like all resources in BibSonomy.
 * 
 * @author Christian Schenk
 */
public class Bookmark extends Resource {

	private List<Tag> tags;
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

	public List<Tag> getTags() {
		return this.tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
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