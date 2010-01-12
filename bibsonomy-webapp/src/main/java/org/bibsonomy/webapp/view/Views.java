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
		
	/* *****************************************************
	 * query views
	 * *****************************************************/
		
	/**
	 * the first page you see when entering the application
	 */
	HOMEPAGE("home"),
	
	/**
	 * the page where a user can change his personal settings
	 */
	SETTINGSPAGE("settings"),
	
	/**
	 * user page displaying the resources of a single user
	 */
	USERPAGE("user"),
	
	/**
	 * the user specific curriculum vitae page
	 */
	CVPAGE("cvpage"),
	
	/**
	 * user-user page to highlight the relevant posts / tags of user 2 to user 1 
	 */
	USERUSERPAGE("useruser"),
	
	/**
	 * user page displaying the resources of a single user tagged with a given list of tags
	 */
	USERTAGPAGE("usertag"),	
	
	/**
	 * groups page showing all groups available
	 */
	GROUPSPAGE("groups"),

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
	 * inbox page
	 */
	
	INBOX("inbox"),
	/**
	 * authors overview page
	 */
	AUTHORSPAGE("authors"),
		
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
	 * all the posts the user has picked in his basket 
	 */
	BASKETPAGE("basket"),
	
	/**
	 * relations page shows all the relations of an user
	 */
	USERRELATED("userRelations"),
	
	/**
	 * details of a publication 
	 */
	BIBTEXDETAILS("bibtexdetails"),
	
	/**
	 * bibtexkey page does something with the bibtexkey, perhaps shows the details for a given bibtexkex  
	 */
	BIBTEXKEYPAGE("bibtexkey"),

	/**
	 * url page, displays all bookmarks for a given url hash  
	 */
	URLPAGE("url"),
	
	/**
	 * popular page
	 */
	POPULAR("popular"),
	
	/**
	 * popular tags page
	 */
	POPULAR_TAGS("popularTags"),
	
	/**
	 * userpage only for publications with documents attached
	 */
	USERDOCUMENTPAGE("userDocument"),
	
	/**
	 * userpage only for publications with documents attached
	 */
	GROUPDOCUMENTPAGE("groupDocument"),
	
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
	 * the author tag showing all resources of a specified author and a given tag or list of tags
	 */
	AUTHORTAGPAGE("authortag"),
	
	/**
	 * search page
	 */
	SEARCHPAGE("search"),
	
	/**
	 * additional posts 
	 */
	MY_GROUP_POSTS_PAGE("myGroupPosts"), 

	/**
	 * relevant-for page
	 */
	RELEVANTFORPAGE("relevantfor"),

	/** scraper info list **/
	SCRAPER_INFO("scraperInfo"),

	/* *****************************************************
	 * AJAX views
	 * *****************************************************/

	/**
	 * response page snippet for ajax requests
	 */
	AJAX("ajax/snippetPlain"),
	
	/**
	 * response page snippet for xml ajax requests
	 */
	AJAX_RESPONSE("ajax/snippetXML"),	
	
	/**
	 * used by editBookmark to get the details for a given Url
	 */
	AJAX_GET_TITLE_FOR_URL("ajax/jsonURLDetails"),
	/**
	 * get titles using ajax in JSON format
	 */
	AJAX_GET_PUBLICATION_TITLES("ajax/jsonGetTitles"),
	/**
	 * get bibtex keys for a given user
	 */
	AJAX_GET_BIBTEXKEYS_FOR_USER("ajax/bibtexkeys"),
	/**
	 * posts 
	 */
	AJAX_POSTS("ajax/posts"),
	
	/**
	 * spammer predictions 
	 */
	AJAX_PREDICTIONS("ajax/predictions"),

	/**
	 * a tag cloud (to reload it using AJAX)
	 */
	AJAX_TAGCLOUD("ajax/tagcloud"),

	/* *****************************************************
	 * ADMIN views
	 * *****************************************************/	
	/**
	 * spam admin page
	 */
	ADMIN_SPAM("actions/admin/spam"),
	/**
	 * lucene admin page
	 */
	ADMIN_LUCENE("actions/admin/lucene"),
	/**
	 * recommender admin page
	 */
	ADMIN_RECOMMENDER("actions/admin/recommender"),
	/**
	 * Show the page for administrating groups
	 */
	ADMIN_GROUP("actions/admin/group"),
	/**
	 * general admin page
	 */
	ADMIN("actions/admin/index"),

	/* *****************************************************
	 * action views
	 * *****************************************************/
	
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
	 * where user can register using her openid url
	 */
	REGISTER_USER_OPENID("actions/register/openid/user"),
	
	/**
	 * OpenID register form prefilled with information from 
	 * the OpenID provider
	 */
	REGISTER_USER_OPENID_PROVIDER_FORM("actions/register/openid/provider_form"),
	
	/**
	 * After a user has successfully registered using OpenID, he will see this view.
	 */
	REGISTER_USER_OPENID_SUCCESS("actions/register/openid/user_success"),
	
	/**
	 * where user can attach an openid url to her/his user account
	 */
	// ATTACH_USER_OPENID("actions/register/openid/user_attach"),
	/**
	 * OpenID register form prefilled with information from 
	 * the OpenID provider
	 */
	// ATTACH_USER_OPENID_PROVIDER_FORM("actions/register/openid/attach_provider_form"),

	
	/**
	 * User registration form for LDAP registering
	 */
	REGISTER_USER_LDAP("actions/register/ldap/user"),

	/**
	 * LDAP register form prefilled with information from LDAP server 
	 * 
	 */
	REGISTER_USER_LDAP_FORM("actions/register/ldap/profile_form"),
	
	/**
	 * After a user has successfully registered using LDAP, he will see this view.
	 */
	REGISTER_USER_LDAP_SUCCESS("actions/register/ldap/user_success"),

	
	/**
	 * Log into the system. 
	 */
	LOGIN("actions/login"),
	
	/**
	 * The dialog to EDIT a bookmark (big dialog).
	 */
	EDIT_BOOKMARK("actions/post/editBookmark"),
	/**
	 * The dialog to EDIT a publication (big dialog).
	 */
	EDIT_PUBLICATION("actions/post/editPublication"),
	/**
	 * The dialog to enter a URL for posting (small dialog).
	 */
	POST_BOOKMARK("actions/post/postBookmark"), 	
	/**
	 * The dialog to post one or more publications (tabbed view)
	 */
	POST_PUBLICATION("actions/post/postPublication"),
	/** 
	 * import view 
	 */
	IMPORT("actions/post/import"),
	
	/**
     * Show a form to request a password reminder.
     */ 
	PASSWORD_REMINDER("actions/user/passwordReminder"), 
	
	/**
	 * Show the form after reminding a password to change it
	 */
	PASSWORD_CHANGE_ON_REMIND("actions/user/passwordChangeOnRemind"),

	/**
	 * Upload page to upload document to an existing bibtex entry
	 */
	UPLOAD_FILE("actions/uploadFile"), 
	
	/**
	 * view for streaming the desired document to the outputResponse
	 */
	DOWNLOAD_FILE("downloadFile"),
	
	
	/**
	 * to edit the tags of publications
	 */
	BATCHEDITBIB("actions/edit/batcheditbib"),
	
	/**
	 * to edit the tags of temporarily stored publications
	 */
	BATCHEDIT_TEMP_BIB("actions/edit/batchedittempbib"),
	/**
	 * to edit the tags of bookmarks 
	 */
	BATCHEDITURL("actions/edit/batchediturl"),
	/**
	 * edit tags
	 */
	EDIT_TAGS("actions/edit/edittags"),
	
	
	
	/* *****************************************************
	 * query independent views to show bookmark or 
	 * publication lists
	 * *****************************************************/

	/**
	 * error page
	 */
	ERROR("error"),
	
	/**
	 * bibtex output
	 */
	BIBTEX("export/bibtex/bibtex"),
	
	/**
	 * EndNote (RIS) output 
	 */
	ENDNOTE("export/bibtex/endnote"),
	
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
	 * RSS output for publications with modification for NEPOMUK project  
	 */
	PUBLRSSNEPOMUK("export/bibtex/rssfeedNepomuk"),
	
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
	 * JSON for both bookmarks and publications
	 */
	JSON("export/json"),
	
	/**
	 * html output for bookmarks
	 */
	BOOKPUBL("export/bookmark/bookpubl"),
	
	/**
	 * BibTeX output for bookmarks 
	 */
	BOOKBIB("export/bookmark/bibtex"),
	
	/**
	 * An XML file printing all output formats supported by UnAPI.
	 * Basically, a list of some of our export formats in XML.
	 */
	UNAPI_SUPPORTED_FORMATS("export/bibtex/unapi"),	
	
	/**
	 * /layout/* pages which are rendered by JabRef 
	 */
	LAYOUT("layout"),
	/**
	 * /csv/ pages rendered by the CSVView
	 */
	CSV("csv"),
	
	/**
	 * show the export page
	 */
	EXPORT("export"),

	/**
	 * export layouts in a JSON object
	 */
	EXPORTLAYOUTS("exportLayouts"),
	
	
	
	/* *************************************************************************
	 * TODO: The following views have been added by not so careful developers 
	 * who forgot to put them at the correct place above this comment. Please
	 * have a look and see, into which section your view fits best!  
	 *  
	 * *************************************************************************/
	/**
	 * Show the page for the purpose tags
	 */
	PURPOSE_TAGS("purpose"),
		
	/**
	 * Show the page with the purpose tags and the urls
	 */
	PURPOSES("purposes"),
	
	/**
	 * show the advanced_search page
	 */
	MYSEARCH("mySearch"),
	
	/**
	 * show button-page view
	 */
	BUTTONS("buttons"),
	
	/**
	 * show relations
	 */
	RELATIONS("relations"), 
	
	/**
	 * show followers
	 */
	FOLLOWERS("followers"),
	


	
	/**
	 * PUMA the first page you see when entering the application
	 */
	PUMAHOMEPAGE("pumahome");
	
	
	
	
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
			"layout".equals(format) ||
			"batcheditbib".equals(format) ||
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
			"batchediturl".equals(format) ||
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
		if ("json".equals(format)) 
			return JSON;
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
		if ("endnote".equals(format))
			return ENDNOTE;
		if ("aparss".equals(format))
			return APARSS;
		if ("xml".equals(format))
			return XML;
		if ("bookpubl".equals(format))
			return BOOKPUBL;
		if ("bookbib".equals(format))
			return BOOKBIB;
		if ("publrssN".equals(format))
			return PUBLRSSNEPOMUK;
		if ("layout".equals(format))
			return LAYOUT;
		if ("batcheditbib".equals(format)) 
			return BATCHEDITBIB;
		if ("batchediturl".equals(format)) 
			return BATCHEDITURL;
		if ("tagcloud".equals(format))
			return AJAX_TAGCLOUD;
		if ("csv".equals(format))
			return CSV;
		
		throw new BadRequestOrResponseException("Invalid format specification.");
	}
}
