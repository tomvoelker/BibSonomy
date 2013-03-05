package org.bibsonomy.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author jensi
 * @version $Id$
 */
public class UrlParameterExtractorTest {

	/**
	 * Tests parseParameterValueFromUrl
	 */
	@Test
	public void testParseParameterValueFromUrl() {
		UrlParameterExtractor serviceObj = new UrlParameterExtractor("hurz");
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hurz=a+b"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hahaha=hihihi&hurz=a+b"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org?hahaha=hihihi&hurz=a+b&hohoho=lalala"));
		Assert.assertEquals("a b", serviceObj.parseParameterValueFromUrl("http://www.biblicious.org/hurz=bla?hurz=a+b&hohoho=lalala"));
	}

}
