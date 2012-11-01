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
 * @version $Id$
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
