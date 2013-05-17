package org.bibsonomy.marc.extractors;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class TitleExtractorTest extends AbstractExtractorTest {
	@Test
	public void testTrimming() {
		BibTex b = new BibTex();
		TitleExtractor e = new TitleExtractor();
		e.extraxtAndSetAttribute(b, createExtendedMarcRecord().withMarcField("245", 'a', " Title "));
		Assert.assertEquals("Title", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcRecord().withMarcField("245", 'a', ""));
		Assert.assertEquals("", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcRecord());
		Assert.assertEquals("", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcRecord().withMarcField("245", 'a', " Title").withMarcField("245", 'b', "bla ; blub  "));
		Assert.assertEquals("Title: bla", b.getTitle());
		b = new BibTex();
		e.extraxtAndSetAttribute(b, createExtendedMarcRecord().withMarcField("245", 'a', "Title").withMarcField("245", 'b', "bla ; blub  "));
		Assert.assertEquals("Title: bla", b.getTitle());
	}
}
