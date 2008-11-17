/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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

package org.bibsonomy.bibtex.util;

import java.util.HashSet;
import java.util.Set;

/** Holds the standard BibTeX fields. Those are all fields which are 
 * represented by the model and which are not put into the "misc" 
 * column.
 * 
 * @author rja
 * @version $Id$
 */
public class StandardBibTeXFields {

	private final static Set<String> standardBibSonomyFields = new HashSet<String>();

	static {
		standardBibSonomyFields.add("abstract");
		standardBibSonomyFields.add("address");
		standardBibSonomyFields.add("annote");
		standardBibSonomyFields.add("author");
		standardBibSonomyFields.add("booktitle");
		standardBibSonomyFields.add("chapter");
		standardBibSonomyFields.add("crossref");
		standardBibSonomyFields.add("edition");
		standardBibSonomyFields.add("editor");
		standardBibSonomyFields.add("howpublished");
		standardBibSonomyFields.add("institution");
		standardBibSonomyFields.add("journal");
		standardBibSonomyFields.add("key");
		standardBibSonomyFields.add("month");
		standardBibSonomyFields.add("note");
		standardBibSonomyFields.add("number");
		standardBibSonomyFields.add("organization");
		standardBibSonomyFields.add("pages");
		standardBibSonomyFields.add("publisher");
		standardBibSonomyFields.add("school");
		standardBibSonomyFields.add("series");
		standardBibSonomyFields.add("title");
		standardBibSonomyFields.add("type");
		standardBibSonomyFields.add("volume");
		standardBibSonomyFields.add("year");
		// added, because otherwise "day" will go to "misc"
		standardBibSonomyFields.add("day");
		// standard fields for bibsonomy
		standardBibSonomyFields.add("description");
		standardBibSonomyFields.add("tags");
		standardBibSonomyFields.add("url");
		standardBibSonomyFields.add("keywords");
		standardBibSonomyFields.add("biburl");   // added because this way it is not added to "misc"
	}


	public static Set<String> getStandardBibSonomyFields() {
		return standardBibSonomyFields;
	}
	
	private final static Set<String> standardBibTeXFields = new HashSet<String>();

	static {
		standardBibTeXFields.add("abstract");
		standardBibTeXFields.add("address");
		standardBibTeXFields.add("annote");
		standardBibTeXFields.add("author");
		standardBibTeXFields.add("booktitle");
		standardBibTeXFields.add("chapter");
		standardBibTeXFields.add("crossref");
		standardBibTeXFields.add("edition");
		standardBibTeXFields.add("editor");
		standardBibTeXFields.add("howpublished");
		standardBibTeXFields.add("institution");
		standardBibTeXFields.add("journal");
		standardBibTeXFields.add("key");
		standardBibTeXFields.add("month");
		standardBibTeXFields.add("note");
		standardBibTeXFields.add("number");
		standardBibTeXFields.add("organization");
		standardBibTeXFields.add("pages");
		standardBibTeXFields.add("publisher");
		standardBibTeXFields.add("school");
		standardBibTeXFields.add("series");
		standardBibTeXFields.add("title");
		standardBibTeXFields.add("type");
		standardBibTeXFields.add("volume");
		standardBibTeXFields.add("year");
	}


	public static Set<String> getStandardBibTeXFields() {
		return standardBibTeXFields;
	}
	
}