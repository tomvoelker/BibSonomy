/**
 * BibSonomy-Web-Common - Common things for web
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
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
