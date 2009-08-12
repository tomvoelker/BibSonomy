package DBLP.update;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import DBLP.db.DBHandler;
import DBLP.parser.DBLPEntry;
import DBLP.parser.DBLPParseResult;

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
	
	private static final int maxNumberOfEntries = 10;

	/*
	 * this method insert 10 entries from the same type in a row
	 */
	public int update(HashSet<String> db_bibtex_keys) {
		
		
		
		int error_count = 0;
		
		log.info("updating article");      error_count += handleEntries(db_bibtex_keys, presult.getArticlelist());
		log.info("updating book");         error_count += handleEntries(db_bibtex_keys, presult.getBooklist());
		log.info("updating incollection"); error_count += handleEntries(db_bibtex_keys, presult.getIncollectionlist());
		log.info("updating inproceeding"); error_count += handleEntries(db_bibtex_keys, presult.getInproceedingslist());
		log.info("updating masterthesis"); error_count += handleEntries(db_bibtex_keys, presult.getMastersthesislist());
		log.info("updating phdthesis");    error_count += handleEntries(db_bibtex_keys, presult.getPhdthesislist());
		log.info("updating proceeding");   error_count += handleEntries(db_bibtex_keys, presult.getProceedingslist());
		log.info("updating crossrefs");    error_count += handleCrossRefs(db_bibtex_keys);
		
		return error_count;
	}


	private int handleCrossRefs(HashSet<String> db_bibtex_keys) {
		LinkedList<DBLPEntry> list = new LinkedList<DBLPEntry>();
		int error_count = 0;
		int listCounter = 0;
		int nullCounter = 0;
		int notNullCounter = 0;

		// iterate over all referenz entries
		for(String keyReferenz: presult.getCrossrefEntries().keySet()){
			HashMap<String, DBLPEntry> crossrefEntries = presult.getCrossrefEntries().get(keyReferenz);
			if(crossrefEntries != null){//check if crossref is already updated
				// iterate over all crossref entry from a referenz entry
				for(DBLPEntry entry: crossrefEntries.values()){
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

					error_count += deleteAndInsertEntries(db_bibtex_keys, list);
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
	private int handleEntries(HashSet<String> db_bibtex_keys, LinkedList<DBLPEntry> entries) {
		int error_count = 0;
		LinkedList<DBLPEntry> updateList = new LinkedList<DBLPEntry>();

		/*
		 * iterate over all entries in given list
		 */
		for (DBLPEntry entry: entries) {

			if(entry.getEntrydate().after(presult.getDblpdate())){
				/*
				 * entry is newer than last insert date --> add it to update list
				 */
				updateList.add(entry);

				//insert crossref entries
				if(presult.getCrossrefEntries().containsKey(entry.getDblpKey())){
					presult.getCrossreflist().add(entry);
					HashMap<String, DBLPEntry> crossrefs = presult.getCrossrefEntries().get(entry.getDblpKey());
					if (crossrefs != null) {
						updateList.addAll(crossrefs.values());
						presult.getCrossrefEntries().put(entry.getDblpKey(), null);
					}
				}
			}else if(presult.getCrossrefEntries().containsKey(entry.getDblpKey())){
				presult.getCrossreflist().add(entry);
			}			

			if(updateList.size() >= maxNumberOfEntries){
				error_count += deleteAndInsertEntries(db_bibtex_keys, updateList);
				updateList.clear();
			}
		}
		if(updateList.size()>1){//store the rest
			error_count += deleteAndInsertEntries(db_bibtex_keys, updateList);
		}else if(updateList.size() == 1){
			updateList.add(updateList.getFirst()); // TODO: dirty hack: bibsonomy does not insert one entry alone by it self
			error_count += deleteAndInsertEntries(db_bibtex_keys, updateList);
		}
		entries.clear();
		return error_count;
	}


	/*
	 * deletes entries from list (their old counterparts) and adds new version   
	 */
	private int deleteAndInsertEntries(HashSet<String> db_bibtex_keys, LinkedList<DBLPEntry> list) {
		try {
			// delete
			httpBibUpdate.deleteOldBibtexByEntry(list, handler, db_bibtex_keys);
			// insert
			HTMLResultHandler.searchFailureMessage(list, presult, httpBibUpdate.insertNewBibtex(list, presult.getEval()));
		} catch(Exception e) {
			/*
			 * error handling
			 */
			System.out.println("DBLPUpdater: BibtexUpdate: " + e);
			e.printStackTrace();
			StringBuffer error = new StringBuffer();
			for (DBLPEntry bibentry: list) {
				error.append(bibentry.getDblpKey() + " ");
			}
			presult.getException().add("insert bibtex=" + e + " " + error);
			handler.close();
			return 1;
		}
		return 0;
	}
}