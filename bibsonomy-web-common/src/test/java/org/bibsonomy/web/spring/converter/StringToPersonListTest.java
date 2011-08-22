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
 * @version $Id$
 */
public class StringToPersonListTest {
	
	@Test
	public void testUpperCase() throws SecurityException, NoSuchMethodException {
		final ConditionalGenericConverter converter = new PersonListToStringConverter();
		
		/*
		 * List<PersonName>
		 */
		final Method serializePersonNames = PersonNameUtils.class.getMethod("serializePersonNames",  List.class);
		final TypeDescriptor targetTypeDescriptor = new TypeDescriptor(new MethodParameter(serializePersonNames, 0));

		/*
		 * String
		 */
		final Method discoverPersonNames = PersonNameUtils.class.getMethod("discoverPersonNames",  String.class);
		final TypeDescriptor sourceTypeDescriptor = new TypeDescriptor(new MethodParameter(discoverPersonNames, 0));

		/*
		 * should match on String to List<PersonName> 
		 */
		assertTrue(converter.matches(sourceTypeDescriptor, targetTypeDescriptor));

		/*
		 * List<Repository>
		 */
		final Method setRepositorys = Post.class.getMethod("setRepositorys",  List.class);
		final TypeDescriptor wrongTargetTypeDescriptor = new TypeDescriptor(new MethodParameter(setRepositorys, 0));		

		/*
		 * should NOT match on String to List<Repository> 
		 */
		assertFalse(converter.matches(sourceTypeDescriptor, wrongTargetTypeDescriptor));
		
	}
}
