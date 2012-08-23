package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * @author rja
 * @version $Id$
 */
public class PersonListToStringConverter implements ConditionalGenericConverter {


	@Override
	public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		final boolean a = String.class.equals(targetType.getObjectType());
		final boolean b = Collection.class.isAssignableFrom(sourceType.getObjectType());
		final boolean c = PersonName.class.equals(sourceType.getElementTypeDescriptor().getType());
		return a && b && c;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		if (!present(source)) {
			return null;
		}
		/*
		 * In the webapp, newline is used as person name delimiter. 
		 * Thus, we substitute the default delimiter (" and ") with "\n"
		 */
		return PersonNameUtils.serializePersonNames((List<PersonName>)source, "\n");
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(List.class, String.class));
	}

}
