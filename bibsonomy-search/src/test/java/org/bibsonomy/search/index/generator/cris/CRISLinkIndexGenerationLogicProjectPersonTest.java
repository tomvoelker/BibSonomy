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
package org.bibsonomy.search.index.generator.cris;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Test;

import java.util.List;

/**
 * tests for the generation logic of cris links that connect projects and persons
 *
 * @author dzo
 */
public class CRISLinkIndexGenerationLogicProjectPersonTest extends AbstractDatabaseManagerTest {

	private static final CRISLinkIndexGenerationLogic GENERATION_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("crisLinkProjectPersonGenerationLogic", CRISLinkIndexGenerationLogic.class);

	@Test
	public void testGetNumberOfEntities() {
		final int numberOfEntities = GENERATION_LOGIC.getNumberOfEntities();
		assertThat(numberOfEntities, is(1));
	}

	@Test
	public void testGetEntities() {
		final List<CRISLink> entities = GENERATION_LOGIC.getEntities(0, 10);

		assertThat(entities.size(), is(1));

		final CRISLink crisLink = entities.get(0);
		final Linkable target = crisLink.getTarget();
		assertThat(target, instanceOf(Person.class));

		final Person person = (Person) target;
		assertThat(person.getNames().size(), is(2));
		assertThat(person.getPersonId(), is("h.muller"));

		final Linkable source = crisLink.getSource();
		assertThat(source, instanceOf(Project.class));

		final Project project = (Project) source;
		assertThat(project.getExternalId(), is("posts"));
	}
}