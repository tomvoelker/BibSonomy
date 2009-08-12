/*
 * This class is used by processBook.jsp to insert the  
 * bookmark data retrieved from BookBean into the bibsonomy database.
 * If transaction is successful the user gets forwarded back to 
 * UserSiteAfterLogin.
 */
package servlets;

import helpers.MultiPartRequestParser;
import helpers.Spammer;
import helpers.constants;
import helpers.database.DBBookmarkManager;
import helpers.database.DBContentManager;
import helpers.database.DBGroupCopyManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.util.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import resources.Bookmark;
import resources.Resource;
import resources.Tag;
import beans.BookmarkHandlerBean;
import beans.UploadBean;
import beans.UserBean;
import filters.ActionValidationFilter;
import filters.SessionSettingsFilter;

public class BookmarkHandler extends HttpServlet{ 
	private static final Log log = LogFactory.getLog(BookmarkHandler.class);
	private static final long serialVersionUID = 3839748679655351876L;
	private DataSource dataSource;

	private static String projectHome  = null;
	private static String rootPath     = null;

	/* The dataSource lookup code is added to the init() method
	 * to avoid the costly JNDI operations for every HTTP request. */
	public void init(ServletConfig config) throws ServletException {	
		super.init(config); 
		try {
			/*
			 * get static parameters and datasource
			 */
			projectHome = config.getServletContext().getInitParameter("projectHome");
			rootPath    = config.getServletContext().getInitParameter("rootPath");
			dataSource  = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
		} catch (NamingException ex){
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doPost(request,response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			// TODO: does this work on bibsonomy.org? I guess, /bibsonomy/ is added, because
			// the servlet API spec says something about that
			String refer = "/login?referer=" + URLEncoder.encode("/bookmarkHandler?" + request.getQueryString(), "UTF-8");
			response.sendRedirect(refer);
			return;
		}
		String redirectURL = request.getHeader("Referer");

		/* Establish all connections, result sets and statements */
		Connection conn    					 = null;
		DBBookmarkManager bookmarkManager    = new DBBookmarkManager();
		// to get groupids, copy Bookmarks for group, etc.
		DBGroupCopyManager<Bookmark> groupman = new DBGroupCopyManager<Bookmark>();


		/* Because the dataSource instance variable is potentially 
		 * shared across multiple threads, access to the variable 
		 * must be from within a synchronized block. */
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} 
				else {
					throw new SQLException("No Datasource");
				}
			}

			// if true, the final redirect is not sent, since we sent a redirect before inserting into the database
			boolean interactive = true;
			// default: do not overwrite bibsonomy bookmarks with imported ones
			boolean overwrite   = true;
			// default: do not change bookmarks (if url != oldurl), instead make a copy
			boolean change      = true;

			LinkedList<Bookmark>bookmarks = new LinkedList<Bookmark>();

			groupman.prepareStatements(conn);

			/* ****************************************************************
			 *  decide, what to do
			 * **************************************************************** */

