package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 * @version $Id$
 */
public class StringToURLConverter implements Converter<String, URL> {

	@Override
	public URL convert(final String source) {
		if (!present(source)) {
			return null;
		}
		
		try {
			// TODO: maybe here is the perfect place to call cleanUrl
			return new URL(source);
		} catch (final MalformedURLException ex) {
			throw new ConversionFailedException(TypeDescriptor.valueOf(String.class), TypeDescriptor.valueOf(URL.class), source, ex);
		}
	}

}
