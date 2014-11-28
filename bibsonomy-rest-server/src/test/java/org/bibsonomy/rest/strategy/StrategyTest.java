/**
 * BibSonomy-Rest-Server - The REST-server.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.rest.strategy;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.rest.enums.HttpMethod;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;
import org.junit.Test;

/**
 */
public class StrategyTest {

	@Test
	public void testChooseGroupingEntity() {
		final Map<String, String[]> parameterMap = new HashMap<String, String[]>();
		final Context ctx = new Context(HttpMethod.GET, "/api/users/egal/posts", RenderingFormat.XML, new RendererFactory(new UrlRenderer("/")), null, null, null, null, parameterMap, null);

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