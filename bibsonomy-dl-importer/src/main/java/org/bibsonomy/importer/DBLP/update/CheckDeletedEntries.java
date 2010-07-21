package org.bibsonomy.importer.DBLP.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.db.DBHandler;


public class CheckDeletedEntries{

	private static final Log log = LogFactory.getLog(CheckDeletedEntries.class);
	
	private HashSet<String> bookmarkKeysInDatabase;
	private HashSet<String> bibtexKeysInDatabase;

	private HTTPBookmarkUpdate bookUpdate = null;
	private HTTPBibtexUpdate bibUpdate = null;
	
	public CheckDeletedEntries(HTTPBookmarkUpdate bookUpdate, HTTPBibtexUpdate bibUpdate) throws MalformedURLException, IOException {
		this.bookUpdate  = bookUpdate;
		this.bibUpdate   = bibUpdate;
	}

	public void deleteOldPosts(final HashSet<String> allKeys, final DBHandler handler) {
		log.info("looking for old posts");
		
		try {

			/*
			 * get all keys from BibSonomy
			 */
			handler.open();
			bookmarkKeysInDatabase = handler.getBibKeysBookmark();
			bibtexKeysInDatabase   = handler.getBibKeysBibtex();
			handler.close();

			final LinkedList<String> bookmarksToDelete = new LinkedList<String>();
			final LinkedList<String> bibtexsToDelete   = new LinkedList<String>();

			/*
			 * collect all keys which are not contained in the dataset any more
			 */
			for (final String key: bookmarkKeysInDatabase) {
				if (!allKeys.contains(key))	bookmarksToDelete.add(key);
			}

			for (final String key: bibtexKeysInDatabase) {
				if(!allKeys.contains(key))	bibtexsToDelete.add(key);
			}

			log.info("Statistics: ");
			log.info("            #allKeys = " + allKeys.size() + 
					           ", #delBook = " + bookmarksToDelete.size() +
					           ", #delBibt = " + bibtexsToDelete.size());
			log.info("            #dbBook = " + bookmarkKeysInDatabase.size() + ", #dbBib = " + bibtexKeysInDatabase.size());
			
			
			bookUpdate.deleteOldBookmarkByKey(bookmarksToDelete, handler);
			bibUpdate.deleteOldBibtexByKey(bibtexsToDelete, handler);
			
		} catch(Exception e) {
			log.fatal(e);
			handler.close();
		}
	}

	public HashSet<String> getBibtexKeysInDatabase() {
		return bibtexKeysInDatabase;
	}

	public HashSet<String> getBookmarkKeysInDatabase() {
		return bookmarkKeysInDatabase;
	}

}