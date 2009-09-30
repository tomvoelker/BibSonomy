package DBLP.update;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import DBLP.DBLPEvaluation;
import DBLP.DBLPException;
import DBLP.db.DBHandler;
import DBLP.parser.DBLPEntry;

/*
 * This class delete all old DBLP entries which will be updated
 */
public class HTTPBibtexUpdate extends HTTPUpdate {

	private static final Log log = LogFactory.getLog(HTTPBibtexUpdate.class);
	
	public HTTPBibtexUpdate (String baseURL, String user, String passhash) throws MalformedURLException, IOException {
		super (baseURL, user, passhash);
	}

	/* 
	 * delete the given publication
	 */
	private void deletePost(String hash) throws IOException, DBLPException {
		callURL(new URL(baseURL + "deletePost?resourceHash=" + hash + "&ckey="+ cKey));
	}


	/*
	 * iterate through all parsed entries and then delete them with delete()
	 */
	public void deleteOldBibtexByEntry(LinkedList<DBLPEntry> presult, DBHandler handler, HashSet<String> db_bibtex_keys) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, DBLPException{
		handler.open();
		for (DBLPEntry entry: presult) {
			String dblpKey = entry.getDblpKey();
			
			log.debug("looking at " + dblpKey);
			
			if (db_bibtex_keys.contains(dblpKey)){ // delete only if entry already exist in DB
				
				log.debug("is in DB -> delete it");
				
				String hash = handler.getBibhash(dblpKey);
				if (hash != null) {
					try {
						/*
						 * delete post
						 */
						deletePost(hash);
					} catch (IOException e) {
						log.fatal("tried to delete publication " + hash + ": " + e);
					}
				} else {
					log.debug("hash == null, could not delete");
				}
			}
		}
		handler.close();
	}

	/*
	 * iterate through all keys and delete
	 */
	public void deleteOldBibtexByKey(LinkedList<String> keys, DBHandler handler) throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, IOException, DBLPException {
		handler.open();
		for (String key: keys){
			String bibhash = handler.getBibhash(key);
			if (bibhash != null) {
				deletePost(bibhash);
			}
		}
		handler.close();
	}

	/*
	 * insert the given snippet
	 */
	private Document insert(String snippet) throws IOException, DBLPException {
		log.debug("inserting entry");
		
		HttpURLConnection conn = (HttpURLConnection) new URL(baseURL + "BibtexHandler?requTask=upload&group=public&description=dblp&ckey=" + cKey).openConnection();
		setCookies(conn);
		conn.setDoOutput(true); // neccessary to write to connection
		conn.setDoInput(true);  // defaults to true ... but just to be sure ...		
		conn.setInstanceFollowRedirects(false); // do not follow redirects!
		conn.setRequestMethod("POST");

		/* write request parameters */
		PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print("selection=" + URLEncoder.encode(snippet, "UTF-8"));
		out.close();

		Tidy tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		Document document = tidy.parseDOM(conn.getInputStream(), null);

		int response = conn.getResponseCode();
		conn.disconnect();
		
		if (!(response > 0 && response < 400)) throw new DBLPException("got " + response + " when trying to inserting bibtex " + snippet);
		
		// return resulting document
		return document;
	}

	/*
	 * insert all given bibtex-entries
	 */
	public Document insertNewBibtex(LinkedList<DBLPEntry> presult, DBLPEvaluation eval) throws IOException, DBLPException {
		StringBuffer snippets = new StringBuffer();
		for (DBLPEntry entry: presult) {
			if (entry.getDblpKey() != null) {
				eval.incrementUpdate(entry);
				snippets.append(entry.generateSnippet());
			}
		}
		return insert(snippets.toString());
	}


}