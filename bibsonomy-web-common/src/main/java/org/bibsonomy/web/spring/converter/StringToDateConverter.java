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

import static org.bibsonomy.util.ValidationUtils.present;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;


/**
 * Converter for converting strings to date format
 * 
 * @author dzo
 */
public class StringToDateConverter implements Converter<String, Date> {
	private static final Log log = LogFactory.getLog(StringToDateConverter.class);
	
	private List<DateTimeFormatter> formats;
	
	@Override
	public Date convert(final String source) {
		if (!present(source)) {
			return null;
		}
		
		/*
		 * loop through the provided formats
		 * the first format that can formats the string
		 * wins
		 */
		for (final DateTimeFormatter format : this.formats) {
			try {
				return format.parseDateTime(source).toDate();
			} catch (final Exception e) {
				log.debug("can't parse '" + source + "' with formatter " + format, e);
				// ignore try another one
			}
		}
		
		/*
		 * conversion failed
		 */
		throw new ConversionFailedException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(Date.class), source, new ParseException(source, 0));
	}
	
	/**
	 * @param formats the formats to set
	 */
	public void setFormats(final List<DateTimeFormatter> formats) {
		this.formats = formats;
	}
}
