package org.bibsonomy.scraper.id.kde.doi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Assert;

import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;
import org.bibsonomy.util.WebUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test for DOIScraper class (no url test)
 * @author tst
 * @version $Id$
 */
public class DOIScraperTest {
	
	/**
	 * test getting URL
	 *  
	 * Im Browser komme ich bei Aufruf von
	 * 
	 * http://dx.doi.org/10.1007/11922162
	 * 
	 * auf 
	 * 
	 * http://www.springerlink.com/content/w425794t7433/
	 * 
	 * raus. Aber {@link WebUtils#getRedirectUrl(URL)} kommt auf 
	 * 
	 * http://www.springerlink.com/link.asp?id=w425794t7433
	 * 
	 * raus. Auch wenn ich 
	 * 
	 * http://www.springerlink.com/index/10.1007/11922162
	 * 
	 * (das ist die alte URL aus dem Test hier) eingebe, komme ich auf 
	 * der ersten URL raus. D.h., irgendwie scheint Springer da je nach
	 * Cookie-Handling, Referer, oder nach Browser woanders hinzuleiten. :-(
	 * 
	 * tst: dem SpringerLinkScraper einfach die Unterstützung für URLs wie http://www.springerlink.com/link.asp?id=w425794t7433 hinzugefügt
	 */
	@Test
	@Ignore
	public void getUrlForDoiTest(){
		try {
			Assert.assertEquals("http://www.springerlink.com/index/10.1007/11922162", DOIScraper.getUrlForDoi("10.1007/11922162").toString());
		} catch (IOException ex) {
			ex.printStackTrace();
			Assert.fail();
		}
	}
	
	
	@Test
	@Ignore
	public void testScraper() throws ScrapingException, MalformedURLException {
		final ScrapingContext sc = new ScrapingContext(new URL("http://dx.doi.org/10.1007/11922162"));
		final DOIScraper scraper = new DOIScraper();
		
		Assert.assertFalse(scraper.scrape(sc));
		
		Assert.assertEquals("http://www.springerlink.com/index/10.1007/11922162", sc.getUrl().toString());
	}

}
