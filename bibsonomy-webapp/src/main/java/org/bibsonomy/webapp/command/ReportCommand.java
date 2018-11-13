package org.bibsonomy.webapp.command;


import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

import java.util.List;

public class ReportCommand extends BaseCommand{
	/**
	 * List of all porjects being returned
	 */
	private List<Post<BibTex>> posts;

	/**
	 * @param posts
	 */
	public void setPosts(List<Post<BibTex>> posts) {
		this.posts = posts;
	}

	/**
	 * @return
	 */
	public List<Post<BibTex>> getPosts() {
		return this.posts;
	}
}
