package DBLP.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import DBLP.db.DBHandler;

public class CheckDeletedEntries{

	private static final Log log = LogFactory.getLog(CheckDeletedEntries.class);
	
	private HashSet<String> db_bookmark;
	private HashSet<String> db_bibtex;

	private HTTPBookmarkUpdate bookUpdate = null;
	private HTTPBibtexUpdate bibUpdate = null;
	
	public CheckDeletedEntries(HTTPBookmarkUpdate bookUpdate, HTTPBibtexUpdate bibUpdate) throws MalformedURLException, IOException {
		this.bookUpdate  = bookUpdate;
		this.bibUpdate   = bibUpdate;
	}

	public void deleteOldPosts(HashSet<String> allKeys, DBHandler handler) {
		log.info("looking for old posts");
		
		try {

			LinkedList<String> toDeleteBookmark = new LinkedList<String>();
			LinkedList<String> toDeleteBibtex   = new LinkedList<String>();

			handler.open();
			db_bookmark = handler.getBibKeysBookmark();
			db_bibtex   = handler.getBibKeysBibtex();
			handler.close();

			for (String key: db_bookmark) {
				if (!allKeys.contains(key))	toDeleteBookmark.add(key);
			}

			for (String key: db_bibtex) {
				if(!allKeys.contains(key))	toDeleteBibtex.add(key);
			}

			log.info("Statistics: ");
			log.info("            #allKeys = " + allKeys.size() + 
					           ", #delBook = " + toDeleteBookmark.size() +
					           ", #delBibt = " + toDeleteBibtex.size());
			log.info("            #dbBook = " + db_bookmark.size() + ", #dbBib = " + db_bibtex.size());
			
			
			bookUpdate.deleteOldBookmarkByKey(toDeleteBookmark, handler);
			bibUpdate.deleteOldBibtexByKey(toDeleteBibtex, handler);
			
		} catch(Exception e) {
			log.fatal(e);
			handler.close();
		}
	}

	public HashSet<String> getDb_bibtex() {
		return db_bibtex;
	}

	public HashSet<String> getDb_bookmark() {
		return db_bookmark;
	}

}