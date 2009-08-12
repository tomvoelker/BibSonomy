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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.database.systemstags.SystemTags;
import org.bibsonomy.recommender.tags.database.RecommenderStatisticsManager;

import resources.Bibtex;
import beans.BibtexHandlerBean;
import filters.SessionSettingsFilter;

public class BibtexShowHandler extends HttpServlet{

	private static final String SYS_RELEVANT_FOR = SystemTags.RELEVANTFOR.getPrefix();
	private static final long serialVersionUID = 3833747689652301876L;
	private static final Log log = LogFactory.getLog(BibtexShowHandler.class);

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
		String defaultTags   = request.getParameter("tags");
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

		final String currUser = SessionSettingsFilter.getUser(request).getName(); 

		if (currUser == null) {
			// TODO: user will be redirected to login and from there back to the site he
			// wanted to post (because referer header is not changed by redirect)
			// this is not the best solution (I would prefer to be redirected back here), but
			// it's the simplest one
			String refer = "/login?referer="+URLEncoder.encode("/ShowBibtexEntry?"+request.getQueryString(), "UTF-8");
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
			final Bibtex bibtex = new Bibtex();
			final BibtexHandlerBean bean = new BibtexHandlerBean(bibtex);

			/*
			 * rja, 2009-06-16: added tags given in URL to allow external (copy) 
			 * links to populate tag field with default tags (needed by Hypertext 2009) 
			 */
			if (defaultTags != null && defaultTags.trim().length() > 0) bibtex.setTags(defaultTags);

			bibtex.setTitle(requTitle);
			bibtex.setAuthor(request.getParameter("author"));
			bibtex.setEditor(request.getParameter("editor"));
			bibtex.setYear(request.getParameter("year"));
			bibtex.setEntrytype(request.getParameter("entrytype"));
			/* the user entered data in a form therefore we have at least the title 
			 * and can look up the database, if the entry exists*/
			if (requTitle != null) {
				String showBibEntry = "SELECT *" 
					+ "FROM bibtex b, groupids i " 
					+ "WHERE b.simhash" + Bibtex.INTRA_HASH + " = ? "                  // this entry ...
					+ "  AND b.user_name = ? "             // from this user
					+ "  AND b.group = i.group ";          // join groupname
				stmtP = conn.prepareStatement(showBibEntry);
				stmtP.setString(1, bibtex.getHash());
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
				String groupWhereQuery = getQueryForGroups (conn, currUser, requUser, "b");
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
				bibtex.setAddress(rst.getString("address"));
				bibtex.setBibtexAbstract(rst.getString("bibtexAbstract"));
				bibtex.setAnnote(rst.getString("annote"));
				bibtex.setAuthor(rst.getString("author"));
				bibtex.setBooktitle(rst.getString("bookTitle"));
				bibtex.setChapter(rst.getString("chapter"));
				bibtex.setCrossref(rst.getString("crossref"));
				bibtex.setEdition(rst.getString("edition"));
				bibtex.setEditor(rst.getString("editor"));
				bibtex.setHowpublished(rst.getString("howpublished"));
				bibtex.setInstitution(rst.getString("institution"));
				bibtex.setJournal(rst.getString("journal"));
				bibtex.setKey(rst.getString("bKey"));
				bibtex.setMonth(rst.getString("month"));
				bibtex.setNote(rst.getString("note"));
				bibtex.setNumber(rst.getString("number"));
				bibtex.setOrganization(rst.getString("organization"));
				bibtex.setPages(rst.getString("pages"));
				bibtex.setPublisher(rst.getString("publisher"));
				bibtex.setSchool(rst.getString("school"));
				bibtex.setSeries(rst.getString("series"));
				bibtex.setTitle(rst.getString("title"));
				bibtex.setType(rst.getString("type"));
				bibtex.setVolume(rst.getString("volume"));
				bibtex.setYear(rst.getString("year"));
				bibtex.setUrl(rst.getString("url"));
				bibtex.setDay(rst.getString("day"));
				bibtex.setRating(rst.getInt("rating"));

				// set privnote only, if this is the same user!
				if (currUser.equals(rst.getString("user_name"))) {
					bibtex.setPrivnote(rst.getString("privnote"));
				}

				bibtex.setMisc(rst.getString("misc"));
				bibtex.setBibtexKey(rst.getString("bibtexKey"));		                   				        	    
				bibtex.setGroup(rst.getString("group_name"));
				bibtex.setDescription(rst.getString("description"));
				bibtex.setEntrytype(rst.getString("entrytype"));

				int content_id  = rst.getInt("content_id");


				// TODO: this tag stuff should be moved to DBTagManager (also tag managing in ResourceHandler) 
				// get all tags for this entry

				if (!copy) {
					// remember old hash to do "move" operation
					bean.setOldhash(bibtex.getHash());

					stmtP = conn.prepareStatement("SELECT tag_name FROM tas WHERE content_id = ?");
					stmtP.setInt(1, content_id);
					rst = stmtP.executeQuery ();
					while (rst.next()) {
						bibtex.addTag(rst.getString("tag_name"));
					}					
				}
			}

			//bean.setJump(request.getParameter("jump")); // TODO: what was this good for? rja, 02.12.2005
			bean.setCopytag(request.getParameter("copytag"));


			/*
			 * handle "relevantFor:group" tags
			 */
			final String tags = bean.getTags();
			if (tags != null && tags.contains(SYS_RELEVANT_FOR)) {
				final StringBuffer buf = new StringBuffer(); // new tag string
				final String[] tagParts = tags.split("\\s"); // tags

				for (final String tag : tagParts) {
					if (tag.indexOf(SYS_RELEVANT_FOR) == 0) {
						// tag starts with sys:relevantFor:
						final String group = tag.substring(SYS_RELEVANT_FOR.length() + 1); // + 1 = ":"
						// FIXME: check validity!
						bean.getRelevantFor().add(group);
					} else {
						buf.append(tag + " ");
					}
				}
				bean.setTags(buf.toString().trim());
			}


			/*
			 * add post id (for recommender) to bean
			 */

			bean.setPostID(RecommenderStatisticsManager.getNewPID());


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


	/* 
	 * returns a String for the Query of groups the user is in (including "friends", if she is a friend of the requested user 
	 */
	private static String getQueryForGroups (Connection conn, String currUser, String requUser, String table) {
		if (currUser != null && requUser != null && requUser.equals(currUser)) {
			return "";
		}
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