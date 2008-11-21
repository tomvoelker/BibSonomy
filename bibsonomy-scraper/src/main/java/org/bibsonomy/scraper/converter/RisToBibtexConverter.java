/**
 * 
 */
package org.bibsonomy.scraper.converter;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

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

	/** Function is taken from JabRef importer
	 * 
	 * @param Ris
	 * @return The resulting BibTeX string.
	 */
	public String RisToBibtex(String Ris) {
		// String array that maps from month number to month string label:
		String[] MONTHS = new String[] { "jan", "feb", "mar", "apr", "may",
				"jun", "jul", "aug", "sep", "oct", "nov", "dec" };

		/**
		 * Parse the entries in the source, and return a List of BibtexEntry
		 * objects.
		 */

		String Type = "", Author = "", Editor = "", StartPage = "", EndPage = "", comment = "";
		SortedMap<String,String> hm = new TreeMap<String,String>();

		String[] fields = Ris.split("\n");

		for (int j = 0; j < fields.length; j++) {
			StringBuffer current = new StringBuffer(fields[j]);
			boolean done = false;
			/*
			 * what is done here?
			 */
			while (!done && (j < fields.length - 1)) {
				/*
				 * what does this "6" mean?
				 */
				if ((fields[j + 1].length() >= 6)
						&& !fields[j + 1].substring(2, 6).equals("  - ")) {
					if ((current.length() > 0)
							&& !Character.isWhitespace(current.charAt(current.length() - 1))
							&& !Character.isWhitespace(fields[j + 1].charAt(0)))
						current.append(' ');
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
						Type = "book";
					else if (val.equals("JOUR") || val.equals("MGZN"))
						Type = "article";
					else if (val.equals("THES"))
						Type = "phdthesis";
					else if (val.equals("UNPB"))
						Type = "unpublished";
					else if (val.equals("RPRT"))
						Type = "techreport";
					else if (val.equals("CONF"))
						Type = "inproceedings";
					else if (val.equals("CHAP"))
						Type = "incollection";//"inbook";

					else
						Type = "other";
				} else if (lab.equals("T1") || lab.equals("TI"))
					hm.put("title", val);//Title
				// =
				// val;
				else if (lab.equals("T2") || lab.equals("T3")
						|| lab.equals("BT")) {
					hm.put("booktitle", val);
				} else if (lab.equals("A1") || lab.equals("AU")) {
					if (Author.equals("")) // don't add " and " for the first author
						Author = val;
					else
						Author += " and " + val;
				} else if (lab.equals("A2")) {
					if (Editor.equals("")) // don't add " and " for the first editor
						Editor = val;
					else
						Editor += " and " + val;
				} else if (lab.equals("JA") || lab.equals("JF")
						|| lab.equals("JO")) {
					if (Type.equals("inproceedings"))
						hm.put("booktitle", val);
					else
						hm.put("journal", val);
				}

				else if (lab.equals("SP"))
					StartPage = val;
				else if (lab.equals("PB"))
					hm.put("publisher", val);
				else if (lab.equals("AD") || lab.equals("CY"))
					hm.put("address", val);
				else if (lab.equals("EP"))
					EndPage = val;
				else if (lab.equals("SN"))
					hm.put("issn", val);
				else if (lab.equals("VL"))
					hm.put("volume", val);
				else if (lab.equals("IS"))
					hm.put("number", val);
				else if (lab.equals("N2") || lab.equals("AB"))
					hm.put("abstract", val);
				else if (lab.equals("UR"))
					hm.put("url", val);
				else if ((lab.equals("Y1") || lab.equals("PY"))
						&& val.length() >= 4) {
					String[] parts = val.split("/");
					hm.put("year", parts[0]);
					if ((parts.length > 1) && (parts[1].length() > 0)) {
						try {
							int month = Integer.parseInt(parts[1]);
							if ((month > 0) && (month <= 12)) {
								//System.out.println(Globals.MONTHS[month-1]);
								hm.put("month", "#" + MONTHS[month - 1] + "#");
							}
						} catch (NumberFormatException ex) {
							// The month part is unparseable, so we ignore it.
						}
					}
				}

				else if (lab.equals("KW")) {
					if (!hm.containsKey("keywords"))
						hm.put("keywords", val);
					else {
						String kw = hm.get("keywords");
						hm.put("keywords", kw + " " + val);
					}
				} else if (lab.equals("U1") || lab.equals("U2")
						|| lab.equals("N1")) {
					if (comment.length() > 0)
						comment = comment + "\n";
					comment = comment + val;
				}
				// Added ID import 2005.12.01, Morten Alver:
				else if (lab.equals("ID"))
					hm.put("refid", val);
			}
		}
		// fix authors
		//	        if (Author.length() > 0) {
		//	            Author = AuthorList.fixAuthor_lastNameFirst(Author);
		hm.put("author", Author);
		//	        }
		//	        if (Editor.length() > 0) {
		//	            Editor = AuthorList.fixAuthor_lastNameFirst(Editor);
		hm.put("editor", Editor);
		//	        }
		//	        if (comment.length() > 0) {
		hm.put("comment", comment);
		//	        }

		hm.put("pages", StartPage + "--" + EndPage);
		//	        BibtexEntry b = new BibtexEntry(BibtexFields.DEFAULT_BIBTEXENTRY_ID, Globals
		//	                        .getEntryType(Type)); // id assumes an existing database so don't

		// Remove empty fields:
		boolean first=true;
		StringBuffer bibtexString = new StringBuffer();
		bibtexString.append("@").append(Type).append("{keyhere,\n");
		final Set<String> keySet = hm.keySet();
		for (final String key: keySet) {
			final String content = hm.get(key);
			if ((content != null) && (content.trim().length() != 0)) {
				if (first) {
					first=false;
				} else {
					bibtexString.append(",\n");
				}
				bibtexString.append(key).append("={").append(content).append(
						"}");
			}
		}
		bibtexString.append("\n}\n");

		return bibtexString.toString();
	}

}
