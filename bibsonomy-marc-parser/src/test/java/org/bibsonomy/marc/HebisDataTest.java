package org.bibsonomy.marc;

import java.util.Arrays;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class HebisDataTest extends AbstractDataDownloadingTestCase {
	@Test
	public void testHEB291478336() {
		BibTex bib = get("HEB291478336");
		Assert.assertEquals(Arrays.asList(new PersonName("Gene", "Smith")), bib.getAuthor());
	}
}
