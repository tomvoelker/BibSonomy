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
				stmtSelectAll    = conn.prepareStatement("SELECT title, author, editor, year, user_name, content_id FROM bibtex LIMIT 30000000;");
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
			final BibTex bibtex = new BibTex();
			// simhash 1 (inter-hash)
			bibtex.setAuthor(rst.getString("author"));
			bibtex.setEditor(rst.getString("editor"));
			bibtex.setYear(rst.getString("year"));
			bibtex.setTitle(rst.getString("title"));


			final String username = rst.getString("user_name");
			final int content_id  = rst.getInt("content_id");

			/*
			 * calculate all hashes
			 */
			final String oldHash1 = SimHashOld.getSimHash(bibtex, HashID.INTER_HASH);
			final String newHash1 = SimHashNew.getSimHash(bibtex, HashID.INTER_HASH);
			if (!oldHash1.equals(newHash1)) {
				/*
				 * update hashes
				 */
				badACtr++;
				System.out.println(badACtr);

				updatePost(content_id, username, oldHash1, newHash1);

			}

		}
		System.out.println("finished");
		System.out.println("ctr: " + ctr + ", badACtr: " + badACtr + ", badECtr: " + badECtr);
		writer.close();
	}

	private void updatePost(final int content_id, final String username, final String oldHash1, final String newHash1) throws IOException {
		// update bibtex table
		writer.write("UPDATE bibtex " +
				"SET simhash" + HashID.INTER_HASH.getId() + " = '" + newHash1 + "' " +
				"WHERE content_id = " + content_id + " " +
				"AND user_name = '" + username + "' " +
				"AND simhash" + HashID.INTER_HASH.getId() + " = '" + oldHash1 + "';\n");

		// decrement old hash
		writer.write("UPDATE bibhash SET ctr = ctr - 1 WHERE type = " + HashID.INTER_HASH.getId() + " AND hash = '" + oldHash1 + "'\n");

		// increment new hash
		writer.write("UPDATE bibhash SET ctr = ctr + 1 WHERE type = " + HashID.INTER_HASH.getId() + " AND hash = '" + newHash1 + "'\n");
	}

}
