package helpers.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import resources.Bookmark;

public class DBBookmarkGetManager {

	private static final String SQL_SELECT_BOOK = "SELECT * FROM bookmark b, urls u, groupids gi "
			+ "WHERE b.user_name = ? "
			+ "  AND gi.group=b.group "
			+ "  AND b.book_url_hash=u.book_url_hash";
	private final String SQL_SELECT_BOOK_HASH = SQL_SELECT_BOOK + " AND b.book_url_hash = ?";
	private final String SQL_SELECT_BOOK_URL  = SQL_SELECT_BOOK + " AND u.book_url = ?";
	private final String SQL_SELECT_TAGS     = "SELECT tag_name FROM tas WHERE content_id = ?";

	private PreparedStatement stmtP  = null;
	private PreparedStatement stmtPT = null;
	private ResultSet rst            = null;

	/**
	 * @param conn
	 * @param hash if <code>true</code> getBookmark() will use the hash, otherwise the real URL
	 * @throws SQLException
	 */
	public void prepareStatements(Connection conn, boolean hash) throws SQLException {
		if (hash) {
			stmtP = conn.prepareStatement(SQL_SELECT_BOOK_HASH);
		} else {
			stmtP = conn.prepareStatement(SQL_SELECT_BOOK_URL);
		}
		stmtPT = conn.prepareStatement(SQL_SELECT_TAGS);
	}

	public void closeStatements() {
		if (stmtP  != null) {try {stmtP.close(); } catch (SQLException e) {}stmtP  = null;}
		if (stmtPT != null) {try {stmtPT.close();} catch (SQLException e) {}stmtPT = null;}
		if (rst    != null) {try {rst.close();   } catch (SQLException e) {}rst    = null;}
	}

	/**
	 * get Bookmark by hash (or url) and user name
	 * 
	 * @param hash
	 * @param user
	 * @return the bibtex object for given user/hash combination, otherwise
	 *         <code>null</code>
	 */
	public Bookmark getBookmark(String hash, String user) throws SQLException {
		Bookmark bookmark = null;
		stmtP.setString(1, user);
		stmtP.setString(2, hash);
		rst = stmtP.executeQuery();
		
		if (rst.next()) {
			// entry found --> fill object
			bookmark = new Bookmark();
			bookmark.setUrl(rst.getString("book_url"));
			bookmark.setTitle(rst.getString("book_description"));
			bookmark.setExtended(rst.getString("book_extended"));
			bookmark.setGroup(rst.getString("group_name"));
			bookmark.setGroupid(rst.getInt("group"));
			bookmark.setContentID(rst.getInt("content_id"));
			bookmark.setUser(user);

			int content_id = rst.getInt("content_id");

			// look for tags
			stmtPT.setInt(1, content_id);
			rst = stmtPT.executeQuery();
			while (rst.next()) {
				bookmark.addTag(rst.getString("tag_name"));
			}
		}
		return bookmark;
	}

}