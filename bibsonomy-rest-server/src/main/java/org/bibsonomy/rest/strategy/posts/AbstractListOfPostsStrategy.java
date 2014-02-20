package org.bibsonomy.rest.strategy.posts;

import static org.bibsonomy.util.ValidationUtils.present;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.RESTUtils;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Jens Illig
 */
public abstract class AbstractListOfPostsStrategy extends AbstractGetListStrategy<List<? extends Post<? extends Resource>>> {
	protected final Class<? extends Resource> resourceType;
	protected final String hash;
	protected final GroupingEntity grouping;
	protected final String groupingValue;
	protected final String tagString;
	protected final List<String> tags;
	protected final String search;
	protected final Order order;
    protected final String sortKeys;
    protected final String sortOrders;
	
	/**
	 * @param context
	 */
	public AbstractListOfPostsStrategy(final Context context) {
		super(context);
		this.tagString = context.getStringAttribute(RESTConfig.TAGS_PARAM, null);
		this.resourceType = ResourceFactory.getResourceClass(context.getStringAttribute(RESTConfig.RESOURCE_TYPE_PARAM, ResourceFactory.RESOURCE_CLASS_NAME));
		this.hash = context.getStringAttribute(RESTConfig.RESOURCE_PARAM, null);
		this.search = context.getStringAttribute(RESTConfig.SEARCH_PARAM, null);
		this.order = context.getEnumAttribute(RESTConfig.ORDER_PARAM, Order.class, null);
        this.sortKeys = context.getStringAttribute(RESTConfig.SORTKEY_PARAM, null);
        this.sortOrders = context.getStringAttribute(RESTConfig.SORTORDER_PARAM, null);
		this.grouping = this.chooseGroupingEntity();
		this.tags = context.getTags(RESTConfig.TAGS_PARAM);
		String groupingValue;
		if (this.grouping != GroupingEntity.ALL) {
			groupingValue = context.getStringAttribute(this.grouping.toString().toLowerCase(), null);
			if (this.grouping == GroupingEntity.USER) {
				groupingValue = RESTUtils.normalizeUser(groupingValue, context);
			}
		} else {
			groupingValue = null;
		}
		
		this.groupingValue = groupingValue;
	}

	@Override
	protected void render(final Writer writer, final List<? extends Post<? extends Resource>> resultList) {
		this.getRenderer().serializePosts(writer, resultList, this.getView());
	}

	@Override
	protected abstract StringBuilder getLinkPrefix();

	@Override
	protected final String getContentType() {
		return "posts";
	}

	@Override
	protected void appendLinkPostFix(final StringBuilder sb) {
		// FIXME: urlencode
		if (this.resourceType != Resource.class) {
			sb.append("&").append(RESTConfig.RESOURCE_TYPE_PARAM).append("=").append(ResourceUtils.toString(this.resourceType).toLowerCase());
		}
		if (present(this.tagString)) {
			sb.append("&").append(RESTConfig.TAGS_PARAM).append("=").append(this.tagString);
		}
		if (present(this.hash)) {
			sb.append("&").append(RESTConfig.RESOURCE_PARAM).append("=").append(this.hash);
		}
		if ((this.grouping != GroupingEntity.ALL) && (present(this.groupingValue))) {
			sb.append('&').append(this.grouping.toString().toLowerCase()).append('=').append(this.groupingValue);
		}
		if (present(this.search)) {
			sb.append("&").append(RESTConfig.SEARCH_PARAM).append("=").append(this.search);
		}		
	}
}