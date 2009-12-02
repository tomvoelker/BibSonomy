/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.bibtex.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.bibtex.util.StandardBibTeXFields;
import org.bibsonomy.model.BibTex;

import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexMacroReference;
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
 * Provides parsing of BibTeX entries represented by {@link String}s into {@link BibTex} objects.
 * 
 * FIXME: before using this in BibSonomy, it must be properly tested! Currently,
 * it puts too many fields into 'misc'.
 * 
 * 
 * @author rja
 * @version $Id$
 */
public class SimpleBibTeXParser {

	/**
	 * To concatenate persons (authors + editors)
	 */
	private static final String AND = " and ";

	/**
	 * Determines, if the parser will stop after the first parsing error 
	 * or if it tries to parse all and store all warnings. 
	 */
	private boolean tryParseAll = false;
	
	public void setTryParseAll(boolean tryParseAll) {
		this.tryParseAll = tryParseAll;
	}

	public boolean isTryParseAll() {
		return this.tryParseAll;
	}

	/**
	 * If tryParseAll is true, it holds all exceptions caught during the last parse process.
	 */
	private ParseException[] caughtExceptions = null;
	
	
	
	public ParseException[] getCaughtExceptions() {
		return this.caughtExceptions;
	}

	public void setCaughtExceptions(ParseException[] caughtExceptions) {
		this.caughtExceptions = caughtExceptions;
	}

	/**
	 * Stores warnings occuring during parsing.
	 */
	private final List<String> warnings;

	/**
	 * @return The warnings created during parsing.
	 */
	public List<String> getWarnings() {
		return this.warnings;
	}

	/**
	 * Clears the warnings.
	 */
	public void clearWarnings() {
		this.warnings.clear();
	}


	public SimpleBibTeXParser() {
		this.warnings = new LinkedList<String>();
	}

	/** Parses one BibTeX entry into a {@link BibTex} object.
	 * 
	 * @param bibtex - the BibTeX entry as string.
	 * @return The filled {@link BibTex} object.
	 * 
	 * @throws ParseException If a serious error during parsing occured. 
	 * 
	 * @throws IOException
	 */
	public BibTex parseBibTeX (final String bibtex) throws ParseException, IOException {
		final List<BibTex> list = parseInternal(bibtex, true);
		if (list.size() > 0)
			return list.get(0);
		return null;
	}

	public List<BibTex> parseBibTeXs (final String bibtex) throws ParseException, IOException { 
		return parseInternal(bibtex, false);
	}

