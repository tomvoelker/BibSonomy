package org.bibsonomy.bibtex.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.bibtex.util.StandardBibTeXFields;
import org.bibsonomy.model.BibTex;

import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexPerson;
import bibtex.dom.BibtexPersonList;
import bibtex.dom.BibtexString;
import bibtex.dom.BibtexToplevelComment;
import bibtex.expansions.CrossReferenceExpander;
import bibtex.expansions.ExpansionException;
import bibtex.expansions.MacroReferenceExpander;
import bibtex.expansions.PersonListExpander;
import bibtex.parser.BibtexParser;
import bibtex.parser.ParseException;



/**
 * @author rja
 * @version $Id$
 */
public class SimpleBibTeXParser {

	final List<String> warnings;
	
	public List<String> getWarnings() {
		return this.warnings;
	}

	public SimpleBibTeXParser() {
		this.warnings = new LinkedList<String>();
	}
	
	public BibTex parseBibTeX (final String bibtex) throws ParseException, IOException {
		final Reader bibReader = new BufferedReader(new StringReader(bibtex));
		final BibtexParser parser = new BibtexParser(true);
		/*
		 * To allow several "keywords" fields (as done by Connotea), we set the policy
		 * to keep all fields, such that we can access all keywords.
		 * 
		 * Default was KEEP_FIRST, changed by rja on 2008-08-26.
		 */
//		parser.setMultipleFieldValuesPolicy(BibtexMultipleFieldValuesPolicy.KEEP_ALL);
		final BibtexFile bibtexFile = new BibtexFile();

		// parse file, exceptions are catched below
		parser.parse(bibtexFile, bibReader);


		// boolean topComment = false;
		// String topLevelComment;//stores comment or snippet, depending on bibtex entries

		// boolean standard = true;

		/* ****************************************************************
		 * expand all macros, crossrefs and convert author/editor field
		 * values into BibtexPersonList objects
		 * ****************************************************************/

		final MacroReferenceExpander macroExpander = new MacroReferenceExpander(true, true, false, false);
		try {
			macroExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}

		final CrossReferenceExpander crossExpander = new CrossReferenceExpander(true);
		try {
			crossExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}

		final PersonListExpander pListExpander = new PersonListExpander(true,	true, false);
		try {
			pListExpander.expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}



		/* ****************************************************************
		 * iterate over all entries and put them in BibTex objects
		 * ****************************************************************/
		for (Object potentialEntry:bibtexFile.getEntries()) {

			if (!(potentialEntry instanceof BibtexEntry)) {
				/*
				 * Process top level comment, but drop macros, because
				 * they are already expanded!
				 */
				if (potentialEntry instanceof BibtexToplevelComment) {
					/*
					 * Retrieve and process Toplevel Comment if
					 * needed??? BibtexToplevelComment comment =
					 * (BibtexToplevelComment) potentialEntry; String
					 * topLevelComment = comment.getContent();
					 */
					continue;
				} else {
					continue;
				}
			}
			// fill other fields from entry
			return fillBibtexFromEntry(potentialEntry);
		}
		/*
		 * no entry found
		 */
		return null;
	}

