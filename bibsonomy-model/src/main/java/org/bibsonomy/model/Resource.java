package org.bibsonomy.model;

import java.util.LinkedList;
import java.util.List;

/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 */
public abstract class Resource {

	/** An Id for this resource; by default ConstantID.IDS_UNDEFINED_CONTENT_ID */
	private int count;
	private String interHash;
	private String intraHash;
	private List<Post<? extends Resource>> posts;


	public String getInterHash() {
		return this.interHash;
	}

	public void setInterHash(String interHash) {
		this.interHash = interHash;
	}

	public String getIntraHash() {
		return this.intraHash;
	}

	public void setIntraHash(String intraHash) {
		this.intraHash = intraHash;
	}

	public List<Post<? extends Resource>> getPosts() {
		if (this.posts == null) {
			this.posts = new LinkedList<Post<? extends Resource>>();
		}
		return this.posts;
	}

	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}