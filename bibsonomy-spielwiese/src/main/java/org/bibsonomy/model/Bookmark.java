package org.bibsonomy.model;

import org.bibsonomy.ibatis.util.ResourceUtils;


/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.model.Resource} like all resources in BibSonomy.
 * 
 * @author Christian Schenk
 */
public class Bookmark extends Resource {

	
	private String description;
	private String extended;
	private String url;
	
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

	public String getHash() {
		return ResourceUtils.hash(url);
	}

}