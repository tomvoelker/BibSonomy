package org.bibsonomy.batch.repair;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.IOException;
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
import org.bibsonomy.util.StringUtils;

/**
 * Repairs the sim hashes of publications that changed due to the new person
 * name normalization ("Last, First").
 * 
 * @author rja
 * @version $Id$
 */
public class SimHashCleaner {

	private static final String QUERY = "SELECT * FROM bibtex"; 

	private final Properties props;

	public static void main(String[] args) throws IOException {
		final SimHashCleaner simHashCleaner = new SimHashCleaner();
		simHashCleaner.compareHashes();
	}

	private void compareHashes() {
		Connection conn = null;
		try {
			conn = this.getConnection();
			/*
			 * do query
			 */
			final PreparedStatement stmt = conn.prepareStatement(QUERY);
			final ResultSet rst = stmt.executeQuery();
			int changedHashesCtr = 0;
			int printedHashesCtr = 0;
			int publCtr = 0;
			while (rst.next()) {
				publCtr++;
				final BibTex newBibtex = getBibTex(rst);
				final OldBibTex oldBibtex = getOldBibTex(rst);
				final String simhash0 = rst.getString("simhash0");
				final String simhash1 = rst.getString("simhash1");
				final String simhash2 = rst.getString("simhash2");

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
					changedHashesCtr++;
					
					final StringBuffer a = getPerson("author", oldBibtex.getAuthor(), newBibtex.getAuthor(), newEqualsDb);
					final StringBuffer e = getPerson("editor", oldBibtex.getEditor(), newBibtex.getEditor(), newEqualsDb);
					if (present(a) || present(e)) {
						
						if (skip(oldBibtex.getAuthor())) continue;
						if (skip(oldBibtex.getEditor())) continue;						
						
						printedHashesCtr++;
						System.out.print("o/db:" + oldEqualsDb + ", ");
						System.out.print("n/db:" + newEqualsDb + ", ");
						System.out.print("o/n:" + oldEqualsNew + ", ");
						if (present(a)) System.out.print(a);
						if (present(e)) System.out.print(e);
						System.out.println();
					}
				}
				if (publCtr % 100000 == 0) System.out.println("--" + publCtr + "--");
			}
			
			System.out.println("-----------------------------------");
			System.out.println("observed posts: " + publCtr);
			System.out.println("changed hashes: " + changedHashesCtr);
			System.out.println("printed hashes: " + printedHashesCtr);


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeConnection(conn);
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
	
	private StringBuffer getPerson(final String personType, final String oldPerson, final List<PersonName> newPerson, final String newEqualsDb) {
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
			final String n = PersonNameUtils.serializePersonNames(newPerson, false);
			if (oldPerson.equals(n)) {
				buf.append(" !!! + " + n + "!!! ");
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

	private BibTex getBibTex(final ResultSet rst) throws SQLException {
		final BibTex bibtex = new BibTex();
		bibtex.setTitle(rst.getString("title"));
		bibtex.setAuthor(PersonNameUtils.discoverPersonNames(rst.getString("author")));
		bibtex.setEditor(PersonNameUtils.discoverPersonNames(rst.getString("editor")));
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
