package org.bibsonomy.batch.authors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @author claus
 * @version $Id: AuthorDB.java,v 1.10 2013-08-09 11:59:03 nosebrain Exp $
 */
public class AuthorDB {
	
	/** DB statements */
	private static final String INSERT_AUTHOR = "INSERT INTO author (first_name, middle, last_name, ctr) VALUES (?, ?, ?, ?)";
	private static final String INSERT_AUTHOR_BIBTEX_CONTENT = "INSERT INTO author_bibtex_content (author_id, content_id) VALUES (?, ?)";
	// TODO: do we need this information?
	private static final String INSERT_AUTHOR_BIBTEX_NAME = "INSERT INTO author_bibtex_name (author_id, bibtex_author_name) VALUES (?, ?)";
	private static final String SELECT_BIBTEX_AUTHORS = "SELECT b.author, b.content_id, b.editor FROM bibtex b WHERE content_id > ? AND content_id <= ? AND b.`group` >= 0;";
	private static final String SELECT_AUTHORS = "SELECT a.author_id, bn.bibtex_author_name, bc.content_id " +
							"FROM author a, author_bibtex_name bn, author_bibtex_content bc " +
							"WHERE a.author_id = bn.author_id " +
							"AND a.author_id = bc.author_id";
	private static final String UPDATE_AUTHOR = "UPDATE author SET ctr = ? WHERE author_id = ?";
	private static final String DELETE_AUTHOR_BIBTEX_CONTENT = "DELETE FROM author_bibtex_content WHERE author_id = ? AND content_id = ?";
	
	private final Connection slaveConnection;
	private final Connection masterConnection;
	
	/**
	 * constructor to create a database connection
	 * @param slaveConnection
	 * @param masterConnection
	 * @throws Exception
	 */
	public AuthorDB(final Connection slaveConnection, final Connection masterConnection) throws Exception {
		this.slaveConnection = slaveConnection;
		this.masterConnection = masterConnection;
	}
	
	/**
	 * closes the database connection
	 * @throws SQLException 
	 */
	public void closeDBConnection() throws SQLException {
		if (this.masterConnection != null) {
			masterConnection.close();
		}
		if (this.slaveConnection != null) {
			slaveConnection.close();
		}
	}
	
	/**
	 * 
	 * @param lastId 
	 * @param currentContentid 
	 * @return ResultSet of all BibtexAuthors
	 * @throws SQLException 
	 */
	public ResultSet getBibtexAuthors(long lastId, final long currentContentid) throws SQLException {
		final PreparedStatement stmt = this.slaveConnection.prepareStatement(SELECT_BIBTEX_AUTHORS);
		stmt.setLong(1, lastId);
		stmt.setLong(2, currentContentid);
		
		return stmt.executeQuery();
	}
	
	/**
	 * 
	 * @return ResultSet of all authors
	 * @throws SQLException 
	 */
	public ResultSet getAuthors() throws SQLException {		
		final PreparedStatement stmt = this.slaveConnection.prepareStatement(SELECT_AUTHORS);
		return stmt.executeQuery();
	}
	
	/**
	 * @param author
	 * @throws Exception
	 */
	public void insertAuthor(final Author author) throws Exception {
		// author table
		final PreparedStatement insertAuthorStatment = this.masterConnection.prepareStatement(INSERT_AUTHOR, Statement.RETURN_GENERATED_KEYS);
		insertAuthorStatment.setString(1, author.getFirstName());
		insertAuthorStatment.setString(2, author.getMiddleName());
		insertAuthorStatment.setString(3, author.getLastName());
		insertAuthorStatment.setInt(4, author.getContentIds().size());
		insertAuthorStatment.executeUpdate();
		
		final ResultSet rs = insertAuthorStatment.getGeneratedKeys();
		if (rs.next()) {
			final long authorId = rs.getLong(1);
			author.setAuthorId(authorId);
			updateAuthorInformation(author);
		}
		rs.close();
		insertAuthorStatment.close();
	}
	
	/**
	 * @param author
	 * @throws SQLException
	 */
	public void updateAuthor(final Author author) throws SQLException {
		final long authorId = author.getAuthorId();
		final PreparedStatement pstmt = this.masterConnection.prepareStatement(UPDATE_AUTHOR);
		pstmt.setInt(1, author.getCtr());
		pstmt.setLong(2, authorId);
		pstmt.executeUpdate();
		pstmt.close();
		
 		final PreparedStatement removeDeletedContentIds = this.masterConnection.prepareStatement(DELETE_AUTHOR_BIBTEX_CONTENT);
		
		for (final long contentId : author.getDeletedContentIds()) {
			removeDeletedContentIds.setLong(1, authorId);
			removeDeletedContentIds.setLong(2, contentId);
			removeDeletedContentIds.addBatch();
		}
		
		removeDeletedContentIds.executeBatch();
		removeDeletedContentIds.close();
		
		updateAuthorInformation(author);
	}

	private void updateAuthorInformation(final Author author) throws SQLException {
		final long authorId = author.getAuthorId();
		final PreparedStatement insertAuthorContentIds = this.masterConnection.prepareStatement(INSERT_AUTHOR_BIBTEX_CONTENT);
		for (final long contentId : author.getContentIds()) {
			insertAuthorContentIds.setLong(1, authorId);
			insertAuthorContentIds.setLong(2, contentId);
			insertAuthorContentIds.addBatch();
		}
		
		insertAuthorContentIds.executeBatch();
		insertAuthorContentIds.close();

		final PreparedStatement insertAuthorPublicationName = this.masterConnection.prepareStatement(INSERT_AUTHOR_BIBTEX_NAME);
		
		for (final String bibtexName : author.getBibtexNames()) {
			insertAuthorPublicationName.setLong(1, authorId);
			insertAuthorPublicationName.setString(2, bibtexName);
			insertAuthorPublicationName.addBatch();
		}
		
		insertAuthorPublicationName.executeBatch();
		insertAuthorPublicationName.close();
	}

	/**
	 * @return the lastest content id in db
	 * @throws SQLException
	 */
	public long getLastContentId() throws SQLException {
		final PreparedStatement currentContentIdStatement = this.slaveConnection.prepareStatement("SELECT max(content_id) FROM bibtex;");
		ResultSet result = currentContentIdStatement.executeQuery();
		if (result.next()) {
			return result.getLong(1);
		}
		
		return -1;
	}
}
