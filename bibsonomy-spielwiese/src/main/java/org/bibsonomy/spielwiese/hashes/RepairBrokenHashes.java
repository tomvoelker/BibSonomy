package org.bibsonomy.spielwiese.hashes;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;

/**
 * @author rja
 * @version $Id$
 */
public class RepairBrokenHashes {

	private static final Logger log = Logger.getLogger(RepairBrokenHashes.class);

	private PreparedStatement stmtSelectAll;
	private BufferedWriter writer;

	/** 
	 * @param conn
	 */
	public RepairBrokenHashes (final Connection conn, final BufferedWriter writer) {
		if (conn != null){
			try {
				stmtSelectAll    = conn.prepareStatement("SELECT volume, number, journal, booktitle, entrytype, title, author, editor, year, user_name, content_id FROM bibtex LIMIT 30000000;");
			} catch (SQLException e) {
				log.fatal("Could not prepare statements");
			}
		}
		this.writer = writer;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {

		Properties prop = new Properties();
		prop.load(new FileInputStream(args[0]));

		final String dbUser = prop.getProperty("db.user");
		final String dbPass = prop.getProperty("db.pass");
		final String dbURL  = prop.getProperty("db.url");




		try {
			/*
			 * connect to DB
			 */
			Class.forName ("com.mysql.jdbc.Driver").newInstance ();
			final Connection conn = DriverManager.getConnection (dbURL, dbUser, dbPass);
			log.info("Database connection established");

			final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("/tmp/updates.sql"), "UTF-8"));

			final RepairBrokenHashes repair = new RepairBrokenHashes(conn, writer);
			repair.checkAndUpdatePosts();


		} catch (SQLException e) {
			log.fatal(e);
		} catch (InstantiationException e) {
			log.fatal(e);
		} catch (IllegalAccessException e) {
			log.fatal(e);		
		} catch (ClassNotFoundException e) {
			log.fatal(e);		
		}

	}

	public void checkAndUpdatePosts() throws SQLException, IOException {
		final ResultSet rst = stmtSelectAll.executeQuery();

		int ctr = 0;
		int badACtr = 0;
		int badECtr = 0;
		while (rst.next()) {
			ctr++;
			BibTex bibtex = new BibTex();
			// simhash 1 (inter-hash)
			bibtex.setAuthor(rst.getString("author"));
			bibtex.setEditor(rst.getString("editor"));
			bibtex.setYear(rst.getString("year"));
			bibtex.setTitle(rst.getString("title"));
			// simhash 0 (old intra-hash)
			bibtex.setBooktitle(rst.getString("booktitle"));
			bibtex.setJournal(rst.getString("journal"));
			bibtex.setEntrytype(rst.getString("entrytype"));
			// simhash (intra-hash)
			bibtex.setVolume(rst.getString("volume"));
			bibtex.setNumber(rst.getString("number"));


			final String username = rst.getString("user_name");
			final int content_id  = rst.getInt("content_id");

			boolean update = false;

			final String[] oldHashes = new String[4];
			final String[] newHashes = new String[4];

			/*
			 * calculate all hashes
			 */
			for (int i = 0; i < oldHashes.length; i++) {
				oldHashes[i] = SimHashOld.getSimHash(bibtex, HashID.getSimHash(i));
				newHashes[i] = SimHashNew.getSimHash(bibtex, HashID.getSimHash(i));
				if (!oldHashes[i].equals(newHashes[i])) update = true;
			}

			if (update) {
				/*
				 * update hashes
				 */
				badACtr++;
				System.out.println(badACtr);

				updatePost(content_id, username, oldHashes, newHashes);

			}

		}
		System.out.println("finished");
		System.out.println("ctr: " + ctr + ", badACtr: " + badACtr + ", badECtr: " + badECtr);
		writer.close();
	}

	private void updatePost(final int content_id, final String username, final String[] oldHashes, final String[] newHashes) throws IOException {
		for (int i = 0; i < oldHashes.length; i++) {

			// update bibtex table
			final String newHash = newHashes[i];
			final String oldHash = oldHashes[i];
			if (!oldHash.equals(newHash)) {
				writer.write("UPDATE bibtex SET simhash" + i + " = '" + newHash + "' WHERE content_id = " + content_id + " AND user_name = '" + username + "' AND simhash" + i + " = '" + oldHash + "';\n");

				// decrement old hash
				writer.write("UPDATE bibhash SET ctr = ctr - 1 WHERE type = " + i + " AND hash = '" + oldHash + "'\n");

				// increment new hash
				writer.write("UPDATE bibhash SET ctr = ctr + 1 WHERE type = " + i + " AND hash = '" + newHash + "'\n");
			}
		}

	}

}