	/**
	 * This method does the main BibTeX work - after parsing it gets all field 
	 * values from the parsed entry and fills the BibTex object.
	 * 
	 * @param potentialEntry
	 * @return
	 */
	private BibTex fillBibtexFromEntry(Object potentialEntry) {
		final BibTex bibtex = new BibTex();
		
		final BibtexEntry entry = (BibtexEntry) potentialEntry;
		/* ************************************************
		 * process non standard bibtex fields 
		 * ************************************************/

		// get set of all current fieldnames - like address, author etc.
		final ArrayList<String> nonStandardFieldNames = new ArrayList<String>(entry.getFields().keySet());
		// remove standard fields from list to retrieve nonstandard ones
		// FIXME: this needs to be adopted according to where we use the parser!
		// in BibSonomy this must be the standardBibSonomyFields!
		nonStandardFieldNames.removeAll(StandardBibTeXFields.getStandardBibTeXFields());

		// iter over arraylist to retrieve nonstandard field values
		final StringBuffer miscBuffer = new StringBuffer();
		for (final String key:nonStandardFieldNames) {
			final String value = ((BibtexString) entry.getFieldValue(key)).getContent();
			miscBuffer.append(key + " = {"	+ value + "},\n");
			bibtex.addMiscField(key, value);
		}
		// remove last colon
		if (miscBuffer.length() > 3) {
			miscBuffer.delete(miscBuffer.length() - 2, miscBuffer.length());
		}

		bibtex.setMisc(miscBuffer.toString());

		/* ************************************************
		 * process standard bibtex fields 
		 * ************************************************/


		/*
		 * add mandatory fields
		 */
		// retrieve entry/bibtex key
		bibtex.setBibtexKey(entry.getEntryKey());
		// retrieve entry type - should not be null or ""
		bibtex.setEntrytype(entry.getEntryType());
		
		BibtexString field = null;
		field = (BibtexString) entry.getFieldValue("title"); if (field != null) bibtex.setTitle(field.getContent());
		field = (BibtexString) entry.getFieldValue("year");  if (field != null) bibtex.setYear(field.getContent()); 

		/*
		 * add optional fields
		 */
		field = (BibtexString) entry.getFieldValue("crossref");     if (field != null) bibtex.setCrossref(field.getContent());     
		field = (BibtexString) entry.getFieldValue("address");      if (field != null) bibtex.setAddress(field.getContent());      
		field = (BibtexString) entry.getFieldValue("annote");       if (field != null) bibtex.setAnnote(field.getContent());       
		field = (BibtexString) entry.getFieldValue("booktitle");    if (field != null) bibtex.setBooktitle(field.getContent());    
		field = (BibtexString) entry.getFieldValue("chapter");      if (field != null) bibtex.setChapter(field.getContent());      
		field = (BibtexString) entry.getFieldValue("day");          if (field != null) bibtex.setDay(field.getContent());
		field = (BibtexString) entry.getFieldValue("edition");      if (field != null) bibtex.setEdition(field.getContent());      
		field = (BibtexString) entry.getFieldValue("howpublished"); if (field != null) bibtex.setHowpublished(field.getContent()); 
		field = (BibtexString) entry.getFieldValue("institution");	if (field != null) bibtex.setInstitution(field.getContent());  
		field = (BibtexString) entry.getFieldValue("journal");      if (field != null) bibtex.setJournal(field.getContent());      
		field = (BibtexString) entry.getFieldValue("key");	        if (field != null) bibtex.setBKey(field.getContent());
		field = (BibtexString) entry.getFieldValue("month");        if (field != null) bibtex.setMonth(field.getContent());        
		field = (BibtexString) entry.getFieldValue("note");         if (field != null) bibtex.setNote(field.getContent());         
		field = (BibtexString) entry.getFieldValue("number");       if (field != null) bibtex.setNumber(field.getContent());       
		field = (BibtexString) entry.getFieldValue("organization"); if (field != null) bibtex.setOrganization(field.getContent()); 
		field = (BibtexString) entry.getFieldValue("pages");        if (field != null) bibtex.setPages(field.getContent());        
		field = (BibtexString) entry.getFieldValue("publisher");    if (field != null) bibtex.setPublisher(field.getContent());    
		field = (BibtexString) entry.getFieldValue("school");       if (field != null) bibtex.setSchool(field.getContent());       
		field = (BibtexString) entry.getFieldValue("series");       if (field != null) bibtex.setSeries(field.getContent());       
		field = (BibtexString) entry.getFieldValue("url");          if (field != null) bibtex.setUrl(field.getContent());           
		field = (BibtexString) entry.getFieldValue("volume");		if (field != null) bibtex.setVolume(field.getContent());        
		field = (BibtexString) entry.getFieldValue("abstract");		if (field != null) bibtex.setBibtexAbstract(field.getContent());
		field = (BibtexString) entry.getFieldValue("type");  		if (field != null) bibtex.setType(field.getContent());          

		/*
		 * parse person names for author + editor
		 */
		bibtex.setAuthor(createPersonString(entry.getFieldValue("author")));
		bibtex.setEditor(createPersonString(entry.getFieldValue("editor")));

		return bibtex;
	}
	
	/** Extracts all persons from the given field value and concatenates their name
	 * with "and".
	 * 
	 * @param fieldValue
	 * @return
	 */
	private String createPersonString (final BibtexAbstractValue fieldValue) {
		// returns unmodifiable list of BibtexPerson objects
		final StringBuffer personBuffer = new StringBuffer();
		if (fieldValue instanceof BibtexPersonList) {
			final BibtexPersonList personString = (BibtexPersonList) fieldValue;
			if (personString != null) {
				final List<BibtexPerson> personList = personString.getList();

				for (final BibtexPerson person:personList) {
					// build one author

					final StringBuffer personName = new StringBuffer();
					final String first = person.getFirst();
					if (first != null) {
						personName.append(first);
					}

					final String preLast = person.getPreLast();
					if (preLast != null) {
						personName.append(" " + preLast);
					}

					final String last = person.getLast();
					if (last != null) {
						personName.append(" " + last);
					}

					personBuffer.append(personName + " and ");
				}
				/* remove last " and " */
				if (!personList.isEmpty()) {
					return(personBuffer.substring(0, personBuffer.lastIndexOf(" and ")));
				} 
			} 
		}
		return null;
	}
}
