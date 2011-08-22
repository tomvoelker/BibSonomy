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
public class PersonListToStringConverter implements Converter<List<PersonName>, String> {

	@Override
	public String convert(final List<PersonName> source) {
		if (!present(source)) return null;
		/*
		 * In the webapp, newline is used as person name delimiter. 
		 * Thus, we substitute the default delimiter (" and ") with "\n"
		 */
		return PersonNameUtils.serializePersonNames(source, "\n");
	}

}