			if (request.getParameter("delete") != null) {
				if (!ActionValidationFilter.isValidCkey(request)) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN, "credentials missing");
					return;
				}

				/* *****************  DELETE **************** */
				Bookmark bookmark = new Bookmark ();
				bookmark.setHash(request.getParameter("delete"));
				bookmark.setOldHash(request.getParameter("delete")); // neccessary to allow decrementing counter ... again a hack :-(
				bookmark.setUser(currUser);
				bookmark.setToDel(true);
				bookmarks.add(bookmark);
				overwrite = false;
			} else if ("delicious".equals(request.getParameter("import"))) {
				/* ******************** DELICIOUS IMPORT ***************** */
				// get bookmarks to add
				try {
					if (!ActionValidationFilter.isValidCkey(request)) throw new IOException("wrong credentials.");
//					bookmarks = getBookmarksFromService(request.getParameter("username"), 
//							request.getParameter("password"), 
//							currUser, 
//							groupman.getGroup(currUser, request.getParameter("group")));
					bookmarks = getBookmarksFromService(request.getParameter("username"), 
							request.getParameter("password"), 
							currUser);
				} catch (IOException e) {
					log.fatal("could not download del.icio.us data: " + e.getMessage());
					request.setAttribute("error", "Sorry, I was not able to get your bookmarks " + e);
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
					return;
				} catch (ParserConfigurationException e) {
					log.fatal("could not parse del.icio.us data: " + e.getMessage());
					request.setAttribute("error", "BookmarkHandler: ParserConfigurationException " + e.getMessage());
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);				
				} catch (SAXException e) {
					log.fatal("could not parse del.icio.us data: " + e.getMessage());
					request.setAttribute("error", "BookmarkHandler: SAXException " + e.getMessage());
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);				
				}
				request.setAttribute("success", "We got your bookmarks from del.icio.us and will insert them now.");
				getServletConfig().getServletContext().getRequestDispatcher("/success.jsp").forward(request, response);
				// set options for database update
				interactive = false;
				overwrite   = "yes".equals(request.getParameter("overwrite"));
			} else if ("firefox".equals(request.getParameter("import"))) {
				/* ******************** FIREFOX IMPORT ***************** */
				// get bookmarks to add
				interactive = false;

				try {
					//retrieve form fields
					MultiPartRequestParser parser  = new MultiPartRequestParser();
					Map<String, FileItem> fieldMap = parser.getFields(request, rootPath);

					// retrieve form field "file"
					FileItem upFile = fieldMap.get("file");
					String currFile = upFile.getName();

					if ("".equals(currFile) || (!currFile.substring(currFile.lastIndexOf(".") + 1).equals("html"))) {
						throw new FileUploadException ("Please check your file. Only html files are accepted.");
					} 
					//retrieve selected form field group and get corresponding id 
					int groupid = groupman.getGroup(currUser, fieldMap.get("group").getString());
					//retrieve form field and set overwrite 
					overwrite = (fieldMap.get("overwrite")) != null && "yes".equals(fieldMap.get("overwrite").getString());

					/* *************************************************
					 * save file with hashed name (currFile+currUser+Time) in temp Dir
					 * *************************************************/													
					File bookmarkFile = new File(rootPath + "bibsonomy_temp", Resource.hash(currFile + currUser + System.currentTimeMillis()));	
					upFile.write(bookmarkFile); // if it fails, Exception is catched below 

					//writing into file was ok -> delete fileitem upfile
					upFile.delete();

					//firefox importer aufrufen (mit filename und groupid)							
					bookmarks = getBookmarksFromFirefox(bookmarkFile, currUser, groupid);

					//retrieving bookmarks successful - delete bookmark file from disk
					bookmarkFile.delete();


				} catch (IOException e) {
					request.setAttribute("error", "Sorry, I was not able to get your bookmarks " + e);
					getServletConfig().getServletContext().getRequestDispatcher("/errors/error.jsp").forward(request, response);
					return;
				} catch (Exception e) {
					// if it failed, send user to error page
					UploadBean bean = new UploadBean();
					bean.setErrors("file", "Your upload failed: " + e);
					request.setAttribute("upBean", bean);
					getServletConfig().getServletContext().getRequestDispatcher("/upload_error").forward(request, response);
					return;
				}
				request.setAttribute("success", "We got your bookmarks from Firefox and will insert them now.");
				getServletConfig().getServletContext().getRequestDispatcher("/success.jsp").forward(request, response);
			} else {
				log.fatal("DEPRECATED: insert / update bookmark should not be done here, already migrated");
				/* ************************ INSERT + UPDATE  ************************** */
				BookmarkHandlerBean b = (BookmarkHandlerBean)request.getAttribute("bookmarkHandlerBean");
				if (b != null && ActionValidationFilter.isValidCkey(request)) {
					/* **************************** tagging of tags **************************** */
					b.doTaggingOfTags(projectHome, currUser);
					/* **************************** create new bookmark ************************ */
					/* create a bookmark object for insert/update and add it to the list */
					// get bookmark from bean
					Bookmark bookmark = b.getBookmark();
					// TODO another DBLP hack ...
					if (bookmark.getDate() == null || !constants.dblpUser.equals(currUser)) bookmark.setDate(new Date());
					bookmark.setToIns(true);
					bookmark.setUser(currUser);
					bookmark.setGroupid(groupman.getGroup(currUser, request.getParameter("group")));

					log.debug("created bookmark: " + bookmark);

					/* **************************** group copy handling ************************ */
					// TODO: this is a dirty hack (so that groupman can use the contentidManager from bookmarkManager)
					bookmarkManager.contentIdManager = new DBContentManager();
					bookmarkManager.contentIdManager.prepareStatementsForBookmark(conn);
					bookmarks.addAll(groupman.getCopiesForGroup(bookmark, bookmarkManager.contentIdManager));

					bookmarks.add(bookmark);
					log.debug ("added bookmark to list");

					/*
					 * check, if bookmark was posted by bookmarklet (jump = true) or not 
					 */
					if (b.isJump()) {
						/*
						 * posted by bookmarklet --> don't change an existing bookmark, but make
						 * a copy of it (with the new, changed URL) 
						 */
						change = false;
						redirectURL = b.getUrl();
					} else {
						redirectURL = "/user/" + URLEncoder.encode(currUser, "UTF-8");
					}
				}
			}


			/* ************************************ manipulate bookmarks in database ************************* */ 
			bookmarkManager.prepareStatements(conn);
			boolean spammer = bookmarkManager.updateBookmarks(bookmarks, currUser, conn, overwrite, change);
			log.debug("inserted bookmark into database");

			/*
			 * FIXME: remove this stuff, its just for debugging the FirefoxImporter
			 * 
			 */
			/*if (request.getParameter("firefox") != null) {
			 System.out.println("doing firefox import");
			 File bookmarkfile = new File("/home/rja/.mozilla/firefox/rwsads92.default/bookmarks.html");
			 //File bookmarkfile = new File("/home/rja/opera6.html");
			  LinkedList<Bookmark> foo = this.getBookmarksFromFirefox(bookmarkfile, currUser, 0);
			  if (foo != null) {
			  Iterator it = foo.iterator();
			  while (it.hasNext()) {
			  System.out.println(it.next());
			  }
			  }

			  }*/

			/* set spammer cookie */
			Spammer.addSpammerCookie (request, response, spammer);


			if (interactive) {
				/* redirect, depending on the calling page */
				response.sendRedirect(redirectURL); 
			}

		} catch (SQLException e) {
			log.fatal("could not insert/delete bookmarks: " + e);
			getServletConfig().getServletContext().getRequestDispatcher("/errors/databaseError.jsp").forward(request, response);
		} finally {
			// Always make sure result sets and statements are closed, and the connection is returned to the pool
			if (conn               != null) {try {conn.close();               } catch (SQLException e) {} conn               = null;}
			bookmarkManager.closeStatements();
			groupman.closeStatements();
		}
	}

	/*
	 *  gets XML file from delicious API, parses it and returns all found bookmarks in a LinkedList 
	 */
	private LinkedList<Bookmark> getBookmarksFromService(String username, String password, String currUser) throws FactoryConfigurationError, ParserConfigurationException, SAXException, IOException {
		LinkedList<Bookmark> bookmarks = new LinkedList<Bookmark>();
		Bookmark bookmark;
		String userpass = username + ":" + password;
		try {

			URL url = new URL ("https", "api.del.icio.us", -1, "/v1/posts/all");
			URLConnection connection = url.openConnection();

			// set header fields TODO: get this user-agent variable from web.xml 
			//connection.setRequestProperty("User-Agent", "de.igada.mydelicious UserPageChecker");
			//connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
			connection.setRequestProperty("User-Agent", "Wget/1.9.1");
			connection.setRequestProperty("Authorization", "Basic " + new sun.misc.BASE64Encoder().encode (userpass.getBytes()));						

			// get the contents of the web page TODO: check for 503 errors (will be IOExceptions
			InputStream stream = connection.getInputStream();
			// parse XML file

			// Get a JAXP parser factory object
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// Tell the factory what kind of parser we want 
			dbf.setValidating(false);
			// Use the factory to get a JAXP parser object
			DocumentBuilder parser = dbf.newDocumentBuilder();

			// Tell the parser how to handle errors.  Note that in the JAXP API,
			// DOM parsers rely on the SAX API for error handling
			parser.setErrorHandler(new ErrorHandler() {
				public void warning(SAXParseException e) {
					log.warn(e);
				}
				public void error(SAXParseException e) {
					log.error(e);
				}
				public void fatalError(SAXParseException e)
				throws SAXException {
					log.fatal(e);
					throw e;   // re-throw the error
				}
			});

			// Finally, use the JAXP parser to parse the file.  This call returns
			// A Document object.  Now that we have this object, the rest of this
			// class uses the DOM API to work with it; JAXP is no longer required.
			Document document = parser.parse(stream);
//			Document document = parser.parse(new File("/home/rja/tmp/crawl/all"));
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			// traverse document and put everything into Bookmark Objects
			NodeList posts = document.getElementsByTagName("post");
			for (int i = 0; i < posts.getLength(); i++) {
				Element post = (Element)posts.item(i);
				bookmark = new Bookmark();

				bookmark.setTitle(post.getAttribute("description"));
				bookmark.setUrl(post.getAttribute("href"));
				bookmark.setTags(post.getAttribute("tag"));
				if (!bookmark.getTag().isValid()) {
					// user has not tag --> insert one
					bookmark.addTag(Tag.IMPORTED_TAG);
				}
				bookmark.setExtended(post.getAttribute("extended"));
				try {
					bookmark.setDate(df.parse(post.getAttribute("time")));
				} catch (ParseException e) {
					System.out.println("BookmarkHandler: " + e);
				}
				bookmark.setToIns(true);
				bookmark.setUser(currUser);
				
				// use grouping settings from del.icio.us
				if (post.hasAttribute("shared")){
					if ("no".equals(post.getAttribute("shared"))){
						bookmark.setGroupid(GroupID.PRIVATE.getId());
					}
				} else {
					bookmark.setGroupid(GroupID.PUBLIC.getId());
				}
				
				//bookmark.setGroupid(groupid);	
				bookmarks.add(bookmark);
			}
			// close all resources that we do not need
			// now that the web page has been obtained
			/* Calling the close() methods on the InputStream or OutputStream 
			 * of an URLConnection after a request may free network resources 
			 * associated with this instance, unless particular protocol 
			 * specifications specify different behaviours for it. 
			 * [http://java.sun.com/j2se/1.4.2/docs/api/ : java.net.URLConnection] */
			stream.close(); // closes also connection 


		} catch (MalformedURLException e) {
			log.fatal(e);
		}
		return bookmarks;
	}


	private LinkedList<Bookmark> getBookmarksFromFirefox(File bookmarkFile, String currUser, int groupid) throws FileNotFoundException {

		final Document document = XmlUtils.getDOM(new FileInputStream(bookmarkFile));

		//DEBUG INFOS ERZEUGEN
		//File fout = new File("Desktop/TEST_OUT.html");
		//FileOutputStream out = new FileOutputStream(fout);
		//org.w3c.dom.Document document = tidy.parseDOM(in, out);

		//get first DL-node containing all links and folders
		try {
			final Node mainFolder = document.getElementsByTagName("body").item(0).getChildNodes().item(1);
			if (mainFolder != null) {
				return createBookmarks(mainFolder, null, null, currUser, groupid);
			}
		} catch (final Exception e) {
			log.fatal("Error on importing FireFox bookmarks: " + e);
		}

		return null;
	}	

	/**
	 * Parses a given node and extracts all links and folders. Uppertags contains all tags provided by nodes above the given node (folder).
	 * Bookmarks is requiered because createBookmarks works recursively.
	 * 
	 * @param Node folder
	 * @param Vector<String> upperTags
	 * @param LinkedList<Bookmark>bookmarks
	 * @return
	 */	
	private LinkedList<Bookmark> createBookmarks(Node folder, Vector<String> upperTags, LinkedList<Bookmark> bookmarks, String user, int groupid) {
		//if no add_time attribute can be found fakeDate is used
		Date fakeDate = new Date();
		//if this method is called for the first time bookmarks has to become initialized
		if (bookmarks == null){
			bookmarks = new LinkedList<Bookmark>();
		}
		//every node requires his own tags
		Vector<String> tags;
		//if tags are provided by upper nodes these tags belong to this node too
		if (upperTags != null){
			tags = (Vector<String>)upperTags.clone();
		}
		//if no tags are provided create a new vector
		else{
			tags = new Vector<String>();
		}
		//nodelist to parse all children of the given node
		NodeList children = folder.getChildNodes();
		//String to save a foldername if its name is given in a sibling of the concerning DL
		String sepTag = "";

		for (int i = 0; i < children.getLength(); i++) {
			Node currentNode = children.item(i);
			//connect all upper tags with the currentNode
			Vector<String> myTags = (Vector<String>)tags.clone();
			if (!"".equals(sepTag)){
				myTags.add(sepTag);
			}

			//is currentNode a folder?
			if ("dd".equals(currentNode.getNodeName())){
				NodeList secondGen = currentNode.getChildNodes();
				//only containing a name?
				//yes, keep tag
				if (secondGen.getLength() == 1 && "h3".equals(secondGen.item(0).getNodeName())){
					sepTag = secondGen.item(0).getFirstChild().getNodeValue().replaceAll("->|<-|\\s", "_");
				} else if (secondGen.getLength() > 1){ //filtert dd-knoten, die nur einen p-knoten besitzen
					//else find all folders an theis names
					for (int j = 0; j < secondGen.getLength(); j++) {
						Node son = secondGen.item(j);
						if ("h3".equals(son.getNodeName())){
							//if sepTag != "" remove last added tag and reset sepTag
							if (!"".equals(sepTag)){
								myTags.remove(sepTag);
								sepTag = "";
							}
							//if upperTags != myTags, a parallel branch was parsed -> reset myTags
							if (tags.size() != myTags.size()){
								myTags = tags;
							}
							//add a found tag
							myTags.add(son.getFirstChild().getNodeValue().replaceAll("->|<-|\\s", "_"));
						}
						//all dl-nodes are new folders
						if ("dl".equals(son.getNodeName())){
							//create bookmarks from new found node
							createBookmarks(son, myTags, bookmarks, user, groupid);
						}
					}//for(int j=...
				}//else if
			}// if ("dd".equals....
			//if its no folder.... is it a link?

			/*sometimes the tidy parser decides that <dt></dt> has childnodes ... need to 
			 * check if the childnode of <dt> is an <a> to avoid NullPointerExceptions!!!! 
			 */
			else if ("dt".equals(currentNode.getNodeName()) && "a".equals(currentNode.getFirstChild().getNodeName())){
				//it is a link
				//create bookmark-object

				//need to check if the <a>-Tag has a name (ChildNodes) i.e. <a href="http://www.foo.bar"></a> causes a failure
				if (currentNode.getFirstChild().hasChildNodes() == true){
					Bookmark bookmark = new Bookmark();
					bookmark.setTitle(currentNode.getFirstChild().getFirstChild().getNodeValue());
					bookmark.setUrl(currentNode.getFirstChild().getAttributes().getNamedItem("href").getNodeValue());
					//add tags/relations to bookmark
					if (upperTags != null){
						//only 1 tag found -> add a tag
						if (upperTags.size() == 1){
							bookmark.setTags(upperTags.elementAt(0));
						}
						else{
							//more tags found -> add relations
							for (int tagCount = 0; tagCount < upperTags.size()-1;tagCount++){
								String upper = upperTags.elementAt(tagCount);
								String lower = upperTags.elementAt(tagCount + 1);
								bookmark.addTagRelation(lower, upper);
								bookmark.addTag(upper);
								bookmark.addTag(lower);
							}
						}
					}
					else{
						/* 
						 * link found in "root-folder" -> no folder hierarchy found
						 *
						 * check for "TAGS" attribute (common in del.icio.us export)
						 */
						final Node tagNode = currentNode.getFirstChild().getAttributes().getNamedItem("tags");
						if (tagNode != null) {
							/*
							 * del.icio.us export tags are comma-separated
							 */
							final StringTokenizer token = new StringTokenizer(tagNode.getNodeValue(), ",");
							while (token.hasMoreTokens()) {
								bookmark.addTag(token.nextToken());
							}
						} else {
							// really no tags found -> set imported tag
							bookmark.setTags(Tag.IMPORTED_TAG);
						}
					}
					bookmark.setDate(fakeDate);
					bookmark.setToIns(true);
					bookmark.setUser(user);
					bookmark.setGroupid(groupid);
					//descriptions are saved in a sibling of of a node containing a link
					if (currentNode.getNextSibling() != null && "dd".equals(currentNode.getNextSibling().getNodeName())){
						bookmark.setExtended(currentNode.getNextSibling().getFirstChild().getNodeValue());
					}
					bookmarks.add(bookmark);
				}
			}
		}
		return bookmarks;
	}


	/**
	 * computes a date from a given firefox-date-string
	 * @param String date
	 * @return Date date
	 */
	private Date computeDate(String dateString) {
		return new Date(Long.valueOf(dateString).longValue() * 1000);
	}


}
