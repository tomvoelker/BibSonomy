package org.bibsonomy.importer.DBLP.parser;

import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
/*
 * this class checks the dblp entries and store them into DBLPParseResult
 */
public class DBLPParseResultManager{

	private static final Log log = LogFactory.getLog(DBLPParseResultManager.class);
	
	private DBLPParseResult result;
	
	private static final String ENTRY_TYPE_ARTICLE = "article";
	private static final String ENTRY_TYPE_INPROCEEDINGS = "inproceedings";
	private static final String ENTRY_TYPE_PROCEEDINGS = "proceedings";
	private static final String ENTRY_TYPE_BOOK = "book";
	private static final String ENTRY_TYPE_INCOLLECTION = "incollection";
	private static final String ENTRY_TYPE_PHDTHESIS = "phdthesis";
	private static final String ENTRY_TYPE_MASTERSTHESIS = "mastersthesis";
	private static final String ENTRY_TYPE_WWW = "www";
	
	
	public DBLPParseResultManager(){
		result = new DBLPParseResult();
	}

	public DBLPParseResult getResult() {
		return result;
	}

	public void setResult(DBLPParseResult result) {
		this.result = result;
	}
	
	public Date getNewDBLPdate() {
		return result.getNewDBLPdate();
	}

	public void setNewDBLPdate(Date newDBLPdate) {
		result.setNewDBLPdate(newDBLPdate);
	}
	
	public Date getDblpdate(){
		return result.getDblpdate();
	}
	
	public void setDblpdate(Date dblpdate){
		result.setDblpdate(dblpdate);
	}
	
	/*
	 * check if entry has crossref and store in crossrefEntries map
	 */
	public boolean checkCrossref(DBLPEntry entry){
		if(entry.getCrossref()!=null){
			
			HashMap<String, HashMap<String, DBLPEntry>> crossrefEntries = result.getCrossrefEntries();

			if(!crossrefEntries.keySet().contains(entry.getCrossref())){
				/*
				 * node for this crossref does not exist --> create new node in crossref list
				 */
				crossrefEntries.put(entry.getCrossref(), new HashMap<String, DBLPEntry>());
			}
			/*
			 * add entry to crossref nodes entry list (crossrefEntries is an inverted index)
			 */
			crossrefEntries.get(entry.getCrossref()).put(entry.getDblpKey(), entry);
			return true;
		}
		return false;
	}
	
	/*
	 * this method checks if the entry has already a failure and store it in a failure or entrytype list
	 */
	public void addEntry(DBLPEntry entry) {
		
		log.debug("adding entry " + entry);
		
		if(entry.getEntryType().equals(ENTRY_TYPE_ARTICLE)){
			result.getEval().incArticle_count();
    		if(!checkCrossref(entry)) {
    			result.getArticlelist().add(entry);
    		}
    		result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_INPROCEEDINGS)){
			result.getEval().incInproceedings_count();
			if(!checkCrossref(entry)){
				result.getInproceedingslist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_PROCEEDINGS)){
			result.getEval().incProceedings_count();
			if(!checkCrossref(entry)) {
				result.getProceedingslist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_BOOK)){
			result.getEval().incBook_count();
			if(!checkCrossref(entry)) {
				result.getBooklist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_INCOLLECTION)){
			result.getEval().incIncollection_count();
			if(!checkCrossref(entry)) {
				result.getIncollectionlist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_PHDTHESIS)){
			result.getEval().incPhdthesis_count();
			if(!checkCrossref(entry)) {
				result.getPhdthesislist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_MASTERSTHESIS)){
			result.getEval().incMastersthesis_count();
			if(!checkCrossref(entry)) {
				result.getMastersthesislist().add(entry);
			}
			result.getAllKeys().add(entry.getDblpKey());
		}else if(entry.getEntryType().equals(ENTRY_TYPE_WWW)){
			result.getEval().incWww_count();
			if(entry.getUrl() != null){
				result.getAllKeys().add(entry.getDblpKey());
				result.getWwwlist().add(entry);
			}else{//empty url failure
				result.getEval().incInsert_bookmark_empty_url_count();
				result.getAllKeys().add(entry.getDblpKey());
				result.getInsert_bookmark_empty_url().add(entry);
			}
		}
	}
}