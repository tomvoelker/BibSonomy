package org.bibsonomy.batch.repair;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.bibsonomy.batch.repair.old.OldBibTex;
import org.bibsonomy.batch.repair.old.OldSimHash;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.SimHash;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.util.StringUtils;

/**
 * Repairs the sim hashes of publications that changed due to the new person
 * name normalization ("Last, First").
 * 
 * @author rja
 * @version $Id$
 */
public class SimHashCleaner {

	private static final boolean UPDATE = false;
	/*
	 * SQL queries to get/manipulate the data
	 */
	private static final String QUERY_GET = "SELECT * FROM bibtex LIMIT 10000000";
	/*
	 * to check, if gold standard posts will change ...
	 */
//	private static final String QUERY_GET = "SELECT * FROM gold_standard_publications";
	/*
	 * update the hashes
	 */
	private static final String QUERY_UPDATE_BIBTEX = "UPDATE bibtex SET simhash0 = ?, simhash1 = ?, simhash2 = ? WHERE content_id = ?";
	private static final String QUERY_UPDATE_BIBHASH = "INSERT INTO bibhash (hash,ctr,type) VALUES (?,1,?) ON DUPLICATE KEY UPDATE ctr=ctr+1;";
	
	/*
	 * read using
	 
	 sort -u /tmp/changed_person_names| grep -v " and'" | grep -v " and '" | grep -v " and and " | grep -v " and  and " | less

	 */
	private static final String CHANGED_PERSON_NAMES_FILE = "/tmp/changed_person_names";
	/*
	 * should never happen: old hash is different in database:
	 
	 grep "^o/db:[0-9]" /tmp/SimHashCleaner.log | less -S
	 
	 */
	private static final String LOG_FILE = "/tmp/" + SimHashCleaner.class.getSimpleName() + ".log";
	

	private final BufferedWriter changedPersonNameWriter;
	private final BufferedWriter logWriter;
	
	private final Properties props;
	
	int changedHashesCtr = 0;
	int printedHashesCtr = 0;
	int updatedHashesCtr = 0;
	int changedPersonCtr = 0; // how many person names would change in the DB, if we would update them as well?
	int publCtr = 0;

	public static void main(String[] args) throws IOException {
		final SimHashCleaner simHashCleaner = new SimHashCleaner();
		simHashCleaner.compareHashes();
	}

	private void compareHashes() {
		Connection conn = null;
		try {
			conn = this.getConnection();
			
			conn.setAutoCommit(false);
			
			/*
			 * prepare statements
			 */
			final PreparedStatement get = conn.prepareStatement(QUERY_GET);
			final PreparedStatement updateBibtex = conn.prepareStatement(QUERY_UPDATE_BIBTEX);
			final PreparedStatement updateBibhash = conn.prepareStatement(QUERY_UPDATE_BIBHASH);
			/*
			 * do query
			 */
			final ResultSet rst = get.executeQuery();
			rst.setFetchSize(10000);
			while (rst.next()) {
				publCtr++;
				final BibTex newBibtex = getBibTex(rst);
				final OldBibTex oldBibtex = getOldBibTex(rst);
				final String simhash0 = rst.getString("simhash0");
				final String simhash1 = rst.getString("simhash1");
				final String simhash2 = rst.getString("simhash2");
				final int contentId = rst.getInt("content_id");

				final String oldEqualsDb = oldEqualsDb(oldBibtex, simhash0, simhash1, simhash2);
				final String newEqualsDb = newEqualsDb(newBibtex, simhash0, simhash1, simhash2);
				final String oldEqualsNew = oldEqualsNew(newBibtex, oldBibtex);

				/*
				 * either the new hash is different from the database or
				 * e have a post that was updated recently and already has the
				 * new hashes in the database 
				 * 
				 */
				if (newEqualsDb.length() != 0 || (oldEqualsDb.length() != 0 && oldEqualsNew.length() == 0)) {
					
					update(updateBibtex, updateBibhash, newBibtex, contentId);
					
					changedHashesCtr++;
					
					final StringBuffer a = getPerson("author", oldBibtex.getAuthor(), newBibtex.getAuthor(), newEqualsDb);
					final StringBuffer e = getPerson("editor", oldBibtex.getEditor(), newBibtex.getEditor(), newEqualsDb);
					if (present(a) || present(e)) {
						
						if (skip(oldBibtex.getAuthor())) continue;
						if (skip(oldBibtex.getEditor())) continue;						
						
						printedHashesCtr++;
						print("o/db:" + oldEqualsDb + ", ");
						print("n/db:" + newEqualsDb + ", ");
						print("o/n:" + oldEqualsNew + ", ");
						if (present(a)) print(a);
						if (present(e)) print(e);
						println();
					}
				}
				if (publCtr % 100000 == 0) println("-- " + publCtr + " --");
				if (UPDATE && updatedHashesCtr % 1000 == 0) conn.commit();
			}
			if (UPDATE) conn.commit();
			
			println("-----------------------------------");
			println("observed posts: " + publCtr);
			println("changed hashes: " + changedHashesCtr);
			println("printed hashes: " + printedHashesCtr);
			println("updated hashes: " + updatedHashesCtr);
			println("changed person: " + changedPersonCtr);

			changedPersonNameWriter.close();
			logWriter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection(conn);
		}
	}

