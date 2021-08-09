/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.controller.ajax;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * @author rja
 */
public class ConceptControllerTest {
	
	/**
	 * tests {@link ConceptController#prepareResponseString(String, List)}
	 */
	@Test
	public void testPrepareResponseString() {
		final List<Tag> pickedConcepts = new LinkedList<>();

		final Tag conf = new Tag("conference");
		conf.addSubTag(new Tag("iccs"));
		conf.addSubTag(new Tag("ecmlpkdd"));
		conf.addSubTag(new Tag("www"));
		conf.addSubTag(new Tag("icm"));
		pickedConcepts.add(conf);

		final Tag loc = new Tag("location");
		loc.addSubTag(new Tag("kassel"));
		loc.addSubTag(new Tag("berlin"));
		loc.addSubTag(new Tag("bremen"));
		loc.addSubTag(new Tag("erfurt"));
		pickedConcepts.add(loc);


		final String response = ConceptController.prepareResponseString("jaeschke", pickedConcepts);

		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<relations user=\"jaeschke\"><relation><upper>conference</upper><lowers id=\"conference\">" +
				"<lower>iccs</lower><lower>ecmlpkdd</lower><lower>www</lower><lower>icm</lower></lowers></relation>" +
				"<relation><upper>location</upper><lowers id=\"location\"><lower>kassel</lower><lower>berlin</lower>" +
				"<lower>bremen</lower><lower>erfurt</lower></lowers></relation></relations>", response);

	}

}
