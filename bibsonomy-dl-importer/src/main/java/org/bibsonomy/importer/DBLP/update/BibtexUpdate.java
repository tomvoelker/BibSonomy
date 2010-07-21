package org.bibsonomy.importer.DBLP.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.importer.DBLP.db.DBHandler;
import org.bibsonomy.importer.DBLP.parser.DBLPEntry;
import org.bibsonomy.importer.DBLP.parser.DBLPParseResult;
import org.w3c.dom.Document;


public class BibtexUpdate{

	private static final Log log = LogFactory.getLog(BibtexUpdate.class);

	private HTTPBibtexUpdate httpBibUpdate = null;
	private DBLPParseResult presult = null;
	private DBHandler handler = null;

	public BibtexUpdate (HTTPBibtexUpdate httpBibUpdate, DBLPParseResult presult, DBHandler handler) throws MalformedURLException, IOException {
		this.presult = presult;
		this.handler = handler;
		this.httpBibUpdate = httpBibUpdate;
	}

	private static final int maxNumberOfEntries = 100;

	/*
	 * this method insert 10 entries from the same type in a row
	 */
	public int update(HashSet<String> bibtexKeysInDatabase) {
		int updateCount = 0;
		log.info("updating article");      updateCount += handleEntries(bibtexKeysInDatabase, presult.getArticlelist());
		log.info("updating book");         updateCount += handleEntries(bibtexKeysInDatabase, presult.getBooklist());
		log.info("updating incollection"); updateCount += handleEntries(bibtexKeysInDatabase, presult.getIncollectionlist());
		log.info("updating inproceeding"); updateCount += handleEntries(bibtexKeysInDatabase, presult.getInproceedingslist());
		log.info("updating masterthesis"); updateCount += handleEntries(bibtexKeysInDatabase, presult.getMastersthesislist());
		log.info("updating phdthesis");    updateCount += handleEntries(bibtexKeysInDatabase, presult.getPhdthesislist());
		log.info("updating proceeding");   updateCount += handleEntries(bibtexKeysInDatabase, presult.getProceedingslist());
		log.info("updating crossrefs");    updateCount += handleCrossRefs(bibtexKeysInDatabase);
		
		log.info("HTTP status codes: " + httpBibUpdate.getHttpStatusCounts());
		return updateCount;
	}


	private int handleCrossRefs(HashSet<String> db_bibtex_keys) {
		final LinkedList<DBLPEntry> list = new LinkedList<DBLPEntry>();
		int error_count = 0;
		int listCounter = 0;
		int nullCounter = 0;
		int notNullCounter = 0;

		// iterate over all referenz entries
		for(final String keyReferenz: presult.getCrossrefEntries().keySet()){
			final HashMap<String, DBLPEntry> crossrefEntries = presult.getCrossrefEntries().get(keyReferenz);
			if(crossrefEntries != null){//check if crossref is already updated
				// iterate over all crossref entry from a referenz entry
				for(final DBLPEntry entry: crossrefEntries.values()){
					listCounter++;
					if(entry.getEntrydate().after(presult.getDblpdate())){
						list.add(entry);
					}
				}
				if(list.size()>0){
					for(DBLPEntry entry: presult.getCrossreflist()){
						if(entry.getDblpKey().equals(keyReferenz)){
							list.add(entry);
							break;
						}
					}

					deleteAndInsertEntries(db_bibtex_keys, list);
					list.clear();
				}
				notNullCounter++;
			} else {nullCounter++;}
		}

		return error_count;
	}


	/*
	 * handles entries for one entry type 
	 */
	private int handleEntries(final HashSet<String> bibtexKeysInDatabase, final LinkedList<DBLPEntry> entries) {
		/*
		 * contains the entries that will be updated
		 */
		final LinkedList<DBLPEntry> updateList = new LinkedList<DBLPEntry>();
		int updateCount = 0;

		log.info("   handling " + entries.size() + " entries");
		/*
		 * iterate over all entries in given list
		 */
		for (final DBLPEntry entry: entries) {

			if (entry.getEntrydate().after(presult.getDblpdate())){
				/*
				 * entry is newer than last insert date --> add it to update list
				 */
				updateList.add(entry);

				/*
				 * insert crossref entries
				 */
				if (presult.getCrossrefEntries().containsKey(entry.getDblpKey())) {
					presult.getCrossreflist().add(entry);
					final HashMap<String, DBLPEntry> crossrefs = presult.getCrossrefEntries().get(entry.getDblpKey());
					if (crossrefs != null) {
						updateList.addAll(crossrefs.values());
						presult.getCrossrefEntries().put(entry.getDblpKey(), null);
					}
				}
			} else if (presult.getCrossrefEntries().containsKey(entry.getDblpKey())){
				presult.getCrossreflist().add(entry);
			}			

			if (updateList.size() >= maxNumberOfEntries) {
				updateCount += updateList.size();
				deleteAndInsertEntries(bibtexKeysInDatabase, updateList);
				updateList.clear();
			}
		}
		updateCount += updateList.size();

		if(updateList.size()>1){//store the rest
			deleteAndInsertEntries(bibtexKeysInDatabase, updateList);
		}else if(updateList.size() == 1){
			updateList.add(updateList.getFirst()); // TODO: dirty hack: bibsonomy does not insert one entry alone by it self
			deleteAndInsertEntries(bibtexKeysInDatabase, updateList);
		}
		entries.clear();
		log.info("   updated " + updateCount + " entries");
		return updateCount;

	}


	/*
	 * deletes entries from list (their old counterparts) and adds new version   
	 */
	private void deleteAndInsertEntries(HashSet<String> db_bibtex_keys, LinkedList<DBLPEntry> list) {
		try {
			// delete
			httpBibUpdate.deleteOldBibtexByEntry(list, handler, db_bibtex_keys);
			// insert
			final Document insertNewBibtexDocument = httpBibUpdate.insertNewBibtex(list, presult.getEval());
			/*
			 * FIXME: rja, 2010-03-29; disabled - does not work after new release
			 */
			if (insertNewBibtexDocument != null) {
				HTMLResultHandler.searchFailureMessage(list, presult, insertNewBibtexDocument);
			}
		} catch(Exception e) {
			/*
			 * error handling
			 */
			log.fatal("BibtexUpdate: ", e);
			handler.close();
		}
	}
}