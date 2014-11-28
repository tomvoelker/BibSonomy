/**
 * BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
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
package org.bibsonomy.util.tex;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Christian Claus
 */
public class TeXDecodeTest {
	
	/**
	 * tests the complete map
	 * 
	 */
	@Test
	public void testCompleteDecode() {
		StringBuffer unclean = new StringBuffer();
		StringBuffer clean = new StringBuffer();						
		
		for(String s : TexDecode.getTexMap().keySet()) {
			unclean.append(s);
			clean.append(TexDecode.getTexMap().get(s));
		}
		assertEquals(clean.toString(), TexDecode.decode(unclean.toString()));
	}
	
	/**
	 * tests a string with leading TeX macro
	 * 
	 */
	@Test
	public void testEncodingWithLeadingMacro() {
		String unclean = "{\\\"A}foo{{\\ss}}bar";
		String clean = "Äfooßbar";

		assertEquals(clean, TexDecode.decode(unclean));
	}
	
	/**
	 * tests a string with tailing TeX macro
	 * 
	 */
	@Test
	public void testEncodingWithTailingMacro() {
		String unclean = "foo  {\\\"{U}}  bar{\\\"A}";
		String clean = "foo  Ü  barÄ";

		assertEquals(TexDecode.decode(unclean), clean);
	}
	
	/**
	 * tests a string which contains a TeX macro
	 * 
	 */
	@Test
	public void testEncodingWithMacro() {
		String unclean = "foo{\\\"{U}}{\\\"A}bar";
		String clean = "fooÜÄbar";

		assertEquals(TexDecode.decode(unclean), clean);
	}
	
	/**
	 * tests a few new replacements
	 */
	@Test
	public void testEncodingWithSpecialUmlauts() {
		String unclean = "foo\\\"{U}\\\"Abar";
		String clean = "fooÜÄbar";

		assertEquals(TexDecode.decode(unclean), clean);		
	}
	
	/**
	 * test for curl replacements
	 */
	@Test
	public void testEncodingWithCurls() {
		String unclean = "{){{}/()as)[[)]";
		String clean = "/as";

		assertEquals(TexDecode.decode(unclean), clean);		
	}

}