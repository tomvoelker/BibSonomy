package org.bibsonomy.web.spring.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.junit.Test;
import org.springframework.core.convert.converter.Converter;

/**
 * @author dzo
 * @version $Id$
 */
public class StringToClassConverterTest {
	
	private static final Converter<String, Class<?>> STRING_TO_CLASS_CONVERTER = new StringToClassConverter();
	
	@Test
	public void testConvertResourceClassNames() {
		assertEquals(BibTex.class, STRING_TO_CLASS_CONVERTER.convert("bibtex"));
		assertEquals(BibTex.class, STRING_TO_CLASS_CONVERTER.convert("publication"));
		assertEquals(Bookmark.class, STRING_TO_CLASS_CONVERTER.convert("bookmark"));
	}
	
	@Test
	public void testConvertNormalClassNames() {
		assertEquals(User.class, STRING_TO_CLASS_CONVERTER.convert("org.bibsonomy.model.User"));
		assertEquals(Post.class, STRING_TO_CLASS_CONVERTER.convert("org.bibsonomy.model.Post"));
	}
	
	@Test
	public void testConvertNull() {
		assertNull(STRING_TO_CLASS_CONVERTER.convert(null));
		assertNull(STRING_TO_CLASS_CONVERTER.convert("   "));
	}
}
