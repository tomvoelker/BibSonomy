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
