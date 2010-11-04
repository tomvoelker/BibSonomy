package org.bibsonomy.sword;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.sword.MetsGenerator;
import org.junit.Test;

/**
 * 
 * @author:  rja
 * @version: $Id$
 * $Author$
 * 
 */
public class MetsGeneratorTest {

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
	
	

	private static BufferedReader getReader(final String s) {
		return new BufferedReader(new StringReader(s));
	}


	
	@Test
	public void testRedirectResolution() throws Exception {
		/*
		 * check if the resolved URL from a bit.ly link is returned 
		 */
		
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
