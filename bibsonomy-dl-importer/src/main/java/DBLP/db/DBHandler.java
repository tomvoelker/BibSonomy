package DBLP.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashSet;

import org.bibsonomy.common.enums.HashID;

import DBLP.DBLPEvaluation;
import DBLP.constants.DBLPConstantsResult;


/*
 * The DBHandler gives Methods to get the MD5-Hash of a user-password and the 
 * hash-value of a given bibtex-key. It needs the values of the DBLPConstants.xml.
 */
public class DBHandler{
		
	private DBLPConstantsResult conResult = null;

	private Connection conn = null;
	
	private PreparedStatement stmtp = null;
	
	private ResultSet rst = null;
	
	private static final String SQL_GET_BIBKEYS = "SELECT bibtexKey FROM bibtex WHERE user_name=?";
	
	private static final String SQL_GET_USERHASH = "SELECT user_password FROM user WHERE user_name=?";
	
	private static final String SQL_GET_BOOKHASH = "SELECT book_url_hash FROM bookmark WHERE LEFT(book_extended,?)=? and user_name=?";
	
	private static final String SQL_GET_BOOK_CONTENT_ID = "SELECT content_id FROM bookmark WHERE LEFT(book_extended,?)=? and user_name=?";
	
	private static final String SQL_GET_BIBKEYS_BOOKMARK = "SELECT book_extended FROM bookmark WHERE user_name=?";	
	
	private static final String SQL_GET_BIBHASH = "SELECT simhash" + HashID.INTRA_HASH.getId() + " FROM bibtex WHERE user_name=? AND bibtexKey=?";
	
	private static final String SQL_GET_LAST_UPDATE = "SELECT lastupdate FROM DBLP ORDER BY lastupdate DESC LIMIT 1";
	
	private static final String SQL_SET_LAST_UPDATE = "INSERT INTO DBLP (`lastupdate`) VALUES (?)";
	
	private static final String SQL_SELECT_ENTRYTYPES = "SELECT entrytype, count(entrytype) AS count FROM bibtex WHERE user_name=? GROUP BY entrytype";
	
	private static final String SQL_COUNT_BOOKMARKS = "SELECT count(*) AS count FROM bookmark WHERE user_name=?";

	
	/* 
	 * DBLP insert failure types
	 */
	private static final String WARNING_FAILURE = "warning";
	
	private static final String DUPLICATE_FAILURE = "duplicate";
	
	private static final String INCOMPLETE_FAILURE = "incomplete";
	
	private static final String INCOMPLETE_AUTHOR_EDITOR_FAILURE = "incomplete_author_editor";

	private static final String BOOKMARK_EMPTY_URL_FAILURE = "bookmark_empty_url";
	
	private static final String EXCEPTION = "exception";
	
	private static final String UPLOAD_ERROR = "upload_error";
	
	public DBHandler(DBLPConstantsResult conResult) throws Exception{
		this.conResult = conResult;
		if(!conResult.isValid() || conResult == null)
			throw new Exception("DBLPConstants.xml is invalid");
	}
	
