package org.bibsonomy.webapp.command.actions;


import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * @author PlatinAge
 * @version $Id$
 */
public class DiffPublicationCommand extends EditPublicationCommand{
	private Post<BibTex> postDiff;

	public Post<BibTex> getPostDiff() {
		return postDiff;
	}

	public void setPostDiff(Post<BibTex> postDiff) {
		this.postDiff = postDiff;
	}
}
