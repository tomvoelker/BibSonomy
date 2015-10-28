package org.bibsonomy.search.es.management.util;

import static org.junit.Assert.assertEquals;

import java.net.URI;

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
		assertEquals("wwwbibsonomyorg_bookmark", ElasticsearchUtils.getIndexName(new URI("http://www.bibsonomy.org/"), Bookmark.class));
	}
}
