package org.bibsonomy.rest.strategy.posts;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Jens Illig
 * @version $Id$
 */
public abstract class AbstractListOfPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {
	protected final Class<? extends Resource> resourceType;
	protected final String hash;
	protected final GroupingEntity grouping;
	protected final String groupingValue;
	protected final String tagString;
	protected final List<String> tags;
	protected final String search;
	
	/**
	 * @param context
	 */
	public AbstractListOfPostsStrategy(final Context context) {
		super(context);
		this.tagString = context.getStringAttribute("tags", null);
		this.resourceType = ResourceUtils.getResource(context.getStringAttribute("resourcetype", "all"));
		this.hash = context.getStringAttribute("resource", null);
		this.search = context.getStringAttribute("search", null);
		this.grouping = chooseGroupingEntity();
		this.tags = context.getTags("tags");
		if (grouping != GroupingEntity.ALL) {
			this.groupingValue = context.getStringAttribute(this.grouping.toString().toLowerCase(), null);
		} else {
			this.groupingValue = null;
		}
	}


	@Override
	protected void render(Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, getView());
	}

	//protected abstract List<? extends Post<? extends Resource>> getList(Class<? extends Resource> resourceType, GroupingEntity grouping, String groupingValue, List<String> tags, String hash, Object object, int start, int end);

	@Override
	protected abstract StringBuilder getLinkPrefix();

	@Override
	protected final String getContentType() {
		return "posts";
	}


	@Override
	protected void appendLinkPostFix(StringBuilder sb) {
		if (this.resourceType != Resource.class) {
			sb.append("&resourcetype=").append(ResourceUtils.toString(this.resourceType).toLowerCase());
		}
		if (this.tagString != null) {
			sb.append("&tags=").append(this.tagString);
		}
		if (this.hash != null) {
			sb.append("&resource=").append(this.hash);
		}
		if (this.grouping != GroupingEntity.ALL && this.groupingValue != null) {
			sb.append('&').append(this.grouping.toString().toLowerCase()).append('=').append(this.groupingValue);
		}
		if (this.search != "" && this.search != null) {
			sb.append("&search=").append(this.search);
		}		
	}
}