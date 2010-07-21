package org.bibsonomy.importer.DBLP;

import org.bibsonomy.importer.DBLP.parser.DBLPEntry;


public class DBLPEvaluation{
	
	private static final String ENTRY_TYPE_ARTICLE = "article";

	private static final String ENTRY_TYPE_INPROCEEDINGS = "inproceedings";
	
	private static final String ENTRY_TYPE_PROCEEDINGS = "proceedings";
	
	private static final String ENTRY_TYPE_BOOK = "book";
	
	private static final String ENTRY_TYPE_INCOLLECTION = "incollection";
	
	private static final String ENTRY_TYPE_PHDTHESIS = "phdthesis";
	
	private static final String ENTRY_TYPE_MASTERSTHESIS = "mastersthesis";
	
	private static final String ENTRY_TYPE_WWW = "www";
	
	private int article_count;

	private int inproceedings_count;

	private int proceedings_count;

	private int book_count;

	private int incollection_count;

	private int phdthesis_count;

	private int mastersthesis_count;

	private int www_count;

	private int update_article_count;

	private int update_inproceedings_count;

	private int update_proceedings_count;

	private int update_book_count;

	private int update_incollection_count;

	private int update_phdthesis_count;

	private int update_mastersthesis_count;

	private int update_www_count;
	
	private int insert_article_count;

	private int insert_inproceedings_count;

	private int insert_proceedings_count;

	private int insert_book_count;

	private int insert_incollection_count;

	private int insert_phdthesis_count;

	private int insert_mastersthesis_count;

	private int insert_www_count;
	
	private int insert_incomplete_author_editor_count;

	private int insert_bookmark_empty_url_count;

	private int insert_warning_count;

	private int insert_duplicate_count;

	private int insert_incomplete_count;

	private int upload_error_count;

	private int exception_count;
	
	private int insert_publications;
	
	private int insert_bookmarks;
		
	/*
	 * TODO: added by rja to count total
	 */
	private int entriesInInsertBibtexCalls = 0;
	
	public DBLPEvaluation(){
		article_count = 0;
		inproceedings_count = 0;
		proceedings_count = 0;
		book_count = 0;
		incollection_count = 0;
		phdthesis_count = 0;
		mastersthesis_count = 0;
		www_count = 0;
		insert_article_count = 0;
		insert_inproceedings_count = 0;
		insert_proceedings_count = 0;
		insert_book_count = 0;
		insert_incollection_count = 0;
		insert_phdthesis_count = 0;
		insert_mastersthesis_count = 0;
		insert_www_count = 0;
		insert_incomplete_author_editor_count = 0;
		insert_bookmark_empty_url_count = 0;
		insert_warning_count = 0;
		insert_duplicate_count = 0;
		insert_incomplete_count = 0;
		upload_error_count = 0;
		exception_count = 0;
		insert_publications = 0; 
		insert_bookmarks = 0;
	}

	public int incArticle_count() {
		return article_count++;
	}

	public void setArticle_count(int article_count) {
		this.article_count = article_count;
	}

	public int incBook_count() {
		return book_count++;
	}

	public void setBook_count(int book_count) {
		this.book_count = book_count;
	}

	public int incException_count() {
		return exception_count++;
	}

	public void setException_count(int exception_count) {
		this.exception_count = exception_count;
	}

	public int incIncollection_count() {
		return incollection_count++;
	}

	public void setIncollection_count(int incollection_count) {
		this.incollection_count = incollection_count;
	}

	public int incInproceedings_count() {
		return inproceedings_count++;
	}

	public void setInproceedings_count(int inproceedings_count) {
		this.inproceedings_count = inproceedings_count;
	}

	public int incInsert_bookmark_empty_url_count() {
		return insert_bookmark_empty_url_count++;
	}

	public void setInsert_bookmark_empty_url_count(
			int insert_bookmark_empty_url_count) {
		this.insert_bookmark_empty_url_count = insert_bookmark_empty_url_count;
	}

	public int incInsert_bookmarks() {
		return insert_bookmarks++;
	}

	public void setInsert_bookmarks(int insert_bookmarks) {
		this.insert_bookmarks = insert_bookmarks;
	}

	public int incInsert_duplicate_count() {
		return insert_duplicate_count++;
	}

