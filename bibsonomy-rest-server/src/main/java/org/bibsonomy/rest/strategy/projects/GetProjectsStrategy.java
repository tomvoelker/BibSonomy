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

import org.bibsonomy.model.cris.Project;
import org.bibsonomy.model.logic.query.ProjectQuery;
import org.bibsonomy.model.logic.querybuilder.ProjectQueryBuilder;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

import java.io.Writer;
import java.util.List;

/**
 * endpoint to query for projects
 *
 * @author pda
 */
public class GetProjectsStrategy extends AbstractGetListStrategy<List<Project>> {
	private final String internalId;

	/**
	 * default constructor
	 * @param context
	 */
	public GetProjectsStrategy(final Context context) {
		super(context);
		internalId = context.getStringAttribute("internalId", null);
	}

	@Override
	protected String getContentType() {
		return "projects";
	}

	@Override
	protected void render(final Writer writer, final List<Project> resultList) {
		this.getRenderer().serializeProjects(writer, resultList, this.getView());
	}

	@Override
	protected List<Project> getList() {
		final ViewModel viewModel = this.getView();
		final ProjectQuery projectQuery = new ProjectQueryBuilder()
				.internalId(this.internalId)
				.start(viewModel.getStartValue())
				.end(viewModel.getEndValue())
				.build();
		return this.getLogic().getProjects(projectQuery);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForProjects();
	}
}
