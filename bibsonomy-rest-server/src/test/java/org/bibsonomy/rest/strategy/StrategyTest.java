package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.junit.Test;

/**
 * @version $Id$
 */
public class StrategyTest {

	@Test
	public void testChooseGroupingEntity() {
		final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		final Context ctx = new Context(HttpMethod.GET, "/users/egal/posts", RenderingFormat.XML, null, null, null, parameterMap, null);

		parameterMap.put("user", new String[] { "" });
		assertEquals(GroupingEntity.USER, ctx.getStrategy().chooseGroupingEntity());

		parameterMap.clear();
		parameterMap.put("group", new String[] { "" });
		assertEquals(GroupingEntity.GROUP, ctx.getStrategy().chooseGroupingEntity());

		parameterMap.clear();
		parameterMap.put("viewable", new String[] { "" });
		assertEquals(GroupingEntity.VIEWABLE, ctx.getStrategy().chooseGroupingEntity());

		parameterMap.clear();
		parameterMap.put("friend", new String[] { "" });
		assertEquals(GroupingEntity.FRIEND, ctx.getStrategy().chooseGroupingEntity());

		parameterMap.clear();
		parameterMap.put("hurz", new String[] { "" });
		assertEquals(GroupingEntity.ALL, ctx.getStrategy().chooseGroupingEntity());
		parameterMap.clear();
		assertEquals(GroupingEntity.ALL, ctx.getStrategy().chooseGroupingEntity());
	}
}