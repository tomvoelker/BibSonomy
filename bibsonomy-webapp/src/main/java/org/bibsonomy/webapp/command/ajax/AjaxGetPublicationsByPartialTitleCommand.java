package org.bibsonomy.webapp.command.ajax;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.BaseCommand;
import org.bibsonomy.webapp.command.ListCommand;
/**
 * @author mve
 * @version $Id$
 */
public class AjaxGetPublicationsByPartialTitleCommand extends BaseCommand {
	private ListCommand<Post<BibTex>> posts = null;
	private String title;

	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * 
	 * @return the titles as JSON output
	 */
	public ListCommand<Post<BibTex>> getPosts() {
		return this.posts;
	}
	/**
	 * 
	 * @param posts Set Posts to posts
	 */
	public void setPosts(ListCommand<Post<BibTex>> posts) {
		this.posts = posts;
	}
}
