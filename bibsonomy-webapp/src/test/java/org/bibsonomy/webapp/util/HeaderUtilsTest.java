package org.bibsonomy.webapp.util;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class HeaderUtilsTest {

	@Test
	public void testGetResponseFormat() {
		
		/*
		 * normal browsers should get no prefix
		 */
		assertEquals("", HeaderUtils.getResponseFormat("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", 1));
		assertEquals("", HeaderUtils.getResponseFormat("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", 2));
		
		/*
		 * bibtex for bookmarks and publications
		 */
		assertEquals("bookbib", HeaderUtils.getResponseFormat("bibtex,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", 1));
		assertEquals("bib", HeaderUtils.getResponseFormat("bibtex,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8", 2));
		
		/*
		 * empty header: HTML
		 */
		assertEquals("", HeaderUtils.getResponseFormat("", 1));
		assertEquals("", HeaderUtils.getResponseFormat(null, 2));
		
		
		assertEquals("swrc", HeaderUtils.getResponseFormat("application/rdf+xml", 2));
		
	}

}
