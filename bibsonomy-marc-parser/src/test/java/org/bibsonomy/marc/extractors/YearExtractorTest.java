package org.bibsonomy.marc.extractors;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author Lukas
 * @version $Id$
 */
public class YearExtractorTest extends AbstractExtractorTest {

	@Test
	public void testYearExtraction() {
		BibTex b = new BibTex();
		YearExtractor yearExtractor = new YearExtractor();
		yearExtractor.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("260", 'c', "1996"));
		Assert.assertEquals("1996", b.getYear());
		
		//test with noise
		b = new BibTex();
		yearExtractor.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("260", 'c', "sdgv1992cc"));
		Assert.assertEquals("1992", b.getYear());
	}
	
}
