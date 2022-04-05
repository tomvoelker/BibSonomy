/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.management.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.bibsonomy.model.Bookmark;
import org.junit.Test;

/**
 * 
 * Tests for {@link ElasticsearchUtils}
 *
 * @author dzo
 */
public class ElasticsearchUtilsTest {

	@Test
	public void testGetIndexName() throws Exception {
		assertThat(ElasticsearchUtils.getIndexName(new URI("http://www.bibsonomy.org/"), Bookmark.class), is("wwwbibsonomyorg_bookmark"));
	}

	@Test
	public void testEscapeQueryString() {
		String actual = ElasticsearchUtils.escapeQueryString("doi:10.18419/opus-11809");
		String expected = "doi\\:10.18419\\/opus\\-11809";
		assertEquals(expected, actual);
	}
}
