package servlets;

import helpers.MySqlError;
import helpers.constants;
import helpers.database.DBPickManager;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import resources.Bibtex;
import resources.Bookmark;
import resources.SplittedAuthors;
import resources.SplittedConcepts;
import resources.SplittedEntireConcepts;
import resources.SplittedTags;
import resources.SystemTags;
import beans.ResourceBean;
import beans.UserBean;
import filters.SessionSettingsFilter;

public class ResourceHandler extends HttpServlet{ 
	private static final Logger log = Logger.getLogger(ResourceHandler.class);

	private static final long serialVersionUID = 3833747689652301876L;
	private DataSource dataSource;
	/* page requests encoded as parameter (through UrlRewriteFilter) in request */
	private static final String PAGE_USER = "user";
	private static final String PAGE_URL = "url";
	private static final String PAGE_USERURL = "userurl";
	private static final String PAGE_BIBTEX = "bibtex";
	private static final String PAGE_USERBIBTEX = "userbibtex";
	private static final String PAGE_TAG = "tag";
	private static final String PAGE_AUTHOR = "author";
	private static final String PAGE_AUTHORTAG = "authortag";	
	private static final String PAGE_USERTAG = "usertag";
	private static final String PAGE_HOME = "home";
	private static final String PAGE_SEARCH = "search";
	private static final String PAGE_POPULAR = "popular";
	private static final String PAGE_BASKET = "basket";
	private static final String PAGE_VIEWABLE = "viewable";
	private static final String PAGE_VIEWABLETAG = "viewabletag";
	private static final String PAGE_GROUP = "group";
	private static final String PAGE_GROUPTAG = "grouptag";
	private static final String PAGE_FRIEND = "friend";
	private static final String PAGE_FRIENDUSER = "frienduser";
	private static final String PAGE_CONCEPT = "concept";
	private static final String PAGE_BIBTEXKEY = "bibtexkey";
	/* servlet-mappings for JSPs to forward */
	private static final String JSP_BASKET = "basket.jsp";
	private static final String JSP_USER = "user.jsp";
	private static final String JSP_USER_FILTER = "userFilter.jsp";
	private static final String JSP_URL = "url.jsp";
	private static final String JSP_BIBTEX = "bibtex.jsp";
	private static final String JSP_USERBIBTEX = "bibtex_entry.jsp";
	private static final String JSP_TAG = "tag.jsp";
	private static final String JSP_AUTHOR = "author.jsp";
	private static final String JSP_AUTHORTAG = "authortag.jsp";
	private static final String JSP_USERTAG = "usertag.jsp";
	private static final String JSP_USERCONCEPT = "userconcept.jsp";
	private static final String JSP_CONCEPT = "concept.jsp";
	private static final String JSP_HOME = "home.jsp";
	private static final String JSP_FRIEND = "friend.jsp";
	private static final String JSP_FRIENDUSER = "frienduser.jsp";
	private static final String JSP_VIEWABLE = "viewable.jsp";
	private static final String JSP_VIEWABLETAG = "viewabletag.jsp";
	private static final String JSP_GROUP = "group.jsp";
	private static final String JSP_GROUPTAG = "grouptag.jsp";
	private static final String JSP_SEARCH = "search.jsp";
	private static final String JSP_POPULAR = "popular.jsp";
	private static final String JSP_BATCHEDIT = "batchedit.jsp";
	private static final String JSP_BIBTEXKEY = "bibtexkey.jsp";
	/* parameter names */
	private static final String REQ_PARAM_PAGE="page";
	private static final String REQ_PARAM_USER="requUser";
	private static final String REQ_PARAM_TAG="requTag";
	private static final String REQ_PARAM_AUTHOR="requAuthor";  
	private static final String REQ_PARAM_URL="requUrl";
	private static final String REQ_PARAM_BIBTEX="requBibtex";
	private static final String REQ_PARAM_SIM="requSim";
	private static final String REQ_PARAM_SEARCH="requSearch";
	private static final String REQ_PARAM_GROUP="requGroup";
	private static final String REQ_PARAM_ACTION="action";
	
	private static final String REQ_PARAM_START_BOOK="startBook";
	private static final String REQ_PARAM_START_BIB="startBib";
	private static final String REQ_PARAM_START_BOOK_NEW="bookmark.start";
	private static final String REQ_PARAM_START_BIB_NEW="bibtex.start";
	private static final String REQ_PARAM_PPP_BOOK="bookmark.postsPerPage";
	private static final String REQ_PARAM_PPP_BIBTEX="bibtex.postsPerPage";
	private static final String REQ_PARAM_SHOW_PDF = "myPDF";
	private static final String REQ_PARAM_DUPLICATES = "myDuplicates";
	private static final String REQ_PARAM_BIBTEXKEY = "requKey";

