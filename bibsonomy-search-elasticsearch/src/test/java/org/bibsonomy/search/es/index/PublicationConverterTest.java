/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.search.es.index;

import static org.junit.Assert.assertFalse;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.index.utils.SimpleFileContentExtractorService;
import org.bibsonomy.testutil.TestUtils;
import org.junit.Test;

import java.util.Map;

/**
 * @author dzo
 */
public class PublicationConverterTest {
	private static final PublicationConverter CONVERTER = new PublicationConverter(TestUtils.createURI("https://www.bibsonomy.org"), new SimpleFileContentExtractorService());

	@Test
	public void testConvert() {
		final Post<BibTex> post = new Post<>();
		post.setUser(new User("testuser"));
		final BibTex publication = new BibTex();
		publication.setMisc("editors = { Wade Wilson and Vanessa Geraldine Carlysle }");
		post.setResource(publication);
		final Map<String, Object> convertedPost = CONVERTER.convert(post);
		assertFalse("converted post contains misc field", convertedPost.containsKey(ESConstants.Fields.Publication.EDITORS));
	}
}