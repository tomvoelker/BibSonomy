package org.bibsonomy.webapp.util.tags;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.Locale;

import org.junit.Test;

/**
 * @author rja
 * @version $Id$
 */
public class TimeDiffFormatterTagTest {

	@Test
	public void testGetDateDiffSeconds() {
		checkTimeDiff(10, "about 10 seconds ago", "en");
	}
	@Test
	public void testGetDateDiffMinutes() {
		checkTimeDiff(60, "a minute ago", "en");
		checkTimeDiff(600, "10 minutes ago", "en");
	}
	@Test
	public void testGetDateDiffHours() {
		checkTimeDiff(60 * 60, "an hour ago", "en");
	}
	@Test
	public void testGetDateDiffDays() {
		checkTimeDiff(60 * 60 * 24, "a day ago", "en");
		checkTimeDiff(60 * 60 * 24 * 2, "2 days ago", "en");
		checkTimeDiff(60 * 60 * 24 * 7, "7 days ago", "en");
	}
	@Test
	public void testGetDateDiffMonths() {
		checkTimeDiff(60 * 60 * 24 * 30, "a month ago", "en");
		checkTimeDiff(60 * 60 * 24 * 7 * 30, "7 months ago", "en");
	}
	@Test
	public void testGetDateDiffYears() {
		checkTimeDiff(60 * 60 * 24 * 30 * 12, "a year ago", "en");
		checkTimeDiff(60 * 60 * 24 * 30 * 13, "a year and a month ago", "en");
		checkTimeDiff(60 * 60 * 24 * 30 * 12 * 7, "7 years ago", "en");
	}

	private void checkTimeDiff(final long seconds, final String expected, final String lang) {
		final long offset = 1000 * seconds;
		final Date startDate = new Date();
		final Date endDate = new Date(startDate.getTime() + offset);
		
		final String formatTimeDiff = TimeDiffFormatterTag.formatTimeDiff(startDate, endDate, new Locale(lang), null);
		System.out.println(formatTimeDiff);
		assertEquals(expected, formatTimeDiff);
	}

}
