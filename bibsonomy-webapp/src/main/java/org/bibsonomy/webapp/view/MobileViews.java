package org.bibsonomy.webapp.view;

import org.bibsonomy.webapp.util.View;

/**
 * @author Waldemar Biller
 * @version $Id$
 */
public enum MobileViews implements View {
	
	/**
	 * the first page you see when entering the application
	 */
	HOMEPAGE("mobile/home"),
	
	/**
	 * user page displaying the resources of a single user
	 */
	USERPAGE("mobile/user"),
	
	/**
	 * tag page show all resources with a given tag or a list of tags
	 */
	TAGPAGE("mobile/tags"),
	
	/**
	 * search page
	 */
	SEARCHPAGE("mobile/search"),
	
	/**
	 * The dialog to enter a URL for posting (small dialog).
	 */
	POST_BOOKMARK("mobile/post_bookmark"),
	
	/**
	 * The dialog to post one or more publications (tabbed view)
	 */
	POST_PUBLICATION("mobile/post_publication"),
	
	/**
	 * details of a publication 
	 */
	BIBTEXDETAILS("mobile/publication_details"),
	
	/**
	 * group page showing all resources of a specified group
	 */
	GROUPPAGE("mobile/group"),
	
	/**
	 * user page displaying the resources of a single user tagged with a given list of tags
	 */
	USERTAGPAGE("mobile/tag"),	
	
	/**
	 * The dialog to EDIT a bookmark.
	 */
	EDIT_BOOKMARK("mobile/edit_bookmark"),
	
	/**
	 * he dialog to EDIT a publication.
	 */
	EDIT_PUBLICATION("mobile/edit_publication"),
	
	/**
	 * 
	 */
	LOGIN("mobile/login");
		
	private String name;
	
	private MobileViews(String name) {
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}