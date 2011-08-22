package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.springframework.core.convert.converter.Converter;

/**
 * @author rja
 * @version $Id$
 */
public class StringToPersonListConverter implements Converter<String, List<PersonName>> {

	@Override
	public List<PersonName> convert(final String source) {
		if (!present(source)) return null;
		/*
		 * In the webapp, newline is/can be used as person name delimiter. 
		 * Thus, we substitute it with the default delimiter (" and ").
		 */
		
		return PersonNameUtils.discoverPersonNames(source.replaceAll("[\n\r]+", PersonNameUtils.PERSON_NAME_DELIMITER));
	}

}
