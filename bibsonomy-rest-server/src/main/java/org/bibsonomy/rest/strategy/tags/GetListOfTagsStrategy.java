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
package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.util.UrlBuilder;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 */
public class GetListOfTagsStrategy extends AbstractGetListStrategy<List<Tag>> {
	/** the resource type */
	protected final Class<? extends Resource> resourceType;
	/** the grouping */
	protected final GroupingEntity grouping;
	/** the grouping value */
	protected final String groupingValue;
	/** the regex */
	protected final String regex;
	/** the hash */
	protected final String hash;
	
	/**
	 * @param context
	 */
	public GetListOfTagsStrategy(final Context context) {
		super(context);
		this.grouping = chooseGroupingEntity();
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.hash = context.getStringAttribute(RESTConfig.RESOURCE_PARAM, null);
		
		if (this.grouping != GroupingEntity.ALL) {
			this.groupingValue = context.getStringAttribute(this.grouping.toString().toLowerCase(), null);
		} else {
			this.groupingValue = null;
		}

		this.regex = context.getStringAttribute(RESTConfig.REGEX_PARAM, null);
	}

	@Override
	protected UrlBuilder getLinkPrefix() {
		return this.getUrlRenderer().createUrlBuilderForTags(this.resourceType, this.grouping, this.groupingValue, this.hash, this.regex, this.getView().getOrder());
	}

	@Override
	protected List<Tag> getList() {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, null, hash, null, regex, null, this.getView().getOrder(), null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}

	@Override
	protected void render(final Writer writer, final List<Tag> resultList) {
		this.getRenderer().serializeTags(writer, resultList, this.getView());
	}

	@Override
	protected String getContentType() {
		return "tags";
	}
}