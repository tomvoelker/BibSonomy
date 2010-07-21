package org.bibsonomy.importer.DBLP.update;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.DBLPEvaluation;
import org.bibsonomy.importer.DBLP.DBLPException;
import org.bibsonomy.importer.DBLP.db.DBHandler;
import org.bibsonomy.importer.DBLP.parser.DBLPEntry;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.w3c.tidy.Tidy;


/*
 * This class delete all old DBLP entries which will be updated
 */
public class HTTPBibtexUpdate extends HTTPUpdate {

	private static final String DEBUG_LOG_FILE = "/tmp/dblp_debug.html";

	private static final Log log = LogFactory.getLog(HTTPBibtexUpdate.class);

	private final LSSerializer writer;

	private final Tidy tidy;

	public HTTPBibtexUpdate (String baseURL, String user, String passhash) throws MalformedURLException, IOException, ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		super (baseURL, user, passhash);
		tidy = new Tidy();
		tidy.setQuiet(true);
		tidy.setShowWarnings(false);
		final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
		final DOMImplementationLS impl = (DOMImplementationLS)registry.getDOMImplementation("LS");
		writer = impl.createLSSerializer();
	}

	private final Map<String, Integer> httpStatusCounts = new HashMap<String, Integer>();
	
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
		log.info("deleting bibtex");
		handler.open();
		for (final String key: keys){
			final String hash = handler.getBibhash(key);

			log.debug("looking at hash " + hash);

			if (hash != null) {
				try {
					/*
					 * delete post
					 */
					deletePost(hash);
				} catch (IOException e) {
					log.fatal("tried to delete bibtex " + hash + ": ", e);
				}
			} else {
				log.debug("did not delete, hash == null");
			}
		}
		handler.close();
	}

	/*
	 * insert the given snippet
	 */
	private Document uploadBibTeXSnippet(final String snippet) throws IOException, DBLPException {
		log.debug("inserting entry");

		final HttpURLConnection conn = (HttpURLConnection) new URL(baseURL + "import/publications?abstractGrouping=public&overwrite=true&editBeforeImport=false&ckey=" + cKey).openConnection();
		setCookies(conn);
		conn.setDoOutput(true); // neccessary to write to connection
		conn.setDoInput(true);  // defaults to true ... but just to be sure ...		
		conn.setInstanceFollowRedirects(false); // do not follow redirects!
		conn.setRequestMethod("POST");

		/* write request parameters */
		final PrintWriter out = new PrintWriter(conn.getOutputStream());
		out.print("selection=" + URLEncoder.encode(snippet, "UTF-8"));
		out.close();

		Document document = null;
		try {
			/*
			 * parse response
			 */
			final int responseStatus = conn.getResponseCode();
			countResponseStatus(responseStatus + "");
			if (!(responseStatus > 0 && responseStatus < 400)) throw new DBLPException("got " + responseStatus + " when trying to insert bibtex " + snippet);

			final InputStream inputStream = conn.getInputStream();
			document = tidy.parseDOM(inputStream, null);
			conn.disconnect();

		} catch (final Exception e) {
			log.fatal("Could not parse response document");
		}
		return document;
	}
	
	private void countResponseStatus(final String code) {
		if (httpStatusCounts.containsKey(code)) {
			httpStatusCounts.put(code, httpStatusCounts.get(code) + 1);
		} else {
			httpStatusCounts.put(code, 1);
		}
	}


	/**
	 * XXX: debug
	 * @param s
	 * @throws IOException 
	 */
	private static final void writeFile(final String s) throws IOException {
		final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(DEBUG_LOG_FILE))));
		w.write(s);
		w.close();
	}

	/**
	 * XXX: debug
	 * @param s
	 * @throws IOException 
	 */
	private static final void writeFile(final InputStream s) throws IOException {
		final BufferedReader r = new BufferedReader(new InputStreamReader(s, "UTF-8"));
		final BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(DEBUG_LOG_FILE))));
		String line;
		while ((line = r.readLine()) != null) {
			w.write(line);	
		}
		r.close();
		w.close();
	}
	/*
	 * insert all given bibtex-entries
	 */
	public Document insertNewBibtex(final LinkedList<DBLPEntry> presult, final DBLPEvaluation eval) throws IOException, DBLPException {
		final StringBuffer snippets = new StringBuffer();
		for (final DBLPEntry entry: presult) {
			if (entry.getDblpKey() != null) {
				eval.incrementUpdate(entry);
				snippets.append(entry.generateSnippet());
			}
		}
		return uploadBibTeXSnippet(snippets.toString());
	}


	public Map<String, Integer> getHttpStatusCounts() {
		return this.httpStatusCounts;
	}


}