package org.bibsonomy.scrapingservice.writers;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

public class RDFWriterTest {

	@Test
	public void testWrite() {
		final RDFWriter writer = new RDFWriter(System.out);
		final BibTex bibtex = new BibTex();
		bibtex.setTitle("Reconsidering Physical Key Secrecy: Teledoplication via Optical Decoding");
		bibtex.setAuthor("Benjamin Laxton and Kai Wand and Stefan Savage");
		bibtex.setBibtexAbstract("The access control provided by a physical lock is based ...");
		bibtex.setBibtexKey("laxton2008reconsidering");
		bibtex.setEntrytype("inproceedings");
		bibtex.setYear("2008");
		bibtex.setMonth("October");
		bibtex.setInstitution("ACM");
		bibtex.setBooktitle("CCS'08");
		bibtex.addMiscField("isbn", "978-1-59593-810-7");
		bibtex.addMiscField("doi", "10.781/978-1-59593-810-7");
		bibtex.setAddress("Alexandria, Virginia, USA");
		bibtex.setUrl("http://portal.acm.org/laxton/2008/reconsidering-physical-key-security");
		
		try {
			writer.write(new URL("http://example.com/laxton/2008/reconsidering").toURI(), bibtex);
		} catch (MalformedURLException e) {
			fail(e.getMessage());
		} catch (URISyntaxException e) {
			fail(e.getMessage());
		}
	}

}