	private void print(final CharSequence s) throws IOException {
		System.out.print(s);
		logWriter.write(s.toString());
	}
	
	private void println(final CharSequence s) throws IOException {
		print(s + "\n");
	}
	private void println() throws IOException {
		print("\n");
	}
	
	/**
	 * 
	 * 
	 * @param updateBibtex	<pre>UPDATE bibtex SET simhash0 = ?, simhash1 = ?, simhash2 = ? WHERE content_id = ?</pre>
	 * @param updateBibhash <pre>INSERT INTO bibhash (hash,ctr,type) VALUES (?,1,?) ON DUPLICATE KEY UPDATE ctr=ctr+1</pre>
	 * @param newBibTex
	 * @param contentId
	 * @throws SQLException 
	 */
	private void update(final PreparedStatement updateBibtex, final PreparedStatement updateBibhash, final BibTex newBibTex, final int contentId) throws SQLException {
		if (UPDATE) {
			updateBibtex.setString(1, newBibTex.getSimHash0());
			updateBibtex.setString(2, newBibTex.getSimHash1());
			updateBibtex.setString(3, newBibTex.getSimHash2());
			updateBibtex.setInt(4, contentId);
			updateBibtex.executeUpdate();
			
			updateBibhash.setString(1, newBibTex.getSimHash0());
			updateBibhash.setInt(2, 0);
			updateBibhash.executeUpdate();
			
			updateBibhash.setString(1, newBibTex.getSimHash1());
			updateBibhash.setInt(2, 1);
			updateBibhash.executeUpdate();
			
			updateBibhash.setString(1, newBibTex.getSimHash2());
			updateBibhash.setInt(2, 2);
			updateBibhash.executeUpdate();
			updatedHashesCtr++;
		}
	}
	
	/**
	 * Skips person names that change because of a reason we have understood and
	 * can not / don't want to avoid.
	 * 
	 * @param person
	 * @return
	 */
	private boolean skip(final String person) {
		if (present(person)) {
			/*
			 * entries having a spare "and " at the end change
			 */
			if (person.endsWith(" and ")) return true;
			/*
			 * entries with two "and"'s in a row change
			 */
			if (person.matches(".*\\s+and\\s+and\\s+.*")) return true;
			/*
			 * we have many entries with "and  and" at the end.
			 */
			if (person.matches(".*\\s+and\\s+and")) return true;
			/*
			 * entries that contain only an "and" change
			 */
			if (person.matches("\\s*and\\s*")) return true;
			/*
			 * names with strange HTML encodings change, e.g., containing "\&\#233;"
			 */
			if (person.matches(".*\\\\&\\\\#[0-9]+;.*")) return true;
			/*
			 * ok, let's skip all entries containing "{" - almost all of them change
			 */
			if (person.contains("{")) return true;
			/*
			 * DBLP numbers authors - we remove these numbers now
			 */
			if (person.matches(".*[0-9]{4}.*")) return true;
		}
		return false;
	}
	
	/*
	 * Examples, where hashes change:
	 * 
	 * 'S. F. Adafre and Maarten de Rijke' 
	 *    --> new = '[m.de rijke,s.adafre]' 
	 *        old = '[m.rijke,s.adafre]'
	 * 
	 * ' {Bundesamt für Sicherheit in der Informationstechnik}' 
	 *    --> new = 'Bundesamt für Sicherheit in der Informationstechnik'
	 *        old = ' Bundesamt für Sicherheit in der Informationstechnik'
	 * 
	 * 'L. Douglas Baker and Andrew K. McCallum'
	 *    --> new = '[a.mccallum,l.douglas baker]'
	 *        old = '[a.mccallum,l.baker]'
	 */
	
