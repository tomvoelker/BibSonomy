package org.bibsonomy.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * tests for sort utils
 */
public class SortUtilsTest {

	@Test
	public void testGetSortKeysAsString() {
		final List<SortCriteria> sortCriteriaList = new LinkedList<>();
		sortCriteriaList.add(new SortCriteria(SortKey.ALPH, SortOrder.ASC));
		sortCriteriaList.add(new SortCriteria(SortKey.EDITOR, SortOrder.DESC));

		final String sortKeysAsString = SortUtils.getSortKeysAsString(sortCriteriaList);
		assertThat(sortKeysAsString, is("ALPH|EDITOR"));
	}

}