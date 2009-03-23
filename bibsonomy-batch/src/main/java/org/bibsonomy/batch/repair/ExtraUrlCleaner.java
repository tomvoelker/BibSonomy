package org.bibsonomy.batch.repair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.bibsonomy.batch.MySQLHelper;

/**
 * Loads all extra URLs (from BibTeX posts) and checks, if they're valid
 * URLs (by calling new URL(url)). If not, cleans the broken URL.
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class ExtraUrlCleaner {

	private static final String BROKENURL = "/brokenurl#";

	private static final Logger log = Logger.getLogger(ExtraUrlCleaner.class);

	private final MySQLHelper db;

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		final ExtraUrlCleaner clean = new ExtraUrlCleaner();
		clean.clean();
	}
	
	public ExtraUrlCleaner() throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		db = new MySQLHelper(ExtraUrlCleaner.class.getSimpleName() + ".properties");
	}

	public void clean() throws SQLException {
		final PreparedStatement select = db.getPreparedStatement("query.selectAllExtraUrls");

		final PreparedStatement update = db.getPreparedStatement("query.updateExtraUrl");
		
		final ResultSet rst = select.executeQuery();

		int urlCtr = 0;
		int brokenUrlCtr = 0;
		int updateCtr = 0;
		while (rst.next()) {
			urlCtr++;
			final String url    = rst.getString("url");
			final int contentId = rst.getInt("content_id");

			try {
				new URL(url);
			} catch (MalformedURLException e) {
				brokenUrlCtr++;
				/*
				 * remove /brokenurl#
				 */
				if (url.startsWith(BROKENURL)) {
					final String newUrl = url.substring(BROKENURL.length()).trim();
					try {
						new URL(newUrl);
						updateCtr += fixUrl(update, contentId, url, newUrl);
					} catch (MalformedURLException e1) {
						/*
						 * fix /brokenurl to /brokenurl# 
						 */
						final String newUrl2 = "http://www.bibsonomy.org" + BROKENURL + newUrl;
						try {
							new URL(newUrl2);
							updateCtr += fixUrl(update, contentId, url, newUrl2);
						} catch (MalformedURLException e2) {
							System.out.println("still broken: " + newUrl2);
						}
					}
				} else {
					/*
					 * fix to /brokenurl# 
					 */
					final String newUrl = "http://www.bibsonomy.org" + BROKENURL + url;
					try {
						new URL(newUrl);
						updateCtr += fixUrl(update, contentId, url, newUrl);
					} catch (MalformedURLException e2) {
						System.out.println("still broken: " + newUrl);
					}
				}
				
				
			}
		}
		select.close();
		update.close();
		db.close();

		
		System.out.println("#urls = " + urlCtr);
		System.out.println("#broken = " + brokenUrlCtr);
		System.out.println("#updated = " + updateCtr);
		

	}
	
	private static int fixUrl(final PreparedStatement update, final int contentId, final String oldUrl, final String newUrl) throws SQLException {
		System.out.println("fixing " + oldUrl + " to " + newUrl);
//		update.setString(1, newUrl);
//		update.setInt(2, contentId);
//		update.setString(3, oldUrl);
//		return update.executeUpdate();
		return 0;
	}
	

}

