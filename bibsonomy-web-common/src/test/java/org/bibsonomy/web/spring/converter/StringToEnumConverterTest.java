package org.bibsonomy.web.spring.converter;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.enums.UserRelation;
import org.bibsonomy.model.enums.Order;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 * @version $Id$
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
