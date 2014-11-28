/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.enums.Order;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 */
public class StringToEnumConverterTest {
	private static <E extends Enum<E>> Converter<String, E> createConverter(final Class<E> targetType) {
		return new StringToEnumConverter<E>(targetType);
	}
	
	@Test
	public void testUpperCase() {
		final Converter<String, Order> orderConverter = createConverter(Order.class);
		
		assertEquals(Order.ADDED, orderConverter.convert("ADDED"));
		assertEquals(Order.FOLKRANK, orderConverter.convert("FOLKRANK"));
		
		final Converter<String, UserRelation> userRelationConverter = createConverter(UserRelation.class);
		
		assertEquals(UserRelation.FOLKRANK, userRelationConverter.convert("FOLKRANK"));
		assertEquals(UserRelation.JACCARD, userRelationConverter.convert("JACCARD"));
		assertEquals(UserRelation.FRIEND_OF, userRelationConverter.convert("FRIEND_OF"));
	}
	
	@Test
	public void testLowerCase() {
		final Converter<String, Order> orderConverter = createConverter(Order.class);
		
		assertEquals(Order.ADDED, orderConverter.convert("added"));
		assertEquals(Order.FOLKRANK, orderConverter.convert("folkrank"));
		
		final Converter<String, UserRelation> userRelationConverter = createConverter(UserRelation.class);
		
		assertEquals(UserRelation.FOLKRANK, userRelationConverter.convert("folkrank"));
		assertEquals(UserRelation.JACCARD, userRelationConverter.convert("jaccard"));
		assertEquals(UserRelation.FRIEND_OF, userRelationConverter.convert("friend_of"));
	}
}
