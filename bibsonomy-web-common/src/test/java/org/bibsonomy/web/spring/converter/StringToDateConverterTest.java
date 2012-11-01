package org.bibsonomy.web.spring.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.convert.ConversionFailedException;

/**
 * @author dzo
 * @version $Id$
 */
public class StringToDateConverterTest {
	
	private static final StringToDateConverter CONVERTER = new StringToDateConverter();
	
	@BeforeClass
	public static void setupConverter() {
		CONVERTER.setFormats(Arrays.<DateTimeFormatter>asList(DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ"), DateTimeFormat.forPattern("yyyy-MM-dd")));
	}

	@Test
	public final void testConvert() {
		try {
			CONVERTER.convert("TodayIsSaturday");
			fail("missed conversion failed exception");
		} catch (final ConversionFailedException e) {
			// ignore
		}
		
		final Date date = CONVERTER.convert("2010-09-12");
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		assertEquals(2010, calendar.get(Calendar.YEAR));
		
		final Date secondDate = CONVERTER.convert("2012-01-06T11:12:42+0100");
		calendar.setTime(secondDate);
		assertEquals(42, calendar.get(Calendar.SECOND));
	}

}
