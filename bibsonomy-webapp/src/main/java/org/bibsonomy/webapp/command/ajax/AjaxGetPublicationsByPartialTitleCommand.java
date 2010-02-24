package org.bibsonomy.webapp.command.ajax;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.webapp.command.BaseCommand;
/**
 * @author mve
 * @version $Id$
 */
public class AjaxGetPublicationsByPartialTitleCommand extends BaseCommand {
	private List<Post<BibTex>> posts = null;
	private String title = null;
	private String userName = null;
	private String requestedUserName = null;
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getRequestedUserName() {
		return this.requestedUserName;
	}
	
	public void setRequestedUserName(String requestedUserName) {
		this.requestedUserName = requestedUserName;
	}
	
	/**
	 * 
	 * @return the titles as JSON output
	 */
	public List<Post<BibTex>> getPosts() {
		return this.posts;
	}
	/**
	 * 
	 * @param posts Set Posts to posts
	 */
	public void setPosts(List<Post<BibTex>> posts) {
		this.posts = posts;
	}
}
