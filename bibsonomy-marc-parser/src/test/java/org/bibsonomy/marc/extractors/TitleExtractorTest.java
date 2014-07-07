package org.bibsonomy.marc.extractors;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author jensi
 */
public class TitleExtractorTest extends AbstractExtractorTest {
	
	@Test
	public void testTrimming() {
		BibTex b = new BibTex();
		TitleExtractor e = new TitleExtractor();
		e.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("245", 'a', " Title "));
		assertEquals("Title", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("245", 'a', ""));
		assertEquals("", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord());
		assertEquals("", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("245", 'a', " Title").withMarcField("245", 'b', "bla ; blub  "));
		assertEquals("Title : bla", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("245", 'a', "Title").withMarcField("245", 'b', "bla ; blub  "));
		assertEquals("Title : bla", b.getTitle());
	}
}
