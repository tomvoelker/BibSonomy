/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package tags;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.util.EnumUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

/**
 * @author dbenz
 */
public class FunctionsTest {
	
	private static final DateTimeFormatter ISO8601_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	@Test
	public void toggleUserSimilarity() {
		UserRelation rel = UserRelation.getUserRelationById(0); // jaccard
		String nextRelString = Functions.toggleUserSimilarity(rel.name());
		UserRelation nextRel = EnumUtils.searchEnumByName(UserRelation.values(), nextRelString);
		assertNotNull(nextRel);
		assertEquals(1, nextRel.getId());
		rel = UserRelation.getUserRelationById(3);
		nextRelString = Functions.toggleUserSimilarity(rel.name());
		nextRel = EnumUtils.searchEnumByName(UserRelation.values(), nextRelString);
		assertNotNull(nextRel);
		assertEquals(0, nextRel.getId());
	}
	
	@Test
	public void testComputeTagFontSize() throws Exception {
		final int[] tagFrequencies = new int[] {1416,1241,3035,1150,1548,1796,1069,4446,2024,2401,2119,1157,6325,1132,1714,1135,1666,1098,1332,2283,1009,2985,1119,1593,2041,1076,1642,1656,2380,1579,1146,976,5751,1808,1248,1367,1904,1790,2311,1369,3348,2203,1231,1107,2218,1672,2570,1057,1038,1268,2228,974,1115,1558,2056,1644,1183,2792,1672,1325,1301,2457,1904,2243,1038,6709,2125,1099,1586,2497,2919,1243,1666,1817,3177,1104,1970,2126,2894,1655,6444,1105,3661,2822,1135,2102,1298,5088,1141,1743,8333,1282,4952,5674,1500,2371,1062,1237,1914,1084};

		for (final int tagFrequency : tagFrequencies) {
			final int fontSize = Functions.computeTagFontsize(tagFrequency, 0, 8333, "popular");
		}
		
	}
	
	@Test
	public void getTagsAsString() {
		final List<Tag> tagList = new LinkedList<Tag>();
		tagList.add(new Tag("test"));
		assertEquals("test", Functions.toTagString(tagList));
		
		tagList.add(new Tag("xyz"));
		tagList.add(new Tag("abc"));
		assertEquals("test xyz abc", Functions.toTagString(tagList));
	}
	
	@Test
	public void testGetLowerPath() {
		assertEquals("user", Functions.getLowerPath("user/jaeschke"));
		assertEquals("user/jaeschke", Functions.getLowerPath("user/jaeschke/foo"));
		assertEquals("user/jaeschke", Functions.getLowerPath("user/jaeschke/foo"));
		// tags with slash did not work with the first implementation: 
		assertEquals("user/foobar", Functions.getLowerPath("user/foobar/use:doi:10.0000/WWW:DD:1.0.0"));
		// TODO: still does not work (requires separate method or parameter for different pages
//		assertEquals("/tag", Functions.getLowerPath("/tag/use:doi:10.0000/WWW:DD:1.0.0"));
		assertEquals("user/thomaslevine", Functions.getLowerPath("user/thomaslevine/pinyin \\xe4\\xb8\\xad\\xe6\\x96\\x87 education China software korean Language?lang=en&.entriesPerPage=5"));
		assertEquals("search", Functions.getLowerPath("search/clustering"));
		assertEquals("", Functions.getLowerPath("groups"));
	}
	
	@Test
	public void testFormatDateISO8601() throws ParseException {
		final String date = "2012-01-30T13:13:16+0100";
		assertEquals(date, Functions.formatDateISO8601(ISO8601_DATE_FORMAT.parseDateTime(date).toDate()));
		
		assertEquals("1970-01-01T01:00:00+0100", Functions.formatDateISO8601(new Date(0)));
	}

	
	@Test
	public void testFormatDateW3CDTF() throws Exception {
		assertEquals("2012-11-07T14:43:16+01:00", Functions.formatDateW3CDTF(ISO8601_DATE_FORMAT.parseDateTime("2012-11-07T14:43:16+0100").toDate()));
		assertEquals("2012-11-07T16:43:16+01:00", Functions.formatDateW3CDTF(ISO8601_DATE_FORMAT.parseDateTime("2012-11-07T14:43:16-0100").toDate())); // FIXME: time changes through parsing? 
	}
	
