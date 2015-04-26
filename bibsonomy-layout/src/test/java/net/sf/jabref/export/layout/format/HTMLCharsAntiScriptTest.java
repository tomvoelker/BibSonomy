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
		assertEquals("&amp;", FORMATTER.format("\\\\&"));
	}
}
