package org.bibsonomy.importer.DBLP.parser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/* 
 * this class represents a DBLP entry and can generate snippets(for bibtex) and extended informations(for bookmark)
 */
public class DBLPEntry {
	
	public static final String DBLPURL = "http://dblp.uni-trier.de/";
	
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	private Date entrydate = null;
	
	private String dblpKey;
	private String entryType;

	/*
	 * holds all fields of bibtex entry
	 */
	private final HashMap<String,String> entryFields;

	@Override
	public String toString () {
		return "@" + entryType + "(" + dblpKey + ")";
	}
	
	public DBLPEntry() {
		entryFields = new HashMap<String,String>();
	}
	
	public Date getEntrydate() {
		return entrydate;
	}
	public void setEntrydate(Date dblpdate) {
		this.entrydate = dblpdate;
	}

	public String getDblpKey() {
		return dblpKey;
	}
	public void setDblpKey(String dblpKey) {
		this.dblpKey = dblpKey;
	}

	/*
	 * generic getting of entries
	 */
	private String getEntryField (String name) {
		return entryFields.get(name);
	}
	/*
	 * generic setting of entries
	 */
	public void setEntryField (String name, String value) {
		entryFields.put(name, value);
	}
	/*
	 * setting of entries for persons
	 */
	public void setEntryFieldPerson (String name, String value) {
		String oldValue = entryFields.get(name);
		if (oldValue == null) {
			setEntryField(name, value);
		} else {
			setEntryField(name, oldValue + " and " + value);
		}
	}
	

	public void setUrl(String url) {
		if (url != null && url.startsWith("db/")) {
			setEntryField(DBLPParserHandler.FIELD_URL, DBLPURL + url);	
		} else {
			setEntryField(DBLPParserHandler.FIELD_URL, url);
		}
		
	}
	public String getEntryType() {
		return entryType;
	}
	public void setEntryType(String entryType){
		this.entryType = entryType;
	}

	
	
	public String getAddress() {
		return getEntryField(DBLPParserHandler.FIELD_ADDRESS);
	}
	public String getAuthor() {
		return getEntryField(DBLPParserHandler.FIELD_AUTHOR);
	}
	public String getBooktitle() {
		return getEntryField(DBLPParserHandler.FIELD_BOOKTITLE);
	}
	public String getCdrom() {
		return getEntryField(DBLPParserHandler.FIELD_CDROM);
	}
	public String getChapter() {
		return getEntryField(DBLPParserHandler.FIELD_CHAPTER);
	}
	public String getCite() {
		return getEntryField(DBLPParserHandler.FIELD_CITE);
	}
	public String getCrossref() {
		return getEntryField(DBLPParserHandler.FIELD_CROSSREF);
	}
	public String getEditor() {
		return getEntryField(DBLPParserHandler.FIELD_EDITOR);
	}
	public String getEe() {
		return getEntryField(DBLPParserHandler.FIELD_EE);
	}
	public String getIsbn() {
		return getEntryField(DBLPParserHandler.FIELD_ISBN);
	}
	public String getJournal() {
		return getEntryField(DBLPParserHandler.FIELD_JOURNAL);
	}
	public String getMonth() {
		return getEntryField(DBLPParserHandler.FIELD_MONTH);
	}
	public String getNote() {
		return getEntryField(DBLPParserHandler.FIELD_NOTE);
	}
	public String getNumber() {
		return getEntryField(DBLPParserHandler.FIELD_NUMBER);
	}
	public String getPages() {
		return getEntryField(DBLPParserHandler.FIELD_PAGES);
	}
	public String getPublisher() {
		return getEntryField(DBLPParserHandler.FIELD_PUBLISHER);
	}
	public String getSchool() {
		return getEntryField(DBLPParserHandler.FIELD_SCHOOL);
	}
	public String getSeries() {
		return getEntryField(DBLPParserHandler.FIELD_SERIES);
	}
	public String getTitle() {
		return getEntryField(DBLPParserHandler.FIELD_TITLE);
	}
	public String getUrl() {
		return getEntryField(DBLPParserHandler.FIELD_URL);
	}
	public String getVolume() {
		return getEntryField(DBLPParserHandler.FIELD_VOLUME);
	}
	public String getYear() {
		return getEntryField(DBLPParserHandler.FIELD_YEAR);
	}
	
	/*
	 * this method generate a bibtex snippet out of the entered data
	 */
	public String generateSnippet() {
		// if we have no key, don't generate a snippet 
		if (dblpKey == null) return null;
		
		final StringBuffer snippet = new StringBuffer("@" + entryType + "{" + dblpKey + ",\n");

        /*
         * loop over possible entry fields
         */
        for (String entryFieldName:DBLPParserHandler.ENTRYFIELDS) {
        	String entryFieldValue = getEntryField(entryFieldName);
			if (entryFieldValue != null) {
				snippet.append(entryFieldName + "={" + entryFieldValue + "},\n");
        	}
        }
        snippet.append("date = {" + DBLPEntry.simpleDateFormat.format(entrydate) + "},");
        snippet.append("keywords = {dblp}}");		
		return snippet.toString();
	}
	
	
	/*
	 * output for bookmark
	 */
	public String generateExtended(){
		boolean newExtendedElement = false;
		StringBuffer extended = new StringBuffer();
        extended.append(dblpKey);
        extended.append(",");
        extended.append("\n");
        /*
         * loop over all entry fields
         */
        for (String entryFieldName:DBLPParserHandler.ENTRYFIELDS) {
        	String entryFieldValue = getEntryField(entryFieldName);
			if (entryFieldValue != null) {
				/* append ", ", if neccessary */
				if (newExtendedElement) 
					extended.append(", ");
				else
					newExtendedElement = true;
				/* append value */
        		extended.append(entryFieldValue);
        	}
        }
        
		return extended.toString();	}
}