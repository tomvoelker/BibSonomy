/**
 *
 *  BibSonomy-Web-Common - A blue social bookmark and publication sharing system.
 *
 *  Copyright (C) 2006 - 2013 Knowledge & Data Engineering Group,
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.web.spring.converter;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.util.PersonNameParser.PersonListParserException;
import org.bibsonomy.model.util.PersonNameUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * @author rja
 * @version $Id$
 */
public class StringToPersonListConverter implements ConditionalGenericConverter {

	@Override
	public boolean matches(final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		final boolean a = String.class.equals(sourceType.getObjectType());
		final boolean b = Collection.class.isAssignableFrom(targetType.getObjectType());
		final boolean c = PersonName.class.equals(targetType.getElementTypeDescriptor().getType());
		return a && b && c;
	}

	@Override
	public Object convert(final Object source, final TypeDescriptor sourceType, final TypeDescriptor targetType) {
		if (!present(source)) {
			return null;
		}
		/*
		 * In the webapp, newline is/can be used as person name delimiter. 
		 * Thus, we substitute it with the default delimiter (" and ").
		 */
		try {
			return PersonNameUtils.discoverPersonNames(((String)source).replaceAll("[\n\r]+", PersonNameUtils.PERSON_NAME_DELIMITER));
		} catch (final PersonListParserException e) {
			// FIXME: is this the best solution?
			return Collections.emptyList();
		}
	}

	@Override
	public Set<ConvertiblePair> getConvertibleTypes() {
		return Collections.singleton(new ConvertiblePair(String.class, List.class));
	}

}
