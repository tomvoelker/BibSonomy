/**
 *
 *  BibSonomy-Model - Java- and JAXB-Model.
 *
 *  Copyright (C) 2006 - 2011 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.util;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.util.StringUtils;
import org.junit.Test;

/**
 * Testcase for the SimHash class
 * 
 * @author Dominik Benz
 * @author Jens Illig
 * @author Christian Schenk
 * @version $Id$
 */
public class SimHashTest {

	/**
	 * tests getSimHash
	 */
	@Test
	public void getSimHash() {
		final BibTex bibTex = new BibTex();		
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
	}
	
	
	/**
	 * Ensure that an empty year and a NULL year don't change the hashes. 
	 */
	@Test
	public void getSimHash2() {
		final BibTex bibTex = new BibTex();
		bibTex.setYear(null);
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
		bibTex.setYear("");
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
		/*
		 * A hypen "-" should also not change the year.  
		 */
		bibTex.setYear("-");
		assertEquals("a127fd1f86e4ab650f2216f09992afa4", SimHash.getSimHash(bibTex, HashID.getSimHash(0)));
		assertEquals("23b58def11b45727d3351702515f86af", SimHash.getSimHash(bibTex, HashID.getSimHash(1)));
		assertEquals("7bb0edd98f22430a03b67f853e83c2ca", SimHash.getSimHash(bibTex, HashID.getSimHash(2)));
		assertEquals("", SimHash.getSimHash(bibTex, HashID.getSimHash(3)));
		
	}
	
	
	/**
	 * some tests to check author normalization
	 * 
	 * it only makes sense to test this with simHash1 (interhash), because normalization
	 * is not applied for intra-hash computation
	 */
	@Test
	public void testAuthorNormalization() {
		BibTex bib = new BibTex();
		bib.setAuthor(PersonNameUtils.discoverPersonNames("b and A"));
		final String interHash = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("B and A"));
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("a and b"));
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("a and a and b and b and a and B and A and B and a"));
		assertEquals(interHash, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		
		bib.setAuthor(PersonNameUtils.discoverPersonNames("John Paul and Bridget Jones"));
		final String interHash2 = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("JoHN pAUl and brIDgeT JOneS"));
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("J PAUL and b jones"));
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("PAUL, J and jones, B"));
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("JoHN pAUl and JOneS, brIDgeT"));
		assertEquals(interHash2, SimHash.getSimHash(bib, HashID.getSimHash(1)));
		
		bib.setAuthor(PersonNameUtils.discoverPersonNames("John and John, Paul"));
		final String interHash3 = SimHash.getSimHash(bib, HashID.getSimHash(1));
		bib.setAuthor(PersonNameUtils.discoverPersonNames("JoHN and Paul jOhN"));
		assertEquals(interHash3, SimHash.getSimHash(bib, HashID.getSimHash(1)));
	}
	
	/**
	 * tests person normalization for simhash 1
	 */
	@Test
	public void testPersonNormalizationForSimhash1() {
		/*
		 * almost all examples from our database! :-(
		 */
		assertEquals("[a.bauer]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames("{Axel} Bauer")));
		assertEquals("[l.antiqueira,l.costa,m.nunes,o.jr.]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames("L. Antiqueira and M.G.V. Nunes and O.N. Oliveira Jr. and L. da F. Costa")));
		assertEquals("[b.informationstechnik]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames(" {Bundesamt für Sicherheit in der Informationstechnik}")));
		assertEquals("[a.weiterbildung]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames("{Arbeitsgruppe Hochschuldidaktische Weiterbildung}")));
		assertEquals("[k.blom]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames(" {Katarina Blom}")));
		assertEquals("[a.stolcke,c.wooters,d.jurafsky,e.fosler,g.tajchman,j.segal,n.morgan]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames("D. Jurafsky and C. Wooters and J. Segal and A. Stolcke and E. Fosler and G. Tajchman andN. Morgan")));
		assertEquals("[k.blom,others]", SimHash.getNormalizedPersons(PersonNameUtils.discoverPersonNames("Katarina Blom and others")));
	}
	

	/**
	 * tests person normalization for simhash 2
	 */
	@Test
	public void testPersonNormalizationForSimhash2() {
		assertEquals("Bundesamt für Sicherheit in der Informationstechnik", StringUtils.removeNonNumbersOrLettersOrDotsOrSpace(PersonNameUtils.serializePersonNames(PersonNameUtils.discoverPersonNames(" {Bundesamt für Sicherheit in der Informationstechnik}"))));
	}
	
}