	private StringBuffer getPerson(final String personType, final String oldPerson, final List<PersonName> newPerson, final String newEqualsDb) throws IOException {
		if (present(oldPerson)) {
			final StringBuffer buf = new StringBuffer();
			final String newNorm1 = SimHash.getNormalizedPersons(newPerson);
			final String oldNorm1 = OldSimHash.getNormalizedAuthor(oldPerson);


			if (!oldNorm1.equals(newNorm1) && newEqualsDb.contains("1")) {
				buf.append("[1: " + personType + " = '" + oldPerson + "', ");
				/*
				 * simhash1 has changed
				 */
				buf.append(personType + "N = '" + newNorm1 + "', ");
				buf.append(personType + "O = '" + oldNorm1 + "', ");
				buf.append("] ");
			}

			/*
			 * we remove trailing whitespace because that was a problem in the old days.
			 */
			final String newNorm2 = StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(newPerson, false)).trim();
			final String oldNorm2 = StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(oldPerson).trim();
			if (!oldNorm2.equals(newNorm2) && newEqualsDb.contains("2")) {
				buf.append("[2: " + personType + " = '" + oldPerson + "', ");
				/*
				 * simhash2 has changed
				 */
				buf.append(personType + "N = '" + newNorm2 + "', ");
				buf.append(personType + "O = '" + oldNorm2 + "', ");
				buf.append("] ");
			}
			String newPersonString = PersonNameUtils.serializePersonNames(newPerson, false);
			String newPersonString2 = PersonNameUtils.serializePersonNames(newPerson, true);
			if (newPersonString == null) newPersonString = "";
			if (newPersonString2 == null) newPersonString2 = "";
			final String oldPersonString = oldPerson.replaceAll("\\s+", ""); 
			/*
			 * We check, if the plain author string changes - just to be sure, 
			 * that don't do too much harm to people
			 * 
			 * (we ignore changes in whitespace)
			 */
			if (!oldPersonString.equals(newPersonString.replaceAll("\\s+", "")) && !oldPersonString.equals(newPersonString2.replaceAll("\\s+", ""))) {
				if (!present(buf)) buf.append(personType + " was " + oldPerson + " and now is");
				buf.append(" !!!" + newPersonString + "!!! ");
				changedPersonNameWriter.write("'" + oldPerson + "' --> '" + newPersonString + "'\n");
				changedPersonCtr++;
			}
			return buf;
		}
		return null;
	}




	private String oldEqualsNew(final BibTex bibtex, final OldBibTex oldBibtex) {
		String result = "";
		if (!oldBibtex.getSimHash0().equals(bibtex.getSimHash0())) result += 0;
		if (!oldBibtex.getSimHash1().equals(bibtex.getSimHash1())) result += 1;
		if (!oldBibtex.getSimHash2().equals(bibtex.getSimHash2())) result += 2;
		return result;
	}

	private String newEqualsDb(final BibTex bibtex, final String simhash0, final String simhash1, final String simhash2) {
		String result = "";
		if (!bibtex.getSimHash0().equals(simhash0)) result += 0;
		if (!bibtex.getSimHash1().equals(simhash1)) result += 1;
		if (!bibtex.getSimHash2().equals(simhash2)) result += 2;
		return result;		
	}

	private String oldEqualsDb(final OldBibTex oldBibtex, final String simhash0, final String simhash1, final String simhash2) {
		String result = "";
		if (!oldBibtex.getSimHash0().equals(simhash0)) result += 0;
		if (!oldBibtex.getSimHash1().equals(simhash1)) result += 1;
		if (!oldBibtex.getSimHash2().equals(simhash2)) result += 2;
		return result;	
	}





	private void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("Could not close connection: ");
				e.printStackTrace();
			}
		}
	}

	private BibTex getBibTex(final ResultSet rst) throws SQLException, PersonListParserException {
		final BibTex bibtex = new BibTex();
		bibtex.setTitle(rst.getString("title"));
		bibtex.setAuthor(PersonNameUtils.discoverPersonNamesIgnoreExceptions(rst.getString("author")));
		bibtex.setEditor(PersonNameUtils.discoverPersonNamesIgnoreExceptions(rst.getString("editor")));
		bibtex.setYear(rst.getString("year"));
		bibtex.setEntrytype(rst.getString("entrytype"));
		bibtex.setJournal(rst.getString("journal"));
		bibtex.setBooktitle(rst.getString("booktitle"));
		bibtex.setVolume(rst.getString("volume"));
		bibtex.setNumber(rst.getString("number"));
		return bibtex;
	}

	private OldBibTex getOldBibTex(final ResultSet rst) throws SQLException {
		final OldBibTex bibtex = new OldBibTex();
		bibtex.setTitle(rst.getString("title"));
		bibtex.setAuthor(rst.getString("author"));
		bibtex.setEditor(rst.getString("editor"));
		bibtex.setYear(rst.getString("year"));
		bibtex.setEntrytype(rst.getString("entrytype"));
		bibtex.setJournal(rst.getString("journal"));
		bibtex.setBooktitle(rst.getString("booktitle"));
		bibtex.setVolume(rst.getString("volume"));
		bibtex.setNumber(rst.getString("number"));
		return bibtex;
	}


	public SimHashCleaner() throws IOException {
		this.props = new Properties();
		this.props.load(this.getClass().getClassLoader().getResourceAsStream(this.getClass().getSimpleName() + ".properties"));
		this.changedPersonNameWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CHANGED_PERSON_NAMES_FILE), "UTF-8"));
		this.logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOG_FILE), "UTF-8"));
	}

	private Connection getConnection() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		return DriverManager.getConnection(	
				props.getProperty("db.url"), 
				props.getProperty("db.user"), 
				props.getProperty("db.pass")
		);
	}
}
