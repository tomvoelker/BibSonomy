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
	XML("export/bookmark/xmlOutput");
	
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
		
		throw new BadRequestOrResponseException("Invalid format specification.");
	}
}
