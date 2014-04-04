package org.bibsonomy.rest.util;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author dzo
 */
public class URLDecodingStringTokenizerTest {
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()}
	 */
	@Test
	public void whiteSpace() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("/test/test%20123.txt", "/");
		assertEquals("test", tokenizer.nextToken());
		assertEquals("test 123.txt", tokenizer.nextToken());
	}
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()} with an url
	 */
	@Test
	public void uri() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("/test/http%3A%2F%2Fwww.bibsonomy.org%2F", "/");
		assertEquals("test", tokenizer.nextToken());
		assertEquals("http://www.bibsonomy.org/", tokenizer.nextToken());
	}
	
	/**
	 * tests {@link URLDecodingStringTokenizer#nextToken()} with + in the url
	 */
	@Test
	@Ignore // @see #1934
	public void plus() {
		final URLDecodingStringTokenizer tokenizer = new URLDecodingStringTokenizer("2009_Science_Discher_GF+ForceInfluenceOnSC.pdf", "/");
		assertEquals("2009_Science_Discher_GF+ForceInfluenceOnSC.pdf", tokenizer.nextToken());
	}
}