	public void setInsert_duplicate_count(int insert_duplicate_count) {
		this.insert_duplicate_count = insert_duplicate_count;
	}

	public int incInsert_incomplete_author_editor_count() {
		return insert_incomplete_author_editor_count++;
	}

	public void setInsert_incomplete_author_editor_count(
			int insert_incomplete_author_editor_count) {
		this.insert_incomplete_author_editor_count = insert_incomplete_author_editor_count;
	}

	public int incInsert_incomplete_count() {
		return insert_incomplete_count++;
	}

	public void setInsert_incomplete_count(int insert_incomplete_count) {
		this.insert_incomplete_count = insert_incomplete_count;
	}

	public int incInsert_publications() {
		return insert_publications++;
	}

	public void setInsert_publications(int insert_publications) {
		this.insert_publications = insert_publications;
	}

	public int incInsert_warning_count() {
		return insert_warning_count++;
	}

	public void setInsert_warning_count(int insert_warning_count) {
		this.insert_warning_count = insert_warning_count;
	}

	public int incMastersthesis_count() {
		return mastersthesis_count++;
	}

	public void setMastersthesis_count(int mastersthesis_count) {
		this.mastersthesis_count = mastersthesis_count;
	}

	public int incPhdthesis_count() {
		return phdthesis_count++;
	}

	public void setPhdthesis_count(int phdthesis_count) {
		this.phdthesis_count = phdthesis_count;
	}

	public int incProceedings_count() {
		return proceedings_count++;
	}

	public void setProceedings_count(int proceedings_count) {
		this.proceedings_count = proceedings_count;
	}

	public int incUpload_error_count() {
		return upload_error_count++;
	}

	public void setUpload_error_count(int upload_error_count) {
		this.upload_error_count = upload_error_count;
	}

	public int incWww_count() {
		return www_count++;
	}

	public void setWww_count(int www_count) {
		this.www_count = www_count;
	}

	public int getArticle_count() {
		return article_count;
	}

	public int getBook_count() {
		return book_count;
	}

	public int getException_count() {
		return exception_count;
	}

	public int getIncollection_count() {
		return incollection_count;
	}

	public int getInproceedings_count() {
		return inproceedings_count;
	}

	public int getInsert_bookmark_empty_url_count() {
		return insert_bookmark_empty_url_count;
	}

	public int getInsert_bookmarks() {
		return insert_bookmarks;
	}

	public int getInsert_duplicate_count() {
		return insert_duplicate_count;
	}

	public int getInsert_incomplete_author_editor_count() {
		return insert_incomplete_author_editor_count;
	}

	public int getInsert_incomplete_count() {
		return insert_incomplete_count;
	}

	public int getInsert_publications() {
		return insert_publications;
	}

	public int getInsert_warning_count() {
		return insert_warning_count;
	}

	public int getMastersthesis_count() {
		return mastersthesis_count;
	}

	public int getPhdthesis_count() {
		return phdthesis_count;
	}

	public int getProceedings_count() {
		return proceedings_count;
	}

	public int getUpload_error_count() {
		return upload_error_count;
	}

	public int getWww_count() {
		return www_count;
	}

	public int incInsert_article_count() {
		return insert_article_count++;
	}

	public void setInsert_article_count(int insert_article_count) {
		this.insert_article_count = insert_article_count;
	}

	public int incInsert_book_count() {
		return insert_book_count++;
	}

	public void setInsert_book_count(int insert_book_count) {
		this.insert_book_count = insert_book_count;
	}

	public int incInsert_incollection_count() {
		return insert_incollection_count++;
	}

	public void setInsert_incollection_count(int insert_incollection_count) {
		this.insert_incollection_count = insert_incollection_count;
	}

	public int incInsert_inproceedings_count() {
		return insert_inproceedings_count++;
	}

	public void setInsert_inproceedings_count(int insert_inproceedings_count) {
		this.insert_inproceedings_count = insert_inproceedings_count;
	}

	public int incInsert_mastersthesis_count() {
		return insert_mastersthesis_count++;
	}

	public void setInsert_mastersthesis_count(int insert_mastersthesis_count) {
		this.insert_mastersthesis_count = insert_mastersthesis_count;
	}

	public int incInsert_phdthesis_count() {
		return insert_phdthesis_count++;
	}

