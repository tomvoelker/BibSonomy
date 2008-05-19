package org.bibsonomy.util;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class BibTeXHashCalculator {

	public static void main(String[] args) {
		final BibTeXHashCalculator calc = new BibTeXHashCalculator();
	
		/*
		 * data neccessary for the interhash
		 */
		final String title  = "Tag Recommendations in Folksonomies";
		final String author = "Robert Jäschke and Leandro Balby Marinho and Andreas Hotho and Lars Schmidt-Thieme and Gerd Stumme";
		final String editor = "Joost N. Kok and Jacek Koronacki and Ramon López de Mántaras and Stan Matwin and Dunja Mladenic and Andrzej Skowron";
		final String year   = "2007";
		
		final String interHash = calc.getInterHash(title, author, editor, year);
		System.out.println("interhash: " + interHash);
		System.out.println("  ->  URL: http://www.bibsonomy.org/bibtex/" + HashID.INTER_HASH.getId() + interHash);

		System.out.println();
		
		/*
		 * data neccessary for the intrahash
		 */
		final String entrytype = "inproceedings";
		final String journal   = null;
		final String booktitle = "Knowledge Discovery in Databases: PKDD 2007, 11th European Conference on Principles and Practice of Knowledge Discovery in Databases";
		final String volume    = "4702";
		final String number    = null;
		
		final String intraHash = calc.getIntraHash(title, author, editor, year, entrytype, journal, booktitle, volume, number);
		System.out.println("intrahash: " + intraHash);
		System.out.println("  ->  URL: http://www.bibsonomy.org/bibtex/" + HashID.INTRA_HASH.getId() + intraHash);
				
	}
	
	public String getInterHash(final String title, final String author, final String editor, final String year) {
		final BibTex bibtex = new BibTex();
		bibtex.setAuthor(author);
		bibtex.setEditor(editor);
		bibtex.setTitle(title);
		bibtex.setYear(year);
		bibtex.recalculateHashes();
		return bibtex.getInterHash();
		
	}

	public String getIntraHash(final String title, final String author, final String editor, final String year, final String entrytype, final String journal, final String booktitle, final String volume, final String number) {
		final BibTex bibtex = new BibTex();
		bibtex.setAuthor(author);
		bibtex.setEditor(editor);
		bibtex.setTitle(title);
		bibtex.setYear(year);
		bibtex.setEntrytype(entrytype);
		bibtex.setJournal(journal);
		bibtex.setBooktitle(booktitle);
		bibtex.setVolume(volume);
		bibtex.setNumber(number);
		bibtex.recalculateHashes();
		return bibtex.getIntraHash();
	}

}

