package org.bibsonomy.search.es.search.util;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.common.enums.Prefix;
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
}