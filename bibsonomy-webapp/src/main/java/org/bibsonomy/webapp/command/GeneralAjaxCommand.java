package org.bibsonomy.webapp.command;

import java.util.List;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * General command for ajax requests
 * 
 * @author fba
 * @version $Id$
 */
public class GeneralAjaxCommand extends AjaxCommand {
	/**
	 * page title
	 */
	private String pageTitle; 	
	/**
	 * page URL
	 */
	private String pageURL;	
	/**
	 * page description
	 */
	private String pageDescription;	
	/**
	 * page keywords
	 */
	private String pageKeywords;
	/**
	 * generic query parameter
	 */
	private String q;
	
	/**
	 * generic user name parameter
	 */
	private String requestedUser;
	
	/**
	 * a list of bibtexs 
	 */
	private List<Post<BibTex>> bibtexPosts;

	public String getPageTitle() {
		return this.pageTitle;
	}

	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}

	public String getPageURL() {
		return this.pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public String getPageDescription() {
		return this.pageDescription;
	}

	public void setPageDescription(String pageDescription) {
		this.pageDescription = pageDescription;
	}

	public void setPageKeywords(String pageKeywords) {
		this.pageKeywords = pageKeywords;
	}

	public String getPageKeywords() {
		return pageKeywords;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public String getQ() {
		return q;
	}

	public void setRequestedUser(String requestedUser) {
		this.requestedUser = requestedUser;
	}

	public String getRequestedUser() {
		return requestedUser;
	}

	public void setBibtexPosts(List<Post<BibTex>> list) {
		this.bibtexPosts = list;
	}

	public List<Post<BibTex>> getBibtexPosts() {
		return bibtexPosts;
	}

}
