/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
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

/*
 * Created on Mar 29, 2003
 * 
 * @author henkel@cs.colorado.edu
 *  
 */
package bibtex.expansions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibtex.dom.BibtexAbstractEntry;
import bibtex.dom.BibtexAbstractValue;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;
import bibtex.dom.BibtexString;

/**
 * This expander expands the crossreferences defined by the crossref fields -
 * you should run the MacroReferenceExpander first.
 * 
 * @author henkel
 */
public final class CrossReferenceExpander extends AbstractExpander implements Expander {

	/** Equivalent to CrossReferenceExpander(true) */
	public CrossReferenceExpander() {
		this(true);
	}

	/**
	 * @param throwAllExpansionExceptions
	 *            Setting this to true means that all exceptions will be thrown
	 *            immediately. Otherwise, the expander will skip over things it
	 *            can't expand and you can use getExceptions to retrieve the
	 *            exceptions later
	 */
	public CrossReferenceExpander(boolean throwAllExpansionExceptions) {
		super(throwAllExpansionExceptions);
	}

	/**
	 * Note: If you don't use the MacroReferenceExpander first, this function
	 * may lead to inconsistent macro references.
	 * 
	 * If you use the flag throwAllExpansionExceptions set to false, you can
	 * retrieve all the exceptions using getExceptions()
	 * 
	 * @param bibtexFile
	 */
	public void expand(final BibtexFile bibtexFile) throws ExpansionException {
		/*
		 * build map of bibtex entry keys
		 */
		final Map<String, BibtexAbstractEntry> entryKey2Entry = new HashMap<String, BibtexAbstractEntry>();
		final List<BibtexEntry> entriesWithCrossReference = new ArrayList<BibtexEntry>();
		for (final BibtexAbstractEntry abstractEntry : bibtexFile.getEntries()) {
			if (!(abstractEntry instanceof BibtexEntry))
				continue;
			final BibtexEntry entry = (BibtexEntry) abstractEntry;
			entryKey2Entry.put(entry.getEntryKey().toLowerCase(), abstractEntry);
			if (entry.getFields().containsKey("crossref")) {
				entriesWithCrossReference.add(entry);
			}
		}
		/*
		 * search for cross references
		 */
		for (final BibtexEntry entry : entriesWithCrossReference) {
			final String crossrefKey = ((BibtexString) entry.getFields().get("crossref")).getContent().toLowerCase();
			/*
			 * line uncommented, such that we can get the crossref data
			 */
			// entry.undefineField("crossref");  
			final BibtexEntry crossrefEntry = (BibtexEntry) entryKey2Entry.get(crossrefKey);
			// check if the referenced entry is available
			if (crossrefEntry == null) {
				throwExpansionException(new CrossReferenceExpansionException("crossref key not found", entry.getEntryKey(), crossrefKey));
			} else {
				// it is available - check if contains another (nested) crossref
				if (crossrefEntry.getFields().containsKey("crossref"))
					throwExpansionException(new CrossReferenceExpansionException(
							"Nested crossref: \""
									+ crossrefKey
									+ "\" is crossreferenced but crossreferences itself \""
									+ ((BibtexString) crossrefEntry.getFields().get("crossref")).getContent()
									+ "\"", entry.getEntryKey(), crossrefKey));
				// copy fields
				final Map<String, BibtexAbstractValue> entryFields = entry.getFields();
				final Map<String, BibtexAbstractValue> crossrefFields = crossrefEntry.getFields();
				for (final String key : crossrefFields.keySet()) {
					if (!entryFields.containsKey(key)) {
						entry.setField(key, crossrefFields.get(key));
					}
				}
			}
		}
		finishExpansion();
	}
}
