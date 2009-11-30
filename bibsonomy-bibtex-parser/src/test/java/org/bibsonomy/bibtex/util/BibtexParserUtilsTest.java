/**
 *  
 *  BibSonomy-BibTeX-Parser - BibTeX Parser from
 * 		http://www-plan.cs.colorado.edu/henkel/stuff/javabib/
 *   
 *  Copyright (C) 2006 - 2009 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.bibtex.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.junit.Test;


/**
 * Tests for the Bibtex parser utility
 * 
 * @author dbenz
 * @version $Id$
 */
public class BibtexParserUtilsTest {

	private BibTex bib;
	
	private static final Log log = LogFactory.getLog(BibtexParserUtilsTest.class);
	
	private String bibString() {
		return BibTexUtils.toBibtexString(this.bib);
	}
	
	private void initBibtex() {
		this.bib = new BibTex();
		this.bib.setEntrytype("inproceedings");
		this.bib.setBibtexKey("MyBestKeyEver");
	}
	
		
	@Test // check if authors are correctly formatted
	public void testFormatAuthors() {
		this.initBibtex();
		
		this.bib.setAuthor("Silie, Peter and Dampf, Hans and Sch�ble, Fritz");
		BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
		assertEquals("Peter Silie and Hans Dampf and Fritz Sch�ble", bibutils.getFormattedAuthorString());
		
		this.bib.setAuthor("Peter Silie and Hans Dampf and Fritz Sch�ble");
		bibutils = new BibtexParserUtils(this.bibString());
		assertEquals("Peter Silie and Hans Dampf and Fritz Sch�ble", bibutils.getFormattedAuthorString());		
	}
	
	@Test // check if Editors are correctly formatted
	public void testFormatEditors() {
		this.initBibtex();
		
		this.bib.setEditor("Silie, Peter and Dampf, Hans and de La Rue, Jean-Jaques");
		BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
		assertEquals("Peter Silie and Hans Dampf and Jean-Jaques de La Rue", bibutils.getFormattedEditorString());
		
		this.bib.setEditor("Peter Silie and Hans Dampf and Fritz Sch�ble");
		bibutils = new BibtexParserUtils(this.bibString());
		assertEquals("Peter Silie and Hans Dampf and Fritz Sch�ble", bibutils.getFormattedEditorString());		
	}	

	@Test // check if exception is thrown when authors are malformed
	public void testMalformedAuthors() {
		this.initBibtex();
		this.bib.setAuthor("&&<)&(^\',,,,,&08  ;P*OIASUDPFOA");
		try {
			BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
			bibutils.getFormattedAuthorString();
			fail("Exception should have been thrown");
		} catch (Exception ex) {			
		}				
	}
	
	@Test // check if exception is thrown when editors are malformed
	public void testMalformedEditors() {
		this.initBibtex();
		this.bib.setEditor("pluha_ j& asldjf; 9, a, ,, ,,, & asdl ll;;;");
		try {
			BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
			bibutils.getFormattedEditorString();
			fail("Exception should have been thrown");
		} catch (Exception ex) {			
		}				
	}
	
	@Test // check if exception is thrown when bibtex is malformed
	public void testMalformedBibtex() {
		this.initBibtex();
		this.bib.setEntrytype("{{{{{");
		log.debug(this.bibString());
		try {
			BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
			fail("Exception should have been thrown");
		} catch (Exception ex) {			
		}			
		
		this.initBibtex();
		this.bib.setChapter("a nice chapter - if there wasn't this single bracket {");
		try {
			BibtexParserUtils bibutils = new BibtexParserUtils(this.bibString());
			fail("Exception should have been thrown");
		} catch (Exception ex) {			
		}		
	}
}
