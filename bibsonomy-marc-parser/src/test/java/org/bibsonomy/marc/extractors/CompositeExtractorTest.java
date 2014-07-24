package org.bibsonomy.marc.extractors;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author Lukas
 */
public class CompositeExtractorTest extends AbstractExtractorTest {

	@Test
	public void testYearNoYearExtraction() {
		BibTex b = new BibTex();
		CompositeAttributeExtractor ex = new CompositeAttributeExtractor();
		ex.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("260", 'c', "1996"));
		assertEquals("1996", b.getYear());
		
		b = new BibTex();
		ex.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord());
		assertEquals("noyear", b.getYear());
	}
	
}
