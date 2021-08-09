/**
 * BibSonomy-Layout - Layout engine for the webapp.
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
package net.sf.jabref.export.layout.format;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * tests for {@link HTMLCharsAntiScript}
 * 
 * @author dzo
 */
public class HTMLCharsAntiScriptTest {
	private static final HTMLCharsAntiScript FORMATTER = new HTMLCharsAntiScript();
	
	/**
	 * tests {@link HTMLCharsAntiScript#format(String)} for new lines
	 */
	@Test
	public void testNewLines() {
		assertEquals("<p>", FORMATTER.format("\n\n"));
		assertEquals("<p>", FORMATTER.format("\n\n\n"));
		assertEquals("<br>", FORMATTER.format("\n"));
	}
	
	@Test
	public void testAmp() {
		assertEquals("&amp;", FORMATTER.format("&"));
		assertEquals("&amp;", FORMATTER.format("\\&"));
	}
}
