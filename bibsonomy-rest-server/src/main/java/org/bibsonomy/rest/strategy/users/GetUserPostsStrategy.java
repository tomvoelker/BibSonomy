package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @version $Id$
 */
public class GetUserPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {

	private final String userName;
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
		final StringBuilder sb = new StringBuilder(this.getUrlRenderer().getApiUrl());
		sb.append(RESTConfig.USERS_URL).append("/").append(this.userName).append("/").append(RESTConfig.POSTS_URL); 
		return sb;
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		return this.getLogic().getPosts(this.resourceType, GroupingEntity.USER, this.userName, this.tags, null, this.search, null, null, null, null, this.getView().getStartValue(), this.getView().getEndValue());
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