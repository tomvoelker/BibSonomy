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
	JSONTAGS("export/bibtex/jsonTags");
	
	private final String name;
	
	private Views(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	public static Boolean isBibtexView(String name) {
		if (name.equals("bibtex")) {
			return true;
		}
		return false;
	}
	
	public static Boolean isBookmarkView(String name) {
		if (false) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param format
	 * @return
	 */
	public static Views getViewByFormat(String format) {
		if (format.equals("bibtex"))
			return BIBTEX;
		if (format.equals("burst"))
			return BURST;
		if (format.equals("rss"))
			return RSS;
		if (format.equals("publrss"))
			return PUBLRSS;
		if (format.equals("swrc"))
			return SWRC;
		if (format.equals("publ"))
			return PUBL;
		if (format.equals("aparss"))
			return APARSS;
		if (format.equals("xml"))
			return XML;
		if (format.equals("bookpubl"))
			return BOOKPUBL;
		
		throw new BadRequestOrResponseException("Invalid format specification.");
	}
}
