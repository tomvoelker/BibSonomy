package org.bibsonomy.model;

import org.bibsonomy.ibatis.util.ResourceUtils;

/**
 * This is a bookmark, which is derived from
 * {@link org.bibsonomy.model.Resource} like all resources in BibSonomy.
 */
public class Bookmark extends Resource {

	private String url;
	private String description;
	private String extended;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

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
		return ResourceUtils.hash(this.url);
	}
}