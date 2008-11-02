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