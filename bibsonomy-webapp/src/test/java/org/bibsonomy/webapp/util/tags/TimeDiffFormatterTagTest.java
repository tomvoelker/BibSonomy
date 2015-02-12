/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.util.tags;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * FIXME: English tests disabled, because they work only on systems having 
 * English as default language (not German). This is caused by a bug (IMO) of
 * Java's mechanism of determining the default language.
 * 
 * @author rja
 */
public class TimeDiffFormatterTagTest {

	private static final String EN = "en";
	private static final String DE = "de";

	private static final DateTimeFormatter FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm");
	
	/**
	 * This date format is used for input parameters (e.g. beans)
	 */
	private static final DateTimeFormatter ISO8601_DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/**
	 * This date gave some strange results.
	 * 
	 * Last time the reason was that someone with a mis-configured 
	 * ResourceBundleEditor comitted a messages_de.properties where the empty
	 * value "time.postfix" was removed. Thus: always enable "Keep properties 
	 * with empty values" in your ResourceBundleEditor settings!
	 * 
	 * @throws ParseException
	 */
	@Test
	public void testGetDateDiffSomeYears() throws ParseException {
		final Date startDate = FORMAT.parseDateTime("2005-12-30 17:04").toDate();
		final Date endDate = FORMAT.parseDateTime("2012-02-02 08:05").toDate();
		
		final ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("messages");
		
		final String formatTimeDiff = TimeDiffFormatterTag.formatTimeDiff(startDate, endDate, new Locale("de"), ms);
//		System.out.println(formatTimeDiff);
		assertEquals("vor 6 Jahren und 2 Monaten", formatTimeDiff);
	}
	
	@Test
	public void testIsoDateParsing() throws ParseException {
		final String date = "2012-01-30T13:13:16+0100";
		assertEquals(date, ISO8601_DATE_FORMAT.print(ISO8601_DATE_FORMAT.parseDateTime(date)));
	}
	
	/*
	 * FIXME: English tests are disabled since they work only on machines with
	 * an English locale. 
	 */
	
	@Test
	@Ignore
	public void testGetDateDiffSecondsEn() {
		checkTimeDiff(10, "about 10 seconds ago", EN);
	}
	
	@Test
	@Ignore
	public void testGetDateDiffMinutesEn() {
		checkTimeDiff(60, "a minute ago", EN);
		checkTimeDiff(600, "10 minutes ago", EN);
	}
	
	@Test
	@Ignore
	public void testGetDateDiffHoursEn() {
		checkTimeDiff(60 * 60, "an hour ago", EN);
	}
	
	@Test
	@Ignore
	public void testGetDateDiffDaysEn() {
		checkTimeDiff(60 * 60 * 24, "a day ago", EN);
		checkTimeDiff(60 * 60 * 24 * 2, "2 days ago", EN);
		checkTimeDiff(60 * 60 * 24 * 7, "7 days ago", EN);
	}
	
	@Test
	@Ignore
	public void testGetDateDiffMonthsEn() {
		checkTimeDiff(60 * 60 * 24 * 30, "a month ago", EN);
		checkTimeDiff(60 * 60 * 24 * 7 * 30, "7 months ago", EN);
	}
	
	@Test
	@Ignore
	public void testGetDateDiffYearsEn() {
		checkTimeDiff(60 * 60 * 24 * 30 * 12, "a year ago", EN);
		checkTimeDiff(60 * 60 * 24 * 30 * 13, "a year and a month ago", EN);
		checkTimeDiff(60 * 60 * 24 * 30 * 12 * 7, "7 years ago", EN);
	}
	
	/**
	 * ignored, because weird things happen on Hudson:
	 * 
	 * expected:<vor [10 Sekunden]> but was:<vor [about 10 seconds]>
	 */
	@Test
	@Ignore
	public void testGetDateDiffSecondsDe() {
		checkTimeDiff(10, "vor 10 Sekunden", DE);
	}
	
	@Test
	public void testGetDateDiffMinutesDe() {
		checkTimeDiff(60, "vor einer Minute", DE);
		checkTimeDiff(600, "vor 10 Minuten", DE);
	}
	
	@Test
	public void testGetDateDiffHoursDe() {
		checkTimeDiff(60 * 60, "vor einer Stunde", DE);
		checkTimeDiff(60 * 60 + 20 * 60, "vor einer Stunde und 20 Minuten", DE);
	}
	
	@Test
	public void testGetDateDiffDaysDe() {
		checkTimeDiff(60 * 60 * 24, "vor einem Tag", DE);
		checkTimeDiff(60 * 60 * 24 * 2, "vor 2 Tagen", DE);
		checkTimeDiff(60 * 60 * 24 * 7, "vor 7 Tagen", DE);
	}
	
	@Test
	public void testGetDateDiffMonthsDe() {
		checkTimeDiff(60 * 60 * 24 * 30, "vor einem Monat", DE);
		checkTimeDiff(60 * 60 * 24 * 30 + 60 * 60 * 24 * 7, "vor einem Monat und 7 Tagen", DE);
		checkTimeDiff(60 * 60 * 24 * 7 * 30, "vor 7 Monaten", DE);
	}
	
	@Test
	public void testGetDateDiffYearsDe() {
		checkTimeDiff(60 * 60 * 24 * 30 * 12, "vor einem Jahr", DE);
		checkTimeDiff(60 * 60 * 24 * 30 * 13, "vor einem Jahr und einem Monat", DE);
		checkTimeDiff(60 * 60 * 24 * 30 * 12 * 7, "vor 7 Jahren", DE);
	}

	private void checkTimeDiff(final long seconds, final String expected, final String lang) {
		final long offset = 1000 * seconds;
		final Date startDate = new Date();
		final Date endDate = new Date(startDate.getTime() + offset);
		
		final ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasename("messages");
		
		final String formatTimeDiff = TimeDiffFormatterTag.formatTimeDiff(startDate, endDate, new Locale(lang), ms);
//		System.out.println(formatTimeDiff);
		assertEquals(expected, formatTimeDiff);
	}

}
