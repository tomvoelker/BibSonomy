package helpers.database;

import helpers.constants;

import java.sql.*;

import resources.Bibtex;
import resources.Resource;


/**
 * This class gets content ids of existing resources (given hash and user name) and also generates new content ids.
 *
 */
public class DBContentManager {
	
	private static final String SQL_GET_CONTENT_ID_FOR_BOOKMARK = "SELECT content_id FROM bookmark WHERE book_url_hash = ? AND user_name = ?"; 
	private static final String SQL_GET_CONTENT_ID_FOR_BIBTEX   = "SELECT content_id FROM bibtex   WHERE simhash" + Bibtex.INTRA_HASH + " = ? AND user_name = ?";	
	
	private static final String SQL_GETNEW_CONTENT_ID  = "SELECT value FROM ids WHERE name        = " + constants.SQL_IDS_CONTENT_ID;
	private static final String SQL_UPDATE_CONTENT_ID  = "UPDATE ids SET value=value+1 WHERE name = " + constants.SQL_IDS_CONTENT_ID;

	
	private PreparedStatement stmtP_get_contentid    = null;
	private PreparedStatement stmtP_getnew_contentid = null;
	private PreparedStatement stmtP_update_contentid = null;
	private ResultSet rst = null;
	
	/* prepares statements */
	public void prepareStatementsForBookmark (Connection conn) throws SQLException {
		stmtP_get_contentid = conn.prepareStatement(SQL_GET_CONTENT_ID_FOR_BOOKMARK);
		prepareGenericStatements(conn);
	}
	public void prepareStatementsForBibtex (Connection conn) throws SQLException {
		stmtP_get_contentid = conn.prepareStatement(SQL_GET_CONTENT_ID_FOR_BIBTEX);
		prepareGenericStatements(conn);
	}
	private void prepareGenericStatements (Connection conn) throws SQLException {
		stmtP_getnew_contentid = conn.prepareStatement(SQL_GETNEW_CONTENT_ID);
		stmtP_update_contentid = conn.prepareStatement(SQL_UPDATE_CONTENT_ID);
	}
	

	
	/* close all statements and the resultset */
	public void closeStatements () {
		if(stmtP_get_contentid    !=null){try{stmtP_get_contentid.close();    } catch (SQLException e){}stmtP_get_contentid    = null;}
		if(stmtP_getnew_contentid !=null){try{stmtP_getnew_contentid.close(); } catch (SQLException e){}stmtP_getnew_contentid = null;}
		if(stmtP_update_contentid !=null){try{stmtP_update_contentid.close(); } catch (SQLException e){}stmtP_update_contentid = null;}
		if(rst                    !=null){try{rst.close();                    } catch (SQLException e){}rst                    = null;}
	}
	
	public int getNewContentID() throws SQLException{
		stmtP_update_contentid.executeUpdate();
		rst = stmtP_getnew_contentid.executeQuery();
		rst.next();
		return rst.getInt(1);
	}
	
	/* query database for an existing content_id */
	public int getContentID (String user, String urlhash) {
		if (urlhash != null && user != null) {
			try {
				stmtP_get_contentid.setString(1, urlhash);
				stmtP_get_contentid.setString(2, user);
				rst = stmtP_get_contentid.executeQuery();
				if (rst.next()) {
					return rst.getInt("content_id");
				}
			} catch (SQLException e) {
				System.out.println("DBCM: " + e);
			} finally {
				if(rst != null) {try { rst.close(); } catch (SQLException e) { } rst = null;}
			}
		}
		return Resource.UNDEFINED_CONTENT_ID;
	}
	
}