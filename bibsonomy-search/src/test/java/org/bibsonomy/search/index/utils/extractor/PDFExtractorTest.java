package org.bibsonomy.search.index.utils.extractor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

/**
 * tests for {@link PDFExtractor}
 *
 * @author dzo
 */
public class PDFExtractorTest {
	private static final PDFExtractor EXTRACTOR = new PDFExtractor();
	
	/**
	 * tests for {@link PDFExtractor#supports(File)}
	 * @throws Exception
	 */
	@Test
	public void testSupports() throws Exception {
		assertFalse(EXTRACTOR.supports("test.txt"));
		assertTrue(EXTRACTOR.supports("test.pdf"));
	}
	
	/**
	 * tests for {@link PDFExtractor#extractContent(File)}
	 * @throws Exception
	 */
	@Test
	public void testExtractContent() throws Exception {
		assertEquals("Adobe Acrobat PDF Files\n" + 
				"Adobe® Portable Document Format (PDF) is a universal file format that preserves all\n" + 
				"of the fonts, formatting, colours and graphics of any source document, regardless of\n" + 
				"the application and platform used to create it.\n" + 
				"Adobe PDF is an ideal format for electronic document distribution as it overcomes the\n" + 
				"problems commonly encountered with electronic file sharing.\n" + 
				"• Anyone, anywhere can open a PDF file. All you need is the free Adobe Acrobat\n" + 
				"Reader. Recipients of other file formats sometimes can't open files because they\n" + 
				"don't have the applications used to create the documents.\n" + 
				"• PDF files always print correctly on any printing device.\n" + 
				"• PDF files always display exactly as created, regardless of fonts, software, and\n" + 
				"operating systems. Fonts, and graphics are not lost due to platform, software, and\n" + 
				"version incompatibilities.\n" + 
				"• The free Acrobat Reader is easy to download and can be freely distributed by\n" + 
				"anyone.\n" + 
				"• Compact PDF files are smaller than their source files and download a\n" + 
				"page at a time for fast display on the Web.", EXTRACTOR.extractContent(new File(PDFExtractorTest.class.getClassLoader().getResource("extraction/test.pdf").getFile())));
	}
}
