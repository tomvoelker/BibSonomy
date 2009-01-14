package org.bibsonomy.webapp.command.special;

import org.bibsonomy.webapp.command.BaseCommand;

/**
 * 
 * Command to do redirects for
 * <ul>
 * <li>/my* pages like /myBibSonomy, /myRelations, etc.,</li>
 * <li>/redirect pages for search forms,</li>
 * <li>/uri/ content negotiation.</li>
 * </ul>
 *  
 * @author rja
 * @version $Id$
 */
public class RedirectCommand extends BaseCommand {

	/**
	 * Name of the /my* page, e.g., "myRelations".
	 */
	private String myPage;

	
	/**
	 * The search terms for the general search form.
	 */
	private String search;
	/**
	 * The scope of the performed search (e.g., "user", "group", ...)
	 */
	private String scope;
	/**
	 * The user to restrict the author search to.  
	 */
	private String requUser;


	/**
	 * The URL to be used for content negotation.
	 */
	private String url;
	
	
	/** Requested URL for content negotiation.
	 * 
	 * @return The URL for content negotiation.
	 */
	public String getUrl() {
		return this.url;
	}

	/** Sets the requested URL for content negotiation.
	 * @param url 
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/** Return the requested user - only relevant for /author pages, i.e., when scope=author.
	 * 
	 * @return The requested user name.
	 */
	public String getRequUser() {
		return this.requUser;
	}

	/** Set the requested user name - only relevant for /author pages, i.e., when scope=author. 
	 * @param requUser
	 */
	public void setRequUser(String requUser) {
		this.requUser = requUser;
	}

	/**
	 * @return The name of the /my* page, e.g., "myRelations".
	 */
	public String getMyPage() {
		return this.myPage;
	}

	/** Set the name of the /my* page, e.g., "myRelations".
	 * @param myPage
	 */
	public void setMyPage(String myPage) {
		this.myPage = myPage;
	}

	/** 
	 * 
	 * @return The search string.
	 */
	public String getSearch() {
		return this.search;
	}

	/** Set the search string.
	 * 
	 * @param search
	 */
	public void setSearch(String search) {
		this.search = search;
	}

	/**
	 * @return The scope of a search.
	 */
	public String getScope() {
		return this.scope;
	}

	/** Sets the scope of a search.
	 * @param scope
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	
}
