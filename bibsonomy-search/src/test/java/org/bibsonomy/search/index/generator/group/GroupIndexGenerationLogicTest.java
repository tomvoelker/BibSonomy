/**
 * BibSonomy Search - Helper classes for search modules.
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
package org.bibsonomy.search.index.generator.group;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Group;
import org.bibsonomy.search.index.generator.GeneralIndexGenerationLogic;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

/**
 * tests for {@link GeneralIndexGenerationLogic} for {@link Group}s
 *
 * @author dzo
 */
public class GroupIndexGenerationLogicTest extends AbstractDatabaseManagerTest {

	private static final GeneralIndexGenerationLogic<Group> GROUP_INDEX_GENERATION_LOGIC = (GeneralIndexGenerationLogic<Group>) SearchSpringContextWrapper.getBeanFactory().getBean("groupGenerationDBLogic");

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = GROUP_INDEX_GENERATION_LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(8));
	}

	@Test
	public void testGetEntities() {
		final List<Group> entities = GROUP_INDEX_GENERATION_LOGIC.getEntities(0, 10);
		assertThat(entities.size(), is(8));

		final Group group = entities.get(2);
		assertThat(group.getName(), is("testgroup3"));
	}
}
