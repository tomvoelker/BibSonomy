package org.bibsonomy.rest.strategy.users;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.RestProperties;
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
		this.tags = context.getTags("tags");
		this.tagString = context.getStringAttribute("tags", null);
		this.search = context.getStringAttribute("search", null);
		this.resourceType = Resource.getResource(context.getStringAttribute("resourcetype", "all"));
	}

	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
		if (this.tagString != null) {
			sb.append("&tags=").append(this.tagString);
		}
		if (this.resourceType != Resource.class) {
			sb.append("&resourcetype=").append(Resource.toString(this.resourceType).toLowerCase());
		}
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		final StringBuilder sb = new StringBuilder( RestProperties.getInstance().getApiUrl() );
		sb.append( RestProperties.getInstance().getUsersUrl() ).append("/").append(this.userName).append("/").append(RestProperties.getInstance().getPostsUrl()); 
		return sb;
	}

	@Override
	protected List<? extends Post<? extends Resource>> getList() {
		return this.getLogic().getPosts(resourceType, GroupingEntity.USER, userName, this.tags, null, null, null, this.getView().getStartValue(), this.getView().getEndValue(), search);
	}

	@Override
	protected void render(Writer writer, List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	public String getContentType() {
		return "posts";
	}
}