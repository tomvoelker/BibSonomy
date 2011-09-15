package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * @author rja
 * @version $Id$
 */
public class StringToPersonListConverter implements ConditionalGenericConverter {

	@Override
	public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
		final boolean a = String.class.equals(sourceType.getObjectType());
		final boolean b = Collection.class.isAssignableFrom(targetType.getObjectType());
		final boolean c = PersonName.class.equals(targetType.getElementType());
		return a && b && c;
	}

	@Override
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (!present(source)) return null;
		/*
		 * In the webapp, newline is/can be used as person name delimiter. 
		 * Thus, we substitute it with the default delimiter (" and ").
		 */
		
		try {
			return PersonNameUtils.discoverPersonNames(((String)source).replaceAll("[\n\r]+", PersonNameUtils.PERSON_NAME_DELIMITER));
		} catch (PersonListParserException e) {
			// FIXME: is this the best solution?
			return Collections.emptyList();
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, List.class));
	}

}
