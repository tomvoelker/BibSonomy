/**
 *
 *  BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
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

/**
 * 
 */
package org.bibsonomy.scraper.converter;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.id.ISBNUtils;

/**
 * @author aho
 *
 */
public class RisToBibtexConverter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String bsp = "TY  - BOOK\n"+
		"JF  - Lecture Notes in Computer Science : Engineering Self-Organising Systems\n"+
		"T1  - T-Man: Gossip-Based Overlay Topology Management\n"+
		"SP  - 1\n"+
		"EP  - 15\n"+
		"PY  - 2006///\n"+
		"UR  - http://dx.doi.org/10.1007/11734697_1\n"+
		"M3  - 10.1007/11734697_1\n"+
		"AU  - M�rk Jelasity\n"+
		"AU  - Ozalp Babaoglu\n"+
		"ER  -\n";

		System.out.println(new RisToBibtexConverter().RisToBibtex(bsp));

	}


	/**
	 * String array that maps from month number to month string label
	 */
	private final static String[] MONTHS = new String[] { "jan", "feb", "mar", "apr", "may",
		"jun", "jul", "aug", "sep", "oct", "nov", "dec" };

	/** Function is taken from JabRef importer
	 * 
	 * @param Ris
	 * @return The resulting BibTeX string.
	 */
	public String RisToBibtex(String Ris) {
		/**
		 * Parse the entries in the source, and return a List of BibtexEntry
		 * objects.
		 */

		String type = "", author = "", editor = "", startPage = "", endPage = "", comment = "";
		final SortedMap<String,String> bibTexMap = new TreeMap<String,String>();
		
		// split the Strint into different entries
		final String[] fields = Ris.split("\n");

		// go through all entries
		for (int j = 0; j < fields.length; j++) {
			final StringBuffer current = new StringBuffer(fields[j]);
			boolean done = false;
			
			/*
			 * Fieldentries that are longer than one line (e.g. the abstract) are put together
			 * e.g. 
			 * XY  - fooooooooo
			 * oooobar
			 * => XY  - fooooooooooooobar
			 */
			while (!done && (j < fields.length - 1)) {
				if ( fields[j + 1].length() < 2 || !fields[j + 1].substring(2).startsWith("  -") ) {
					/*
					 *  the next line has less than 2 characters (so no new field) or
					 *  does not begin with "xy  -"
					 *  thus, the next line is not a new field
					 */
					if ((current.length() > 0)
							&& !Character.isWhitespace(current.charAt(current.length() - 1))
							&& !Character.isWhitespace(fields[j + 1].charAt(0))) {
							current.append(' ');
					}
					current.append(fields[j + 1].trim());
					j++;
				} else
					done = true;
			}
			String entry = current.toString();
			if (entry.length() < 6)
				continue;
			else {
				String lab = entry.substring(0, 2);
				String val = entry.substring(6).trim();
				if (lab.equals("TY")) {
					if (val.equals("BOOK"))
						type = "book";
					else if (val.equals("JOUR") || val.equals("MGZN"))
						type = "article";
					else if (val.equals("THES"))
						type = "phdthesis";
					else if (val.equals("UNPB"))
						type = "unpublished";
					else if (val.equals("RPRT"))
						type = "techreport";
					else if (val.equals("CONF"))
						type = "inproceedings";
					else if (val.equals("CHAP"))
						type = "incollection";//"inbook";

					else
						type = "other";
				} else if (lab.equals("T1") || lab.equals("TI"))
					bibTexMap.put("title", val);//Title
				// =
				// val;
				else if (lab.equals("T2") || lab.equals("T3")
						|| lab.equals("BT")) {
					bibTexMap.put("booktitle", val);
				} else if (lab.equals("A1") || lab.equals("AU")) {
					if (author.equals("")) // don't add " and " for the first author
						author = val;
					else
						author += " and " + val;
				} else if (lab.equals("A2")) {
					if (editor.equals("")) // don't add " and " for the first editor
						editor = val;
					else
						editor += " and " + val;
				} else if (lab.equals("JA") || lab.equals("JF")
						|| lab.equals("JO")) {
					if (type.equals("inproceedings"))
						bibTexMap.put("booktitle", val);
					else
						bibTexMap.put("journal", val);
				}

				else if (lab.equals("SP"))
					startPage = val;
				else if (lab.equals("PB"))
					bibTexMap.put("publisher", val);
				else if (lab.equals("AD") || lab.equals("CY"))
					bibTexMap.put("address", val);
				else if (lab.equals("EP"))
					endPage = val;
				else if (lab.equals("SN")) {
					String[] _s = val.split(" "); 
					String _isbn = "";
					String _issn = "";
					
					for (int i = 0; i < _s.length; ++i) {
						_s[i] = _s[i].trim();
						if (ISBNUtils.extractISBN(_s[i]) != null) {
							_isbn += ISBNUtils.extractISBN(_s[i]) + " ";
						} else if (ISBNUtils.extractISSN(_s[i]) != null){
							_issn += ISBNUtils.extractISSN(_s[i]) + " ";
						}
					}
					
					if (_isbn.length() > 0)
						bibTexMap.put("isbn", _isbn.trim());
					if (_issn.length() > 0)
						bibTexMap.put("issn", _issn.trim());
				}
				else if (lab.equals("VL"))
					bibTexMap.put("volume", val);
				else if (lab.equals("IS"))
					bibTexMap.put("number", val);
				else if (lab.equals("N2") || lab.equals("AB"))
					bibTexMap.put("abstract", val);
				else if (lab.equals("UR"))
					bibTexMap.put("url", val);
				else if ((lab.equals("Y1") || lab.equals("PY"))
						&& val.length() >= 4) {

					// handle the case of spaces instead of slashes (ie. 2007 Jan)
					String delim = "/";
					if (val.indexOf("/") == -1
							&& val.indexOf(" ") != -1) {
						delim = " ";
					}
					
					String[] parts = val.split(delim);
					bibTexMap.put("year", parts[0]);
					if ((parts.length > 1) && (parts[1].length() > 0)) {
						try {
							int month = Integer.parseInt(parts[1]);
							if ((month > 0) && (month <= 12)) {
								// System.out.println(Globals.MONTHS[month-1]);
								bibTexMap.put("month", "#" + MONTHS[month - 1] + "#");
							}
						} catch (NumberFormatException ex) {
							// The month part is unparseable, so we ignore it.
						}
					}
				}

				else if (lab.equals("KW")) {
					if (!bibTexMap.containsKey("keywords"))
						bibTexMap.put("keywords", val);
					else {
						String kw = bibTexMap.get("keywords");
						bibTexMap.put("keywords", kw + " " + val);
					}
				} else if (lab.equals("U1") || lab.equals("U2")
						|| lab.equals("N1")) {
					if (comment.length() > 0)
						comment = comment + "\n";
					comment = comment + val;
				}
				// Added ID import 2005.12.01, Morten Alver:
				else if (lab.equals("ID"))
					bibTexMap.put("refid", val);
			}
		}
		// fix authors
		//	        if (Author.length() > 0) {
		//	            Author = AuthorList.fixAuthor_lastNameFirst(Author);
		bibTexMap.put("author", author);
		//	        }
		//	        if (Editor.length() > 0) {
		//	            Editor = AuthorList.fixAuthor_lastNameFirst(Editor);
		bibTexMap.put("editor", editor);
		//	        }
		//	        if (comment.length() > 0) {
		bibTexMap.put("comment", comment);
		//	        }

		bibTexMap.put("pages", startPage + "--" + endPage);
		//	        BibtexEntry b = new BibtexEntry(BibtexFields.DEFAULT_BIBTEXENTRY_ID, Globals
		//	                        .getEntryType(Type)); // id assumes an existing database so don't

		// Remove empty fields:
		boolean first=true;
		final StringBuffer bibtexString = new StringBuffer();
		final String bibtexKey = BibTexUtils.generateBibtexKey(bibTexMap.get("author"), bibTexMap.get("editor"), bibTexMap.get("year"), bibTexMap.get("title"));
		bibtexString.append("@").append(type).append("{" + bibtexKey	+ ",\n");
		final Set<String> keySet = bibTexMap.keySet();
		for (final String key: keySet) {
			final String content = bibTexMap.get(key).trim();
			if ((content != null) && (content.trim().length() != 0)) {
				if (first) {
					first=false;
				} else {
					bibtexString.append(",\n");
				}
				bibtexString.append(key).append(" = {").append(content).append(
				"}");
			}
		}
		bibtexString.append("\n}\n");

		return bibtexString.toString();
	}

	/**
	 * returns true if the snippet contains only ris entries, false otherwise
	 * WARNING: this is a heuristic!
	 * @param snippet
	 * @return true if snippet is ris
	 */
	public static boolean canHandle(final String snippet) {
	
		// remove leading whitespace and retrieve first line
		String firstLine = snippet.trim().split("\n", 2)[0];
		// patter: 1 capital letter + 1 other character (non whitespace) + 2 space + "-"
		final Pattern eachLinePattern  = Pattern.compile("^[A-Z]\\S\\s{2}-");
		return firstLine.length()>=5 && eachLinePattern.matcher( firstLine.substring(0, 5)  ).lookingAt();
	}

}
