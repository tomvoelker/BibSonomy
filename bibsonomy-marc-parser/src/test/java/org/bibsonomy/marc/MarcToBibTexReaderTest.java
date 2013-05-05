package org.bibsonomy.marc;

import java.util.Collection;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReaderTest {
	@Test
	public void testSomething() {
		MarcToBibTexReader reader = new MarcToBibTexReader();

		Collection<BibTex> bibs = reader.read(getClass().getClassLoader().getResourceAsStream("marc_files/part29.dat"));
		Collection<BibTex> springerBibs = reader.read(getClass().getClassLoader().getResourceAsStream("marc_files/c2013.mrc"));
		
		for (BibTex b : springerBibs) {
			System.out.println("############## new bibtex ######################");
			System.out.println("BibtexKey:\t" 	+ b.getBibtexKey());
			System.out.println("Misc:\t\t" 		+ b.getMisc());
			System.out.println("Abstract:\t" 	+ b.getAbstract());
			System.out.println("Entrytype:\t" 	+ b.getEntrytype());
			System.out.println("Address:\t" 	+ b.getAddress());
			System.out.println("Annote:\t\t" 	+ b.getAnnote());
			System.out.println("Author:\t\t" 	+ b.getAuthor());
			System.out.println("Title:\t\t" 	+ b.getTitle());
			System.out.println("Booktitle:\t" 	+ b.getBooktitle());
			System.out.println("Chapter:\t" 	+ b.getChapter());
			System.out.println("Crossref:\t" 	+ b.getCrossref());
			System.out.println("Edition:\t" 	+ b.getEdition());
			System.out.println("Editors:\t" 	+ b.getEditor());
			System.out.println("Howpublished:\t" + b.getHowpublished());
			System.out.println("Institution:\t" + b.getInstitution());
			System.out.println("Organization:\t" + b.getOrganization());
			System.out.println("Journal:\t" 	+ b.getJournal());
			System.out.println("Note:\t\t" 		+ b.getNote());
			System.out.println("Number:\t\t" 	+ b.getNumber());
			System.out.println("Pages:\t\t" 	+ b.getPages());
			System.out.println("Publisher:\t" 	+ b.getPublisher());
			System.out.println("School:\t\t" 	+ b.getSchool());
			System.out.println("Series:\t\t" 	+ b.getSeries());
			System.out.println("Volume:\t\t" 	+ b.getVolume());
			System.out.println("Day:\t\t" 		+ b.getDay());
			System.out.println("Month:\t\t" 	+ b.getMonth());
			System.out.println("Year:\t\t" 		+ b.getYear());
			System.out.println("Type:\t\t" 		+ b.getType());
			System.out.println("ScraperId:\t" 	+ b.getScraperId());
			System.out.println("URL:\t\t" 		+ b.getUrl());
			System.out.println("PrivNote:\t" 	+ b.getPrivnote());
			System.out.println("OpenUrl:\t" 	+ b.getOpenURL() + "\n");
						
//			System.out.println(b);
		}
	}
}
