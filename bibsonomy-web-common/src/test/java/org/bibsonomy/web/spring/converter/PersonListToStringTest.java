/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.util.PersonNameUtils;
import org.junit.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;

/**
 * @author rja
 */
public class PersonListToStringTest {
	
	@Test
	public void testUpperCase() throws SecurityException, NoSuchMethodException {
		final ConditionalGenericConverter converter = new PersonListToStringConverter();
		
		/*
		 * List<PersonName>
		 */
		final Method serializePersonNames = PersonNameUtils.class.getMethod("serializePersonNames",  List.class);
		final TypeDescriptor sourceTypeDescriptor = new TypeDescriptor(new MethodParameter(serializePersonNames, 0));

		/*
		 * String
		 */
		final Method discoverPersonNames = PersonNameUtils.class.getMethod("discoverPersonNames",  String.class);
		final TypeDescriptor targetTypeDescriptor = new TypeDescriptor(new MethodParameter(discoverPersonNames, 0));

		/*
		 * should match on List<PersonName> to String
		 */
		assertTrue(converter.matches(sourceTypeDescriptor, targetTypeDescriptor));

		/*
		 * List<Repository>
		 */
		final Method setRepositorys = Post.class.getMethod("setRepositorys",  List.class);
		final TypeDescriptor wrongSourceTypeDescriptor = new TypeDescriptor(new MethodParameter(setRepositorys, 0));		

		/*
		 * should NOT match on List<Repository> to String
		 */
		assertFalse(converter.matches(wrongSourceTypeDescriptor, targetTypeDescriptor));
		
	}
}
