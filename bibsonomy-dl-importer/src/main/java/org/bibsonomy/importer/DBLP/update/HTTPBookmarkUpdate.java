package org.bibsonomy.importer.DBLP.update;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.DBLPException;
import org.bibsonomy.importer.DBLP.db.DBHandler;
import org.bibsonomy.importer.DBLP.parser.DBLPEntry;
import org.bibsonomy.importer.DBLP.parser.DBLPParseResult;


/*
 * This class delete all old DBLP entries which will be updated
 */
public class HTTPBookmarkUpdate extends HTTPUpdate {			

	private static final Log log = LogFactory.getLog(HTTPBookmarkUpdate.class);
	
	private static final String dblpHomeUrlHash = "4f5c70202dd14bb2186b0872fe494886"; // TODO: it's not useful, to have this as a constant here :-(

	public HTTPBookmarkUpdate (String baseURL, String user, String passhash) throws MalformedURLException, IOException {
		super (baseURL, user, passhash);
	}
	
	/*
	 * delete the given publication bookmarkHandler?delete=&user=
	 */
	private void deletePost(String urlhash) throws IOException, DBLPException {
		callURL(new URL((baseURL + "deletePost?resourceHash=" + urlhash + "&ckey=" + cKey)));
	}

	/*
	 * iterate through all parsed entries and then delete them with delete()
	 */
	public void deleteOldBookmarkByEntry(final LinkedList<DBLPEntry> list, final DBHandler handler, final HashSet<String> db_bookmark_keys) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, DBLPException {
		handler.open(); 

		for (final DBLPEntry entry: list) {
			if (db_bookmark_keys.contains(entry.getDblpKey())) {
				final String urlhash = handler.getUrlHash(entry.getDblpKey());
				if (urlhash != null) {
					try {
						deletePost(urlhash);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		handler.close();
	}

	/** Updates the URL of DBLP.
	 *  
	 * @throws DBLPException
	 */
	public void updateDBLPHome() throws DBLPException {
		log.debug("updating DBLP url");
		try {
			deletePost(dblpHomeUrlHash);
		} catch (IOException e) {
			log.fatal("could not delete DBLP url", e);
		}		
		try {
			insertPost("http://dblp.uni-trier.de/", "DBLP Computer Science Bibliography", "", null);
		} catch (IOException e) {
			log.fatal("could not insert DBLP url", e);
		}		
	}

	/*
	 * iterate through all keys and delete
	 */
	public void deleteOldBookmarkByKey(LinkedList<String> keys, DBHandler handler) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, DBLPException {
		log.info("deleting bookmarks");
		handler.open();
		for (final String key: keys) {
			final String hash = handler.getUrlHash(key);
			
			log.debug("looking at hash " + hash);
			
			if (hash != null) {
				try {
					/*
					 * delete post
					 */
					deletePost(hash);
				} catch (IOException e) {
					log.fatal("tried to delete bookmark " + hash + ": ", e);
				}
			} else {
				log.debug("did not delete, hash == null");
			}
		}
		handler.close();
	}

	
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");	
	
	/*
	 * insert the given bookmark
	 */
	private void insertPost(String url, String title, String description, Date date) throws IOException, DBLPException {
		if (url.startsWith("db/")) {
			url = DBLPEntry.DBLPURL + url;
		}
		
		final HttpURLConnection conn = (HttpURLConnection) new URL(baseURL + "editBookmark").openConnection();
		setCookies(conn);
		conn.setDoOutput(true); // neccessary to write to connection
		conn.setDoInput(true);  // defaults to true ... but just to be sure ...		
		conn.setInstanceFollowRedirects(false); // do not follow redirects!
		
		/* write request parameters */
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print("url=" + URLEncoder.encode(url, "UTF-8"));
		out.print("&description=" + URLEncoder.encode(title, "UTF-8"));
		out.print("&extended=" + URLEncoder.encode(description, "UTF-8"));
		out.print("&tags=" + URLEncoder.encode("dblp", "UTF-8"));
		out.print("&ckey=" + cKey);
		if (date != null) out.print("&date=" + URLEncoder.encode(simpleDateFormat.format(date), "UTF-8"));
		out.close();
		
		conn.getContentLength(); // actually connect
		
		final int response = conn.getResponseCode();
		conn.disconnect();
		
		if (!(response > 0 && response < 400)) throw new DBLPException("got " + response + " when trying to inserting bookmark " + url);
		
	}
	
	/*
	 * insert all bookmarks from entrylist
	 */
	public void insertNewBookmark(LinkedList<DBLPEntry> entrylist, DBLPParseResult presult) throws DBLPException {
		for (DBLPEntry entry: entrylist) {
			try {
				if (entry.getUrl() != null) {
					/*
					 * entry has URL - use it for bookmark
					 */
					presult.getEval().incrementUpdate(entry);
					/*
					 * clean title to contain author name
					 */
					String title = entry.getTitle();
					String author = entry.getAuthor();
					if ("Home Page".equals(title) && author != null) {
						
						title += " of " + author;
					}
					/*
					 * insert post
					 */
					insertPost(entry.getUrl(), title, entry.generateExtended(), entry.getEntrydate());
				} else if (entry.getEe() != null) {
					/*
					 * entry has EE (electronic edition) entry - use it to generate an URL 
					 */
					presult.getEval().incrementUpdate(entry);
					insertPost(DBLPEntry.DBLPURL + entry.getEe(), entry.getTitle(), entry.generateExtended(), entry.getEntrydate());
				} else {
					/*
					 * no URL found - don't insert entry
					 */
					presult.getEval().incrementUpdate(entry);
					presult.getInsert_bookmark_empty_url().add(entry);
				}
			} catch(IOException e) {
				log.fatal("BookmarkInsert: ", e);
			}
		}
	}


}