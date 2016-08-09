/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.classification;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.bibsonomy.model.Classification;
import org.junit.Test;

import de.unikassel.puma.openaccess.classification.PublicationClassification;
import de.unikassel.puma.openaccess.classification.PublicationClassificator;

public class ClassificationTest {
	
	private static final PublicationClassificator pubClass = new PublicationClassificator();
	
	public void getAvailableClassificationsTest() {
		Set<Classification> available  = pubClass.getAvailableClassifications();
		
		assertEquals(3, available.size());
	}
	
	@Test
	public void getClassificationDetailsTest() {
		String description  = pubClass.getDescription("JEL", "A");
	
		assertEquals("General Economics: General", description);
		
		description  = pubClass.getDescription("acmccs98-1.2.3", "D.2.11");
		
		assertEquals("Software Architectures", description);

		description  = pubClass.getDescription("acmccs98-1.2.3", "D.0");
	}
	
	@Test
	public void dDCClassificationParserTest() {
		String description = pubClass.getDescription("ddc22_000-999", "0");

		assertEquals("Computer science, information & general works" , description);
		
		description = pubClass.getDescription("ddc22_000-999", "00");
		assertEquals("Computer science, knowledge & systems" , description);
		
		description = pubClass.getDescription("ddc22_000-999", "000");
		assertEquals("Computer science, information & general works" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "3");
		assertEquals("Social sciences" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "30");
		assertEquals("Social sciences, sociology & anthropology" , description);
		

		description = pubClass.getDescription("ddc22_000-999", "300");
		assertEquals("Social sciences" , description);
	}
	
	@Test
	public void getClassificationChildrenTest() {
		
		List<PublicationClassification> children = pubClass.getChildren("acmccs98-1.2.3", "D.2.");
		
		assertEquals(15, children.size());
	}
}
