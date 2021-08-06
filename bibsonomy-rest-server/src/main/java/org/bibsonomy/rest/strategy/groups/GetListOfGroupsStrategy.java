/**
 * BibSonomy-Rest-Server - The REST-server.
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
package org.bibsonomy.rest.strategy.groups;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.model.Group;
import org.bibsonomy.model.logic.query.GroupQuery;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetListOfGroupsStrategy extends AbstractGetListStrategy<List<Group>> {
	private final String internalId;
	private final Boolean organization;

	/**
	 * @param context
	 */
	public GetListOfGroupsStrategy(final Context context) {
		super(context);
		this.internalId = context.getStringAttribute("internalId", null);
		final String organizationFilter = context.getStringAttribute(RESTConfig.ORGANIZATION_PARAM, null);
		if (present(organizationFilter)) {
			this.organization = Boolean.parseBoolean(organizationFilter);
		} else {
			this.organization = null; // no filter set groups can be also organizations
		}
	}

	@Override
	public String getContentType() {
		return "groups";
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().getUrlBuilderForGroups().addParameter(RESTConfig.ORGANIZATION_PARAM, String.valueOf(this.organization));
	}

	@Override
	protected List<Group> getList() {
		final ViewModel view = this.getView();
		final GroupQuery groupQuery = GroupQuery.builder()
						.start(view.getStartValue())
						.end(view.getEndValue())
						.pending(false)
						.organization(this.organization)
						.externalId(this.internalId).build();
		return this.getLogic().getGroups(groupQuery);
	}

	@Override
	protected void render(final Writer writer, final List<Group> resultList) {
		this.getRenderer().serializeGroups(writer, resultList, getView());
	}
}