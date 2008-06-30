/*
 * Created on 08.10.2007
 */
package org.bibsonomy.webapp.view;

import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.webapp.util.View;

/**
 * some symbols for views in the application, along with information 
 * which views are capable to display bibtex / bookmark only
 * 
 * @author Jens Illig
 * @version $Id$
 */
/**
 * @author rja
 *
 */
public enum Views implements View {
		
	/**
	 * error page
	 */
	ERROR("error"),
	
	/**
	 * the first page you see when entering the application
	 */
	HOMEPAGE("home"),
	
	/**
	 * user page displaying the resources of a single user
	 */
	USERPAGE("user"),
	
	/**
	 * user page displaying the resources of a single user tagged with a given list of tags
	 */
	USERTAGPAGE("usertag"),	
	
	/**
	 * group page showing all resources of a specified group
	 */
	GROUPPAGE("group"),
	
	/**
	 * group page showing all resources of a specified group and a given tag or list of tags
	 */	
	GROUPTAGPAGE("grouptag"),
	
	/**
	 * tag page show all resources with a given tag or a list of tags
	 */
	TAGPAGE("tag"),
	
	/**
	 * concept page shows all suptags of an requested tag
	 */
	CONCEPTPAGE("concept"),
	
	/**
	 * friends page show all tags whose are viewable for friends by a friend of you
	 */
	FRIENDSPAGE("friends"),
	
	/**
	 * friend page shows all posts which are set viewable for friends of the requested user
	 */
	FRIENDPAGE("friend"),
	
	/**
	 * bibtex page shows all publications with the given inter-/intrahash
	 */
	BIBTEXPAGE("bibtex"),
	
	/**
	 * details of a publication 
	 */
	BIBTEXDETAILS("bibtexdetails"),
	
	/**
	 * bibtexkey page does something with the bibtexkey, perhaps shows the details for a given bibtexkex  
	 */
	BIBTEXKEYPAGE("bibtexkey"),
		
	/**
	 * bibtex output
	 */
	BIBTEX("export/bibtex/bibtex"),
	
	/**
	 * url page, displays all bookmarks for a given url hash  
	 */
	URLPAGE("url"),
	
	/**
	 * userpage only for publications with documents attached
	 */
	USERDOCUMENTPAGE("userDocument"),
	
	/**
	 * userpage only for publications with documents attached
	 */
	GROUPDOCUMENTPAGE("groupDocument"),
	
	/**
	 * burst output for publications
	 */
	BURST("export/bibtex/burst"),
	
	/**
	 * rss bookmark outout for bookmarks
	 */
	RSS("export/bookmark/rssfeed"),
	
	/**
	 * rss output for publications
	 */
	PUBLRSS("export/bibtex/rssfeed"),
	
	/**
	 * swrc output for publications
	 */
	SWRC("export/bibtex/swrc"),
	
	/**
	 * html output for publications
	 */
	PUBL("export/bibtex/htmlOutput"),
	
	/**
	 * aparss output for publications
	 */
	APARSS("export/bibtex/aparssfeed"),
	
	/**
	 * xml output for bookmarks
	 */
	XML("export/bookmark/xmlOutput"),
	
	/**
	 * viewable page
	 */
	VIEWABLEPAGE("viewable"),
	
	/**
	 * viewable page showing all resources of a specified group and a given tag or list of tags
	 */	
	VIEWABLETAGPAGE("viewabletag"),
	
	/**
	 * author page
	 */
	AUTHORPAGE("author"),
	
	/**
	 * search page
	 */
	SEARCHPAGE("search"),
	
	/**
	 * html output for bookmarks
	 */
	BOOKPUBL("export/bookmark/bookpubl"),
	
	/**
	 * admin page
	 */
	ADMINPAGE("admin"),
	
	/**
	 * response page snippet for ajax requests
	 */
	AJAX("ajax"),
	
	/**
	 * posts 
	 */
	AJAX_POSTS("ajaxPosts"),
	
	/**
	 * spammer predictions 
	 */
	AJAX_PREDICTIONS("ajaxPredictions"), 
	
	/**
	 * A json output tag page 
	 */
	JSONTAGS("export/bibtex/jsonTags"),
	
	/**
	 * where users can register
     * TODO: we will probably move those action parts
     * into a separate Views class!
	 */
	REGISTER_USER("actions/register/user"),
	
	/**
	 * After a user has successfully registered, he will see this view.
	 */
	REGISTER_USER_SUCCESS("actions/register/user_success"),
	
	/**
	 * When admins successfully register a user, this page shows them
	 * the details.
	 */
	REGISTER_USER_SUCCESS_ADMIN("actions/register/user_success_admin"),
	
	/**
	 * Log into the system. 
	 */
	LOGIN("actions/login"),
	/**
     * Show a form to request a password reminder.
     */ 
	PASSWORD_REMINDER("actions/user/passwordReminder"), 
	
	/**
	 * Show the form after reminding a password to change it
	 */
	PASSWORD_CHANGE_ON_REMIND("actions/user/passwordChangeOnRemind");
	
	private final String name;
	
	private Views(final String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.webapp.util.View#getName()
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Helper method to identify those formats whose corresponding view 
	 * displays ONLY bibtex posts
	 * 
	 * @param format the name of the format
	 * @return true if the corresponding view displays only bibtex posts, false otherwise
	 */
	public static Boolean isBibtexOnlyFormat(String format) {
		if ("bibtex".equals(format) || 
			"publrss".equals(format) ||
			"publ".equals(format) ||			
			"aparss".equals(format) ||
			"burst".equals(format) || 
			"swrc".equals(format)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Helper method to identify those formats whose corresponding view 
	 * displays ONLY bookmark posts
	 * 
	 * @param format the name of the format
	 * @return true if the corresponding view displays only bookmark posts, false otherwise
	 */
	public static Boolean isBookmarkOnlyFormat(String format) {
		if ("xml".equals(format) || 
			"rss".equals(format) ||
			"bookpubl".equals(format)) {
				return true;
			}
		return false;
	}
	
	/**
	 * Helper method to retrieve a view by a format string (passed to the
	 * application via e.g. ?format=rss)
	 * 
	 * @param format the name of the format
	 * @return the corresponding view for a given format
	 */
	public static Views getViewByFormat(String format) {
		if ("bibtex".equals(format))
			return BIBTEX;
		if ("burst".equals(format))
			return BURST;
		if ("rss".equals(format))
			return RSS;
		if ("publrss".equals(format))
			return PUBLRSS;
		if ("swrc".equals(format))
			return SWRC;
		if ("publ".equals(format))
			return PUBL;
		if ("aparss".equals(format))
			return APARSS;
		if ("xml".equals(format))
			return XML;
		if ("bookpubl".equals(format))
			return BOOKPUBL;
		
		throw new BadRequestOrResponseException("Invalid format specification.");
	}
}
