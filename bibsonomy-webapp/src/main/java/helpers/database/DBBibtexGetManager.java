package helpers.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import resources.Bibtex;

public class DBBibtexGetManager {
	
	private PreparedStatement stmtP  = null;
	private PreparedStatement stmtPT = null;
	private ResultSet rst            = null;
	
	private final String SQL_SELECT_BIBTEX    = "SELECT * FROM bibtex b WHERE simhash" + Bibtex.INTRA_HASH + " = ? AND user_name = ?";
	private final String SQL_SELECT_TAGS      = "SELECT tag_name FROM tas WHERE content_id = ?";

	public void prepareStatements (Connection conn) throws SQLException {
		stmtP = conn.prepareStatement(SQL_SELECT_BIBTEX);
		stmtPT = conn.prepareStatement(SQL_SELECT_TAGS);
	}
	
	public void closeStatements () {
		if (stmtP  != null) {try {stmtP.close();  } catch (SQLException e) {} stmtP  = null;}
		if (stmtPT != null) {try {stmtPT.close(); } catch (SQLException e) {} stmtPT = null;}
		if (rst    != null) {try {rst.close();    } catch (SQLException e) {} rst    = null;}
	}
	
	
	/** get bibtex by hash and user name
	 * @param hash
	 * @param user
	 * @return the bibtex object for given user/hash combination, otherwise <code>null</code>
	 */
	public Bibtex getBibtex(String hash, String user) throws SQLException {
		Bibtex bibtex = null;
		stmtP.setString(1, hash);
		stmtP.setString(2, user);
		rst = stmtP.executeQuery();
		
		if (rst.next()) {
			// entry found --> fill object
			bibtex = new Bibtex();
			
			int content_id = rst.getInt("content_id");
			
			bibtex.setAddress(rst.getString("address"));
			bibtex.setAnnote(rst.getString("annote"));
			bibtex.setAuthor(rst.getString("author"));
			bibtex.setBibtexAbstract(rst.getString("bibtexAbstract"));
			bibtex.setBibtexKey(rst.getString("bibtexKey"));
			bibtex.setBooktitle(rst.getString("booktitle"));
			bibtex.setChapter(rst.getString("chapter"));
			bibtex.setContentID(content_id);
			bibtex.setCrossref(rst.getString("crossref"));
			bibtex.setDay(rst.getString("day"));
			bibtex.setDescription(rst.getString("description"));
			bibtex.setEdition(rst.getString("edition"));
			bibtex.setEditor(rst.getString("editor"));
			bibtex.setEntrytype(rst.getString("entrytype"));
			bibtex.setHowpublished(rst.getString("howPublished"));
			bibtex.setInstitution(rst.getString("institution"));
			bibtex.setJournal(rst.getString("journal"));
			bibtex.setKey(rst.getString("bkey"));
			bibtex.setMisc(rst.getString("misc"));
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
			bibtex.setUrl(rst.getString("url"));
			bibtex.setVolume(rst.getString("volume"));
			bibtex.setYear(rst.getString("year"));
			bibtex.setRating(rst.getInt("rating"));
			
			bibtex.setUser(user);
			bibtex.setGroupid(rst.getInt("group"));
			
			// look for tags
			stmtPT.setInt(1, content_id);
			rst = stmtPT.executeQuery();
			while (rst.next()) {
				bibtex.addTag(rst.getString("tag_name"));
			}
		}
		return bibtex;
	}

}