	@Test
	public void testFormatDateISO8601NPECheck() throws ParseException {
		assertEquals("", Functions.formatDateISO8601(null));
	}
	
	

	
	@Test
	public void testGetDate() throws Exception {
		final Locale locale = new Locale("de");
		assertEquals("15.02.2010", Functions.getDate(" 15 ", "february", "2010", locale));
		assertEquals("15.02.2010", Functions.getDate("15", "feb", "2010", locale));
		assertEquals("15.02.2010", Functions.getDate("15", "FEB", "2010", locale));
		assertEquals("Februar 2010", Functions.getDate("", "feb", "2010", locale));
		assertEquals("März 2010", Functions.getDate("", "march", "2010", locale));
		assertEquals("2010", Functions.getDate("", "", "2010", locale));
		/*
		 * fallback: return plain strings if parsing fails
		 */
		assertEquals("22 März 2010", Functions.getDate(" 22 ", " März", "2010", locale)); 
		assertEquals("Oktober 2010", Functions.getDate("", "Oktober", "2010", locale));
		/*
		 * test Bibtex cleansing
		 */
		assertEquals("22 März 2010", Functions.getDate(" 22 ", " \\emph{März}", "2010", locale)); 
		assertEquals("22 März 2010", Functions.getDate(" 22 ", " \\emph{M\"arz}", "2010", locale));
		/*
		 * the same with another locale
		 */
		final Locale gbLocale = new Locale("gb");
		assertEquals("Feb 15, 2010", Functions.getDate(" 15 ", "february", "2010", gbLocale));
		assertEquals("Feb 15, 2010", Functions.getDate("15", "feb", "2010", gbLocale));
		assertEquals("Feb 15, 2010", Functions.getDate("15", "FEB", "2010", gbLocale));
		assertEquals("February 2010", Functions.getDate("", "feb", "2010", gbLocale));
		assertEquals("March 2010", Functions.getDate("", "march", "2010", gbLocale));
		assertEquals("2010", Functions.getDate("", "", "2010", gbLocale));

		assertEquals("January 2011", Functions.getDate("", "#jan#", "2011", gbLocale));
		
	}

	/**
	 * tests {@link Functions#getSWRCEntryType(String)}
	 * @throws Exception
	 */
	@Test
	public  void testGetSWRCEntryType() throws Exception {
		assertEquals("Book", Functions.getSWRCEntryType(BibTexUtils.BOOK));
		assertEquals("Misc", Functions.getSWRCEntryType(BibTexUtils.PRESENTATION));
		assertEquals("Misc", Functions.getSWRCEntryType("dfsdf"));
		assertEquals("Article", Functions.getSWRCEntryType(BibTexUtils.ARTICLE));
	}

	/**
	 * tests {@link Functions#getRISEntryType(String)}
	 * @throws Exception
	 */
	@Test
	public  void testGetRISEntryType() throws Exception {
		assertEquals("Journal Article", Functions.getRISEntryType(BibTexUtils.ARTICLE));
		assertEquals("Generic", Functions.getRISEntryType(BibTexUtils.MISC));
		assertEquals("Generic", Functions.getRISEntryType(BibTexUtils.MANUAL));
		assertEquals("Book", Functions.getRISEntryType(BibTexUtils.BOOK));
		assertEquals("Book", Functions.getRISEntryType(BibTexUtils.BOOKLET));
	}
	
	/**
	 * tests {@link Functions#isSameHost(String, String)}
	 */
	@Test
	public void testIsSameHost() {
		assertTrue(Functions.isSameHost("http://localhost/", "http://localhost/"));
		assertTrue(Functions.isSameHost("http://localhost/", "https://localhost/"));
		assertFalse(Functions.isSameHost("http://localhost/", "http://localhost2/"));
		assertFalse(Functions.isSameHost("http://localhost/", "http://www.localhost/"));
	}
}