	public void setInsert_phdthesis_count(int insert_phdthesis_count) {
		this.insert_phdthesis_count = insert_phdthesis_count;
	}

	public int incInsert_proceedings_count() {
		return insert_proceedings_count++;
	}

	public void setInsert_proceedings_count(int insert_proceedings_count) {
		this.insert_proceedings_count = insert_proceedings_count;
	}

	public int incInsert_www_count() {
		return insert_www_count++;
	}

	public void setInsert_www_count(int insert_www_count) {
		this.insert_www_count = insert_www_count;
	}

	public int getInsert_article_count() {
		return insert_article_count;
	}

	public int getInsert_book_count() {
		return insert_book_count;
	}

	public int getInsert_incollection_count() {
		return insert_incollection_count;
	}

	public int getInsert_inproceedings_count() {
		return insert_inproceedings_count;
	}

	public int getInsert_mastersthesis_count() {
		return insert_mastersthesis_count;
	}

	public int getInsert_phdthesis_count() {
		return insert_phdthesis_count;
	}

	public int getInsert_proceedings_count() {
		return insert_proceedings_count;
	}

	public int getInsert_www_count() {
		return insert_www_count;
	}

	public int incUpdate_article_count() {
		return update_article_count++;
	}

	public void setUpdate_article_count(int update_article_count) {
		this.update_article_count = update_article_count;
	}

	public int incUpdate_book_count() {
		return update_book_count++;
	}

	public void setUpdate_book_count(int update_book_count) {
		this.update_book_count = update_book_count;
	}

	public int incUpdate_incollection_count() {
		return update_incollection_count++;
	}

	public void setUpdate_incollection_count(int update_incollection_count) {
		this.update_incollection_count = update_incollection_count;
	}

	public int incUpdate_inproceedings_count() {
		return update_inproceedings_count++;
	}

	public void setUpdate_inproceedings_count(int update_inproceedings_count) {
		this.update_inproceedings_count = update_inproceedings_count;
	}

	public int incUpdate_mastersthesis_count() {
		return update_mastersthesis_count++;
	}

	public void setUpdate_mastersthesis_count(int update_mastersthesis_count) {
		this.update_mastersthesis_count = update_mastersthesis_count;
	}

	public int incUpdate_phdthesis_count() {
		return update_phdthesis_count++;
	}

	public void setUpdate_phdthesis_count(int update_phdthesis_count) {
		this.update_phdthesis_count = update_phdthesis_count;
	}

	public int incUpdate_proceedings_count() {
		return update_proceedings_count++;
	}

	public void setUpdate_proceedings_count(int update_proceedings_count) {
		this.update_proceedings_count = update_proceedings_count;
	}

	public int incUpdate_www_count() {
		return update_www_count++;
	}

	public void setUpdate_www_count(int update_www_count) {
		this.update_www_count = update_www_count;
	}

	public int getUpdate_article_count() {
		return update_article_count;
	}

	public int getUpdate_book_count() {
		return update_book_count;
	}

	public int getUpdate_incollection_count() {
		return update_incollection_count;
	}

	public int getUpdate_inproceedings_count() {
		return update_inproceedings_count;
	}

	public int getUpdate_mastersthesis_count() {
		return update_mastersthesis_count;
	}

	public int getUpdate_phdthesis_count() {
		return update_phdthesis_count;
	}

	public int getUpdate_proceedings_count() {
		return update_proceedings_count;
	}

	public int getUpdate_www_count() {
		return update_www_count;
	}
	
