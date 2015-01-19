/**
 * BibSonomy-MARC-Parser - Marc Parser for BibSonomy
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.marc.extractors;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.model.BibTex;
import org.junit.Test;

/**
 * @author Lukas
 */
public class YearExtractorTest extends AbstractExtractorTest {

	@Test
	public void testYearExtraction() {
		BibTex b = new BibTex();
		YearExtractor yearExtractor = new YearExtractor();
		yearExtractor.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("260", 'c', "1996"));
		assertEquals("1996", b.getYear());
		
		//test with noise
		b = new BibTex();
		yearExtractor.extraxtAndSetAttribute(b, createExtendedMarcWithPicaRecord().withMarcField("260", 'c', "sdgv1992cc"));
		assertEquals("1992", b.getYear());
	}
	
}
