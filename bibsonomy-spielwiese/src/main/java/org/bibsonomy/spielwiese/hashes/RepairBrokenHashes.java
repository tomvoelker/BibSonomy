package org.bibsonomy.spielwiese.hashes;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
	private PreparedStatement stmtSelectPost;
	private PreparedStatement stmtUpdateBibtex;
	private PreparedStatement stmtDecHash;
	private PreparedStatement stmtIncHash;
	private Connection conn;

	/** 
	 * @param conn
	 */
	public RepairBrokenHashes (final Connection conn) {
		if (conn != null){
			try {
				stmtSelectAll    = conn.prepareStatement("SELECT volume, number, journal, booktitle, entrytype, title, author, editor, year, user_name, content_id FROM bibtex LIMIT 30000000;");
				stmtSelectPost   = conn.prepareStatement("SELECT volume, number, journal, booktitle, entrytype, title, author, editor, year, user_name, content_id FROM bibtex WHERE content_id = ? AND user_name = ?");
				stmtUpdateBibtex = conn.prepareStatement("UPDATE bibtex SET simhash? = ? WHERE content_id = ? AND user_name = ? AND simhash? = ?");
				stmtDecHash      = conn.prepareStatement("UPDATE bibhash SET ctr = ctr - 1 WHERE type = ? AND hash = ?");
				stmtIncHash      = conn.prepareStatement("UPDATE bibhash SET ctr = ctr + 1 WHERE type = ? AND hash = ?");
			} catch (SQLException e) {
				log.fatal("Could not prepare statements");
			}
			this.conn = conn;
		}
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

			final RepairBrokenHashes repair = new RepairBrokenHashes(conn);
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

	public void checkAndUpdatePosts() throws SQLException {
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
	}

	private void updatePost(final int content_id, final String username, final String[] oldHashes, final String[] newHashes) throws SQLException {
		stmtSelectPost.setInt(1, content_id);
		stmtSelectPost.setString(2, username);
		final ResultSet rst = stmtSelectPost.executeQuery(); 

		if (rst.next()) {
			conn.setAutoCommit(false);
			try {
				/*
				 * update bibtex table
				 */

				for (int i = 0; i < oldHashes.length; i++) {
					// UPDATE bibtex SET simhash? = ? WHERE content_id = ? AND user_name = ? AND simhash? = ?");
					stmtUpdateBibtex.setInt(1, i);
					stmtUpdateBibtex.setString(2, newHashes[1]);
					stmtUpdateBibtex.setInt(3, content_id);
					stmtUpdateBibtex.setString(4, username);
					stmtUpdateBibtex.setInt(5, i);
					stmtUpdateBibtex.setString(6, oldHashes[1]);
					final int updateCount = stmtUpdateBibtex.executeUpdate();
					if (updateCount != 1) log.error("Updated " + updateCount + " rows instead of 1 row in bibtex table");
					
					/*
					 * update counters
					 */
					
					// decrement old hash
					stmtDecHash.setInt(1, i);
					stmtDecHash.setString(2, oldHashes[i]);
					final int decCount = stmtDecHash.executeUpdate();
					if (decCount != 1) log.error("Decremented" + decCount + " rows instead of 1 row in bibhash table");
					
					// increment new hash
					stmtIncHash.setInt(1, i);
					stmtIncHash.setString(2, newHashes[i]);
					final int incCount = stmtIncHash.executeUpdate();
					if (incCount != 1) log.error("Incremented" + incCount + " rows instead of 1 row in bibhash table");
				}
				
				conn.commit();
			} catch (final SQLException e) {
				log.fatal("Could not update post from user " + username + " with content_id " + content_id + " in bibtex table: " + e );
				conn.rollback();
			}
			conn.setAutoCommit(true);
		} else {
			log.error("Could not find post from user " + username + " with content_id " + content_id + " in bibtex table");
		}
	}

}
