/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.model.validation;

import static org.junit.Assert.assertEquals;

import org.bibsonomy.common.errors.ErrorMessage;
import org.bibsonomy.model.cris.Project;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * tests for {@link ProjectValidator}
 * @author dzo
 */
public class ProjectValidatorTest {

	private static final ProjectValidator VALIDATOR = new ProjectValidator();

	@Test
	public void testValidateProject() {
		final Project project = new Project();
		final float budget = 13000.45f;
		project.setBudget(budget);
		final Date startDate = new DateTime().withMillisOfSecond(0).toDate();
		project.setStartDate(startDate);
		final Date endDate = new DateTime(startDate.getTime() + 50 * 10000).withMillisOfSecond(0).toDate();

		project.setEndDate(endDate);
		final String projectTitle = "REGIO";
		project.setTitle(projectTitle);
		final String projectType = "BMBF";
		project.setType(projectType);
		final String internalId = "122323-2323";
		project.setInternalId(internalId);
		project.setExternalId("regio");

		final List<ErrorMessage> firstValidationResults = VALIDATOR.validateProject(project);
		assertEquals(0, firstValidationResults.size());

		project.setExternalId(null);
		final List<ErrorMessage> secondValidationResults = VALIDATOR.validateProject(project);
		assertEquals(1, secondValidationResults.size());

		project.setStartDate(null);
		final List<ErrorMessage> errorMessages3 = VALIDATOR.validateProject(project);
		assertEquals(2, errorMessages3.size());

		project.setEndDate(null);
		final List<ErrorMessage> errorMessages4 = VALIDATOR.validateProject(project);
		assertEquals(3, errorMessages4.size());

		project.setTitle(null);
		final List<ErrorMessage> errorMessages5 = VALIDATOR.validateProject(project);
		assertEquals(4, errorMessages5.size());
	}
}