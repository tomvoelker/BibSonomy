/**
 *  
 *  BibSonomy-Common - Common things (e.g., exceptions, enums, utils, etc.)
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
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
	
	private TexEncode encoder;
	
	/**
	 * tests the complete map
	 * 
	 */
	public void testEncoding() {
		String unclean = "";
		String clean = "ÇçïîìíåÅæÆßÄÖÜäääÄÖÜäëüöàèòùêôâûáéóúÉñÄÖÜäüöÑ";
		
		encoder = new TexEncode();
		
		for(String s : encoder.getTEX()) {
			unclean += s;
		}

		assertEquals(clean, encoder.encode(unclean));
	}
	
	/**
	 * tests a string with leading TeX macro
	 * 
	 */
	public void testEncodingWithLeadingMacro() {
		String unclean = "{\\\"A}foo{{\\ss}}bar";
		String clean = "Äfooßbar";

		encoder = new TexEncode();
		
		assertEquals(encoder.encode(unclean), clean);
	}
	
	/**
	 * tests a string with tailing TeX macro
	 * 
	 */
	public void testEncodingWithTailingMacro() {
		String unclean = "foo  {\\\"{U}}  bar{\\\"A}";
		String clean = "foo  Ü  barÄ";

		encoder = new TexEncode();
		
		assertEquals(encoder.encode(unclean), clean);
	}
	
	/**
	 * tests a string which contains a TeX macro
	 * 
	 */
	public void testEncodingWithMacro() {
		String unclean = "foo{\\\"{U}}{\\\"A}bar";
		String clean = "fooÜÄbar";

		encoder = new TexEncode();
		
		assertEquals(encoder.encode(unclean), clean);
	}

}