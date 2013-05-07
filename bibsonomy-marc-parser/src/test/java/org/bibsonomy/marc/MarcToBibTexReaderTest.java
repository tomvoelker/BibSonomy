package org.bibsonomy.marc;

import java.util.ArrayList;
import java.util.Collection;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.ImportResource;
import org.bibsonomy.model.util.data.ClasspathResourceData;
import org.bibsonomy.model.util.data.Data;
import org.bibsonomy.model.util.data.DualDataWrapper;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class MarcToBibTexReaderTest {
	
	private static final String[] RESOURCES = {"HEB01711621X.marc:HEB01711621X.pica","HEB066747139.marc:HEB066747139.pica","HEB090081897.marc:HEB090081897.pica","HEB096323825.marc:HEB096323825.pica","HEB105279358.marc:HEB105279358.pica","HEB107022060.marc:HEB107022060.pica","HEB107111365.marc:HEB107111365.pica","HEB108585530.marc:HEB108585530.pica","HEB113338945.marc:HEB113338945.pica","HEB120722186.marc:HEB120722186.pica","HEB172851289.marc:HEB172851289.pica","HEB174347847.marc:HEB174347847.pica","HEB174591853.marc:HEB174591853.pica","HEB174708645.marc:HEB174708645.pica","HEB174777434.marc:HEB174777434.pica","HEB178552666.marc:HEB178552666.pica","HEB180245627.marc:HEB180245627.pica","HEB183850076.marc:HEB183850076.pica","HEB186996160.marc:HEB186996160.pica","HEB281109346.marc:HEB281109346.pica","HEBr80267612X.marc:HEBr80267612X.pica","HEBr804030006.marc:HEBr804030006.pica","HEBr805893423.marc:HEBr805893423.pica","HEBr805893431.marc:HEBr805893431.pica","HEBr805893474.marc:HEBr805893474.pica","HEBr806588497.marc:HEBr806588497.pica","HEBr808275577.marc:HEBr808275577.pica","HEBr808372440.marc:HEBr808372440.pica","HEBr808372599.marc:HEBr808372599.pica","HEBr808372912.marc:HEBr808372912.pica","HEBr808373153.marc:HEBr808373153.pica","HEBr808685287.marc:HEBr808685287.pica","HEBr810588382.marc:HEBr810588382.pica","HEBr810802708.marc:HEBr810802708.pica","HEBr811003698.marc:HEBr811003698.pica","HEBr811003701.marc:HEBr811003701.pica","HEBr811658295.marc:HEBr811658295.pica","HEBr811658309.marc:HEBr811658309.pica","HEBr811943364.marc:HEBr811943364.pica","HEBr811943496.marc:HEBr811943496.pica","HEBr812345436.marc:HEBr812345436.pica","HEBr814229344.marc:HEBr814229344.pica","HEBr814230490.marc:HEBr814230490.pica","HEBr814230547.marc:HEBr814230547.pica","HEBr814230555.marc:HEBr814230555.pica","HEBr814230563.marc:HEBr814230563.pica","HEBr814231063.marc:HEBr814231063.pica","HEBr814256422.marc:HEBr814256422.pica","HEBr814256430.marc:HEBr814256430.pica","HEBr817570578.marc:HEBr817570578.pica","HEBr823762718.marc:HEBr823762718.pica","HEBr823764710.marc:HEBr823764710.pica","HEBr823764729.marc:HEBr823764729.pica","HEBr823764737.marc:HEBr823764737.pica","HEBr823764745.marc:HEBr823764745.pica","HEBr825598656.marc:HEBr825598656.pica","HEBr826413951.marc:HEBr826413951.pica","HEBr828420041.marc:HEBr828420041.pica","HEBr830003894.marc:HEBr830003894.pica","HEBr834327821.marc:HEBr834327821.pica","HEBr836885619.marc:HEBr836885619.pica","HEBr838005276.marc:HEBr838005276.pica","HEBr839571917.marc:HEBr839571917.pica","HEBr840566956.marc:HEBr840566956.pica","HEBr840753837.marc:HEBr840753837.pica","HEBr841718229.marc:HEBr841718229.pica","HEBr842456864.marc:HEBr842456864.pica","HEBr844470414.marc:HEBr844470414.pica","HEBr845341154.marc:HEBr845341154.pica","HEBr845757695.marc:HEBr845757695.pica","HEBr846364298.marc:HEBr846364298.pica","HEBr846940655.marc:HEBr846940655.pica","HEBr847074102.marc:HEBr847074102.pica","HEBr847074110.marc:HEBr847074110.pica","HEBr847074129.marc:HEBr847074129.pica","HEBr847084310.marc:HEBr847084310.pica","HEBr851786286.marc:HEBr851786286.pica","HEBr854161503.marc:HEBr854161503.pica","HEBr859433641.marc:HEBr859433641.pica","HEBr85959968X.marc:HEBr85959968X.pica"};
	
	private static final int RESOURCES_TO_TEST = 40;
	
	@Test
	public void testSomething() {
		MarcToBibTexReader reader = new MarcToBibTexReader();
		Collection<ImportResource> bibs = reader.read(new ClasspathResourceData("/hebis_data/HEB01711621X.marc", "application/marc"));
		Collection<ImportResource> springerBibs = reader.read(new ClasspathResourceData("/marc_files/part29.dat", "application/marc"));
		
		printStuff(bibs);
	}

	public void printStuff(Collection<ImportResource> bibs) {
		for (BibTex b : bibs) {
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
	
	@Test
	public void testHebisMarcPlusPica() {
		MarcToBibTexReader reader = new MarcToBibTexReader();
		Collection<ImportResource> bibs = new ArrayList<ImportResource>();
		Data dat;
		for(int i = 0; i < RESOURCES_TO_TEST; i++) {
			dat = new DualDataWrapper(new ClasspathResourceData("/hebis_data/"+RESOURCES[i].split(":")[0], "application/marc"), new ClasspathResourceData("/hebis_data/"+RESOURCES[i].split(":")[1], "application/pica"));
			bibs.addAll(reader.read(dat));
		}
		printStuff(bibs);
	}
}
