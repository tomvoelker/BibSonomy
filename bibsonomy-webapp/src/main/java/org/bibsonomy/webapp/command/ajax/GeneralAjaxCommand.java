package org.bibsonomy.webapp.command.ajax;

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

	/**
	 * @return the pageTitle
	 */
	@Override
	public String getPageTitle() {
		return this.pageTitle;
	}

	/**
	 * @param pageTitle the pageTitle to set
	 */
	@Override
	public void setPageTitle(final String pageTitle) {
		this.pageTitle = pageTitle;
	}

	/**
	 * @return the pageURL
	 */
	public String getPageURL() {
		return this.pageURL;
	}

	/**
	 * @param pageURL the pageURL to set
	 */
	public void setPageURL(final String pageURL) {
		this.pageURL = pageURL;
	}

	/**
	 * @return the pageDescription
	 */
	public String getPageDescription() {
		return this.pageDescription;
	}

	/**
	 * @param pageDescription the pageDescription to set
	 */
	public void setPageDescription(final String pageDescription) {
		this.pageDescription = pageDescription;
	}

	/**
	 * @return the pageKeywords
	 */
	public String getPageKeywords() {
		return this.pageKeywords;
	}

	/**
	 * @param pageKeywords the pageKeywords to set
	 */
	public void setPageKeywords(final String pageKeywords) {
		this.pageKeywords = pageKeywords;
	}

	/**
	 * @return the q
	 */
	public String getQ() {
		return this.q;
	}

	/**
	 * @param q the q to set
	 */
	public void setQ(final String q) {
		this.q = q;
	}

	/**
	 * @return the requestedUser
	 */
	public String getRequestedUser() {
		return this.requestedUser;
	}

	/**
	 * @param requestedUser the requestedUser to set
	 */
	public void setRequestedUser(final String requestedUser) {
		this.requestedUser = requestedUser;
	}

	/**
	 * @return the bibtexPosts
	 */
	public List<Post<BibTex>> getBibtexPosts() {
		return this.bibtexPosts;
	}

	/**
	 * @param bibtexPosts the bibtexPosts to set
	 */
	public void setBibtexPosts(final List<Post<BibTex>> bibtexPosts) {
		this.bibtexPosts = bibtexPosts;
	}

}
