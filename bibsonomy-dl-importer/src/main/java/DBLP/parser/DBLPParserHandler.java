package DBLP.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * dblp.xml parse handler
 */
public class DBLPParserHandler extends DefaultHandler {
	
	private static final Log log = LogFactory.getLog(DBLPParserHandler.class);
	
	public static final String FIELD_CHAPTER = "chapter";
	public static final String FIELD_SCHOOL = "school";
	public static final String FIELD_SERIES = "series";
	public static final String FIELD_ISBN = "isbn";
	public static final String FIELD_CROSSREF = "crossref";
	public static final String FIELD_NOTE = "note";
	public static final String FIELD_PUBLISHER = "publisher";
	public static final String FIELD_CITE = "cite";
	public static final String FIELD_CDROM = "cdrom";
	public static final String FIELD_EE = "ee";
	public static final String FIELD_URL = "url";
	public static final String FIELD_MONTH = "month";
	public static final String FIELD_NUMBER = "number";
	public static final String FIELD_VOLUME = "volume";
	public static final String FIELD_JOURNAL = "journal";
	public static final String FIELD_ADDRESS = "address";
	public static final String FIELD_YEAR = "year";
	public static final String FIELD_PAGES = "pages";
	public static final String FIELD_BOOKTITLE = "booktitle";
	public static final String FIELD_TITLE = "title";
	public static final String FIELD_EDITOR = "editor";
	public static final String FIELD_AUTHOR = "author";

	/*
	 * check if entry date is new 
	 */
	private boolean validEntry;
	
	/*
	 * straight filled dblp entry
	 */
	private DBLPEntry entry;

	/*
	 * results of parsing
	 */
	private DBLPParseResultManager manager;
	
	/*
	 * holds literals (i.e. the "real" data) 
	 */
	private StringBuffer buf = null;
	
	/*
	 * to parse dates
	 */
  	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	
	/*
	 * all XML elements from dblp.xml
	 */
	
	public static final String[] ENTRYTYPES = new String[] {"article", "inproceedings", "proceedings", "book", "incollection", "phdthesis", "mastersthesis", "www"};

	public static final String[] ENTRYFIELDS = new String[] {FIELD_AUTHOR,FIELD_EDITOR,FIELD_TITLE,FIELD_BOOKTITLE,FIELD_PAGES,FIELD_YEAR,FIELD_ADDRESS,
		FIELD_JOURNAL,FIELD_VOLUME,FIELD_NUMBER,FIELD_MONTH,FIELD_URL,FIELD_EE,FIELD_CDROM,FIELD_CITE,FIELD_PUBLISHER,FIELD_NOTE,FIELD_CROSSREF,FIELD_ISBN,
		FIELD_SERIES,FIELD_SCHOOL,FIELD_CHAPTER};
		
	HashSet<String> entryTypes;
	HashSet<String> entryFields;
	
	public DBLPParserHandler(Date dblpdate){
		super();
		manager = new DBLPParseResultManager();
	    entry = null;
	    manager.setNewDBLPdate(null);
	    manager.setDblpdate(dblpdate);
	    
	    /*
	     * initialize set with entry types
	     */
	    entryTypes = new HashSet<String>();
	    for (String entryType:ENTRYTYPES) {
	    	entryTypes.add(entryType);
	    }
	    /*
	     * initialize set with entry fields
	     */
	    entryFields = new HashSet<String>();
	    for (String entryField:ENTRYFIELDS) {
	    	entryFields.add(entryField);
	    }

	}

    /*
     * parse start-tags
     */
    public void startElement (String uri, String name, String qName, Attributes atts) {
    	if (name.equals("dblp")) {
    		/*
    		 * do nothing
    		 */
    		log.debug("found dblp root node");
    	} else if (entryTypes.contains(name.toLowerCase())) {
    		/*
    		 * start of entry
    		 */
    		log.debug("found new entry");
    		newEntry(name, atts);
    	} else if (validEntry && entryFields.contains(name)) {
    		/*
    		 * entry field found
    		 */
    		buf = new StringBuffer();
		}
    }

	private void newEntry(String name, Attributes atts) {
		Date entrydate = null;
		try {
			entrydate = df.parse(atts.getValue("mdate"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (entrydate != null) {
			// update, get only new or changed  entries
			if (manager.getNewDBLPdate() == null || entrydate.after(manager.getNewDBLPdate())) // increases date for next update
				manager.setNewDBLPdate(entrydate);
			int index = atts.getIndex("key");
			if (validEntryType(name) && index != -1) { // key is set
				entry = new DBLPEntry();
				entry.setEntryType(name);
				entry.setDblpKey(atts.getValue(index));
				entry.setEntrydate(entrydate);
				validEntry = true;
			} else {
				validEntry = false;
			}
		}
	}

    /*
     * parse end-tags
     */
    public void endElement (String uri, String name, String qName){
    	
    	if (validEntry && validEntryField(name)) {
    		/* lastReadElement is an entry field */
    		if (FIELD_AUTHOR.equals(name) || FIELD_EDITOR.equals(name)) {
    			/* author or editor */
    			entry.setEntryFieldPerson(name, buf.toString());
    		} else if (FIELD_URL.equals(name)) {
    			/* set url */
    			entry.setUrl(buf.toString());
    		} else {
    			/* other field */
    			entry.setEntryField(name, buf.toString());
    		}
	    } 
    	
    	if (validEntry && validEntryType(name)) {
    		manager.addEntry(entry);
    		entry = null;
    	}
    }

    /*
     * parse textdata between start and end-tag --> buffer it
     */
    public void characters (char ch[], int start, int length){
    	buf.append(ch, start, length);
	}

	/*
	 * check if given string is a valid dblp entry type or not 
	 */
	private boolean validEntryType(String entryType) {
		return entryTypes.contains(entryType);
	}
	
	/*
	 * check if given string is a valid dblp element or not 
	 */
	private boolean validEntryField(String entry) {
		return entryFields.contains(entry);
	}

	public DBLPParseResult getResult() {
		return manager.getResult();
	}
	
}