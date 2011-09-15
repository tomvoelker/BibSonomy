package org.bibsonomy.rest.validation;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class ServersideModelValidatorTest {

	/**
	 * Tests whether author/editor names are normalizes
	 * @throws PersonListParserException 
	 */
	@Test
	public void testCheckPublication() throws PersonListParserException {
		
		final BibTex pub = new BibTex();
		pub.setTitle("Some author names that might cause problems");
		pub.setAuthor(PersonNameUtils.discoverPersonNames("D. E. Knuth and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo"));
		pub.setEditor(PersonNameUtils.discoverPersonNames("Hans Christian Andersen and {Die Brüder Grimm} and others"));

		
		final ServersideModelValidator ssmv = ServersideModelValidator.getInstance();

		/*
		 * modifies the author and editor names!
		 */
		ssmv.checkPublication(pub);
		assertEquals("Knuth, D. E. and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo", PersonNameUtils.serializePersonNames(pub.getAuthor()));
		assertEquals("Andersen, Hans Christian and {Die Brüder Grimm} and others", PersonNameUtils.serializePersonNames(pub.getEditor()));

	}
	
}
