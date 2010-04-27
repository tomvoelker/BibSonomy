/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2010 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.util.tex;

import junit.framework.TestCase;

/**
 * @author Christian Claus
 * @version $Id$
 */
public class TeXEncodeTest extends TestCase {
	
	/**
	 * tests the complete map
	 * 
	 */
	public void testEncoding() {
		StringBuffer unclean = new StringBuffer();
		StringBuffer clean = new StringBuffer();
		//String clean = "ÇçïîìíåÅæÆßÄÖÜäöüÄÖÜäëüöàèòùêôâûáéóúÉñÄÖÜäüöÑÄÖÜäüöííèÇçÃãËëÄÖÜäüö";
						
		
		for(String s : TexDecode.getTEX()) {
			unclean.append(s);
		}
		for (String s: TexDecode.getUNICODE()) {
			clean.append(s);
		}

		assertEquals(clean.toString(), TexDecode.decode(unclean.toString()));
	}
	
	/**
	 * tests a string with leading TeX macro
	 * 
	 */
	public void testEncodingWithLeadingMacro() {
		String unclean = "{\\\"A}foo{{\\ss}}bar";
		String clean = "Äfooßbar";

		assertEquals(clean, TexDecode.decode(unclean));
	}
	
	/**
	 * tests a string with tailing TeX macro
	 * 
	 */
	public void testEncodingWithTailingMacro() {
		String unclean = "foo  {\\\"{U}}  bar{\\\"A}";
		String clean = "foo  Ü  barÄ";

		assertEquals(TexDecode.decode(unclean), clean);
	}
	
	/**
	 * tests a string which contains a TeX macro
	 * 
	 */
	public void testEncodingWithMacro() {
		String unclean = "foo{\\\"{U}}{\\\"A}bar";
		String clean = "fooÜÄbar";

		assertEquals(TexDecode.decode(unclean), clean);
	}
	
	/**
	 * tests a few new replacements
	 */
	public void testEncodingWithSpecialUmlauts() {
		String unclean = "foo\\\"{U}\\\"Abar";
		String clean = "fooÜÄbar";

		assertEquals(TexDecode.decode(unclean), clean);		
	}
	
	/**
	 * test for curl replacements
	 */
	public void testEncodingWithCurls() {
		String unclean = "{){{}/()as)[[)]";
		String clean = "/as";

		assertEquals(TexDecode.decode(unclean), clean);		
	}

}