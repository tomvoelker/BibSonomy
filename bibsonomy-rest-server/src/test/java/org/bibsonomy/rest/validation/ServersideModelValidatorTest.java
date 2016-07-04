/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.validation;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.junit.Test;

/**
 * @author rja
 */
public class ServersideModelValidatorTest {
	private static final ServersideModelValidator MODEL_VALIDATOR = new ServersideModelValidator();

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

		/*
		 * modifies the author and editor names!
		 */
		MODEL_VALIDATOR.checkPublication(pub);
		assertEquals("Knuth, D. E. and von und zu Schmitz, Hans and {Long Company Name} and Bal Mar, Leo", PersonNameUtils.serializePersonNames(pub.getAuthor()));
		assertEquals("Andersen, Hans Christian and {Die Brüder Grimm} and others", PersonNameUtils.serializePersonNames(pub.getEditor()));
	}
	
}
