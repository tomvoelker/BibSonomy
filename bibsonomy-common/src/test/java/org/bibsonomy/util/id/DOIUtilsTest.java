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

package org.bibsonomy.util.id;

import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.bibsonomy.util.WebUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class DOIUtilsTest {

	private static final Random rand = new Random();

	private static final String bibtexWithDoi = "@article{Pevzner02,\n" + 
	"address = {Cambridge, MA, USA},\n" +
	"author = {Lev Pevzner and Marti A. Hearst},\n" +
	"interHash = {707f8d31137f6d39edd1d54664351d0d},\n" +
	"intraHash = {330a11348556280054ec5fceeb23649c},\n" +
	"journal = {Comput. Linguist.},\n" +
	"number = {1},\n" +
	"pages = {19--36},\n" +
	"publisher = {MIT Press},\n" +
	"title = {A critique and improvement of an evaluation metric for text segmentation},\n" +
	"url = {http://portal.acm.org/citation.cfm?id=636737&dl=GUIDE&coll=GUIDE&CFID=27698866&CFTOKEN=51312152},\n" +
	"volume = {28},\n" +
	"year = {2002},\n" +
	"issn = {0891-2017},\n" +
	"doi = {http://dx.doi.org/10.1162/089120102317341756},\n" + 
	"}";
	
	private static final String bibtexDoi = "10.1162/089120102317341756";

	private static final String[] dois = new String[] 
	                                                {
		"10.1016/j.cemconres.2003.10.011",
		"doi:10.1016/j.cemconres.2003.10.011",
		"10.1023/A:1009769707641",
		"10.1016/S0169-7552(98)00110-X",
		"10.1145/860451",
		"10.1002/cpe.607",
		"doi:10.1109/ISSTA.2002.1048560",
		"DOI: 10.1016/j.spl.2008.05.017",
		"10.1145/160688.160713"
	                                                };

	private static final String[] nonDois = new String[] 
	                                                   {
		" 10.1016/j.cemconres.2003.10.011 ",
		"10.1016/j.cemconres.2003.10.011\"",
		"10.1016/j.cemconres.2003.10.011'",
		null,
		"",
		"ysdfklaskld",
		"10.1016/j.cemconres.2003.10.011 asef",
		"\n10.1016/j.cemconres.2003.10.}011"
	                                                   };

	private static final String[] fuzzyStarts = new String[] {
		"  ",
		":",
		"\"",
		"'",
		"("
	};

	private static final String[] fuzzyEndings = new String[] {
		" ",
		"\"sadfasdf",
		"'"
	};



	private static final String[] fuzzyDoiOnlyStarts = new String[] {
		"  ",
		":",
		"\"",
		"'",
		"("
	};

	private static final String[] fuzzyDoiOnlyEndings = new String[] {
		" ",
		"\"",
		"'"
	};

	private static final Pattern DOI_START = Pattern.compile("^doi:\\s*(.*)", Pattern.CASE_INSENSITIVE);

	@Test
	public void testIsDOIURL() {
		for(final String doi: dois) {
			try {
				Assert.assertTrue(DOIUtils.isDOIURL(DOIUtils.getURL(doi)));
			} catch (MalformedURLException ex) {
				fail(ex.getMessage());
			}
		}
	}


	private String fuzzifyDoiOnlyDOI(final String doi) {
		return 
		fuzzyDoiOnlyStarts[rand.nextInt(fuzzyDoiOnlyStarts.length)] + 
		doi + 
		fuzzyDoiOnlyEndings[rand.nextInt(fuzzyDoiOnlyEndings.length)];
	}

	private String fuzzifyDOI(final String doi) {
		return 
		fuzzyStarts[rand.nextInt(fuzzyStarts.length)] + 
		doi + 
		fuzzyEndings[rand.nextInt(fuzzyEndings.length)];
	}

	private String stripDOI(final String doi) {
		final Matcher matcher = DOI_START.matcher(doi);
		if (matcher.find()) {
			return matcher.group(1);
		}
		
		return doi;
	}

	@Test
	public void testExtractDOI() {
		for(final String doi: dois) {
			final String fuzzyDoi = fuzzifyDOI(doi);
			Assert.assertEquals(stripDOI(doi), DOIUtils.extractDOI(fuzzyDoi));
		}
		Assert.assertEquals(bibtexDoi, DOIUtils.extractDOI(bibtexWithDoi));
	}

	@Test
	public void testContainsOnlyDOI() {
		for(final String doi: dois) {
			final String fuzzyDoi = fuzzifyDoiOnlyDOI(doi);
			Assert.assertTrue(DOIUtils.containsOnlyDOI(doi));
			Assert.assertTrue(DOIUtils.containsOnlyDOI(fuzzyDoi));
		}
		Assert.assertFalse(DOIUtils.containsOnlyDOI(bibtexWithDoi));
	}

	@Test
	public void testContainsDOI() {
		for(final String doi: dois) {
			Assert.assertTrue(DOIUtils.containsDOI(doi));
			Assert.assertTrue(DOIUtils.containsDOI(fuzzifyDOI(doi)));
		}
		Assert.assertTrue(DOIUtils.containsDOI(bibtexWithDoi));
	}


	@Test
	public void testIsDOI() {
		for(final String doi: dois) {
			Assert.assertTrue(DOIUtils.isDOI(doi));
		}
		Assert.assertFalse(DOIUtils.isDOI(bibtexWithDoi));
	}

	@Test
	public void testIsNonDOI() {
		for(final String doi: nonDois) {
			Assert.assertFalse(DOIUtils.isDOI(doi));
		}
		for(final String doi: dois) {
			Assert.assertFalse(DOIUtils.isDOI(fuzzifyDOI(doi)));
		}
	}


	/**
	 * test getting URL
	 * FIXME: Thomas, kannst Du bitte mal schauen, was hier schiefläuft? Ist ja total seltsam ...
	 * 
	 * Im Browser komme ich bei Aufruf von
	 * 
	 * http://dx.doi.org/10.1007/11922162
	 * 
	 * auf 
	 * 
	 * http://www.springerlink.com/content/w425794t7433/
	 * 
	 * raus. Aber {@link WebUtils#getRedirectUrl(URL)} kommt auf 
	 * 
	 * http://www.springerlink.com/link.asp?id=w425794t7433
	 * 
	 * raus. Auch wenn ich 
	 * 
	 * http://www.springerlink.com/index/10.1007/11922162
	 * 
	 * (das ist die alte URL aus dem Test hier) eingebe, komme ich auf 
	 * der ersten URL raus. D.h., irgendwie scheint Springer da je nach
	 * Cookie-Handling, Referer, oder nach Browser woanders hinzuleiten. :-(
	 * 
	 *   
	 */
	@Test
	@Ignore
	public void getUrlForDoiTest(){
		Assert.assertEquals("http://www.springerlink.com/link.asp?id=w425794t7433", DOIUtils.getUrlForDoi("10.1007/11922162").toString());
	}
}
