package org.bibsonomy.webapp.command;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * command for the hash example
 * just holding a reference to a publication post
 * 
 * @author janus
 */
public class HashExampleCommand extends BaseCommand {

	private Post<BibTex> post;

	/**
	 * @return the post
	 */
	public Post<BibTex> getPost() {
		return this.post;
	}

	/**
	 * @param post the post to set
	 */
	public void setPost(Post<BibTex> post) {
		this.post = post;
	}
}