	/* request attributes */
	private static final String REQ_ATTRIB_START_BOOK="startBook";
	private static final String REQ_ATTRIB_START_BIB="startBib";
	private static final String REQ_ATTRIB_ALL_BOOKS="allBookRows";
	private static final String REQ_ATTRIB_HAVE_BOOKS="haveBookRows";
	private static final String REQ_ATTRIB_ALL_BIB="allBibRows";
	private static final String REQ_ATTRIB_HAVE_BIB="haveBibRows";
	private static final String REQ_ATTRIB_IS_RESOURCE_SITE = "isResourceSite";
	private static final String REQ_ATTRIB_BEAN="ResourceBean";
	private static final String REQ_ATTRIB_IS_USERPAGE="isUserPage";
	private static final String REQ_ATTRIB_WARNING = "warning";


	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			Context initContext = new InitialContext();
			Context envContext = (Context) initContext.lookup("java:/comp/env");
			dataSource = (DataSource) envContext.lookup("jdbc/bibsonomy");
		} catch (NamingException ex){
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ResultSet rst = null;
		DBContext c   = new DBContext();

		/* *************************************************************************
		 * get user info
		 * *************************************************************************/
		UserBean user = SessionSettingsFilter.getUser(request); 
		String currUser = user.getName();


		/* *************************************************************************
		 *                Request Parameter Handling
		 * *************************************************************************/
		String requUser     = request.getParameter(REQ_PARAM_USER);
		String requPage     = request.getParameter(REQ_PARAM_PAGE);		
		String requUrl      = request.getParameter(REQ_PARAM_URL);
		String requBibtex   = request.getParameter(REQ_PARAM_BIBTEX);
		String requSim      = request.getParameter(REQ_PARAM_SIM);
		String requTag      = request.getParameter(REQ_PARAM_TAG);
		String requAuthor   = request.getParameter(REQ_PARAM_AUTHOR);
		String requAction   = request.getParameter(REQ_PARAM_ACTION);
		String requGroup    = request.getParameter(REQ_PARAM_GROUP);
		String requBibtexkey= request.getParameter(REQ_PARAM_BIBTEXKEY);
		String search       = request.getParameter(REQ_PARAM_SEARCH);
		String requFilter   = request.getParameter("filter");
		
		boolean showPdf     = REQ_PARAM_SHOW_PDF.equals(requFilter) && requUser != null && requUser.equals(currUser);

		/* *************************************************************************
		 * some other settings
		 * *************************************************************************/
		String forwPage     = JSP_HOME;
		String requPath     = "";
		// ensure, that we always show some page
		if (requPage == null) {
			requPage = PAGE_HOME;
		}
		// check type of feed
		String requType       = (String)request.getAttribute("type");
		boolean isRssFeed     = "rss".equals(requType); 
		boolean isXmlFeed     = "xml".equals(requType);
		boolean isBookBibFeed = "bookbib".equals(requType);
		boolean isPublFeed    = "publ".equals(requType); 
		boolean isPublKDEFeed = "publkde".equals(requType);
		boolean isPublCSVFeed = "publcsv".equals(requType);
		boolean isPublRSSNepoFeed = "publrssN".equals(requType);
		boolean isBibFeed     = "bib".equals(requType);
		boolean isSWRCFeed    = "swrc".equals(requType);
		boolean isEndFeed     = "endnote".equals(requType); 
		boolean isPublRssFeed = "publrss".equals(requType);
		boolean isPublApaRss  = "aparss".equals(requType);
		boolean isBurstFeed   = "burst".equals(requType);
		boolean isLayoutFeed  = "layout".equals(requType);
		boolean isNRLFeed     = "nrl".equals(requType);
		boolean isjsonFeed    = "json".equals(requType); 
		/*  
		 * TODO: currently every page can be edited and in batchedit.jsp only the users own entries are shown
		 * Nevertheless they're gotten from the database, which is not neccessary at all --> maybe there should
		 * be an option to get only the users own entries. 
		 */
		boolean isBatchEditUrl= "batchediturl".equals(requType);  
		boolean isBatchEditBib= "batcheditbib".equals(requType);  
		boolean isUserPage    = false; // only posts of one user on page?

		boolean getOnlyBibtex = isBibFeed || isPublFeed || isPublKDEFeed || isPublCSVFeed ||isPublRSSNepoFeed || isPublRssFeed || isLayoutFeed || isBurstFeed || isBatchEditBib || isEndFeed || isSWRCFeed || isjsonFeed;

		/* *************************************************************************
		 *  get/set value of maximal number of rows to retrieve from database
		 * *************************************************************************/
		int itemCount = user.getItemcount();
		int startBook = 0;
		int startBib  = 0; 

		
		/*
		 * TODO: workaround to recognize new style parameters
		 */
		int bookmarkPostsPerPage;
		try {
			bookmarkPostsPerPage = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_PPP_BOOK)));
		} catch (Exception e) {
			bookmarkPostsPerPage = itemCount;
		}
		int bibtexPostsPerPage;
		try {
			bibtexPostsPerPage = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_PPP_BIBTEX)));
		} catch (Exception e) {
			bibtexPostsPerPage = itemCount;
		}
		
		// add one element, so that we can check, if more rows follow
		itemCount++;
		bookmarkPostsPerPage++;
		bibtexPostsPerPage++;

		/*
		 * get startBook Parameter to calculate an OFFSET for database
		 */			
		try {
			startBook = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_START_BOOK)));
		} catch (Exception e) {
			startBook = 0;
		}
		/*
		 * TODO: workaround to recognize new style parameters (neccessary for /bediturl)
		 */
		try {
			int temp = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_START_BOOK_NEW)));
			if (temp >= 0) startBook = temp;
		} catch (Exception e) {
			// ignore
		}

		
		request.setAttribute(REQ_ATTRIB_START_BOOK, startBook);

		
		/*
		 * get startBib Parameter to calculate an OFFSET for database
		 */
		try {
			startBib = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_START_BIB)));			
		} catch (Exception e) {
			startBib = 0;
		}
		/*
		 * TODO: workaround to recognize new style parameters (neccessary for /beditbib)
		 */
		try {
			int temp = Math.abs(Integer.parseInt(request.getParameter(REQ_PARAM_START_BIB_NEW)));
			if (temp >= 0) startBib = temp;
		} catch (Exception e) {
			// ignore
		}
		
		request.setAttribute(REQ_ATTRIB_START_BIB, startBib);


		/* *************************************************************************
		 *                               do database queries
		 * *************************************************************************/
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					c.conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}



			// all Sites are ResourceSites
			request.setAttribute(REQ_ATTRIB_IS_RESOURCE_SITE, "yes");			

			// query database depending on the requested page
			if (requPage.equals(PAGE_HOME)) {
				// handle /
				queryPageHome (c, requFilter);
				request.setAttribute(REQ_ATTRIB_ALL_BIB, "y"); // disable "next" Button
				request.setAttribute(REQ_ATTRIB_ALL_BOOKS, "y"); // disable "next" Button
			} else { 
				if (requPage.equals(PAGE_USER)) {
					// handle /user/USER
					forwPage   = JSP_USER;
					requPath   = PAGE_USER + "/" + URLEncoder.encode(requUser, "UTF-8");
					isUserPage = true;
					if (REQ_PARAM_DUPLICATES.equals(requFilter)) {
						itemCount = Integer.MAX_VALUE; 
						forwPage = JSP_USER_FILTER;
						request.setAttribute(REQ_ATTRIB_ALL_BIB, "y"); // disable "next" Button
						queryPageDuplicate (c, currUser);
					} else if (showPdf) {
						/*
						 * user wants to see all bibtex posts, which have a document attached
						 */
						itemCount = Integer.MAX_VALUE;
						forwPage = JSP_USER_FILTER;
						request.setAttribute(REQ_ATTRIB_ALL_BIB, "y"); // disable "next" Button
						queryPageUserPDF (c, currUser);
					} else {
						queryPageUser (c, currUser, requUser, -1, bookmarkPostsPerPage, bibtexPostsPerPage, startBook, startBib);	
					}

				}
				if (requPage.equals(PAGE_CONCEPT)){
					request.setAttribute(REQ_ATTRIB_IS_USERPAGE, "1");
					//	handle /concept/user/USER/TAG
					if (requUser != null) {
						forwPage = JSP_USERCONCEPT;
						requPath = PAGE_CONCEPT + "/" + PAGE_USER + "/" + URLEncoder.encode(requUser, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8"); 
						queryPageUserConcept (c, currUser, requUser, requTag, itemCount, startBook, startBib);						
						isUserPage = true;
					} 
					// handle /concept/tag/TAG
					else {
						forwPage = JSP_CONCEPT;
						requPath = PAGE_CONCEPT + "/" + PAGE_TAG + "/" + URLEncoder.encode(requTag, "UTF-8"); 
						queryPageConcept(c, currUser, requTag, itemCount, startBook, startBib);						
					}	
				}
				if (requPage.equals(PAGE_USERTAG)) {
					// handle /user/USER/TAG
					forwPage   = JSP_USERTAG;
					requPath   = PAGE_USER + "/" + URLEncoder.encode(requUser, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8");
					isUserPage = true;
					queryPageUserTag (c, currUser, requUser, -1, requTag, itemCount, startBook, startBib);
				} 
				if (requPage.equals(PAGE_TAG)) {
					// handle /tag/TAG
					forwPage = JSP_TAG;
					requPath = PAGE_TAG +  "/" + URLEncoder.encode(requTag, "UTF-8");
					String order = request.getParameter("order");
					if("folkrank".equals(order)) {
						request.setAttribute(REQ_ATTRIB_ALL_BIB, "y"); // disable "next" Button
						request.setAttribute(REQ_ATTRIB_ALL_BOOKS, "y"); // disable "next" Button
					}
					queryPageTag (c, requTag, constants.SQL_CONST_GROUP_PUBLIC, itemCount, startBook, startBib, order);
				} 
				if (requPage.equals(PAGE_AUTHOR) || requPage.equals(PAGE_AUTHORTAG)) {
					if (requPage.equals(PAGE_AUTHOR)) {
						forwPage = JSP_AUTHOR;	
						requPath = PAGE_AUTHOR +  "/" + URLEncoder.encode(requAuthor, "UTF-8");						
						queryPageAuthor(c, requAuthor, itemCount, startBib);
					} else {
						forwPage = JSP_AUTHORTAG;	
						requPath = PAGE_AUTHOR +  "/" + URLEncoder.encode(requAuthor, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8");			
						queryPageAuthorTag(c, requAuthor, requTag, itemCount, startBib);
					}					
				}

				if (requPage.equals(PAGE_URL)) {
					// handle /url/HASH
					forwPage = JSP_URL;
					requPath = PAGE_URL + "/" + requUrl;
					queryPageUrl (c, requUrl, itemCount, startBook);					
				} 
				if (requPage.equals(PAGE_USERURL)) {
					// handle /url/HASH/USER
					forwPage   = JSP_URL;
					requPath   = PAGE_URL + "/" + requUrl + "/" + URLEncoder.encode(requUser, "UTF-8");
					isUserPage = true;
					queryPageUserUrl (c, requUrl, requUser, currUser);
				} 
				if (requPage.equals(PAGE_BIBTEX)) {
					// handle /bibtex/HASH
					forwPage = JSP_BIBTEX;
					requPath = PAGE_BIBTEX + "/" + requSim + requBibtex; // TODO: repair requPath
					queryPageBibtex (c, requBibtex, requSim, itemCount, startBib);
				} 
				if (requPage.equals(PAGE_USERBIBTEX)) {
					// handle /bibtex/HASH/USER
					if (requSim.equals(Integer.toString(Bibtex.INTRA_HASH))) {
						// this is the user hash (only one per user) --> show single entry
						forwPage = JSP_USERBIBTEX;
					} else {
						// this is another hash ... several per user allowed --> show several entries
						forwPage = JSP_BIBTEX;
					}
					requPath   = PAGE_BIBTEX + "/" + requSim + requBibtex + "/" + URLEncoder.encode(requUser, "UTF-8"); // TODO: repair requPath
					isUserPage = true;
					queryPageUserBibtex (c, requBibtex, requSim, requUser, currUser);
				} 
				if (requPage.equals(PAGE_BIBTEXKEY)) {
					// handle /bibtexkey/[BIBTEXKEY]/[USERNAME]
					forwPage = JSP_BIBTEXKEY;
					requPath = PAGE_BIBTEXKEY + "/" + URLEncoder.encode(requBibtexkey,"UTF-8");
					queryPageUserBibtexKey(c, requUser, requBibtexkey, itemCount, startBib);
				}				
				if (requPage.equals(PAGE_SEARCH)) {
					// handle /search
					/*
					 * get username for search and clean search expression
					 */
					if (search.matches(".*user:.*")) {
						// get username of first user
						int start = search.indexOf("user:");
						int ende  = search.indexOf(" ", start);
						if (ende == -1) {ende = search.length();} // if user:* is last word
						requUser = search.substring(start + "user:".length(), ende); // extract user
						// set warning, if more than one "user:" string contained in search 
						request.setAttribute(REQ_ATTRIB_WARNING, search.replaceFirst("user:[^\\s]*", "").matches(".*user:.*"));
						// replace all occurences of "user:*"
						search = search.replaceAll("user:[^\\s]*", "").trim();
						// remember search string for output in JSP input box
						request.setAttribute("search", search + " user:" + requUser);
						requPath = PAGE_SEARCH + "/" + URLEncoder.encode(search, "UTF-8") + " user:" + URLEncoder.encode(requUser, "UTF-8");
					} else {
						request.setAttribute("search", search);
						requPath = PAGE_SEARCH + "/" + URLEncoder.encode(search, "UTF-8");
					}
					forwPage = JSP_SEARCH;
					queryPageSearch (c, search, requUser, itemCount, startBook, startBib);
				}
				if (requPage.equals(PAGE_POPULAR)) {
					// handle /popular
					forwPage = JSP_POPULAR;
					requPath = PAGE_POPULAR;
					queryPagePopular (c);
					request.setAttribute(REQ_ATTRIB_ALL_BIB, "y"); // disable "next" Button
					request.setAttribute(REQ_ATTRIB_ALL_BOOKS, "y"); // disable "next" Button
				}
				if(requPage.equals(PAGE_BASKET)){
					// use always currUser, because every logged in user has access to basket page
					forwPage = JSP_BASKET;
					requPath = PAGE_BASKET;
					queryPageBasket (c, currUser);
					itemCount = 10000; // to ensure, that every entry from basket page is shown
				} 
				if (requPage.equals(PAGE_FRIEND)) {
					// handle /friend(s) (show all "friend" entries from users which have currUser as friend)
					forwPage = JSP_FRIEND;
					requPath = PAGE_FRIEND;
					queryPageFriend (c, currUser, itemCount, startBook, startBib);
				}
				if (requPage.equals(PAGE_FRIENDUSER)) {
					// handle /friend/USER and /friend/USER/TAG (show all "friend" entries from requUser, if he has currUser as friend)
					forwPage   = JSP_FRIENDUSER;
					requPath   = PAGE_FRIEND + "/" + URLEncoder.encode(requUser, "UTF-8");
					isUserPage = true;
					// user is friend or himself
					if (isFriendOf(c, currUser, requUser) || (requUser != null && requUser.equals(currUser))) {
						// user is friend
						// is a tag given?
						if (requTag == null) {
							queryPageUser (c, currUser, requUser, constants.SQL_CONST_GROUP_FRIENDS, bookmarkPostsPerPage, bibtexPostsPerPage, startBook, startBib);
						} else {
							queryPageUserTag (c, currUser, requUser, constants.SQL_CONST_GROUP_FRIENDS, requTag, itemCount, startBook, startBib);
							// set page
							requPath = PAGE_FRIEND + "/" + URLEncoder.encode(requUser, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8");
						}
					}
					// add group id to request (to get tags for this group)
					request.setAttribute("group", new Integer(constants.SQL_CONST_GROUP_FRIENDS));
				}
				if ((requPage.equals(PAGE_VIEWABLE) || requPage.equals(PAGE_VIEWABLETAG)) && currUser != null) {
					// handle /viewable/GROUP and /viewable/GROUP/TAG
					// if tag given, forward to viewabletag page
					if (requPage.equals(PAGE_VIEWABLETAG)) {
						requPath = PAGE_VIEWABLE + "/" + URLEncoder.encode(requGroup, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8");
						forwPage = JSP_VIEWABLETAG;
					} else {
						requPath = PAGE_VIEWABLE + "/" + URLEncoder.encode(requGroup, "UTF-8");
						forwPage = JSP_VIEWABLE;
					}
					// handle special groups public, private, friends
					int group = constants.SQL_CONST_GROUP_PUBLIC;
					if (requGroup.equals("public") || requGroup.equals("private") || requGroup.equals("friends")) {
						// show users own bookmarks, which are private, public or for friends
						if (requGroup.equals("private")) {
							group = constants.SQL_CONST_GROUP_PRIVATE;
						} else if (requGroup.equals("friends")) {
							group = constants.SQL_CONST_GROUP_FRIENDS;
						}
						requUser   = currUser; // so that we can set book.user = requUser
						isUserPage = true;
						// did user request a tag?
						if (requPage.equals(PAGE_VIEWABLETAG)) {
							queryPageUserTag (c, currUser, currUser, group, requTag, itemCount, startBook, startBib);
						} else {
							queryPageUser (c, currUser, currUser, group, bookmarkPostsPerPage, bibtexPostsPerPage, startBook, startBib);
						}
						// add group id to request (to get tags for this group)
						request.setAttribute("group", new Integer(group));
					} else {
						// is user in this group?
						//if (group != NOT_EXISTING_GROUP) {
						if (user.getGroups().contains(requGroup)) {
							group = getGroupForUser(c, requGroup, currUser); // TODO: test this
							// did user request a tag?
							if (requPage.equals(PAGE_VIEWABLETAG)) {
								queryPageTag (c, requTag, group, itemCount, startBook, startBib, request.getParameter("order"));
							} else {
								queryPageViewable (c, group, itemCount, startBook, startBib);
							}
							// add group id to request (to get tags for this group) 
							request.setAttribute("group", new Integer(group));
						} // user is not in this group or group does not exist ---> show nothing

					}
				} // PAGE_VIEWABLE and PAGE_VIEWABLETAG

				if (requPage.equals(PAGE_GROUP) || requPage.equals(PAGE_GROUPTAG)) {
					// handle /group/GROUP and /group/GROUP/TAG (aggregation over users of group)
					if (requPage.equals(PAGE_GROUPTAG)) {
						requPath = PAGE_GROUP + "/" + URLEncoder.encode(requGroup, "UTF-8") + "/" + URLEncoder.encode(requTag, "UTF-8");
						forwPage = JSP_GROUPTAG;
					} else {
						requPath = PAGE_GROUP + "/" + URLEncoder.encode(requGroup, "UTF-8");
						forwPage = JSP_GROUP;
					}
					// if user wants to see his own groups ---> send redirect to correct page
					if (requGroup.equals("public") || requGroup.equals("private") || requGroup.equals("friends")) {
						if (requPage.equals(PAGE_GROUPTAG)) {
							forwPage = "/" + PAGE_VIEWABLE + "/" + requGroup + "/" + URLEncoder.encode(requTag, "UTF-8");
						} else { 
							forwPage = "/" + PAGE_VIEWABLE + "/" + requGroup;
						}
						if (c.conn != null) {try {c.conn.close(); } catch (SQLException e) {} c.conn = null; }
						response.sendRedirect(forwPage);
						return;
					}
					int group = getGroupForUser(c, requGroup, null);
					if (group != NOT_EXISTING_GROUP) {
						// prepare Query with or without tag 
						if (requPage.equals(PAGE_GROUPTAG)) {
							queryPageGroupTag (c, requTag, currUser, group, itemCount, startBook, startBib);
						} else {
							queryPageGroup (c, currUser, group, itemCount, startBook, startBib);
						}
						request.setAttribute("group", new Integer(group));
					}
				} // PAGE_GROUP and PAGE_GROUPTAG
			} // PAGE_HOME

			// initialize variables
			ResourceBean bean = new ResourceBean();
			int contentID;
			int count;



			/* *************************************************************************
			 * get Bookmark rows
			 * *************************************************************************/
			if (c.bookStmtP    != null && !getOnlyBibtex) { 

				rst           = c.bookStmtP.executeQuery();
				Bookmark book = new Bookmark();
				count         = 1;
				contentID     = Bookmark.UNDEFINED_CONTENT_ID;

				while (rst != null && rst.next()) {
					if (contentID == rst.getInt("content_id")) {
						// the same content_id as before --> just add tags
						book.addTag(rst.getString("tag_name"));
					} else {
						// content_id has changed
						if (! rst.isFirst()) {
							// not the first row
							bean.addBookmark(book);
							count++;
							book = new Bookmark();
						} else {
							// remember, that we have at least one row: set parameter for next/prev navigation
							request.setAttribute(REQ_ATTRIB_HAVE_BOOKS, "y");
						}
						// populate bookmark
						book.setTitle(rst.getString("book_description"));
						book.setExtended(rst.getString("book_extended"));
						book.setDate(rst.getTimestamp("date"));
						book.setUrl(rst.getString("book_url"));
						book.setRating(rst.getInt("rating"));
						// set URL counter
						if (! requPage.equals(PAGE_URL)) {
							book.setCtr(rst.getInt("book_url_ctr"));
						}
						// if just one user on page: set group and user name
						if (isUserPage) {
							book.setUser(requUser);
							book.setGroup(rst.getString("group_name"));
						} else {
							// otherwise: set user_name from result set
							book.setUser(rst.getString("user_name"));
						}
						// on group aggregation pages we do also group checks, so view group
						if (requPage.equals(PAGE_GROUP) || requPage.equals(PAGE_GROUPTAG)) {
							book.setGroup(rst.getString("group_name"));
						}
						book.addTag(rst.getString("tag_name"));
						contentID = rst.getInt("content_id");
					}
					if (rst.isLast() && count < bookmarkPostsPerPage) {
						bean.addBookmark(book);
						// fetched less than itemCount rows --> no more rows in Query 
						request.setAttribute(REQ_ATTRIB_ALL_BOOKS, "y");
					}
					if (rst.isLast()) {
						// set title (=URL) for /url page
						bean.setTitle(rst.getString("book_url"));						
					}
				}
			}
			/* ************************************************************************************************************ 		    
			 BIBTEX PART
			 ************************************************************************************************************ */		    
			if (c.bibStmtP     != null && !isXmlFeed && !isBookBibFeed && !isRssFeed && !isBatchEditUrl && !isNRLFeed) { 

				rst        = c.bibStmtP.executeQuery();
				Bibtex bib = new Bibtex();
				contentID  = Bibtex.UNDEFINED_CONTENT_ID;
				count      = 1;

				while (rst != null && rst.next()) {
					if (contentID == rst.getInt("content_id")) {
						// the same content_id as before --> just add tags
						bib.addTag(rst.getString("tag_name"));
					} else {
						// content_id has changed
						if (! rst.isFirst()) {
							// not the first row
							// set tags
							// add bibtex to bean
							bean.addBibtex(bib);
							count++;							
							// new bibtex
							bib = new Bibtex();
						} else {
							// remember, that we have at least one row: set parameter for next/prev navigation
							request.setAttribute(REQ_ATTRIB_HAVE_BIB, "y");
						}
						// new list for tags
						// populate bibtex
						bib.setTitle(rst.getString("title"));
						bib.setAuthor(rst.getString("author"));
						bib.setEditor(rst.getString("editor"));
						bib.setJournal(rst.getString("journal"));
						bib.setBooktitle(rst.getString("booktitle"));
						bib.setYear(rst.getString("year"));
						bib.setEntrytype(rst.getString("entrytype"));
						bib.setUrl(rst.getString("url"));
						bib.setDescription(rst.getString("description"));
						bib.setRating(rst.getInt("rating"));

						// set counter
						if (! requPage.equals(PAGE_BIBTEX)) {
							bib.setCtr(rst.getInt("ctr"));
						}						
						// if just one user on page: set group and user name
						if (isUserPage) {
							bib.setUser(requUser);
							bib.setGroup(rst.getString("group_name"));
						} else {
							// otherwise: set user_name from result set
							bib.setUser(rst.getString("user_name"));
						}
						// on group aggregation pages we do also group checks, so view group
						if (requPage.equals(PAGE_GROUP) || requPage.equals(PAGE_GROUPTAG)) {
							bib.setGroup(rst.getString("group_name"));
						}
						bib.setBibtexKey(rst.getString("bibtexKey"));
						bib.setVolume(rst.getString("volume"));
						bib.setNumber(rst.getString("number"));
						bib.setPages(rst.getString("pages"));
						bib.setType(rst.getString("type"));
						bib.setBibtexAbstract(rst.getString("bibtexAbstract"));
						bib.setEdition(rst.getString("edition"));
						bib.setChapter(rst.getString("chapter"));
						bib.setMonth(rst.getString("month"));
						bib.setDay(rst.getString("day"));
						bib.setInstitution(rst.getString("institution"));
						bib.setOrganization(rst.getString("organization"));
						bib.setPublisher(rst.getString("publisher"));
						bib.setAddress(rst.getString("address"));
						bib.setSchool(rst.getString("school"));
						bib.setSeries(rst.getString("series"));
						bib.setAnnote(rst.getString("annote"));
						bib.setNote(rst.getString("note"));
						bib.setMisc(rst.getString("misc"));
						bib.setKey(rst.getString("bKey"));
						bib.setHowpublished(rst.getString("howPublished"));
						bib.setCrossref(rst.getString("crossref"));

						if (requPage.equals(PAGE_USERBIBTEX)) {
							bib.setDocHash(rst.getString("hash"));
							bib.setDocName(rst.getString("name"));
							bib.setPrivnote(rst.getString("privnote"));
						}

						// To link the document on the user page we need some additional data
						if (requPage.equals(PAGE_USER) && showPdf) {
							bib.setDocHash(rst.getString("hash"));
							bib.setDocName(rst.getString("name"));
						}


						bib.setDate(rst.getTimestamp("date"));
						//bib.setContentID(rst.getInt("content_id"));
						// add tag to bibtex
						bib.addTag(rst.getString("tag_name"));

						// remember contentID
						contentID = rst.getInt("content_id");
					}
					if (rst.isLast() && count < bibtexPostsPerPage) {
						bean.addBibtex(bib);
						// fetched less than itemCount rows --> no more rows in Query 
						request.setAttribute(REQ_ATTRIB_ALL_BIB, "y");
					}
					// set title for /bibtex page
					if (rst.isLast()) {
						bean.setTitle(rst.getString("title"));
					}
				}

			}

			// get total counts of bookmark / bibtex
			if (c.bookTCStmtP  != null) { 
				rst = c.bookTCStmtP.executeQuery();
				if (rst.next()) {
					bean.setBookmarkTotalCount(rst.getInt(1));
				}
			}
			if (c.bibTCStmtP   != null) { 
				rst = c.bibTCStmtP.executeQuery();
				if (rst.next()) {
					bean.setBibtexTotalCount(rst.getInt(1));
				}
			}


			// empty URL? allow user to enter this as a new bookmark
//			if (currUser != null && forwPage == JSP_URL 
//					&& (bean.getBookmarkTotalCount() == 0 && bean.getBookmarkCount() == 0)) {
//				/*
//				 * TODO: how to implement? Immediate redirect or only a small snippet which
//				 * shows a link to edit_bookmark with appropriate URL?
//			     * Problem: how to get URL (HERE we get only a hash!)
//				 */ 
//
//				String redirectURL = (String)request.getSession(true).getAttribute("url");
//				request.getSession(true).removeAttribute("url");
//
//				response.sendRedirect("/ShowBookmarkEntry?url=" + URLEncoder.encode(redirectURL, "UTF-8"));
//
//			}
			
			
			/*
			 * remember path
			 */
			if (startBib != 0 || startBook != 0) {
				// append page-navigation information
				requPath = requPath + "?startBib=" + startBib + "&startBook=" + startBook;
			}
			request.setAttribute("requPath", requPath);			

			request.setAttribute(REQ_ATTRIB_IS_USERPAGE, isUserPage); // only posts from one user on page?
			request.setAttribute(REQ_ATTRIB_BEAN, bean);		// put bean (Model) into request

			if (isRssFeed) forwPage = "RSSFeed"; 				// RSS Feed handling
			if (isXmlFeed) forwPage = "XMLOutput"; 				// XML Feed handling
			if (isBookBibFeed) forwPage = "BibBookOutput.jsp";	// BibTeX for bookmarks handling
			if (isNRLFeed) forwPage = "NRLOutput.jsp"; 			// NRL Feed handling
			if (isBibFeed) forwPage = "BIBOutput.jsp"; 			// Bib Feed Handling 
			if (isPublFeed) forwPage = "PublOutput.jsp";		// Publ Html Feed Handling
			if (isPublKDEFeed) forwPage = "PublKDEOutput.jsp";  // Publ KDE Html Feed Handling
			if (isPublCSVFeed) forwPage = "PublCSVOutput.jsp";  // Publ CSV Feed Handling
			if (isPublRSSNepoFeed) forwPage = "PublRSSNepomukFeed.jsp";  // Publ NepoHtml Feed Handling
			if (isPublRssFeed) forwPage = "PublRSSFeed.jsp";	// Publ RSS Feed Handling
			if (isPublApaRss) forwPage = "PublApaRSSFeed.jsp";	// Publ RSS Feed according APA 5th
			if (isBurstFeed) forwPage = "BuRSToutput.jsp";		// BuRST Feed Handling
			if (isEndFeed) forwPage = "EndNoteOutput.jsp"; 		// Endnote Feed Handling
			if (isSWRCFeed) forwPage = "SWRCoutput.jsp"; 		// SWRC Feed Handling
			if (isLayoutFeed) forwPage = "LayoutHandler"; 	// JabRef Layout Feed Handling 
			if (isBatchEditBib || isBatchEditUrl) forwPage = JSP_BATCHEDIT;          // for batch editing tags
			if (isjsonFeed) forwPage = "JSONOutput.jsp";		// Publ Html Feed Handling

			/*
			 * pick/unpick action handling
			 */ 
			if (currUser != null && "pick".equals(requAction)) { 
				Iterator<Bibtex> it = bean.getBibtex().iterator();
				while (it.hasNext()) {
					Bibtex bib = it.next();
					DBPickManager.pickEntryForUser(bib.getHash(), bib.getUser(), currUser);
				}
				// sets the current pick count for the user bean
				user.setPostsInBasket(DBPickManager.getPickCount(currUser));
			} else if(currUser != null && "unpick".equals(requAction)) {
				Iterator<Bibtex> it = bean.getBibtex().iterator();
				while (it.hasNext()) {
					Bibtex bib = it.next();
					DBPickManager.unPickEntryForUser(bib.getHash(), bib.getUser(), currUser);
				}
				// sets the current pick count for the user bean
				user.setPostsInBasket(DBPickManager.getPickCount(currUser));
			}

			// forward to JSP (View)
			getServletContext().getRequestDispatcher("/" + forwPage).forward(request, response);


		} catch (SQLException e) {
			log.fatal(e);
			/*
			 * special handling for interrupted queries
			 */
			if (e.getErrorCode() == MySqlError.ER_QUERY_INTERRUPTED) {
				/*
				 * TODO: improve this special error handling!
				 */
				request.setAttribute("error", "timeout");
				getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
				return;
			}
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
			//response.sendRedirect("/errors/databaseError.jsp");
		}
		finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst            != null) {try {rst.close();				    } catch (SQLException e) {}	rst = null;			    }
			if (c.bibStmtP     != null) {try {c.bibStmtP.close();			} catch (SQLException e) {}	c.bibStmtP = null;		}
			if (c.bookStmtP    != null) {try {c.bookStmtP.close();			} catch (SQLException e) {}	c.bookStmtP = null;		}
			if (c.bibTCStmtP   != null) {try {c.bibTCStmtP.close();			} catch (SQLException e) {}	c.bibTCStmtP = null;	}
			if (c.bookTCStmtP  != null) {try {c.bookTCStmtP.close();		} catch (SQLException e) {}	c.bookTCStmtP = null;	}
			if (c.conn         != null) {try {c.conn.close();				} catch (SQLException e) {}	c.conn = null;			}
		}
	}

	/*
	 * checks, if a given user is in the given group
	 *   returns groupid, if user is in group, otherwise -1
	 * if no user is given, checks only, if group exists
	 *   returns groupid, if group exists, otherwise -1
	 */
	private static int NOT_EXISTING_GROUP = -1;
	private int getGroupForUser(DBContext c, String requGroup, String currUser) throws SQLException {
		int groupid = NOT_EXISTING_GROUP;
		ResultSet rst;
		if (currUser == null) {
			// get id of group
			c.bookStmtP = c.conn.prepareStatement("SELECT g.group FROM groupids g WHERE g.group_name = ?");
		} else {
			c.bookStmtP = c.conn.prepareStatement("SELECT g.group FROM groupids i, groups g WHERE i.group_name = ? AND g.user_name = ? AND g.group = i.group");
			c.bookStmtP.setString(2, currUser);			
		}
		c.bookStmtP.setString(1, requGroup);
		rst = c.bookStmtP.executeQuery();
		if (rst.next()) {
			// group found --> return groupid
			groupid = rst.getInt("group");
		}
		c.bookStmtP.close();
		c.bookStmtP = null;		
		return groupid;
	}

	/** Check, if the currUser is a friend of the requUser, i.e., currUser appears in requUser's friends list
	 * 
	 * @param c context which holds database statements
	 * @param currUser name of current user
	 * @param requUser name of requested user
	 * @return <code>true</code> if currUser is a friend of requUser
	 * @throws SQLException
	 */
	private boolean isFriendOf(DBContext c, String currUser, String requUser) throws SQLException {
		c.bookStmtP = c.conn.prepareStatement("SELECT user_name FROM friends WHERE user_name = ? AND f_user_name = ?");
		c.bookStmtP.setString(1, requUser);
		c.bookStmtP.setString(2, currUser);
		boolean isFriend = c.bookStmtP.executeQuery().next(); // if we get a row, currUser is friend of requUser
		c.bookStmtP.close();
		c.bookStmtP = null;			
		return isFriend;
	}

	/**
	 * Holds database statements which are prepared in queryPage* methods and used to retrieve bibtex and bookmark lists. 
	 *
	 */
	private class DBContext {
		public Connection conn = null;
		public PreparedStatement bookStmtP = null;
		public PreparedStatement bibStmtP = null;
		public PreparedStatement bookTCStmtP = null;  // total count of bookmarks for query
		public PreparedStatement bibTCStmtP = null;   // total count of bibtex for query
	}

	/**
	 * return appropriate select query string for different tables
	 */
	public static String getBibtexSelect (String table) {	
		String[] columns = {"address","annote","booktitle","chapter","crossref","edition",
				"howpublished","institution","journal","bkey","month","note","number","organization",
				"pages","publisher","school","series","type","volume","day","url", 
				"content_id", "description", "bibtexKey", "misc", "bibtexAbstract", "user_name", "date",
				"title","author", "editor", "year", "entrytype", "rating"};
		StringBuffer select = new StringBuffer();
		for (String col:columns) {
			select.append(table + "." + col + ",");
		}
		select.deleteCharAt(select.length()-1);
		return select.toString();		
	}

	/** PAGE_BASKET
	 * 
	 * This method prepares a query which retrieves all publications the user 
	 * has in his basket.
	 * The result is shown on the page /basket. 
	 * Since every user can only see his OWN basket, we use currUser as
	 * restriction for the user name and not requUser (which is not set, anyway).
	 * 
	 * 
	 * @param c context which holds database statements.
	 * @param currUser name of user whose basket we want to retrieve.
	 * @throws SQLException
	 */
	private void queryPageBasket (DBContext c, String currUser) throws SQLException {
		c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect ("b") + ",t.tag_name,h.ctr "
				+ "  FROM (SELECT content_id, date FROM collector WHERE user_name = ?) AS c"		 
				+ "  LEFT OUTER JOIN tas AS t ON c.content_id = t.content_id, bibtex b, bibhash h"
				+ "  WHERE b.content_id = c.content_id "
				+ "    AND h.type = " + Bibtex.INTER_HASH
				+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
				+ "  ORDER BY c.date DESC, content_id DESC");
		c.bibStmtP.setString(1, currUser);	
	}


	/** PAGE_HOME
	 * 
	 * This method prepares queries which retrieve all bookmarks and publications 
	 * for the home page of BibSonomy.
	 * These are typically the X last posted entries.
	 * Only public posts are shown.
	 * 
	 * @param c context which holds database statements
	 * @throws SQLException
	 */
	private void queryPageHome (DBContext c, String filter) throws SQLException {

		String query = "SELECT bbb.content_id,bbb.book_url_hash,bbb.book_description,bbb.book_extended,bbb.date,bbb.rating,"
			+ " bbb.user_name,u.book_url,u.book_url_ctr,tt.tag_name "
			+ " FROM ("
			+ "		SELECT"
			+ "			bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,"
			+ "			bb.user_name, bb.rating"
			+ "		FROM ("
			+ "			SELECT"
			+ "				b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,"
			+ "				b.user_name, b.rating"
			+ "	 		FROM bookmark b"
			+ "	 		WHERE b.group = " + constants.SQL_CONST_GROUP_PUBLIC
			+ "	 		ORDER BY date DESC"
			+ "	 		LIMIT 100"
			+ "		) AS bb"
			+ "		LEFT JOIN tas AS t ON bb.content_id=t.content_id"
			+ "		LEFT JOIN spammer_tags AS s ON s.tag_name=t.tag_name" 
			+ "		GROUP BY bb.content_id"
			+ "		HAVING COUNT(s.tag_name) = 0"
			+ "		ORDER BY date DESC"
			+ "		LIMIT 20) AS bbb"
			+ "	LEFT JOIN tas AS tt USING (content_id) " 
			+ " JOIN urls u USING (book_url_hash)"
			+ "	ORDER BY bbb.date DESC, bbb.content_id DESC";

		String unfilteredQuery = "SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.user_name,bb.rating,bb.book_url,bb.book_url_ctr,t.tag_name"
			+ "  FROM "
			+ "    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,b.user_name,b.rating,u.book_url,u.book_url_ctr" 
			+ "       FROM bookmark b, urls u" 
			+ "       WHERE b.group = " + constants.SQL_CONST_GROUP_PUBLIC  
			+ "         AND u.book_url_hash = b.book_url_hash" 
			+ "       ORDER BY date DESC" 
			+ "       LIMIT 20) AS bb" 
			+ "  LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id"
			+ "  ORDER BY bb.date DESC, bb.content_id DESC";
		/*
		 * choose query
		 */
		if ("no".equals(filter)) {
			c.bookStmtP = c.conn.prepareStatement(unfilteredQuery);
		} else {
			c.bookStmtP = c.conn.prepareStatement(query);
		}

		String bibQuery = "SELECT " + getBibtexSelect ("b") + ",t.tag_name,b.ctr "
		+ "  FROM "
		+ "    (SELECT " + getBibtexSelect ("b") + ", h.ctr"
		+ "      FROM bibtex b FORCE INDEX (group_date_content_id_idx), bibhash h"
		+ "      WHERE `group` = " + constants.SQL_CONST_GROUP_PUBLIC
		+ "        AND h.type = " + Bibtex.INTER_HASH 
		+ "        AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
		+ "		ORDER BY date DESC"
		+ "      LIMIT 15) AS b"
		+ "  LEFT OUTER JOIN tas t ON b.content_id=t.content_id"
		+ "  ORDER BY b.date DESC, b.content_id DESC";
		c.bibStmtP  = c.conn.prepareStatement(bibQuery);
	}

	/** PAGE_POPULAR
	 * 
	 * This method prepares queries which retrieve all bookmarks and publications 
	 * for the /popular page of BibSonomy.
	 * The lists are retrieved from two separate temporary tables which are filled 
	 * by an external script.
	 * 
	 * @param c context which holds database statements
	 * @throws SQLException
	 */
	private void queryPagePopular (DBContext c) throws SQLException {
		String query = "SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.user_name,bb.rating,u.book_url,u.book_url_ctr,t.tag_name"
			+ "  FROM temp_bookmark bb "
			+ "    LEFT OUTER JOIN tas AS t ON t.content_id=bb.content_id, urls u"
			+ "    WHERE u.book_url_hash = bb.book_url_hash"
			+ "  ORDER BY bb.rank";
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bibStmtP  = c.conn.prepareStatement("SELECT " + getBibtexSelect ("b") + ", h.ctr, t.tag_name"
				+ "  FROM temp_bibtex b"
				+ "    LEFT OUTER JOIN tas t ON b.content_id=t.content_id, bibhash h"
				+ "    WHERE b.simhash" + Bibtex.INTER_HASH + " = h.hash"
				+ "      AND h.type = " + Bibtex.INTER_HASH
				+ "  ORDER BY b.rank");
	}


	/** PAGE_USER (also used for PAGE_FRIENDUSER and PAGE_VIEWABLE) /user/MaxMustermann
	 *  
	 *  This method prepares queries which retrieve all bookmarks and publications 
	 *  for a given user name (requUser). Additionally the group to be shown can be
	 *  restricted. The queries are built in a way, that not only public posts are 
	 *  retrieved, but also friends or private or other groups, depending upon if 
	 *  currUser us allowed to see them.
	 *  
	 * @param c context which holds database statements
	 * @param currUser the user who wants to see the results
	 * @param requUser the name of the user which currUser requested to see
	 * @param requGroup the group to which the entries should belong to (default: -1,
	 *                  which means all groups which the user is allowed to see) 
	 * @param itemCount number of entries to retrieve
	 * @param startBook with which bookmark entry to start
	 * @param startBib with which bibtex entry to start
	 * @throws SQLException
	 */
	private void queryPageUser (DBContext c, String currUser, String requUser, int requGroup, int bookmarkPostsPerPage, int bibtexPostsPerPage, int startBook, int startBib) throws SQLException {

		String groupWhereQuery;
		if (requGroup == -1) {
			// check, which group the user may see
			groupWhereQuery = getQueryForGroups (c.conn, currUser, requUser, "b");
		} else {
			// we want to see a certain group (used for /friend/USER)
			groupWhereQuery = " AND b.group = " + requGroup;
		}

		// bookmark query 
		String query = "SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.rating,bb.book_url,bb.book_url_ctr,t.tag_name,g.group_name"
			+ "  FROM" 
			+ "    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,b.rating,u.book_url,u.book_url_ctr,b.group" 
			+ "       FROM bookmark b, urls u" 
			+ "       WHERE u.book_url_hash=b.book_url_hash "
			+ "         AND b.user_name = ? "
			+ groupWhereQuery
			+ "       ORDER BY date DESC" 
			+ "       LIMIT ? OFFSET ?) AS bb" 
			+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"
			+ "    WHERE bb.group = g.group "
			+ "    ORDER BY bb.date DESC, bb.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setString(1, requUser);
		c.bookStmtP.setInt(2, bookmarkPostsPerPage);
		c.bookStmtP.setInt(3, startBook);

		c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect("bb") + ",t.tag_name,g.group_name, bb.ctr"
				+ "  FROM" 
				+ "    (SELECT " + getBibtexSelect ("b") + ",b.group, h.ctr" 
				+ "       FROM bibtex b, bibhash h" 
				+ "       WHERE b.simhash" + Bibtex.INTER_HASH + " = h.hash "
				+ "         AND h.type = " + Bibtex.INTER_HASH
				+ "         AND b.user_name = ? "
				+ groupWhereQuery
				+ "       ORDER BY date DESC" 
				+ "       LIMIT ? OFFSET ?) AS bb" 
				+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"
				+ "    WHERE bb.group = g.group "
				+ "    ORDER BY bb.date DESC, bb.content_id DESC");

		c.bibStmtP.setString(1, requUser);
		c.bibStmtP.setInt(2, bibtexPostsPerPage);
		c.bibStmtP.setInt(3, startBib);

		// counts
		c.bookTCStmtP = c.conn.prepareStatement("SELECT count(*) from bookmark b WHERE b.user_name = ? " + groupWhereQuery);
		c.bookTCStmtP.setString(1, requUser);

		c.bibTCStmtP = c.conn.prepareStatement("SELECT count(*) from bibtex b WHERE b.user_name = ? " + groupWhereQuery);
		c.bibTCStmtP.setString(1, requUser);


	}

	/** Shows all bibtex posts of the user, which have a document attached (PDF, PS, DJVU)
	 * @param c
	 * @param currUser
	 * @throws SQLException
	 */
	private void queryPageUserPDF (DBContext c, String currUser) throws SQLException {
		// bibtex query
		// Just checking if the user wants to see his private document 
		// and if he really is the user he requested
		c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect("bb") + ",t.tag_name,g.group_name, bb.ctr, bb.hash, bb.name"
				+ "  FROM" 
				+ "    (SELECT " + getBibtexSelect ("b") + ",b.group, h.ctr, d.hash, d.name" 
				+ "       FROM bibtex b, bibhash h, document d" 
				+ "       WHERE b.simhash" + Bibtex.INTER_HASH + " = h.hash "
				+ "         AND h.type = " + Bibtex.INTER_HASH
				+ "         AND b.user_name = ? "
				+ "			AND d.content_id = b.content_id"
				+ "       ORDER BY d.date DESC) AS bb" 
				+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"
				+ "    WHERE bb.group = g.group"
				+ "    ORDER BY bb.date DESC, bb.content_id DESC");

		c.bibStmtP.setString(1, currUser);

