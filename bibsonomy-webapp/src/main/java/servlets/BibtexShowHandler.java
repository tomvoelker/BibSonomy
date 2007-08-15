/*
 * This class is used to retrieve  
 * bibtex data from the database
 */
package servlets;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

import org.apache.log4j.Logger;

import recommender.RecommenderFrontEnd;
import resources.Bibtex;
import beans.BibtexHandlerBean;
import beans.UserBean;
import filters.SessionSettingsFilter;

public class BibtexShowHandler extends HttpServlet{
	
	private static final long serialVersionUID = 3833747689652301876L;
	private static final Logger log = Logger.getLogger(BibtexShowHandler.class);
	
	private DataSource dataSource;
	
	public void init(ServletConfig config) throws ServletException{	
		super.init(config); 
		try {
			dataSource = (DataSource) ((Context) new InitialContext().lookup("java:/comp/env")).lookup("jdbc/bibsonomy");
		} catch (NamingException ex){
			log.fatal("Could not retrieve java:comp/env/bibsonomy: " + ex);
			throw new ServletException("Cannot retrieve java:/comp/env/bibsonomy",ex);
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		/* GET PARAMETERS 
		 * at least a title or hash should be given, so that we can check in the
		 * database, if this entry exists
		 * author, editor, year and entrytype are also accepted as parameters and
		 * checked against the database */
		String requTitle     = request.getParameter("title");
		String requHash      = request.getParameter("hash");
		/* if we want to copy an entry from another user, we have to know
		 * the username; if a username is given, we assume that the user
		 * wants to copy this entry*/
		String requUser = request.getParameter("user"); 
		boolean copy    = requUser != null; 

		/* if hash and title are empty, we send the user back to the post_site to enter something */
		if ((requTitle == null || requTitle.equals("")) && (requHash == null || requHash.equals(""))) {
			response.sendRedirect("/post_bibtex");
			return;
		}
		
		/* get username */
		UserBean user = SessionSettingsFilter.getUser(request);
		String currUser = user.getName(); 
	
		if (currUser == null) {
			// TODO: user will be redirected to login and from there back to the site he
			// wanted to post (because referer header is not changed by redirect)
			// this is not the best solution (I would prefer to be redirected back here), but
			// it's the simplest one
			String refer = "/login?referer="+URLEncoder.encode("/ShowBibEntry?"+request.getQueryString(), "UTF-8");
			response.sendRedirect(refer);
			return;
		}

		/* Establish all connections, result sets and statements 
		 * which we will need */
		Connection conn         = null;
		ResultSet rst           = null;
		PreparedStatement stmtP = null;
	
		
		/* Because the dataSource instance variable is potentially 
		 * shared across multiple threads, access to the variable 
		 * must be from within a synchronized block. */
		try {
			synchronized(dataSource) {
				if(dataSource != null){
					conn = dataSource.getConnection();
				} else {
					throw new SQLException("No Datasource");
				}
			}
			
			/* generate a new bean and fill it with the request-parameters
			 * this way (with a filled bean) we can get the apropriate hash
			 * and can ask the database just for the hash */
			BibtexHandlerBean bean = new BibtexHandlerBean();
			bean.setTitle(requTitle);
			bean.setAuthor(request.getParameter("author"));
			bean.setEditor(request.getParameter("editor"));
			bean.setYear(request.getParameter("year"));
			bean.setEntrytype(request.getParameter("entrytype"));
			
			/* the user entered data in a form therefore we have at least the title 
			 * and can look up the database, if the entry exists*/
			if (requTitle != null) {
				String showBibEntry = "SELECT *" 
					+ "FROM bibtex b, groupids i " 
					+ "WHERE b.simhash" + Bibtex.INTRA_HASH + " = ? "                  // this entry ...
					+ "  AND b.user_name = ? "             // from this user
					+ "  AND b.group = i.group ";          // join groupname
				stmtP = conn.prepareStatement(showBibEntry);
				stmtP.setString(1, bean.getHash());
				stmtP.setString(2, currUser);
			}
			
			/* the user wants to edit an entry, therefore we have a hash, but no requUser */
			if (requTitle == null && requHash != null && !copy) {
				String showBibEntry = "SELECT * "
					+ "FROM bibtex b, groupids i "
					+ "WHERE b.simhash" + Bibtex.INTRA_HASH + " = ?"                   // this entry
					+ "  AND b.user_name = ?"              // from this user
					+ "  AND b.group = i.group ";          // join groupname
				stmtP = conn.prepareStatement(showBibEntry);
				stmtP.setString(1, requHash);
				stmtP.setString(2, currUser);
			}
			
			/* the user wants to copy an entry, therefore we have a hash and also a requUser */
			if (requTitle == null && requHash != null && copy) {
				/* check, in which groups the current user is */
				String groupWhereQuery = ResourceHandler.getQueryForGroups (conn, currUser, requUser, "b");
				String showBibEntry = "SELECT * "
					+ "FROM bibtex b, groupids i "
					+ "WHERE b.simhash" + Bibtex.INTRA_HASH + " = ?"
					+ "  AND b.user_name = ?"
					+ groupWhereQuery
					+ "  AND b.group = i.group";
				stmtP = conn.prepareStatement(showBibEntry);
				stmtP.setString(1, requHash);
				stmtP.setString(2, requUser);
			}
			

			/* get the entry from the database */
			rst = stmtP.executeQuery();
			if (rst.next()) {
				// found entry in database --> fill bean
				bean.setAddress(rst.getString("address"));
				bean.setBibtexAbstract(rst.getString("bibtexAbstract"));
				bean.setAnnote(rst.getString("annote"));
				bean.setAuthor(rst.getString("author"));
				bean.setBooktitle(rst.getString("bookTitle"));
				bean.setChapter(rst.getString("chapter"));
				bean.setCrossref(rst.getString("crossref"));
				bean.setEdition(rst.getString("edition"));
				bean.setEditor(rst.getString("editor"));
				bean.setHowpublished(rst.getString("howpublished"));
				bean.setInstitution(rst.getString("institution"));
				bean.setJournal(rst.getString("journal"));
				bean.setKey(rst.getString("bKey"));
				bean.setMonth(rst.getString("month"));
				bean.setNote(rst.getString("note"));
				bean.setNumber(rst.getString("number"));
				bean.setOrganization(rst.getString("organization"));
				bean.setPages(rst.getString("pages"));
				bean.setPublisher(rst.getString("publisher"));
				bean.setSchool(rst.getString("school"));
				bean.setSeries(rst.getString("series"));
				bean.setTitle(rst.getString("title"));
				bean.setType(rst.getString("type"));
				bean.setVolume(rst.getString("volume"));
				bean.setYear(rst.getString("year"));
				bean.setUrl(rst.getString("url"));
				bean.setDay(rst.getString("day"));
				
				bean.setMisc(rst.getString("misc"));
				bean.setBibtexKey(rst.getString("bibtexKey"));		                   				        	    
				bean.setGroup(rst.getString("group_name"));
				bean.setDescription(rst.getString("description"));
				bean.setEntrytype(rst.getString("entrytype"));

				int content_id  = rst.getInt("content_id");
				

				// TODO: this tag stuff should be moved to DBTagManager (also tag managing in ResourceHandler) 
				// get all tags for this entry
				if (!copy) {
					// remember old hash to do "move" operation
					bean.setOldhash(bean.getHash());
					
					stmtP = conn.prepareStatement("SELECT tag_name FROM tas WHERE content_id = ?");
					stmtP.setInt(1, content_id);
					rst = stmtP.executeQuery ();
					while (rst.next()) {
						bean.addTag(rst.getString("tag_name"));
					}					
				}
			}
			
			bean.setRecommendedTags( RecommenderFrontEnd.getRecommendation( currUser, bean.getHash(), Bibtex.class, bean.getTitle()) );
			//bean.setJump(request.getParameter("jump")); // TODO: what was this good for? rja, 02.12.2005
			bean.setCopytag(request.getParameter("copytag"));
			
			/* Bean wird dem request angehängt */
			request.setAttribute("bibtexHandlerBean",bean);			
			getServletConfig().getServletContext().getRequestDispatcher("/edit_bibtex").forward(request, response);	
			
		} catch (SQLException e) {
			log.fatal(e);
			response.sendRedirect("/errors/databaseError.jsp");
		} finally {
			// Always make sure result sets and statements are closed,
			// and the connection is returned to the pool
			if (rst   != null) {try {rst.close();  } catch (SQLException e) {} rst   = null;}
			if (stmtP != null) {try {stmtP.close();} catch (SQLException e) {} stmtP = null;}
			if (conn  != null) {try {conn.close(); } catch (SQLException e) {} conn  = null;}
		}
	}	
}