/*
 * This class is used to retrieve  
 * bookmark data from the database
 */
package servlets;

import filters.SessionSettingsFilter;
import helpers.database.DBBibtexManager;
import helpers.database.DBBookmarkGetManager;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.UrlCompositeScraper;

import resources.Bookmark;
import beans.BookmarkHandlerBean;
import beans.UserBean;

public class BookmarkShowHandler extends HttpServlet{

	private static final Log log = LogFactory.getLog(DBBibtexManager.class);
	private static final long serialVersionUID = 3833747689652301876L;
	private DataSource dataSource;

	private static String projectHome = null;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
			projectHome = config.getServletContext().getInitParameter("projectHome");
		} catch (NamingException ex) {
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy", ex);
		}
	}


	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doPost(request,response);
	}


	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		log.fatal("DEPRECATED: " + BookmarkShowHandler.class.getSimpleName() + " called. Query string: " + request.getQueryString());
		
		/* get url */
		String requUrl  = request.getParameter("url");
		String requHash = request.getParameter("hash");
		if (requUrl == null && requHash == null) {
			// no URL given --> go to bookmark posting page
			response.sendRedirect("/post_bookmark");
			return;
		}	

		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
		if (currUser == null) {
			// TODO: user will be redirected to login and from there back to the site she
			// wanted to bookmark (because referer header is not changed by redirect)
			// this is not the best solution (I would prefer to be redirected back here), but
			// it's the simplest one
			String refer = "/login?referer="+URLEncoder.encode("/ShowBookmarkEntry?"+request.getQueryString(), "UTF-8");
			response.sendRedirect(refer);
			return;
		}

		/* Establish all connections, result sets and statements */
		Connection conn = null;
		DBBookmarkGetManager bookman = new DBBookmarkGetManager();

		/* Because the dataSource instance variable is potentially 
		 * shared across multiple threads, access to the variable 
		 * must be from within a synchronized block. */
		try {
			synchronized(dataSource) {
				if (dataSource != null) {
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}
			/*
			 * try to get bookmark from database (either per URL or per hash)
			 */
			Bookmark bookmark = null;
			if (requUrl != null) {
				bookman.prepareStatements(conn, false);
				bookmark = bookman.getBookmark(requUrl, currUser);
			} else if (requHash != null) {
				bookman.prepareStatements(conn, true);
				bookmark = bookman.getBookmark(requHash, currUser);

			}

			/*
			 * if not found, fill bookmark from request parameters 
			 */
			if (bookmark == null) {
				bookmark = new Bookmark();
				bookmark.setUrl(requUrl);
				bookmark.setExtended(request.getParameter("extended"));
				bookmark.setGroup(request.getParameter("group"));
				/* null values let recommender die, therefore: set description only if not null */ 
				if (request.getParameter("description") != null) {
					bookmark.setTitle(request.getParameter("description"));
				}
			}

			/*
			 * build bean
			 */
			BookmarkHandlerBean bean = new BookmarkHandlerBean(bookmark);
			bean.setOldurl(bookmark.getUrl());
			bean.setCopytag(request.getParameter("copytag"));
			bean.setJump(request.getParameter("jump"));
			/*
			 * tagging of tags
			 * 
			 * this methods extracts the tag from the url but does not add the relations
			 */
			bean.getTaggedTag(projectHome, currUser);


			/*
			 * if the bookmark is scrapable the bibtex-string will be put into the request
			 */
			String _tempBib = tryToScrape(requUrl);
			if ( _tempBib != null){
				request.setAttribute("scraped", _tempBib);
			}

			/* Bean wird dem request angehängt */ 
			request.setAttribute("bookmarkHandlerBean",bean);			
			getServletConfig().getServletContext().getRequestDispatcher("/edit_bookmark").forward(request, response);

		} catch (SQLException e) {
			log.fatal("Could not get Bookmark: " + e);
			response.sendRedirect("/errors/databaseError.jsp");
		} finally {
			if (conn  != null) {try {conn.close(); } catch (SQLException e) {} conn  = null;}
		}
	}

	private String tryToScrape(String url) {
		try {
			//init ScrapingContext
			ScrapingContext sc = new ScrapingContext(new URL(url));

			//get all scrapers except the snippetscraper
			UrlCompositeScraper test = new UrlCompositeScraper();
			
			//if the bookmark is scrapable return the bibtexresult 
			if (test.scrape(sc)){
				return sc.getBibtexResult();
			}
		} catch (Exception e) {
			log.fatal("Could not scrape URL: " + e);
		}
		return null;
	}
}
