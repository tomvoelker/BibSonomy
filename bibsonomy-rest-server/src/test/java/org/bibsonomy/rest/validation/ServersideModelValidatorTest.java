package org.bibsonomy.rest.validation;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.PersonNameUtils;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class ServersideModelValidatorTest {

	/**
	 * Tests whether author/editor names are normalizes
	 */
	@Test
	public void testCheckPublication() {
		
		final BibTex pub = new BibTex();
		pub.setTitle("Some author names that might cause problems");
		pub.setAuthor("D. E. Knuth and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo");
		pub.setEditor("Hans Christian Andersen and {Die Brüder Grimm} and others");

		
		final ServersideModelValidator ssmv = ServersideModelValidator.getInstance();

		/*
		 * modifies the author and editor names!
		 */
		ssmv.checkPublication(pub);
//		FIXME: change in Sept.		
//		assertEquals("Knuth, D. E. and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo", pub.getAuthor());
//		assertEquals("Andersen, Hans Christian and {Die Brüder Grimm} and others", pub.getEditor());

		assertEquals("D. E. Knuth and Hans von und zu Schmitz and  {Long Company Name} and Leo Bal Mar", pub.getAuthor());
		assertEquals("Hans Christian Andersen and  {Die Brüder Grimm} and others", pub.getEditor());

	}

	
	public static void main(String[] args) {
		final String author = "D. E. Knuth and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo";
		final BibTex pub = new BibTex();
		final int rounds = 1000000;
		
		final long startTime1 = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			pub.setAuthor(author);
		}
		final long endTime1 = System.currentTimeMillis();
		System.out.println(rounds + " rounds of setAuthor() took " + ((endTime1 - startTime1)/1000.0) + " seconds");
		
		final long startTime2 = System.currentTimeMillis();
		for (int i = 0; i < rounds; i++) {
			PersonNameUtils.extractList(author);
		}
		final long endTime2 = System.currentTimeMillis();
		System.out.println(rounds + " rounds of extractList() took " + ((endTime2 - startTime2)/1000.0) + " seconds");
		
		
	}
	
}