	public void increment(DBLPEntry entry){
		if(entry.getEntryType().equals(ENTRY_TYPE_ARTICLE)){
			article_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_INPROCEEDINGS)){
			inproceedings_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_PROCEEDINGS)){
			proceedings_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_BOOK)){
			book_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_INCOLLECTION)){
			incollection_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_PHDTHESIS)){
			phdthesis_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_MASTERSTHESIS)){
			mastersthesis_count++;
		}else if(entry.getEntryType().equals(ENTRY_TYPE_WWW)){
			www_count++;
		}
	}
		
	public void setInsert(String entrytype, int count){
		if(entrytype.equals(ENTRY_TYPE_ARTICLE)){
			insert_article_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_INPROCEEDINGS)){
			insert_inproceedings_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_PROCEEDINGS)){
			insert_proceedings_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_BOOK)){
			insert_book_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_INCOLLECTION)){
			insert_incollection_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_PHDTHESIS)){
			insert_phdthesis_count= count;
		}else if(entrytype.equals(ENTRY_TYPE_MASTERSTHESIS)){
			insert_mastersthesis_count= count;
		}
		insert_publications = insert_article_count + insert_inproceedings_count + insert_proceedings_count + insert_book_count + insert_incollection_count + insert_phdthesis_count + insert_mastersthesis_count;
	}
	
	public void incrementUpdate(DBLPEntry entry){
		entriesInInsertBibtexCalls++; // TODO: added by rja */
		final String entryType = entry.getEntryType();
		if(entryType.equals(ENTRY_TYPE_ARTICLE)){
			update_article_count++;
		}else if(entryType.equals(ENTRY_TYPE_INPROCEEDINGS)){
			update_inproceedings_count++;
		}else if(entryType.equals(ENTRY_TYPE_PROCEEDINGS)){
			update_proceedings_count++;
		}else if(entryType.equals(ENTRY_TYPE_BOOK)){
			update_book_count++;
		}else if(entryType.equals(ENTRY_TYPE_INCOLLECTION)){
			update_incollection_count++;
		}else if(entryType.equals(ENTRY_TYPE_PHDTHESIS)){
			update_phdthesis_count++;
		}else if(entryType.equals(ENTRY_TYPE_MASTERSTHESIS)){
			update_mastersthesis_count++;
		}else if(entryType.equals(ENTRY_TYPE_WWW)){
			update_www_count++;
		}
	}

	public String eval(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("##############################\nEntries in insertBibtexCalls: " + entriesInInsertBibtexCalls + "############################\n");
		buffer.append("parsed entrys: \n"); 
		buffer.append("\n");
		buffer.append("article=" + article_count + "\n");
		buffer.append("inproceedings=" + inproceedings_count + "\n");
		buffer.append("proceedings=" + proceedings_count + "\n");
		buffer.append("book=" + book_count + "\n");
		buffer.append("incollection=" + incollection_count + "\n");
		buffer.append("phdthesis=" + phdthesis_count + "\n");
		buffer.append("mastersthesis=" + mastersthesis_count + "\n");
		buffer.append("www=" + www_count + "\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("updated entrys: \n");
		buffer.append("\n");
		buffer.append("article=" + update_article_count + "\n");
		buffer.append("inproceedings=" + update_inproceedings_count + "\n");
		buffer.append("proceedings=" + update_proceedings_count + "\n");
		buffer.append("book=" + update_book_count + "\n");
		buffer.append("incollection=" + update_incollection_count + "\n");
		buffer.append("phdthesis=" + update_phdthesis_count + "\n");
		buffer.append("mastersthesis=" + update_mastersthesis_count + "\n");
		buffer.append("www=" + update_www_count + "\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("entrys in DB: \n");
		buffer.append("\n");
		buffer.append("article=" + insert_article_count + "\n");
		buffer.append("inproceedings=" + insert_inproceedings_count + "\n");
		buffer.append("proceedings=" + insert_proceedings_count + "\n");
		buffer.append("book=" + insert_book_count + "\n");
		buffer.append("incollection=" + insert_incollection_count + "\n");
		buffer.append("phdthesis=" + insert_phdthesis_count + "\n");
		buffer.append("mastersthesis=" + insert_mastersthesis_count + "\n");
		buffer.append("\n");
		buffer.append("all publications=" + insert_publications + "\n");
		buffer.append("all bookmarks=" + insert_bookmarks + "\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("\n");
		buffer.append("failures: \n");
		buffer.append("\n");
		buffer.append("bookmarks with empty url field=" + insert_bookmark_empty_url_count + "\n");
		buffer.append("bibtex insert warnings=" + insert_warning_count + "\n");
		buffer.append("bibtex insert duplicate failures=" + insert_duplicate_count + "\n");
		buffer.append("bibtex insert incomplete failures=" + insert_incomplete_count + "\n");
		buffer.append("bibtex insert incomplete(author/editor) failures(insert as bookmarks)=" + insert_incomplete_author_editor_count + "\n");
		buffer.append("exceptions=" + exception_count + "\n");
		return buffer.toString();
	}

}