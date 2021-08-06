/**
 * BibSonomy-Web-Common - Common things for web
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

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
	public void testConvertModelClasses() {
		assertThat(STRING_TO_CLASS_CONVERTER.convert("User"), equalTo(User.class));
		assertThat(STRING_TO_CLASS_CONVERTER.convert("user"), equalTo(User.class));
	}
	
	@Test
	public void testConvertNull() {
		assertNull(STRING_TO_CLASS_CONVERTER.convert(null));
		assertNull(STRING_TO_CLASS_CONVERTER.convert("   "));
	}
}
