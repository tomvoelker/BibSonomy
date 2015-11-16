/**
 * BibSonomy-Scraper - Web page scrapers returning BibTeX for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * 
 */
package org.bibsonomy.scraper.converter;

import static org.bibsonomy.util.ValidationUtils.present;

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
	 * String array that maps from month number to month string label
	 */
	private final static String[] MONTHS = new String[] { "jan", "feb", "mar", "apr", "may",
		"jun", "jul", "aug", "sep", "oct", "nov", "dec" };

	/** Function is taken from JabRef importer
	 * 
	 * @param Ris
	 * @return The resulting BibTeX string.
	 */
	public String risToBibtex(String Ris) {
		/**
		 * Parse the entries in the source, and return a List of BibtexEntry
		 * objects.
		 */

		String type = "", author = "", editor = "", startPage = "", endPage = "", comment = "";
		final SortedMap<String,String> bibtexMap = new TreeMap<String,String>();

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
							&& fields[j + 1].length() > 0
							&& !Character.isWhitespace(fields[j + 1].charAt(0))) {
						current.append(' ');
					}
					current.append(fields[j + 1].trim());
					j++;
				} else
					done = true;
			}
			final String entry = current.toString();
			if (entry.length() < 6)
				continue;
			else {
				final String key = entry.substring(0, 2);
				String value = entry.substring(6).trim();
				if (key.equals("TY")) {
					if (value.equals("BOOK"))
						type = BibTexUtils.BOOK;
					else if (value.equals("JOUR") || value.equals("MGZN"))
						type = BibTexUtils.ARTICLE;
					else if (value.equals("THES"))
						type = BibTexUtils.PHD_THESIS;
					else if (value.equals("UNPB"))
						type = BibTexUtils.UNPUBLISHED;
					else if (value.equals("RPRT"))
						type = BibTexUtils.TECH_REPORT;
					else if (value.equals("CONF"))
						type = BibTexUtils.INPROCEEDINGS;
					else if (value.equals("CTLG"))
						type = BibTexUtils.BOOKLET;
					else if (value.equals("CPAPER"))
						type = BibTexUtils.CONFERENCE;
					else if (value.equals("EJOUR") || value.equals("BLOG") || value.equals("ELEC"))
						type = BibTexUtils.ELECTRONIC;
					else if (value.equals("CHAP"))
						type = BibTexUtils.INBOOK;
					//					else if (value.equals("XXXX"))
					//						type = "manual";
					//					else if (value.equals("THESIS"))
					//						type = "mastersthesis";
					else if (value.equals("PAT"))
						type = BibTexUtils.PATENT;
					else if (value.equals("SER") || value.equals("MGZN"))
						type = BibTexUtils.PERIODICAL;
					else if (value.equals("SLIDE"))
						type = BibTexUtils.PRESENTATION;
					//					else if (value.equals("CONF"))
					//						type = "proceedings";
					else if (value.equals("STAND"))
						type = BibTexUtils.STANDARD;
					else
						type = BibTexUtils.MISC;
				} else if (key.equals("T1") || key.equals("TI")) {
					if (value.endsWith(",") || value.endsWith(".")) {
						value = value.substring(0, value.length() - 1);
					}
					bibtexMap.put("title", value); 
				} else if (key.equals("T2") || key.equals("T3") || key.equals("BT")) {
					bibtexMap.put("booktitle", value);
				} else if (key.equals("A1") || key.equals("AU")) {
					// take care of trailing ","
					if (value.endsWith(",")) {
						value = value.substring(0, value.length() - 1);
					}
					// remove trailing ", Jr." (wrong place for BibTeX)
					if (value.endsWith(", Jr.")) {
						value = value.substring(0, value.length() - ", Jr.".length());
					}
					// take care of entries like 
					// A1  - Braams, Johannes.
					if (value.endsWith(".") && value.lastIndexOf(" ") < value.length() - 3) {
						value = value.substring(0, value.length() - 1);
					}
					if (author.equals("")) // don't add " and " for the first author
						author = value;
					else
						author += " and " + value;
				} else if (key.equals("A2")) {
					if (editor.equals("")) // don't add " and " for the first editor
						editor = value;
					else
						editor += " and " + value;
				} else if (key.equals("JA") || key.equals("JF")	|| key.equals("JO")) {
					if ("inproceedings".equals(type))
						bibtexMap.put("booktitle", value);
					else {
						/*
						 * Since we don't want JA (abbreviated journal) to 
						 * overwrite JO (long journal), we check for JA, if a
						 * journal entry already exists.
						 */
						if (!key.equals("JA") || !bibtexMap.containsKey("journal"))
							bibtexMap.put("journal", value);
					}
				}
				else if (key.equals("DO")) 
					bibtexMap.put("doi", value);
				else if (key.equals("SP"))
					startPage = value;
				else if ("PB".equals(key)) {
					/*
					 * Special handling for techreports: map the publisher to the
					 * institution field (as discussed in bibsonomy-discuss).
					 */
					if ("techreport".equals(type)) {
						bibtexMap.put("institution", value);
					} else {
						bibtexMap.put("publisher", value);
					}
				} else if (key.equals("AD") || key.equals("CY"))
					bibtexMap.put("address", value);
				else if (key.equals("EP"))
					endPage = value;
				else if (key.equals("SN")) {
					String[] _s = value.split(" "); 
					String _isbn = "";
					String _issn = "";

					for (int i = 0; i < _s.length; ++i) {
						_s[i] = _s[i].trim();
						String extractedISBN = ISBNUtils.extractISBN(_s[i]);
						if (present(extractedISBN)) {
							_isbn += extractedISBN + " ";
						} else if (ISBNUtils.extractISSN(_s[i]) != null){
							_issn += ISBNUtils.extractISSN(_s[i]) + " ";
						}
					}

					if (_isbn.length() > 0)
						bibtexMap.put("isbn", _isbn.trim());
					if (_issn.length() > 0)
						bibtexMap.put("issn", _issn.trim());
				}
				else if (key.equals("VL"))
					bibtexMap.put("volume", value);
				else if (key.equals("IS"))
					bibtexMap.put("number", value);
				else if (key.equals("N2") || key.equals("AB"))
					bibtexMap.put("abstract", value);
				else if (key.equals("UR"))
					bibtexMap.put("url", value);
				else if (key.equals("AD"))
					bibtexMap.put("address", value);
				else if ((key.equals("Y1") || key.equals("PY"))
						&& value.length() >= 4) {

					// handle the case of spaces instead of slashes (ie. 2007 Jan)
					String delim = "/";
					if (value.indexOf("/") == -1
							&& value.indexOf(" ") != -1) {
						delim = " ";
					}

					String[] parts = value.split(delim);
					bibtexMap.put("year", parts[0]);
					if ((parts.length > 1) && (parts[1].length() > 0)) {
						try {
							int month = Integer.parseInt(parts[1]);
							if ((month > 0) && (month <= 12)) {
								// System.out.println(Globals.MONTHS[month-1]);
								bibtexMap.put("month", MONTHS[month - 1]);
							}
						} catch (NumberFormatException ex) {
							// The month part is unparseable, so we ignore it.
						}
					}
				}

				else if (key.equals("KW")) {
					if (!bibtexMap.containsKey("keywords"))
						bibtexMap.put("keywords", value);
					else {
						String kw = bibtexMap.get("keywords");
						bibtexMap.put("keywords", kw + " " + value);
					}
				} else if (key.equals("U1") || key.equals("U2")
						|| key.equals("N1")) {
					if (comment.length() > 0)
						comment = comment + "\n";
					comment = comment + value;
				}
				// Added ID import 2005.12.01, Morten Alver:
				else if (key.equals("ID"))
					bibtexMap.put("refid", value);
			}
		}
		// fix authors
		//	        if (Author.length() > 0) {
		//	            Author = AuthorList.fixAuthor_lastNameFirst(Author);
		bibtexMap.put("author", author);
		//	        }
		//	        if (Editor.length() > 0) {
		//	            Editor = AuthorList.fixAuthor_lastNameFirst(Editor);
		bibtexMap.put("editor", editor);
		//	        }
		//	        if (comment.length() > 0) {
		bibtexMap.put("comment", comment);
		//	        }

		if (present(startPage)) bibtexMap.put("pages", startPage + "--" + endPage);
		//	        BibtexEntry b = new BibtexEntry(BibtexFields.DEFAULT_BIBTEXENTRY_ID, Globals
		//	                        .getEntryType(Type)); // id assumes an existing database so don't

		// Remove empty fields:
		boolean first=true;
		final StringBuffer bibtexString = new StringBuffer();
		final String bibtexKey = BibTexUtils.generateBibtexKey(bibtexMap.get("author"), bibtexMap.get("editor"), bibtexMap.get("year"), bibtexMap.get("title"));
		bibtexString.append("@").append(type).append("{" + bibtexKey	+ ",\n");
		final Set<String> keySet = bibtexMap.keySet();
		for (final String key: keySet) {
			final String content = bibtexMap.get(key).trim();
			if (present(content)) {
				if (first) {
					first = false;
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
		final String firstLine = snippet.trim().split("\n", 2)[0];
		// patter: 1 capital letter + 1 other character (non whitespace) + 2 space + "-"
		final Pattern eachLinePattern  = Pattern.compile("^[A-Z]\\S\\s{2}-");
		return firstLine.length()>=5 && eachLinePattern.matcher( firstLine.substring(0, 5)  ).lookingAt();
	}

}
