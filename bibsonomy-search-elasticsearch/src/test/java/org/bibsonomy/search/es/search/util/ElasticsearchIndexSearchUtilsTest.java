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
package org.bibsonomy.search.es.search.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Set;

import org.bibsonomy.common.enums.Prefix;
import org.bibsonomy.search.es.index.generator.post.PostEntityInformationProvider;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;

/**
 * tests for {@link ElasticsearchIndexSearchUtils}
 */
public class ElasticsearchIndexSearchUtilsTest {

	@Test
	public void testGetPrefixForString() {
		assertThat(ElasticsearchIndexSearchUtils.getPrefixForString("50 cent"), is(Prefix.NUMBER));
		assertThat(ElasticsearchIndexSearchUtils.getPrefixForString("REGIO"), is(Prefix.R));
	}

	@Test
	public void testMultiMatch() {
		final String searchTerms = "elastic wave";
		Set<String> fields = PostEntityInformationProvider.PUBLIC_FIELDS;
		final MultiMatchQueryBuilder builder = QueryBuilders.multiMatchQuery(searchTerms)
				.operator(Operator.AND)
				.fuzziness("auto")
				.tieBreaker(1.0f);

		fields.forEach(builder::field);
		builder.toString();
	}
}