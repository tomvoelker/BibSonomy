/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.projects;

import java.io.Writer;

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.strategy.AbstractUpdateStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.ValidationUtils;

/**
 * strategy to update a project
 *
 * @author pda
 */
public class UpdateProjectStrategy extends AbstractUpdateStrategy {
	private final String externalId;

	public UpdateProjectStrategy(final Context context, final String externalId) {
		super(context);
		ValidationUtils.requirePresent(externalId, "No externalId given.");
		this.externalId = externalId;
	}

	@Override
	protected void render(final Writer writer, final String resourceID) {
		this.getRenderer().serializeProjectId(writer,resourceID);
	}

	@Override
	protected String update() {
		final Project project = this.getRenderer().parseProject(this.doc);
		this.getLogic().updateProject(this.externalId, project);
		return project.getExternalId();
	}
}