	private List<BibTex> parseInternal (final String bibtex, final boolean firstEntryOnly) throws ParseException, IOException {
		final List<BibTex> result = new LinkedList<BibTex>();

		final BibtexParser parser = new BibtexParser(!tryParseAll);
		/*
		 * configure the parser
		 */
		/*
		 * To allow several "keywords" fields (as done by Connotea), we set the policy
		 * to keep all fields, such that we can access all keywords.
		 * 
		 * Default was KEEP_FIRST, changed by rja on 2008-08-26.
		 */
		//		parser.setMultipleFieldValuesPolicy(BibtexMultipleFieldValuesPolicy.KEEP_ALL);
		final BibtexFile bibtexFile = new BibtexFile();

		/*
		 * parse the string
		 */
		parser.parse(bibtexFile, new BufferedReader(new StringReader(bibtex)));


		// boolean topComment = false;
		// String topLevelComment;//stores comment or snippet, depending on bibtex entries

		// boolean standard = true;

		/* 
		 * expand all macros, crossrefs and author/editor field
		 */
		expandMacrosCrossRefsPersonLists(bibtexFile);



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
			/*
			 * add entry to result list
			 */
			result.add(fillBibtexFromEntry((BibtexEntry) potentialEntry));
			/*
			 * skip remaining entries
			 */
			if (firstEntryOnly) {
				return result;
			}
		}
		setCaughtExceptions(parser.getExceptions());
		return result;
	}

	/** Expands all macros, crossrefs and person lists. Any exceptions occuring are put into 
	 * the {@link #warnings}.
	 * 
	 * @param bibtexFile
	 */
	private void expandMacrosCrossRefsPersonLists(final BibtexFile bibtexFile) {
		try {
			/*
			 * rja, 2009-10-15; changed second parameter to "false" because 
			 * otherwise we can't store months as "jun", since the parser
			 * always expands them to "June".
			 */
			new MacroReferenceExpander(true, false, false, false).expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}

		try {
			new CrossReferenceExpander(true).expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}

		try {
			new PersonListExpander(true, true, false).expand(bibtexFile);
		} catch (ExpansionException ee) {
			warnings.add(ee.getMessage());
		}
	}

	/**
	 * This method does the main BibTeX work - after parsing it gets all field 
	 * values from the parsed entry and fills the BibTex object.
	 * 
	 * @param entry
	 * @return
	 */
	protected BibTex fillBibtexFromEntry(final BibtexEntry entry) {
		final BibTex bibtex = new BibTex();

		/* ************************************************
		 * process non standard bibtex fields 
		 * ************************************************/

		/*
		 * get set of all current fieldnames - like address, author etc. 
		 */
		final ArrayList<String> nonStandardFieldNames = new ArrayList<String>(entry.getFields().keySet());
		/*
		 * remove standard fields from list to retrieve nonstandard ones
		 * 
		 * FIXME: this needs to be adopted according to where we use the parser!
		 * in BibSonomy this must be the standardBibSonomyFields!
		 */
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
		field = (BibtexString) entry.getFieldValue("key");	        if (field != null) bibtex.setKey(field.getContent());
		field = (BibtexString) entry.getFieldValue("note");         if (field != null) bibtex.setNote(field.getContent());         
		field = (BibtexString) entry.getFieldValue("number");       if (field != null) bibtex.setNumber(field.getContent());       
		field = (BibtexString) entry.getFieldValue("organization"); if (field != null) bibtex.setOrganization(field.getContent()); 
		field = (BibtexString) entry.getFieldValue("pages");        if (field != null) bibtex.setPages(field.getContent());        
		field = (BibtexString) entry.getFieldValue("publisher");    if (field != null) bibtex.setPublisher(field.getContent());    
		field = (BibtexString) entry.getFieldValue("school");       if (field != null) bibtex.setSchool(field.getContent());       
		field = (BibtexString) entry.getFieldValue("series");       if (field != null) bibtex.setSeries(field.getContent());       
		field = (BibtexString) entry.getFieldValue("url");          if (field != null) bibtex.setUrl(field.getContent());           
		field = (BibtexString) entry.getFieldValue("volume");		if (field != null) bibtex.setVolume(field.getContent());        
		field = (BibtexString) entry.getFieldValue("abstract");		if (field != null) bibtex.setAbstract(field.getContent());
		field = (BibtexString) entry.getFieldValue("type");  		if (field != null) bibtex.setType(field.getContent());          

		/*
		 * special handling for month - it can be a macro!
		 */
		final BibtexAbstractValue month = entry.getFieldValue("month");
		if (month instanceof BibtexMacroReference) {
			bibtex.setMonth(((BibtexMacroReference) month).getKey());
		} else {
			field = (BibtexString) month;        if (field != null) bibtex.setMonth(field.getContent());        
		}

		/*
		 * parse person names for author + editor
		 */
		bibtex.setAuthor(createPersonString(entry.getFieldValue("author")));
		bibtex.setEditor(createPersonString(entry.getFieldValue("editor")));

		/*
		 * rja, 2009-06-30 (added this to BibTeXHandler and copied it here - but deactivated it)
		 * CiteULike uses the "comment" field to export (private) notes in the form
		 * 
		 * comment = {(private-note)This is a test note!}, 
		 * 
		 * Thus, we here extract the field and remove the "(private-note)" part
		 * 
		 * FIXME: add a test for this!
		 */
		field = (BibtexString) entry.getFieldValue("comment");	if (field != null) bibtex.setPrivnote(field.getContent().replace("(private-note)", ""));
		/*
		 * we export our private notes as "privnote" - add it here
		 */
		field = (BibtexString) entry.getFieldValue("privnote");	if (field != null) bibtex.setPrivnote(field.getContent());
		
		return bibtex;
	}
	
	
	

	/** Extracts all persons from the given field value and concatenates their names
	 * with {@value #AND}.
	 * 
	 * @param fieldValue
	 * @return The persons names concatenated with " and ".
	 */
	private String createPersonString (final BibtexAbstractValue fieldValue) {
		if (fieldValue != null && fieldValue instanceof BibtexPersonList) {
			/*
			 * cast into a person list and extract the persons
			 */
			final List<BibtexPerson> personList = ((BibtexPersonList) fieldValue).getList();
			/*
			 * result buffer
			 */
			final StringBuffer personBuffer = new StringBuffer();
			/*
			 * build person names
			 */
			for (final BibtexPerson person:personList) {
				/*
				 * build one person
				 * 
				 * FIXME: what is done here breaks author names whose last name
				 * consists of several parts, e.g.,
				 * Vander Wal, Thomas 
				 * If written as
				 * Thomas Vander Wal,
				 * "Vander" is interpreted as second name and the name is 
				 * treated in the wrong way at several occasions.
				 * Thus, we must ensure to store all author names as
				 * lastname, firstname
				 * and only change the order in the JSPs.
				 *  
				 */
				final StringBuffer personName = new StringBuffer();
				/*
				 * first name
				 */
				final String first = person.getFirst();
				if (first != null) personName.append(first);
				/*
				 * between first and last name
				 */
				final String preLast = person.getPreLast();
				if (preLast != null) personName.append(" " + preLast);
				/*
				 * last name
				 */
				final String last = person.getLast();
				if (last != null) personName.append(" " + last);
				/*
				 * "others" has a special meaning in BibTeX (it's converted to "et al."),
				 * so we must not ignore it! 
				 */
				if (person.isOthers()) personName.append("others");
				/*
				 * next name
				 */
				personBuffer.append(personName.toString().trim() + AND);
			}
			/* 
			 * remove last " and " 
			 */
			if (personBuffer.length() > AND.length()) {
				return personBuffer.substring(0, personBuffer.length() - AND.length());
			} 
		}
		return null;
	}
}