//		c.bibTCStmtP = c.conn.prepareStatement("SELECT count(*) from document d WHERE d.user_name = ? AND content_id != 0");
//		c.bibTCStmtP.setString(1, requUser);

	}

	/** PAGE_USERTAG /user/MaxMustermann/EinTag
	 * 
	 * 	This method prepares queries which retrieve all bookmarks and publications 
	 *  for a given user name (requUser) and given tags. 
	 *  Additionally the group to be shown can be restricted. The queries are built 
	 *  in a way, that not only public posts are retrieved, but also friends or 
	 *  private or other groups, depending upon if currUser us allowed to see them.
	 * 
	 * 
	 * @param c context which holds database statements
	 * @param currUser the user who wants to see the results
	 * @param requUser the name of the user which currUser requested to see
	 * @param requGroup the group to which the entries should belong to (default: -1,
	 *                  which means all groups which the user is allowed to see) 
	 * @param requTag the string of tags the user requests to see as given by the user.
	 *                The string is parsed with the helps of SplittedTags and used to
	 *                construct the JOIN for the tag selection.
	 * @param itemCount number of entries to retrieve
	 * @param startBook with which bookmark entry to start
	 * @param startBib with which bibtex entry to start
	 * @throws SQLException
	 */
	private void queryPageUserTag (DBContext c, String currUser, String requUser, int requGroup, String requTag, int itemCount, int startBook, int startBib) throws SQLException {
		SplittedTags tags      = new SplittedTags(requTag, "", true);
		String tagWhereQuery   = tags.getQuery();
		String groupWhereQuery;
		if (requGroup == -1) {
			// check, which group the user may see
			groupWhereQuery = getQueryForGroups (c.conn, currUser, requUser, "t1");
		} else {
			// we want to see a certain group (used for /friend/USER
			groupWhereQuery = " AND t1.group = " + requGroup;
		}
		int queryParamPos      = 1;
		String tag;
		String query = "SELECT a.content_id,t.tag_name,a.book_url,a.book_url_hash,a.book_description,a.book_extended,a.date,a.rating,a.book_url_ctr,g.group_name"
			+ "  FROM " 
			+ "    (SELECT b.content_id,u.book_url,b.book_url_hash,b.book_description,b.book_extended,b.date,b.rating,u.book_url_ctr,b.group"
			+ "      FROM bookmark b, urls u, "
			+ tagWhereQuery + " "
			+ groupWhereQuery
			+ "        AND t1.content_type=" + Bookmark.CONTENT_TYPE
			+ "        AND t1.user_name = ? " 
			+ "        AND b.book_url_hash=u.book_url_hash"
			+ "        AND t1.content_id=b.content_id"
			+ "      ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS a "
			+ "  LEFT OUTER JOIN tas AS t ON a.content_id=t.content_id, groupids AS g"
			+ "  WHERE a.group = g.group "
			+ "  ORDER BY a.date DESC, a.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);
		String bibQuery = "SELECT " + getBibtexSelect ("b") + ",t.tag_name, g.group_name, h.ctr  "
		+ "  FROM bibtex b, tas t, groupids g, bibhash h,"
		+ "    (SELECT t1.content_id FROM "
		+ tagWhereQuery + " "
		+ groupWhereQuery
		+ "        AND t1.content_type = " + Bibtex.CONTENT_TYPE
		+ "        AND t1.user_name = ?"
		+ "      ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS tt"
		+ "  WHERE tt.content_id=b.content_id"
		+ "    AND tt.content_id=t.content_id"
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND g.group = b.group"
		+ "  ORDER BY b.date DESC, b.content_id DESC;";
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		Iterator it = tags.iterator();
		while (it.hasNext()) {
			tag = (String)it.next();
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
		}
		c.bookStmtP.setString(queryParamPos, requUser);
		c.bibStmtP.setString(queryParamPos, requUser);
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, itemCount);
		c.bibStmtP.setInt(queryParamPos, itemCount);
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, startBook);
		c.bibStmtP.setInt(queryParamPos, startBib);
	}

	/** PAGE_CONCEPT
	 * Shows the page /concept/tag/TAG(S)
	 * 
	 * This method prepares queries which retrieve all bookmarks and publications
	 * for given tags. These tags are interpreted as supertags and the queries are built 
	 * in a way that they results reflect the semantics of 
	 * http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199 p. 91, formular (4).
	 * At the moment there are only public recources are retrieved.
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param requTag the string of supertags given by the user.
	 *                The string is parsed with the helps of SplittedEntireConcepts and used to
	 *                construct the JOIN for the tag selection.
	 * @param itemCount number of posts to retrieve
	 * @param startBook with which bookmark post to start 
	 * @param startBib with which bibtex post to start
	 * @throws SQLException
	 */
	private void queryPageConcept(DBContext c, String currUser, String requTag, int itemCount, int startBook, int startBib) throws SQLException {
		SplittedEntireConcepts tags = new SplittedEntireConcepts(requTag, "", true);		

		/*
		 * FIXME: special handling for dblp tag
		 */
		if (tags.contains("dblp")) {
			queryPageTag(c, requTag, constants.SQL_CONST_GROUP_PUBLIC, itemCount, startBook, startBib, "date");
			return;
		}
		
		int queryParamPos      		= 1;
		// bookmarks
		String conceptBookmarkQuery = tags.getQuery(Bookmark.CONTENT_TYPE);
		String bookQuery = "SELECT "
			+ "				a.content_id,t.tag_name,a.book_url,a.book_url_hash,a.book_description, "
			+ "				a.book_extended,a.date,a.book_url_ctr,a.user_name,a.rating "
			+ "			FROM "
			+ "			( "
			+ "				SELECT 	" 
			+ "					b.content_id,u.book_url,b.book_url_hash,b.book_description, "
			+ "					b.book_extended,b.date,u.book_url_ctr,b.group, b.user_name, b.rating "
			+ conceptBookmarkQuery 
			+ " 			ORDER BY t.date DESC "
			+ "				LIMIT ? OFFSET ? "
			+ "			) AS a "
			+ "			JOIN tas t ON a.content_id = t.content_id "
			+ " 		ORDER BY a.date DESC";			
		c.bookStmtP = c.conn.prepareStatement(bookQuery);

		String conceptBibtexQuery = tags.getQuery(Bibtex.CONTENT_TYPE);
		String bibQuery = "	SELECT " + getBibtexSelect("a") + ", t.tag_name, a.ctr "
		+ "			FROM ( "  
		+ "				SELECT " + getBibtexSelect("b") + ", h.ctr, b.simhash1 "
		+ conceptBibtexQuery
		+ "				ORDER BY t.date DESC "
		+ "				LIMIT ? OFFSET ? "
		+ "			) AS a " 	
		+ "			JOIN tas t ON a.content_id = t.content_id "
		+ "			ORDER BY a.date DESC";
		c.bibStmtP = c.conn.prepareStatement(bibQuery);

		// set tags
		Iterator iter = tags.iterator();
		while (iter.hasNext()) {
			String tag = (String) iter.next();			
			c.bibStmtP.setString(queryParamPos, tag);			
			c.bookStmtP.setString(queryParamPos++, tag);
		}

		// set tags
		Iterator iter2 = tags.iterator();
		while (iter2.hasNext()) {
			String tag = (String) iter2.next();
			for (int i = 0; i < 3; i++) {				
				c.bibStmtP.setString(queryParamPos, tag);
				c.bookStmtP.setString(queryParamPos++, tag);
			}			
		}

		c.bibStmtP.setInt(queryParamPos, itemCount);
		c.bookStmtP.setInt(queryParamPos++, itemCount);

		c.bibStmtP.setInt(queryParamPos, startBib);
		c.bookStmtP.setInt(queryParamPos, startBook);	
		
		/*
		 * set query timeout to stop long queries (e.g. for dblp)
		 */
		c.bibStmtP.setQueryTimeout(10); // 10 seconds
		c.bookStmtP.setQueryTimeout(10); // 10 seconds
	}

	/** PAGE_CONCEPT
	 * Shows the page /concept/user/MaxMustermann/EinTag
	 * 
	 * This method prepares queries which retrieve all bookmarks and publications
	 * for a given user name (requUser) and given tags. The tags are interpreted 
	 * as supertags and the queries are built in a way that they results reflect
	 * the semantics of http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199
	 * p. 91, formular (4).
	 * Additionally the group to be shown can be restricted. The queries are built 
	 * in a way, that not only public posts are retrieved, but also friends or 
	 * private or other groups, depending upon if currUser us allowed to see them.
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param currUser the user who wants to see the results 
	 * @param requUser the name of the user which currUser requested to see
	 * @param requTag the string of tags the user requests to see as given by the user.
	 *                The string is parsed with the helps of SplittedConcepts and used to
	 *                construct the JOIN for the tag selection.
	 * @param itemCount number of posts to retrieve
	 * @param startBook with which bookmark post to start 
	 * @param startBib with which bibtex post to start
	 * @throws SQLException
	 */
	private void queryPageUserConcept (DBContext c, String currUser, String requUser, String requTag, int itemCount, int startBook, int startBib) throws SQLException {
		SplittedConcepts tags  = new SplittedConcepts(requTag, "", true);
		String tagWhereQuery   = tags.getQuery();
		String groupWhereQuery = getQueryForGroups (c.conn, currUser, requUser, "b");
		int queryParamPos      = 1;
		String tag;
		String query = "SELECT a.content_id,t.tag_name,a.book_url,a.book_url_hash,a.book_description,a.book_extended,a.date,a.rating,a.book_url_ctr,gi.group_name"
			+ "  FROM " 
			+ "    (SELECT DISTINCT b.content_id,u.book_url,b.book_url_hash,b.book_description,b.book_extended,b.date,b.rating,u.book_url_ctr,b.group"
			+ "      FROM bookmark b, urls u "
			+ tagWhereQuery + " "
			+ groupWhereQuery
			+ "        AND t1.content_type=" + Bookmark.CONTENT_TYPE
			+ "        AND t1.user_name = ?"
			+ "        AND b.book_url_hash=u.book_url_hash"
			+ "        AND t1.content_id=b.content_id"
			+ "      GROUP BY b.content_id ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS a "
			+ "  LEFT OUTER JOIN tas AS t ON a.content_id=t.content_id, groupids gi "
			+ "  WHERE a.group = gi.group "
			+ "  ORDER BY a.date DESC, a.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);

		String bibQuery = "SELECT " + getBibtexSelect("b") + ",t.tag_name,g.group_name,h.ctr  "
		+ "  FROM bibtex b, tas t, groupids g, bibhash h, "
		+ "    (SELECT DISTINCT b.content_id " +
		"FROM bibtex b "
		+ tagWhereQuery + " "
		+ groupWhereQuery
		+ "        AND t1.content_type = " + Bibtex.CONTENT_TYPE
		+ "        AND t1.user_name = ?"
		+ "        AND t1.content_id = b.content_id"
		+ "      GROUP BY b.content_id ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS tt"
		+ "  WHERE tt.content_id=b.content_id"
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND tt.content_id=t.content_id"
		+ "    AND g.group = b.group"
//		+ "    AND b.group IN (0,1,2)" TODO: WOZU sollte das gut sein?? bei der Bookmark Query war es auch drin!
		+ "  ORDER BY b.date DESC, b.content_id DESC;";
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		Iterator it = tags.iterator();
		while (it.hasNext()) {
			tag = (String)it.next();
			c.bibStmtP.setString(queryParamPos, requUser);
			c.bookStmtP.setString(queryParamPos, requUser);
			queryParamPos++;
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
		}
		c.bookStmtP.setString(queryParamPos, requUser);
		c.bibStmtP.setString(queryParamPos, requUser);
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, itemCount);
		c.bibStmtP.setInt(queryParamPos, itemCount);
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, startBook);
		c.bibStmtP.setInt(queryParamPos, startBib);

	}
	
	/**
	 * PAGE_USER_BIBTEXKEY
	 * 
	 * shows the page /bibtexkey/[BIBTEXKEY]/[USERNAME]
	 * 
	 * Shows all bibtex entries of the user with the given BibTeX-Key
	 * 
	 * @param c 
	 * 			the database context
	 * @param requUser
	 * 			the requested username
	 * @param requBibtexKey
	 * 			the BibTeX-Key
	 * @param itemCount
	 * 			number of bibtex to retrieve
	 * @param startBib
	 * 			startposition of bibtex entries
	 * @throws SQLException
	 */
	private void queryPageUserBibtexKey(DBContext c, String requUser, String requBibtexKey, int itemCount, int startBib) throws SQLException {
		SystemTags systemTags = new SystemTags(requBibtexKey);
		int counter = 1;	
	
		String bibQuery = "	SELECT " + getBibtexSelect("b") + ", t.tag_name,h.ctr "
				+ "			FROM bibtex b, tas t, bibhash h "
				+ "			WHERE "				
				+ "			b.content_id = t.content_id "
				+ "			AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
				+ "			AND h.type=" + Bibtex.INTER_HASH 
				+ systemTags.generateSqlQuery(SystemTags.USER_NAME, "b") 
				+ "			AND b.bibtexKey = ? "
				+ "			ORDER BY date DESC "
				+ "			LIMIT ? OFFSET ?";		
		
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		
		if (systemTags.isUsed(SystemTags.USER_NAME)) {			
			c.bibStmtP.setString(counter++, systemTags.getValue(SystemTags.USER_NAME));			
		}		
		c.bibStmtP.setString(counter++, systemTags.getCleanedString());	
		c.bibStmtP.setInt(counter++, itemCount);
		c.bibStmtP.setInt(counter++, startBib);
	}

	/** PAGE_TAG and PAGE_VIEWABLETAG
	 * 
	 * Prepares the queries for the pages /tag/EinTag and /viewable/EineGruppe/EinTag
	 * On the /tag page only public entries are shown (accomplished by setting 
	 * group = constants.SQL_CONST_GROUP_PUBLIC in the call to queryPageTag())
	 * which have all of the given tags attached.
	 * On the /viewable/ page only posts are shown which are set viewable to the 
	 * given group and which have all of the given tags attached. 
	 *  
	 * @param c the context for the DB, containing the queries
	 * @param requTag the string of tags the user requests to see as given by the user.
	 *                The string is parsed with the helps of SplittedConcepts and used to
	 *                construct the JOIN for the tag selection.
	 * @param group the group the posts should belong to. 
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageTag (DBContext c, String requTag, int group, int itemCount, int startBook, int startBib, String order) throws SQLException {
		SplittedTags tags      = new SplittedTags(requTag, "", true);		
		int queryParamPos      = 1;
		// TODO: avoid "FORCE INDEX (content_type_group_tag_name_date_content_id_idx)"

		if ("folkrank".equals(order)) {			
			String folkrankQuery   = tags.getFolkrankQuery();

			String query = "SELECT "
				+ "				b.content_id, t.tag_name, b.book_description, b.book_extended, b.user_name, "
				+ "				b.date, b.book_url_hash, b.rating, u.book_url, u.book_url_ctr, tt.weight "
				+ "			FROM " 
				+ "				tas t, urls u, bookmark b, "
				+ "				(SELECT " 
				+ "					b.book_url_hash, MIN(b.content_id) AS content_id, xy.weight "
				+ "				 FROM  bookmark b, "
				+ "					(SELECT w.item, SUM(weight) AS weight "
				+ "				 	FROM rankings r "
				+ "						JOIN weights w USING (id) "
				+ "				 	WHERE r.dim = 0 AND (" +  folkrankQuery + ") "
				+ "						AND w.dim = 2 AND w.itemtype = " + Bookmark.CONTENT_TYPE
				+ "				 	GROUP BY w.item "
				+ "				 	ORDER BY 2 DESC "
				+ "				 	LIMIT ? OFFSET ?) AS xy "
				+ "				WHERE xy.item = b.book_url_hash	"			
				+ "					AND b.group = " + group
				+ "				GROUP BY b.book_url_hash "
				+ "				ORDER BY weight DESC) AS tt " 
				+ "			WHERE t.content_id = tt.content_id "
				+ "				AND u.book_url_hash = tt.book_url_hash "
				+ "				AND b.content_id = tt.content_id";			
			c.bookStmtP = c.conn.prepareStatement(query);				

			c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect("b") + ",t.tag_name, h.ctr " 
					+ " 		FROM bibtex b, tas t, bibhash h, "
					+ "				(SELECT b.simhash" + Bibtex.INTER_HASH + ", MIN(b.content_id) AS content_id, xy.weight "
					+ "			 	FROM bibtex b, "
					+ "					(SELECT w.item, SUM(weight) AS weight "
					+ "				 	FROM rankings r "
					+ "						JOIN weights w USING (id) "
					+ "				 	WHERE r.dim = 0 AND (" + folkrankQuery + ") "
					+ "						AND w.dim = 2 AND w.itemtype =" + Bibtex.CONTENT_TYPE
					+ "				 	GROUP BY w.item "
					+ "				 	ORDER BY 2 DESC "
					+ "				 	LIMIT ? OFFSET ?) AS xy "
					+ "				WHERE xy.item = b.simhash" + Bibtex.INTER_HASH
					+ "					AND b.group = " + group
					+ "				GROUP BY b.simhash" + Bibtex.INTER_HASH + " "
					+ "				ORDER BY weight DESC) AS tt "
					+ "			WHERE tt.content_id = b.content_id "
					+ "				AND t.content_id = b.content_id "
					+ "				AND b.simhash" + Bibtex.INTER_HASH + " = h.hash "
					+ "				AND h.type = " + Bibtex.INTER_HASH);						
		} else {
			String tagWhereQuery   = tags.getQuery();

			//TODO: avoid "FORCE INDEX (content_type_group_tag_name_date_content_id_idx)"
			String query = "SELECT b.content_id,t.tag_name,b.book_description,b.book_extended,b.user_name,b.date,b.book_url_hash,b.rating,u.book_url,u.book_url_ctr"
				+ "  FROM bookmark b, urls u, tas t, "
				+ "    (SELECT t1.content_id"
				+ "      FROM "
				+ tagWhereQuery
				+ "      AND t1.content_type=" + Bookmark.CONTENT_TYPE
				+ "      AND t1.group = " + group
				+ "      ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS tt"
				+ "    WHERE b.content_id=tt.content_id"
				+ "      AND t.content_id=tt.content_id"
				+ "      AND b.book_url_hash=u.book_url_hash"
				+ "      ORDER BY b.date DESC,b.content_id DESC";
			c.bookStmtP = c.conn.prepareStatement(query);
			c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect("b") + ",t.tag_name, h.ctr"
					+ "  FROM bibtex b, tas t, bibhash h, "
					+ "    (SELECT t1.content_id"
					+ "      FROM "
					+ tagWhereQuery
					+ "      AND t1.content_type = " + Bibtex.CONTENT_TYPE
					+ "      AND t1.group = " + group
					+ "      ORDER BY t1.date DESC LIMIT ? OFFSET ?) AS tt"
					+ "    WHERE b.content_id=tt.content_id"
					+ "      AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
					+ "      AND h.type = " + Bibtex.INTER_HASH
					+ "      AND t.content_id=tt.content_id"
					+ "      ORDER BY b.date DESC,b.content_id DESC");			
		}

		for (String tag: tags) {
			c.bookStmtP.setString(queryParamPos, tag);
			c.bibStmtP.setString(queryParamPos++, tag);
		}
		c.bookStmtP.setInt(queryParamPos, itemCount);
		c.bibStmtP.setInt(queryParamPos++, itemCount);
		c.bookStmtP.setInt(queryParamPos, startBook);
		c.bibStmtP.setInt(queryParamPos, startBib);
	}


	/** PAGE_AUTHOR
	 * 
	 * Prepares the query for the pages /author/EinAutor
	 * On public entries are shown (accomplished by setting 
	 * group = constants.SQL_CONST_GROUP_PUBLIC in the call to queryPageAuthor())
	 * which all are published by the given author.
	 *  
	 * @param c the context for the DB, containing the queries
	 * @param requAuthor the string contains the authors name	 
	 * @param itemCount	
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageAuthor(DBContext c, String requAuthor, int itemCount, int startBib) throws SQLException {
		SplittedAuthors authors = new SplittedAuthors(requAuthor);
		SystemTags systemTags   = new SystemTags(requAuthor);		
		String authorMatch = authors.getQuery();		
		int argCtr = 1;

		String query = "SELECT " + getBibtexSelect("tt") + ", t.tag_name, h.ctr"
				   + "     FROM tas t, bibhash h,"    
			       + "         (SELECT " + getBibtexSelect("b") + ",b.simhash" + Bibtex.INTER_HASH
				   + "         FROM search s JOIN bibtex b ON (s.content_id = b.content_id "
				   + systemTags.generateSqlQuery(SystemTags.BIBTEX_YEAR, "b") + ")"
				   + "         WHERE MATCH(s.author) AGAINST (? IN BOOLEAN MODE) " 
				   + "         AND s.content_type= " + Bibtex.CONTENT_TYPE
				   + "         AND s.group = " + constants.SQL_CONST_GROUP_PUBLIC
				   + systemTags.generateSqlQuery(SystemTags.GROUP_NAME, "s")	
				   + systemTags.generateSqlQuery(SystemTags.USER_NAME, "s")
				   + "         ORDER BY s.date DESC LIMIT ? OFFSET ?) AS tt " 
				   + "     WHERE t.content_id=tt.content_id " 
				   + "         AND tt.simhash" + Bibtex.INTER_HASH +" = h.hash "    
				   + "         AND h.type = " + Bibtex.INTER_HASH          
				   + "         ORDER BY tt.date DESC,tt.content_id DESC ";     
		
		c.bibStmtP = c.conn.prepareStatement(query);
		c.bibStmtP.setString(argCtr++, authorMatch);
		if (systemTags.isUsed(SystemTags.GROUP_NAME) ) 
			c.bibStmtP.setString(argCtr++, systemTags.getValue(SystemTags.GROUP_NAME));		
		if (systemTags.isUsed(SystemTags.USER_NAME) ) 
			c.bibStmtP.setString(argCtr++, systemTags.getValue(SystemTags.USER_NAME));
				
		c.bibStmtP.setInt(argCtr++, itemCount);
		c.bibStmtP.setInt(argCtr++, startBib);

		argCtr = 1;
		//  counts
		c.bibTCStmtP = c.conn.prepareStatement("SELECT count(*) FROM search s " 
			            + " JOIN bibtex b USING (content_id)"
				        + " WHERE MATCH(s.author) AGAINST (? IN BOOLEAN MODE) " 
				        + " AND s.content_type = " + Bibtex.CONTENT_TYPE 
				        + " AND s.group = " + constants.SQL_CONST_GROUP_PUBLIC
				        + systemTags.generateSqlQuery(SystemTags.USER_NAME, "s") 	
				        + systemTags.generateSqlQuery(SystemTags.GROUP_NAME, "s")
				        + systemTags.generateSqlQuery(SystemTags.BIBTEX_YEAR,"b"));
		c.bibTCStmtP.setString(argCtr++,authorMatch);
		if (systemTags.isUsed(SystemTags.USER_NAME)) 
			c.bibTCStmtP.setString(argCtr++, systemTags.getValue(SystemTags.USER_NAME));	
		if (systemTags.isUsed(SystemTags.GROUP_NAME)) 
			c.bibTCStmtP.setString(argCtr++, systemTags.getValue(SystemTags.GROUP_NAME));	
	}

	/** PAGE_AUTHOR_TAG 
	 * 
	 * /author/AUTHORS/TAG
	 * 
	 * Does basically the same as queryPageAuthor with the additionaly possibility 
	 * to restrict the tag the bibtex entries have to have. 
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param requAuthor the string contains the authors name(s)
	 * @param requTag tag 	 
	 * @param itemCount	 
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageAuthorTag(DBContext c, String requAuthor, String requTag, int itemCount, int startBib) throws SQLException {
		SplittedAuthors authors = new SplittedAuthors(requAuthor);
		SystemTags systemTags = new SystemTags(requAuthor);
		String authorMatch = authors.getQuery();
		int argCtr = 1;

		String query = "SELECT " + getBibtexSelect("tt") + ", t.tag_name, h.ctr"		
		+ "		FROM tas t, bibhash h,"			
		+ "			(SELECT " + getBibtexSelect("b") + ", b.simhash" + Bibtex.INTER_HASH
		+ "			 FROM search s, tas t1, bibtex b"
		+ "			 WHERE MATCH(s.author) AGAINST (? IN BOOLEAN MODE)"
		+ " 		 	AND s.content_type = " + Bibtex.CONTENT_TYPE
		+ "			 	AND s.group = " + constants.SQL_CONST_GROUP_PUBLIC
		+ "			 	AND s.content_id = t1.content_id"
		+ "				AND b.content_id = s.content_id"			
		+ "			 	AND t1.tag_name = ? "
		+ systemTags.generateSqlQuery(SystemTags.BIBTEX_YEAR,"b")
		+ systemTags.generateSqlQuery(SystemTags.GROUP_NAME, "s")
		+ systemTags.generateSqlQuery(SystemTags.USER_NAME, "s")
		+ "				ORDER BY s.date DESC LIMIT ? OFFSET ?) AS tt" 		
		+ "		WHERE t.content_id = tt.content_id" 		
		+ "			AND tt.simhash" + Bibtex.INTER_HASH +" = h.hash" 			
		+ "			AND h.type = " + Bibtex.INTER_HASH			
		+ "		ORDER BY tt.date DESC, tt.content_id DESC";
		
		c.bibStmtP = c.conn.prepareStatement(query);
		c.bibStmtP.setString(argCtr++, authorMatch);		
		c.bibStmtP.setString(argCtr++, requTag);
		if (systemTags.isUsed(SystemTags.GROUP_NAME)) 
			c.bibStmtP.setString(argCtr++, systemTags.getValue(SystemTags.GROUP_NAME));		
		if (systemTags.isUsed(SystemTags.USER_NAME)) 
			c.bibStmtP.setString(argCtr++, systemTags.getValue(SystemTags.USER_NAME));		
		c.bibStmtP.setInt(argCtr++, itemCount);
		c.bibStmtP.setInt(argCtr++, startBib);

		argCtr = 1;
		// count 
		c.bibTCStmtP = c.conn.prepareStatement(" SELECT count(*) FROM search s, tas t1, bibtex b"
				+ "			 WHERE MATCH(s.author) AGAINST (? IN BOOLEAN MODE)"
				+ " 		 	AND s.content_type = " + Bibtex.CONTENT_TYPE
				+ "			 	AND s.group = " + constants.SQL_CONST_GROUP_PUBLIC
				+ "			 	AND s.content_id = t1.content_id "
				+ "				AND s.content_id = b.content_id "	
				+ "				AND t1.tag_name = ?"
				+ systemTags.generateSqlQuery(SystemTags.BIBTEX_YEAR,"b")
				+ systemTags.generateSqlQuery(SystemTags.GROUP_NAME, "s")
				+ systemTags.generateSqlQuery(SystemTags.USER_NAME, "s"));
		
		c.bibTCStmtP.setString(argCtr++, authorMatch);
		c.bibTCStmtP.setString(argCtr++, requTag);
		if (systemTags.isUsed(SystemTags.GROUP_NAME)) 
			c.bibTCStmtP.setString(3, systemTags.getValue(SystemTags.GROUP_NAME));
		if (systemTags.isUsed(SystemTags.USER_NAME)) 
			c.bibTCStmtP.setString(3, systemTags.getValue(SystemTags.USER_NAME));
	}

	/** PAGE_URL
	 * 
	 * Prepares a query which retrieves all bookmarks which are represented by the given hash.
	 * Retrieves only public bookmarks!  
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param requUrl hash representation of URL (bookmark)
	 * @param itemCount
	 * @param startBook
	 * @throws SQLException
	 */
	private void queryPageUrl (DBContext c, String requUrl, int itemCount, int startBook) throws SQLException {
		String query = "SELECT b.content_id,t.tag_name,b.book_description,b.book_extended,b.user_name,b.rating,b.date,b.book_url_hash,u.book_url,u.book_url_ctr"
			+ "  FROM"
			+ "    (SELECT book_url_hash,book_description,book_extended,user_name,date,rating,content_id"
			+ "      FROM bookmark "
			+ "      WHERE book_url_hash=? "
			+ "        AND bookmark.group = " + constants.SQL_CONST_GROUP_PUBLIC
			+ "      ORDER BY date DESC "
			+ "      LIMIT ? OFFSET ?) AS b"
			+ "  LEFT OUTER JOIN tas t ON b.content_id=t.content_id, urls u"
			+ "      WHERE b.book_url_hash=u.book_url_hash"
			+ "      ORDER BY b.date DESC,b.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setString (1, requUrl);
		c.bookStmtP.setInt(2, itemCount);
		c.bookStmtP.setInt(3, startBook);
		// counts 
		c.bookTCStmtP = c.conn.prepareStatement("SELECT count(*) FROM bookmark WHERE book_url_hash = ? AND bookmark.group = " + constants.SQL_CONST_GROUP_PUBLIC);
		c.bookTCStmtP.setString(1, requUrl);
	}

	/** PAGE_USERURL
	 * 
	 * Prepares a query which retrieves the bookmark (which is represented by the 
	 * given hash) for a given user.
	 * Since user name is given, full group checking is done, i.e., everbody who may
	 * see the bookmark will see it.   
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param requUrl hash representation of URL (bookmark)
	 * @param requUser user whose bookmark we want to retrieve
	 * @param itemCount
	 * @param startBook
	 * @throws SQLException
	 */
	private void queryPageUserUrl (DBContext c, String requUrl, String requUser, String currUser) throws SQLException {
		String groupWhereQuery = getQueryForGroups (c.conn, currUser, requUser, "bb");
		String query = "SELECT b.content_id,t.tag_name,b.book_description,b.book_extended,b.user_name,b.rating,b.date,b.book_url_hash,u.book_url,u.book_url_ctr,g.group_name"
			+ "  FROM"
			+ "    (SELECT book_url_hash,book_description,book_extended,user_name,rating,date,content_id,bb.group"
			+ "      FROM bookmark bb"
			+ "      WHERE book_url_hash = ? "
			+ groupWhereQuery
			+ "        AND user_name = ?"
			+ "      ORDER BY date DESC) AS b"
			+ "  LEFT OUTER JOIN tas t ON b.content_id=t.content_id, urls u, groupids g"
			+ "      WHERE b.book_url_hash=u.book_url_hash"
			+ "        AND b.group = g.group"
			+ "      ORDER BY b.date DESC,b.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setString (1, requUrl);
		c.bookStmtP.setString (2, requUser);
	}

	/** PAGE_BIBTEX
	 * /bibtex/023847123ffa8976a969786f876f78e68
	 * 
	 * Prepares a query which retrieves all bibtex posts whose hash no. requSim is 
	 * equal to requBibtex.
	 * Only public posts are retrieved. 
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param requBibtex hash representing the bibtex post(s)
	 * @param requSim number of sim hash we want to use for comparison. 
	 *                Is appended to "simhash" to select the corresponding 
	 *                column in the bibtex table (i.e., simhash0, simhash1, ...) 
	 * @param itemCount
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageBibtex (DBContext c, String requBibtex, String requSim, int itemCount, int startBib) throws SQLException {
		String query = "SELECT " + getBibtexSelect("b") + ",t.tag_name, h.ctr"
		+ "  FROM"
		+ "    (SELECT " + getBibtexSelect("bibtex") + ",bibtex.simhash" + Bibtex.INTER_HASH
		+ "      FROM bibtex"
		+ "      WHERE simhash" + requSim + " = ? "
		+ "        AND bibtex.group = " + constants.SQL_CONST_GROUP_PUBLIC
		+ "      ORDER BY date DESC"
		+ "      LIMIT ? OFFSET ?) AS b"
		+ "  LEFT OUTER JOIN tas t ON b.content_id=t.content_id, bibhash h"
		+ "    WHERE b.simhash" + Bibtex.INTER_HASH + " = h.hash"
		+ "      AND h.type = " + Bibtex.INTER_HASH
		+ "    ORDER BY b.date DESC,b.content_id DESC";
		c.bibStmtP = c.conn.prepareStatement(query);
		c.bibStmtP.setString (1, requBibtex);
		c.bibStmtP.setInt(2, itemCount);
		c.bibStmtP.setInt(3, startBib);
		// counts 
		c.bibTCStmtP = c.conn.prepareStatement("SELECT ctr FROM bibhash WHERE hash = ? AND type = " + Bibtex.INTER_HASH);
		c.bibTCStmtP.setString(1, requBibtex);
	}

	/** PAGE_USERBIBTEX
	 * /bibtex/023847123ffa8976a969786f876f78e68/MaxMustermann
	 * 
	 * Prepares a query which retrieves all bibtex posts whose hash no. requSim is
	 * equal to requBibtex and they're owned by requUser. 
	 * Full group checking is done.
	 * 
	 * Additionally, if requUser = currUser, the document table is joined so that
	 * we can present the user a link to the uploaded document.
	 * 
	 * @param c the context for the DB, containing the queries
	 * @param requBibtex hash representing the bibtex post(s)
	 * @param requSim number of sim hash we want to use for comparison. 
	 *                Is appended to "simhash" to select the corresponding 
	 *                column in the bibtex table (i.e., simhash0, simhash1, ...) 
	 * @param requUser name of user who owns the entris.
	 * @param currUser
	 * @throws SQLException
	 */
	private void queryPageUserBibtex (DBContext c, String requBibtex, String requSim, String requUser, String currUser) throws SQLException {
		/* here we join the document table to the bibtex table to get the corresponding 
		 * documents and link them to the user; if currUser != requUser no documents
		 * are joined so that a user can only see her own documents 
		 */
		String groupWhereQuery = getQueryForGroups (c.conn, currUser, requUser, "bb");
		String query = "SELECT *"
			+ "  FROM"
			+ "    (SELECT *"
			+ "      FROM bibtex bb"
			+ "      WHERE simhash" + requSim + " = ? "
			+ groupWhereQuery
			+ "        AND user_name = ?"
			+ "      ORDER BY date DESC) AS b"
			+ "  LEFT OUTER JOIN tas t ON b.content_id=t.content_id"
			+ "  LEFT OUTER JOIN document d ON "
			+ "    (d.content_id = b.content_id AND d.user_name = b.user_name), bibhash h, groupids g"
			+ "    WHERE b.simhash" + Bibtex.INTER_HASH + " = h.hash"
			+ "      AND h.type = " + Bibtex.INTER_HASH
			+ "      AND b.group = g.group"
			+ "  ORDER BY b.date DESC,b.content_id DESC";
		c.bibStmtP = c.conn.prepareStatement(query);
		c.bibStmtP.setString (1, requBibtex);
		c.bibStmtP.setString (2, requUser);
	}

	/** PAGE_SEARCH
	 * /search/ein+lustiger+satz
	 * 
	 * Prepares queries to retrieve posts which match a fulltext search in the
	 * fulltext search table. 
	 *  
	 * @param c the context for the DB, containing the queries.
	 * @param search the search string, as given by the user. The string will 
	 *               be mangled up in the method to do what the user expects
	 *               (AND searching). Unfortunately this also destroys some
	 *               other features (eg. "phrase searching").
	 * @param requUserv if given, only (public) posts from the given user are searched. 
	 *                  Otherwise all (public) posts are searched.
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageSearch (DBContext c, String search, String requUser, int itemCount, int startBook, int startBib) throws SQLException {

		search = search.replaceAll("([\\s]|^)([\\S&&[^-]])"," +$2");
		// to search just inside a users bookmarks
		String usersearch = "";
		int argCtr = 1;
		if (requUser != null) {
			usersearch = " AND s.user_name = ? ";
		}
		String bookQuery = "SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.user_name,bb.rating,bb.book_url,bb.book_url_ctr,t.tag_name"
			+ "  FROM "
			+ "    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,b.user_name,b.rating,u.book_url,u.book_url_ctr" 
			+ "       FROM bookmark b, urls u, search s" 
			+ "       WHERE s.group = " + constants.SQL_CONST_GROUP_PUBLIC 
			+ "         AND MATCH (s.content) AGAINST (? IN BOOLEAN MODE) "
			+ "         AND s.content_type = " + Bookmark.CONTENT_TYPE
			+ "         AND u.book_url_hash = b.book_url_hash"
			+ "         AND s.content_id = b.content_id "
			+ usersearch
			+ "       ORDER BY s.date DESC" 
			+ "       LIMIT ? OFFSET ?) AS bb" 
			+ "  LEFT OUTER JOIN tas AS t ON t.content_id=bb.content_id"
			+ "  ORDER BY bb.date DESC,bb.content_id";
		c.bookStmtP = c.conn.prepareStatement(bookQuery);
		c.bookStmtP.setString(argCtr++, search);
		if (requUser != null) {
			c.bookStmtP.setString(argCtr++, requUser);
		}
		c.bookStmtP.setInt(argCtr++, itemCount);
		c.bookStmtP.setInt(argCtr, startBook);

		c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect ("bb") + ",t.tag_name, bb.ctr"
				+ "  FROM "
				+ "    (SELECT " + getBibtexSelect ("b") + ", h.ctr"
				+ "       FROM bibtex b, bibhash h, search s " 
				+ "       WHERE s.group = " + constants.SQL_CONST_GROUP_PUBLIC
				+ "         AND MATCH (s.content) AGAINST (? IN BOOLEAN MODE)"
				+ "         AND s.content_type = " + Bibtex.CONTENT_TYPE
				+ "         AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
				+ "         AND h.type = " + Bibtex.INTER_HASH
				+ "         AND b.content_id = s.content_id "
				+ usersearch
				+ "       ORDER BY s.date DESC" 
				+ "       LIMIT ? OFFSET ?) AS bb" 
				+ "  LEFT OUTER JOIN tas AS t ON t.content_id=bb.content_id"
				+ "  ORDER BY bb.date DESC,bb.content_id");
		argCtr = 1; // reset argument counter
		c.bibStmtP.setString(argCtr++, search);
		if (requUser != null) {
			c.bibStmtP.setString(argCtr++, requUser);
		}
		c.bibStmtP.setInt(argCtr++, itemCount);
		c.bibStmtP.setInt(argCtr, startBib);

		// counts 
		c.bookTCStmtP = c.conn.prepareStatement("SELECT count(*) from search s WHERE s.group = " + constants.SQL_CONST_GROUP_PUBLIC + " AND MATCH (s.content) AGAINST (? IN BOOLEAN MODE) AND s.content_type = " + Bookmark.CONTENT_TYPE + usersearch);
		c.bookTCStmtP.setString(1, search);
		c.bibTCStmtP = c.conn.prepareStatement("SELECT count(*) from search s WHERE s.group = " + constants.SQL_CONST_GROUP_PUBLIC + " AND MATCH (s.content) AGAINST (? IN BOOLEAN MODE) AND s.content_type = " + Bibtex.CONTENT_TYPE + usersearch);
		c.bibTCStmtP.setString(1, search);
		if (requUser != null) {
			c.bookTCStmtP.setString(2, requUser);
			c.bibTCStmtP.setString(2, requUser);
		}
		
		/*
		 * set query timeout to stop long queries (e.g. for dblp)
		 */
		c.bibStmtP.setQueryTimeout(10); // 10 seconds
		c.bookStmtP.setQueryTimeout(10); // 10 seconds

	}

	/** PAGE_VIEWABLE
	 * /viewable/EineGruppe
	 *  
	 * Prepares queries to retrieve posts which are set viewable to group.
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param group the group to which the posts are set viewable for.
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageViewable (DBContext c, int group, int itemCount, int startBook, int startBib) throws SQLException {
		// group query 
		String query = "SELECT bb.content_id,bb.user_name,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.rating,bb.book_url,bb.book_url_ctr,t.tag_name"
			+ "  FROM" 
			+ "    (SELECT b.content_id,b.user_name,b.book_url_hash,b.book_description,b.book_extended,b.date,b.rating,u.book_url,u.book_url_ctr,b.group" 
			+ "       FROM bookmark b, urls u" 
			+ "       WHERE u.book_url_hash=b.book_url_hash "
			+ "         AND b.group = ?"
			+ "       ORDER BY date DESC" 
			+ "       LIMIT ? OFFSET ?) AS bb" 
			+ "    LEFT OUTER JOIN tas AS t ON t.content_id=bb.content_id"
			+ "    ORDER BY bb.date DESC, bb.content_id DESC";
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setInt(1, group);
		c.bookStmtP.setInt(2, itemCount);
		c.bookStmtP.setInt(3, startBook);
		// bibtex query
		c.bibStmtP = c.conn.prepareStatement("SELECT " + getBibtexSelect ("bb") + ",t.tag_name, bb.ctr"
				+ "  FROM" 
				+ "    (SELECT " + getBibtexSelect ("b") + ",b.group, h.ctr"
				+ "       FROM bibtex b, bibhash h" 
				+ "       WHERE b.group = ?"
				+ "         AND h.type = " + Bibtex.INTER_HASH
				+ "         AND b.simhash" + Bibtex.INTER_HASH + " = h.hash"
				+ "       ORDER BY date DESC" 
				+ "       LIMIT ? OFFSET ?) AS bb" 
				+ "    LEFT OUTER JOIN tas AS t ON t.content_id=bb.content_id"
				+ "    ORDER BY bb.date DESC, bb.content_id DESC");
		c.bibStmtP.setInt(1, group);
		c.bibStmtP.setInt(2, itemCount);
		c.bibStmtP.setInt(3, startBib);
	}	

	/** PAGE_GROUP -- aggregiert über alle User der Gruppe
	 *  
	 *  /group/EineGruppe
	 * 
	 * Prepares queries which show all posts of all users belonging to the group.
	 * This is an aggregated view of all posts of the group members!  
	 * Full viewable-for checking is done - ie everybody sees everything he is 
	 * allowed to see. 
	 * 
	 * see also http://www.bibsonomy.org/bibtex/1d28c9f535d0f24eadb9d342168836199
	 * page 92, formula (9) for formal semantics of this query
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param currUser name of user who wants to see the posts 
	 * @param group requested group over which we aggregate the posts
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageGroup (DBContext c, String currUser, int group, int itemCount, int startBook, int startBib) throws SQLException {
		String groupWhereQuery = getQueryForGroups (c.conn, currUser, null, "b");
		// bookmark query 
		/* because MySQL 5.0 optimizes subqueries badly, we reformulated this query to use JOINs and UNION instead of a subquery:
		 * (note: users own (private,friends) bookmarks (if he is in the group) are missing here)
		 *		String query = "SELECT bb.content_id,bb.book_url_hash,bb.book_description,bb.book_extended,bb.date,bb.book_url,bb.book_url_ctr,t.tag_name,g.group_name,t.user_name"
			+ "  FROM" 
			+ "    (SELECT b.content_id,b.book_url_hash,b.book_description,b.book_extended,b.date,u.book_url,u.book_url_ctr,b.group" 
			+ "       FROM bookmark b, urls u "
			+ "       WHERE ((" 
			+ "            b.user_name IN (SELECT user_name FROM groups WHERE groups.group = ?) "
			+                  groupWhereQuery + ") "
			+ "            OR (b.user_name IN (SELECT f.user_name FROM friends f, groups g WHERE f.f_user_name = ? AND g.user_name=f.user_name AND g.group = ?)"
			+ "                AND b.group = " + constants.SQL_CONST_GROUP_FRIENDS + " ))"
			+ "         AND u.book_url_hash=b.book_url_hash "
			+ "       ORDER BY date DESC" 
			+ "       LIMIT ? OFFSET ?) AS bb"
			+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"
			+ "    WHERE bb.group = g.group "
			+ "    ORDER BY bb.date DESC, bb.content_id DESC";
		 * 
		 */
		String query = "SELECT bb.content_id,b.book_url_hash,b.book_description,b.book_extended,b.rating,bb.date,u.book_url,u.book_url_ctr,t.tag_name,g.group_name,t.user_name"
			+ "  FROM urls u, bookmark b, " 
			+ "		   ((SELECT content_id, date "   				// bookmarks from users of group which currUser may see
			+ "            FROM bookmark b, groups g "
			+ "            WHERE g.group = ? "
			+ "              AND g.user_name = b.user_name "        // user owns this bookmark
			+                groupWhereQuery
			+ "         )UNION("
			+ "          SELECT content_id, date"                   // bookmarks from users of group which have currUser as friend
			+ "            FROM bookmark b, groups g, friends f "
			+ "            WHERE f.f_user_name = ?"                 // currUser is friend
			+ "              AND g.user_name = f.user_name"         // user is in group
			+ "              AND b.user_name = f.user_name"         // user owns this bookmark
			+ "              AND g.group = ?"                       
			+ "              AND b.group = " + constants.SQL_CONST_GROUP_FRIENDS // bookmark is only for friends
			+ "         )UNION("
			+ "          SELECT content_id, date"                   // currUsers bookmarks, ...
			+ "            FROM bookmark b, groups g"
			+ "            WHERE b.user_name = ?" 
			+ "              AND g.user_name = b.user_name"         // only, if currUser ...
			+ "              AND g.group = ?"                       // is in this group
			+ "         ) "
			+ "         ORDER BY date DESC" 
			+ "         LIMIT ? OFFSET ?) AS bb"
			+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas
			+ "    WHERE t.group = g.group "                                               // join groupname
			+ "      AND u.book_url_hash=b.book_url_hash "                                 // join url
			+ "      AND b.content_id=bb.content_id "                                      // join title, description, ...
			+ "    ORDER BY bb.date DESC, bb.content_id DESC";

		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setInt(1, group);			// the group we are looking for
		c.bookStmtP.setString(2, currUser);  	// to see, which users have currUser as friend
		c.bookStmtP.setInt(3, group);			// ... and are also in this group
		c.bookStmtP.setString(4, currUser);  	// to get ALL the users own bookmarks
		c.bookStmtP.setInt(5, group);			// ... if he is in the respective group
		c.bookStmtP.setInt(6, itemCount);
		c.bookStmtP.setInt(7, startBook);
		// bibtex query
		String bibQuery = "SELECT " + getBibtexSelect("b") + ",t.tag_name,g.group_name, h.ctr"
		+ "  FROM bibtex b, bibhash h,"
		+ "    ((SELECT content_id, date"						// publications from users of group which currUser may see
		+ "        FROM bibtex b, groups g"
		+ "        WHERE g.group = ? "
		+ "          AND g.user_name = b.user_name "			// user owns this publication
		+            groupWhereQuery
		+ "     )UNION("
		+ "      SELECT content_id, date"						// publications from users of group which have currUser as friend
		+ "        FROM bibtex b, groups g, friends f"
		+ "        WHERE f.f_user_name = ?"					    // currUser is friend
		+ "          AND g.user_name = f.user_name"			    // user is in group
		+ "          AND b.user_name = f.user_name"			    // user owns this publication
		+ "          AND g.group = ?"
		+ "          AND b.group = " + constants.SQL_CONST_GROUP_FRIENDS // publication is only for friends
		+ "     )UNION("
		+ "      SELECT content_id, date"                       // currUsers publications, ...
		+ "        FROM bibtex b, groups g"
		+ "        WHERE b.user_name = ?"
		+ "          AND g.user_name = b.user_name"             // onliy, if currUser ...
		+ "          AND g.group = ?"                           // is in this group
		+ "     ) "
		+ "     ORDER BY date DESC" 
		+ "     LIMIT ? OFFSET ?) AS bb" 
		+ "  LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas (get tags)
		+ "  WHERE t.group = g.group "                                               // join groupname
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash "                       // join counts, ...
		+ "    AND b.content_id = bb.content_id"                                     // join rest of entry
		+ "    ORDER BY bb.date DESC, bb.content_id DESC";
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		c.bibStmtP.setInt(1, group);
		c.bibStmtP.setString(2, currUser);
		c.bibStmtP.setInt(3, group);
		c.bibStmtP.setString(4, currUser);		// to get all the users own bookmarks
		c.bibStmtP.setInt(5, group);			// ... if he is in the respective group
		c.bibStmtP.setInt(6, itemCount);
		c.bibStmtP.setInt(7, startBib);
		// counts TODO: these are just approximations - users own private/friends bookmarks and friends bookmarks are not included (same for publications)
		c.bookTCStmtP = c.conn.prepareStatement("SELECT count(*) from bookmark b, groups g WHERE g.group = ? AND b.user_name=g.user_name " + groupWhereQuery);
		c.bookTCStmtP.setInt(1, group);
		c.bibTCStmtP = c.conn.prepareStatement("SELECT count(*) from bibtex b, groups g WHERE g.group = ? AND b.user_name=g.user_name " + groupWhereQuery);
		c.bibTCStmtP.setInt(1, group);
	}

	/** PAGE_GROUP_TAG -- aggregiert über alle User der Gruppe und wählt Tag aus
	 * 
	 * /group/EineGruppe/EinTag+NochEinTag
	 * 
	 * Does basically the same as queryPageGroup with the additionaly possibility 
	 * to restrict the tags the posts have to have. 
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param requTag tag string as given by the user. see also {@link #queryPageTag(servlets.ResourceHandler.DBContext, String, int, int, int, int)}
	 * @param currUser name of user who wants to see the posts
	 * @param group requested group over which we aggregate the posts
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageGroupTag (DBContext c, String requTag, String currUser, int group, int itemCount, int startBook, int startBib) throws SQLException {
		String groupWhereQuery = getQueryForGroups (c.conn, currUser, null, "t1");
		SplittedTags tags      = new SplittedTags(requTag, "", true);
		String tagWhereQuery   = tags.getQuery();
		int queryParamPos      = 1;
		String tag;
		/* *********************************************************************
		 *                             build queries
		 * *********************************************************************/
		/*
		 * This query part  selects all content ids (for the specific content type)
		 * - from all users of the group, if currUser is allowed to see them
		 * - from users of group, if currUser is friend of one of the users
		 * - from currUser, if in the group
		 */
		String selectContentIDs = 			
			"((SELECT t1.content_id, t1.date "   				// items from users of group which currUser may see
			+ "    FROM groups g, "
			+      tagWhereQuery
			+ "      AND g.group = ? "
			+ "      AND g.user_name = t1.user_name "        // user owns this item
			+        groupWhereQuery
			+ "      AND t1.content_type = ?"
			+ " )UNION("
			+ "  SELECT t1.content_id, t1.date"                    // items from users of group which have currUser as friend
			+ "    FROM groups g, friends f, "
			+      tagWhereQuery
			+ "      AND g.group = ?"
			+ "      AND f.f_user_name = ?"                  // currUser is friend
			+ "      AND g.user_name = f.user_name"          // user is in group
			+ "      AND t1.user_name = f.user_name"         // user owns this item                       
			+ "      AND t1.group = " + constants.SQL_CONST_GROUP_FRIENDS // item is only for friends
			+ "      AND t1.content_type = ?"
			+ " )UNION("
			+ "  SELECT t1.content_id, t1.date"                    // currUsers items, ...
			+ "    FROM groups g, "
			+      tagWhereQuery
			+ "      AND g.group = ?"                        // if in this group ...
			+ "      AND g.user_name = t1.user_name"         // is currUser
			+ "      AND t1.user_name = ?"
			+ "      AND t1.content_type = ?"
			+ " ) "
			+ "   ORDER BY date DESC" 
			+ "   LIMIT ? OFFSET ?) AS bb";
		// bookmark query
		String query = "SELECT bb.content_id,b.book_url_hash,b.book_description,b.book_extended,b.rating,bb.date,u.book_url,u.book_url_ctr,t.tag_name,g.group_name,t.user_name"
			+ "  FROM urls u, bookmark b, " 
			+ selectContentIDs
			+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas
			+ "    WHERE t.group = g.group "                                               // join groupname
			+ "      AND u.book_url_hash=b.book_url_hash "                                 // join url
			+ "      AND b.content_id=bb.content_id "                                      // join title, description, ...
			+ "    ORDER BY bb.date DESC, bb.content_id DESC"; 
		// bibtex query
		String bibQuery = "SELECT " + getBibtexSelect("b") + ",t.tag_name,g.group_name, h.ctr"
		+ "  FROM bibtex b, bibhash h, "
		+ selectContentIDs
		+ "  LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas (get tags)
		+ "  WHERE t.group = g.group "                                               // join groupname
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash "                       // join counts, ...
		+ "    AND b.content_id = bb.content_id"                                     // join rest of entry
		+ "    ORDER BY bb.date DESC, bb.content_id DESC";

		/* *********************************************************************
		 *                           prepare statements
		 * *********************************************************************/
		c.bookStmtP = c.conn.prepareStatement(query);
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		/* *********************************************************************
		 *                           fill in parameters
		 * *********************************************************************/
		// First union
		Iterator it = tags.iterator();
		while (it.hasNext()) {                                       // tags
			tag = (String)it.next();
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
		}
		c.bookStmtP.setInt(queryParamPos, group);	// the group we are looking for
		c.bibStmtP.setInt(queryParamPos, group);	// the group we are looking for
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, Bookmark.CONTENT_TYPE);	// just select content_ids from bookmarks!
		c.bibStmtP.setInt(queryParamPos, Bibtex.CONTENT_TYPE);	    // just select content_ids from bibtex!
		queryParamPos++;
		// UNION
		it = tags.iterator();
		while (it.hasNext()) {                                      // tags
			tag = (String)it.next();
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
		}
		c.bookStmtP.setInt(queryParamPos, group);		// to see, which users have currUser as friend ... and are also in this group
		c.bibStmtP.setInt(queryParamPos, group);		// to see, which users have currUser as friend ... and are also in this group
		queryParamPos++;
		c.bookStmtP.setString(queryParamPos, currUser); // currUser as friend
		c.bibStmtP.setString(queryParamPos, currUser); // currUser as friend
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, Bookmark.CONTENT_TYPE);	// just select content_ids from bookmarks!
		c.bibStmtP.setInt(queryParamPos, Bibtex.CONTENT_TYPE);	// just select content_ids from bibtex!
		queryParamPos++;
		// UNION
		it = tags.iterator();
		while (it.hasNext()) {                                      // tags
			tag = (String)it.next();
			c.bibStmtP.setString(queryParamPos, tag);
			c.bookStmtP.setString(queryParamPos, tag);
			queryParamPos++;
		}
		c.bookStmtP.setInt(queryParamPos, group);       // ... if currUser is in the respective group
		c.bibStmtP.setInt(queryParamPos, group);       // ... if currUser is in the respective group
		queryParamPos++;
		c.bookStmtP.setString(queryParamPos, currUser); // to get ALL the users own bookmarks
		c.bibStmtP.setString(queryParamPos, currUser); // to get ALL the users own bookmarks
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, Bookmark.CONTENT_TYPE);	// just select content_ids from bookmarks!
		c.bibStmtP.setInt(queryParamPos, Bibtex.CONTENT_TYPE);	// just select content_ids from bibtex!
		// LIMIT ? OFFSET ?
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, itemCount);
		c.bibStmtP.setInt(queryParamPos, itemCount);
		queryParamPos++;
		c.bookStmtP.setInt(queryParamPos, startBook);
		c.bibStmtP.setInt(queryParamPos, startBib);
	}

	/** PAGE_FRIEND -- aggregiert über alle User, bei denen currUser Friend ist
	 * 
	 * /friends
	 * 
	 * Prepares queries which show all posts of users which have currUser as their 
	 * friend.
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param currUser name of user who wants to see the posts
	 * @param itemCount
	 * @param startBook
	 * @param startBib
	 * @throws SQLException
	 */
	private void queryPageFriend (DBContext c, String currUser, int itemCount, int startBook, int startBib) throws SQLException {
		String query = "SELECT bb.content_id,b.book_url_hash,b.book_description,b.book_extended,b.rating,bb.date,u.book_url,u.book_url_ctr,t.tag_name,g.group_name,t.user_name"
			+ "  FROM urls u, bookmark b, " 
			+ "		   (SELECT content_id, date "   				// bookmarks from users of group which currUser may see
			+ "           FROM bookmark b, friends f"
			+ "           WHERE f.f_user_name = ?"                  // currUser is friend
			+ "             AND b.user_name = f.user_name"          // take alle rows, which are owned by friend                     
			+ "             AND b.group = " + constants.SQL_CONST_GROUP_FRIENDS // bookmark is only for friends
			+ "           ORDER BY date DESC" 
			+ "           LIMIT ? OFFSET ?) AS bb"
			+ "    LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas
			+ "    WHERE t.group = g.group "                                               // join groupname
			+ "      AND u.book_url_hash=b.book_url_hash "                                 // join url
			+ "      AND b.content_id=bb.content_id "                                      // join title, description, ...
			+ "    ORDER BY bb.date DESC, bb.content_id DESC";

		c.bookStmtP = c.conn.prepareStatement(query);
		c.bookStmtP.setString(1, currUser);  	// to see, which users have currUser as friend
		c.bookStmtP.setInt(2, itemCount);
		c.bookStmtP.setInt(3, startBook);
		// bibtex query
		String bibQuery = "SELECT " + getBibtexSelect("b") + ",t.tag_name,g.group_name, h.ctr"
		+ "  FROM bibtex b, bibhash h,"
		+ "    (SELECT content_id, date"						// publications from users of group which currUser may see
		+ "       FROM bibtex b, friends f"
		+ "       WHERE f.f_user_name = ?"                  // currUser is friend
		+ "         AND b.user_name = f.user_name"          // take alle rows, which are owned by friend                     
		+ "         AND b.group = " + constants.SQL_CONST_GROUP_FRIENDS // bookmark is only for friends
		+ "       ORDER BY date DESC" 
		+ "       LIMIT ? OFFSET ?) AS bb" 
		+ "  LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas (get tags)
		+ "  WHERE t.group = g.group "                                               // join groupname
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash "                       // join counts, ...
		+ "    AND b.content_id = bb.content_id"                                     // join rest of entry
		+ "    ORDER BY bb.date DESC, bb.content_id DESC";
		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		c.bibStmtP.setString(1, currUser);		// to see, which users have currUser as friend 
		c.bibStmtP.setInt(2, itemCount);
		c.bibStmtP.setInt(3, startBib);
	}

	/** PAGE_DUPLICATE -- zeigt alle Resourcen an, die doppelt vorhanden sind
	 * 
	 * Prepares a query which returns all duplicate bibtex posts of the requested user.
	 * Duplicates are bibtex posts which have the same simhash1, but a different
	 * simhash0 (the latter is always true within the posts of a single user).
	 * 
	 * @param c the context for the DB, containing the queries.
	 * @param currUser name of user who wants to see the posts
	 * @param requUser
	 * @throws SQLException
	 */
	private void queryPageDuplicate (DBContext c, String currUser) throws SQLException {

		String bibQuery = "SELECT " + getBibtexSelect("b") + ",t.tag_name,g.group_name, h.ctr"
		+ "  FROM bibtex b, bibhash h,"
		+ "    (SELECT b2.content_id,b2.date FROM "
		+ "       (SELECT b0.simhash" + Bibtex.INTER_HASH + ",count(b0.simhash" + Bibtex.INTER_HASH + ") AS ctr FROM bibtex b0 WHERE b0.user_name = ? GROUP BY b0.simhash" + Bibtex.INTER_HASH + " HAVING ctr > 1) AS b1"
		+ "     JOIN bibtex b2 ON b1.simhash" + Bibtex.INTER_HASH + " = b2.simhash" + Bibtex.INTER_HASH + " AND b2.user_name = ?"
		+ "    ) AS bb" 
		+ "  LEFT OUTER JOIN tas AS t ON bb.content_id=t.content_id, groupids AS g"  // join with tas (get tags)
		+ "  WHERE t.group = g.group " // join groupname         
		+ "    AND h.type = " + Bibtex.INTER_HASH
		+ "    AND b.simhash" + Bibtex.INTER_HASH + " = h.hash "                       // join counts, ...
		+ "    AND b.content_id = bb.content_id"                                     // join rest of entry
		+ "    ORDER BY b.simhash" + Bibtex.INTER_HASH + " DESC, bb.date DESC";

		c.bibStmtP = c.conn.prepareStatement(bibQuery);
		c.bibStmtP.setString(1, currUser);
		c.bibStmtP.setString(2, currUser);
	}
	/* 
	 * returns a String for the Query of groups the user is in (including "friends", if she is a friend of the requested user 
	 */
	public static String getQueryForGroups (Connection conn, String currUser, String requUser, String table) {
		if (currUser != null && requUser != null && requUser.equals(currUser)) {
			return "";
		} else {
			StringBuffer result = new StringBuffer();
			result.append("AND " + table + ".group in (0,");
			if (currUser != null) {
				PreparedStatement StmtP = null;
				ResultSet rst;
				try {
					// check, if the users are friends
					if (requUser != null) {
						StmtP = conn.prepareStatement("SELECT user_name FROM friends WHERE f_user_name = ? AND user_name = ?");
						StmtP.setString(1, currUser);
						StmtP.setString(2, requUser);
						rst = StmtP.executeQuery();
						if (rst.next()) {
							// they are friends!
							result.append("2,");
						}
					}
					// get all the groups of the current user
					StmtP = conn.prepareStatement("SELECT `group` FROM groups WHERE user_name = ?");
					StmtP.setString(1, currUser);
					rst = StmtP.executeQuery();
					while (rst.next()) {
						// collect groups
						result.append(rst.getInt("group")).append(",");
					}				
				} catch (SQLException e) {
					log.fatal("getQueryForGroups() caught SQLException: " + e);
				} finally {
					if (StmtP != null) { try {StmtP.close(); } catch (SQLException e) {} StmtP = null; }
				}
			}
			// remove last ","
			result.deleteCharAt(result.length()-1).append(") ");
			return result.toString();
		}
	}
}
