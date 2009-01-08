package org.bibsonomy.scraper.util;


/**
 * @author rja
 * @version $Id$
 */
public class BibTeXUtils {

	/** Adds the field <code>fieldName</code> to the BibTeX entry, if the entry 
	 * does not already contain it.
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * 
	 * @return The new BibTeX entry.
	 */
	public static String addFieldIfNotContained(final String bibtex, final String fieldName, final String fieldValue) {
		if (bibtex == null) return bibtex;
		
		final StringBuffer buf = new StringBuffer(bibtex);
		addFieldIfNotContained(buf, fieldName, fieldValue);
		return buf.toString();
	}
	
	/** Adds the field <code>fieldName</code> to the BibTeX entry, if the entry 
	 * does not already contain it.
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 * 
	 */
	public static void addFieldIfNotContained(final StringBuffer bibtex, final String fieldName, final String fieldValue) {
		if (bibtex == null) return;
		/*
		 * it seems, we can do regex stuff only on strings ... so we have 
		 * to convert the buffer into a string :-(
		 */
		final String bibtexString = bibtex.toString();
		/*
		 * The only way safe to find out if the entry already contains
		 * the field is to parse it. This is expensive, thus we only 
		 * do simple heuristics, which is of course, error prone! 
		 */
		if (!bibtexString.matches("(?s).*" + fieldName + "\\s*=\\s*.*")) {
			/*
			 * add the field at the end before the last brace
			 */
			addField(bibtex, fieldName, fieldValue);
		}
	}

	/** Adds the given field at the end of the given BibTeX entry by placing
	 * it before the last brace. 
	 * 
	 * @param bibtex - the BibTeX entry
	 * @param fieldName - the name of the field
	 * @param fieldValue - the value of the field
	 */
	public static void addField(final StringBuffer bibtex, final String fieldName, final String fieldValue) {
		if (bibtex == null) return;
		final int lastIndexOf = bibtex.lastIndexOf("}");
		bibtex.replace(lastIndexOf, bibtex.length(), "," + fieldName + " = {" + fieldValue + "}\n}");
	}
	
	
}
