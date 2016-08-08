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
package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 */
public class GetUserPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	/** the requested user name */
	protected final String userName;
	private final List<String> tags;
	private final String tagString;
	private final String search;
	private final Class<? extends Resource> resourceType;

	/**
	 * @param context
	 * @param userName
	 */
	public GetUserPostsStrategy(final Context context, final String userName) {
		super(context);
		this.userName = userName;
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		this.tagString = context.getStringAttribute(RESTConfig.TAGS_PARAM, null);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
	}

	@Override
	protected void appendLinkPostFix(final StringBuilder sb) {
		if (this.tagString != null) {
			sb.append("&").append(RESTConfig.TAGS_PARAM).append("=").append(this.tagString);
		}
		if (this.resourceType != Resource.class) {
			sb.append("&").append(RESTConfig.RESOURCE_TYPE_PARAM).append("=").append(ResourceFactory.getResourceName(this.resourceType));
		}
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.getUrlRenderer().createHrefForUserPosts(this.userName));
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		// TODO: support other search types
		return this.getLogic().getPosts(this.resourceType, GroupingEntity.USER, this.userName, this.tags, null, this.search, SearchType.LOCAL, null, null, null, null, this.getView().getStartValue(),
				this.getView().getEndValue());
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	public String getContentType() {
		return "posts";
	}
}