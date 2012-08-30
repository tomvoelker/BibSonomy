package org.bibsonomy.importer.DBLP.parser;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.bibsonomy.importer.DBLP.DBLPEvaluation;


/*
 * This class stores the results of the DBLP.xml parse
 */
public class DBLPParseResult {
	
	private DBLPEvaluation eval;

	/*
	 * new DBLP update date
	 */
	private Date newDBLPdate;
	
	/*
	 * old DBLP update date
	 */
	private Date dblpdate;
	
	/*
	 * all bibkeys
	 */
	private HashSet<String> allKeys;

	/*
	 * all bibtex entries stored for each entry type
	 */
	private final LinkedList<DBLPEntry> articlelist;

	private LinkedList<DBLPEntry> inproceedingslist;

	private LinkedList<DBLPEntry> proceedingslist;

	private LinkedList<DBLPEntry> booklist;

	private LinkedList<DBLPEntry> incollectionlist;

	private LinkedList<DBLPEntry> phdthesislist;

	private LinkedList<DBLPEntry> mastersthesislist;

	private LinkedList<DBLPEntry> wwwlist;

	/*
	 * stores all keys of crossreferenced entries with the reference and his dblpkey
	 */
	private HashMap<String, HashMap<String, DBLPEntry>> crossrefEntries;

	/*
	 * stores all crossreferenced entries
	 */
	private LinkedList<DBLPEntry> crossreflist;

	/*
	 * this list stores the four different types of errors
	 */
	private LinkedList<DBLPEntry> insert_incomplete_author_editor;

	private LinkedList<DBLPEntry> insert_bookmark_empty_url;

	public DBLPParseResult() {
		articlelist = new LinkedList<DBLPEntry>();
		inproceedingslist = new LinkedList<DBLPEntry>();
		proceedingslist = new LinkedList<DBLPEntry>();
		booklist = new LinkedList<DBLPEntry>();
		incollectionlist = new LinkedList<DBLPEntry>();
		phdthesislist = new LinkedList<DBLPEntry>();
		mastersthesislist = new LinkedList<DBLPEntry>();
		crossreflist = new LinkedList<DBLPEntry>();
		wwwlist = new LinkedList<DBLPEntry>();
		insert_incomplete_author_editor = new LinkedList<DBLPEntry>();
		insert_bookmark_empty_url = new LinkedList<DBLPEntry>();
		crossrefEntries = new HashMap<String, HashMap<String, DBLPEntry>>();
		allKeys = new HashSet<String>();
		eval = new DBLPEvaluation();
	}

	public HashSet<String> getAllKeys() {
		return allKeys;
	}

	public void setAllKeys(HashSet<String> allKeys) {
		this.allKeys = allKeys;
	}

	public Date getDblpdate() {
		return dblpdate;
	}

	public void setDblpdate(Date dblpdate) {
		this.dblpdate = dblpdate;
	}

	public LinkedList<DBLPEntry> getArticlelist() {
		return articlelist;
	}

	/*
	public void setArticlelist(LinkedList<DBLPEntry> articlelist) {
		this.articlelist = articlelist;
	}
	*/

	public LinkedList<DBLPEntry> getBooklist() {
		return booklist;
	}

	public void setBooklist(LinkedList<DBLPEntry> booklist) {
		this.booklist = booklist;
	}

	public LinkedList<DBLPEntry> getIncollectionlist() {
		return incollectionlist;
	}

	public void setIncollectionlist(
			LinkedList<DBLPEntry> incollectionlist) {
		this.incollectionlist = incollectionlist;
	}

	public LinkedList<DBLPEntry> getInproceedingslist() {
		return inproceedingslist;
	}

	public void setInproceedingslist(
			LinkedList<DBLPEntry> inproceedingslist) {
		this.inproceedingslist = inproceedingslist;
	}

	public LinkedList<DBLPEntry> getMastersthesislist() {
		return mastersthesislist;
	}

	public void setMastersthesislist(
			LinkedList<DBLPEntry> mastersthesislist) {
		this.mastersthesislist = mastersthesislist;
	}

	public LinkedList<DBLPEntry> getPhdthesislist() {
		return phdthesislist;
	}

	public void setPhdthesislist(LinkedList<DBLPEntry> phdthesislist) {
		this.phdthesislist = phdthesislist;
	}

	public LinkedList<DBLPEntry> getProceedingslist() {
		return proceedingslist;
	}

	public void setProceedingslist(LinkedList<DBLPEntry> proceedingslist) {
		this.proceedingslist = proceedingslist;
	}

	public LinkedList<DBLPEntry> getWwwlist() {
		return wwwlist;
	}

	public void setWwwlist(LinkedList<DBLPEntry> wwwlist) {
		this.wwwlist = wwwlist;
	}

	public Date getNewDBLPdate() {
		return newDBLPdate;
	}

	public void setNewDBLPdate(Date newDBLPdate) {
		this.newDBLPdate = newDBLPdate;
	}

	public LinkedList<DBLPEntry> getInsert_incomplete_author_editor() {
		return insert_incomplete_author_editor;
	}

	public void setInsert_incomplete_author_editor(
			LinkedList<DBLPEntry> author_editor_failure) {
		this.insert_incomplete_author_editor = author_editor_failure;
	}

	public LinkedList<DBLPEntry> getInsert_bookmark_empty_url() {
		return insert_bookmark_empty_url;
	}

	public void setInsert_bookmark_empty_url(
			LinkedList<DBLPEntry> insert_bookmark_url) {
		this.insert_bookmark_empty_url = insert_bookmark_url;
	}

	public HashMap<String, HashMap<String, DBLPEntry>> getCrossrefEntries() {
		return crossrefEntries;
	}

	public void setCrossrefEntries(HashMap<String, HashMap<String, DBLPEntry>> crossrefkeys) {
		this.crossrefEntries = crossrefkeys;
	}

	public LinkedList<DBLPEntry> getCrossreflist() {
		return crossreflist;
	}

	public void setCrossreflist(LinkedList<DBLPEntry> crossreflist) {
		this.crossreflist = crossreflist;
	}

	public String printStartValues(){
		StringBuffer buffer = new StringBuffer();
		
		return buffer.toString();
	}

	public DBLPEvaluation getEval() {
		return eval;
	}

	public void setEval(DBLPEvaluation eval) {
		this.eval = eval;
	}
	
}