	public void open() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		String url = "jdbc:mysql://" + conResult.getDbhost() + "/" + conResult.getDbname() + "?autoReconnect=true&amp;useUnicode=true&amp;characterEncoding=utf-8&amp;mysqlEncoding=utf8";
       	Class.forName ("com.mysql.jdbc.Driver").newInstance ();
       	conn = DriverManager.getConnection (url, conResult.getDbuser(), conResult.getDbpassword());		
	}

	public void close() {
		try {if (rst   != null)	rst.close();   } catch (SQLException e) {} rst   = null;
		try {if (stmtp != null)	stmtp.close(); } catch (SQLException e) {} stmtp = null;
		try {if (conn  != null)	conn.close();  } catch (SQLException e) {} conn  = null;		
	}

	/*
	 * count the number of bookmarks from dblp-user and store it in DBLPEvaluation
	 */
	public void countBookmarks(DBLPEvaluation eval) throws Exception{
		if(conn!=null){
			stmtp = conn.prepareStatement(SQL_COUNT_BOOKMARKS);
			stmtp.setString(1, conResult.getUser());
			rst = stmtp.executeQuery();
			
			if(rst.next()){
				eval.setInsert_bookmarks(rst.getInt("count"));
			}			
		}
	}
	
	/*
	 * count the number of entrytypes and store it in DBLPEvaluation
	 */
	public void countEntrytypes(DBLPEvaluation eval) throws Exception{
		if(conn!=null){
			stmtp = conn.prepareStatement(SQL_SELECT_ENTRYTYPES);
			stmtp.setString(1, conResult.getUser());
			rst = stmtp.executeQuery();
			
			while(rst.next()){
				eval.setInsert(rst.getString("entrytype"), rst.getInt("count"));
			}			
		}
	}
	
	/*
	 * get all bibtex-keys from dblp-user
	 */
	public HashSet<String> getBibKeysBibtex() throws SQLException{
		HashSet<String> set = new HashSet<String>();
		if (conn != null) {
			stmtp = conn.prepareStatement(SQL_GET_BIBKEYS);
			stmtp.setString(1, conResult.getUser());
			rst = stmtp.executeQuery();
			
			while (rst.next()) {
				set.add(rst.getString("bibtexKey"));
			}
		}
		return set;
	}
	
	
	/*
	 * get all bookmark-keys from dblp-user
	 */
	public HashSet<String> getBibKeysBookmark() throws SQLException {
		HashSet<String> set = new HashSet<String>();
		if (conn != null) {
			stmtp = conn.prepareStatement(SQL_GET_BIBKEYS_BOOKMARK);
			stmtp.setString(1, conResult.getUser());
			rst = stmtp.executeQuery();
			while (rst.next()) {
				String extended = rst.getString("book_extended");
				 
				int indexOfComma = extended.indexOf(",");
				if (indexOfComma > 0) {
					set.add(extended.substring(0, indexOfComma));
				}
			}
		}
		return set;		
	}
	
	/*
	 * returns the date of the last DBLP update
	 */
	public Date getDBLPDate() throws Exception {
		if (conn != null) {
			stmtp = conn.prepareStatement(SQL_GET_LAST_UPDATE);
			rst = stmtp.executeQuery();

			if(rst.next())
				return rst.getDate("lastupdate");
		} else 
			throw new Exception("Cannot connect to database server");
		return null;
	}

	/*
	 * set the date of the last DBLP update
	 */
	public void setDBLPDate(Date dblpdate) throws Exception{
       	if(conn!=null){
       		stmtp = conn.prepareStatement(SQL_SET_LAST_UPDATE);
       		stmtp.setDate(1, new java.sql.Date(dblpdate.getTime()));
       		stmtp.executeUpdate();
       	}else
       		throw new Exception("Cannot connect to database server");
	}

	/*
	 * returns hash from a bookmark with a given bibtexKey(in book_extended row) from the 
	 * BibSonomy-DBLP-User(from DBLPConstants.xml)
	 */
	public String getUrlHash(String bibKey) throws SQLException{
		bibKey = bibKey + ",";
		if (conn != null) {
       		stmtp = conn.prepareStatement(SQL_GET_BOOKHASH);
       		stmtp.setInt(1, bibKey.length());
       		stmtp.setString(2, bibKey);
       		stmtp.setString(3, conResult.getUser());
       		rst = stmtp.executeQuery();
       		
       		if(rst.next()) return rst.getString("book_url_hash");
       	}else
       		throw new SQLException("Cannot connect to database server");
		return null;
	}
	
	/*
	 * returns content_id from a bookmark with a given bibtexKey(in book_extended row) from the 
	 * BibSonomy-DBLP-User(from DBLPConstants.xml)
	 */
	public int getBookContentId(String bibKey) throws Exception{
		bibKey = bibKey + ",";
		int bookid = 0;
		if(conn!=null){
       		stmtp = conn.prepareStatement(SQL_GET_BOOK_CONTENT_ID);
       		stmtp.setInt(1, bibKey.length());
       		stmtp.setString(2, bibKey);
       		stmtp.setString(3, conResult.getUser());
       		rst = stmtp.executeQuery();
       		
       		if(rst.next())
       			bookid = rst.getInt("content_id");
       	}else
       		throw new Exception("Cannot connect to database server");
		return bookid;
	}
	
	/*
	 * returns hash from a publ with a given bibtexKey from the 
	 * BibSonomy-DBLP-User(from DBLPConstants.xml)
	 */
	public String getBibhash(String bibkey) throws SQLException{
		String bibhash = null;
       	if(conn!=null){
       		stmtp = conn.prepareStatement(SQL_GET_BIBHASH);
       		stmtp.setString(1, conResult.getUser());
       		stmtp.setString(2, bibkey);
       		rst = stmtp.executeQuery();
       		
       		if(rst.next())
       			bibhash = rst.getString("simhash" + HashID.INTRA_HASH.getId());
       	}else
       		throw new SQLException("Cannot connect to database server");
		return bibhash;
	}
	
	/*
	 * returns the MD5-Hash of the password for the given user name
	 */
	public String getUserhash() throws SQLException {
       	if (conn != null) {
       		stmtp = conn.prepareStatement(SQL_GET_USERHASH);
       		stmtp.setString(1, conResult.getUser());
       		rst = stmtp.executeQuery();
       		if(rst.next()) return rst.getString("user_password");
       	} else
       		throw new SQLException("Cannot connect to database server");
		return null;
	}

}