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
package org.bibsonomy.search.index.update.cris;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.bibsonomy.common.JobResult;
import org.bibsonomy.common.enums.Status;
import org.bibsonomy.database.managers.AbstractDatabaseManagerTest;
import org.bibsonomy.database.managers.CRISLinkDatabaseManager;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.User;
import org.bibsonomy.model.cris.CRISLink;
import org.bibsonomy.model.cris.Linkable;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.search.testutils.SearchSpringContextWrapper;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * tests for the {@link CRISLinkUpdateLogic} for {@link org.bibsonomy.model.cris.Project} and {@link org.bibsonomy.model.Person} links
 *
 * @author dzo
 */
public class CRISLinkUpdateLogicProjectPersonTest extends AbstractDatabaseManagerTest {

	private static final CRISLinkUpdateLogic UPDATE_LOGIC = SearchSpringContextWrapper.getBeanFactory().getBean("crisLinkProjectPersonSearchUpdateLogic", CRISLinkUpdateLogic.class);

	private static final CRISLinkDatabaseManager CRIS_LINK_DATABASE_MANAGER = testDatabaseContext.getBean(CRISLinkDatabaseManager.class);

	private static final String PROJECT_ID = "posts";
	private static final String PERSON_ID = "h.muller";

	@Test
	public void testGetNewerEntities() {
		final List<CRISLink> newerEntities = UPDATE_LOGIC.getNewerEntities(0, new Date(), 10, 0);
		assertThat(newerEntities.size(), is(1));

		final CRISLink crisLink = newerEntities.get(0);

		final Linkable source = crisLink.getSource();
		assertThat(source, instanceOf(Project.class));

		final Project project = (Project) source;
		assertThat(project.getExternalId(), is(PROJECT_ID));

		final Linkable target = crisLink.getTarget();
		assertThat(target, instanceOf(Person.class));

		final Person person = (Person) target;
		assertThat(person.getNames().size(), is(2));
		assertThat(person.getPersonId(), is(PERSON_ID));
	}

	@Test
	public void testGetDeletedEntities() {
		final Date lastLogDate = new Date();
		final List<CRISLink> deletedEntities = UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(deletedEntities.size(), is(0));

		final Project source = new Project();
		source.setExternalId("posts");
		final Person target = new Person();
		target.setPersonId(PERSON_ID);
		final JobResult jobResult = CRIS_LINK_DATABASE_MANAGER.deleteCRISLink(source, target, new User("testuser1"), this.dbSession);
		assertThat(jobResult.getStatus(), is(Status.OK));

		final List<CRISLink> afterDelete = UPDATE_LOGIC.getDeletedEntities(lastLogDate);
		assertThat(afterDelete.size(), is(1));

		final CRISLink deletedCRISLink = afterDelete.get(0);
		assertThat(deletedCRISLink.getId(), is(1));
	}
}