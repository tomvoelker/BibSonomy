package org.bibsonomy.sword;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.sword.UrlProvider;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class UrlProviderTest {

	final String[] html = new String[] {
			"<html>\n  <head>\n    <title>Foo</title>\n  </head>\n</html>\n",
			"<html>\n  <head>\n    <title>Foo\nBar</title>\n  </head>\n</html>\n",
			"<html>\n  <head>\n    <title>Foo\nBar\n    Blubb</title>\n  </head>\n</html>\n",
			"<html>\n  <head>\n  </head>\n</html>\n"

	};

	final String[] title = new String[] {
			"Foo",
			"Foo Bar",
			"Foo Bar Blubb",
			""
	};


	final String[] urls = new String[] {
			"http://www.bibsonomy.org/",
			"http://www.kde.cs.uni-kassel.de/",
			"http://is.gd/bRqBF",
			"http://fotos.oern.de/v/brugge/"
	};
	final String[] onlineUrlTitle = new String[] {
			"BibSonomy :: home",
			"Fachgebiet Wissensverarbeitung",
			"Twitter Papers at the WWW 2010 Conference - marcua's blog",
			"Brügge"
	};
	final String[] offlineUrlTitle = new String[] {
			"www.bibsonomy.org",
			"www.kde.cs.uni-kassel.de",
			"is.gd",
			"fotos.oern.de"
	};
	
	
	@Test
	public void testExtractTitle() throws IOException {
		final UrlProvider up = new UrlProvider();
		for (int i = 0; i < html.length; i++) {
			assertEquals(title[i], up.extractTitle(getReader(html[i])));
		}
	}


	private static BufferedReader getReader(final String s) {
		return new BufferedReader(new StringReader(s));
	}


	@Test
	public void testResolveUrl() throws Exception {
		final UrlProvider up = new UrlProvider();
		final boolean isOnlineTest = isOnlineTest();
		
		for (int i = 0; i < urls.length; i++) {
			final String url = urls[i]; 
			final Bookmark bookmark = up.resolveUrl(url);
			if (isOnlineTest) {
				assertEquals(onlineUrlTitle[i], bookmark.getTitle());
			} else {
				assertEquals(offlineUrlTitle[i], bookmark.getTitle());
			}
		}
	}
	
	@Test
	public void testRedirectResolution() throws Exception {
		/*
		 * check if the resolved URL from a bit.ly link is returned 
		 */
		
	}

	@Test
	public void testGetEmptyTitle() throws Exception {
		 final UrlProvider up = new UrlProvider();
		 assertEquals("www.bibsonomy.org", up.getEmptyTitle("http://www.bibsonomy.org/user/jaeschke"));
	}
	
	/**
	 * Checks, if we have network access.
	 * 
	 * @return
	 */
	private boolean isOnlineTest() {
		try {
			return new HttpClient().executeMethod(new GetMethod("http://www.uni-kassel.de/")) == 200;
		} catch (final Exception e) {
			return false;
		}
	}
}
