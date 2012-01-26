package org.bibsonomy.webapp.util.tags;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * FIXME: English tests disabled, because they work only on systems having 
 * English as default language (not German). This is caused by a bug (IMO) of
 * Java's mechanism of determining the default language.
 * 
 * @author rja
 * @version $Id$
 */
@Ignore
public class TimeDiffFormatterTagTest {

	private static final String EN = "en";
	private static final String DE = "de";

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

	
	
	@Test
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
		
		final String formatTimeDiff = TimeDiffFormatterTag.formatTimeDiff(startDate, endDate, null, ms);
		System.out.println(formatTimeDiff);
		assertEquals(expected, formatTimeDiff);
	}

}
