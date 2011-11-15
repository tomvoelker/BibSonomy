package org.bibsonomy.rest.strategy.tags;

import java.io.Writer;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.model.util.ResourceUtils;
import org.bibsonomy.rest.RESTConfig;
import org.bibsonomy.rest.strategy.AbstractGetListStrategy;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author Manuel Bork <manuel.bork@uni-kassel.de>
 * @author Christian Kramer
 * @version $Id$
 */
public class GetListOfTagsStrategy extends AbstractGetListStrategy<List<Tag>> {
	protected final Class<? extends Resource> resourceType;
	private final GroupingEntity grouping;
	private final String groupingValue;
	private final String regex;
	private final String hash;
	
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
	protected void appendLinkPostFix(final StringBuilder sb) {
		if (grouping != GroupingEntity.ALL && groupingValue != null) {
			sb.append("&").append(grouping.toString().toLowerCase()).append("=").append(groupingValue);
		}
		if (regex != null) {
			sb.append("&").append(RESTConfig.REGEX_PARAM).append("=").append(regex);
		}
		if (this.getView().getOrder() == Order.FREQUENCY) {
			sb.append("&").append(RESTConfig.ORDER_PARAM).append("=").append(this.getView().getOrder().toString().toLowerCase());
		}
		if (resourceType != Resource.class) {
			sb.append("&").append(RESTConfig.RESOURCE_TYPE_PARAM).append("=").append(ResourceUtils.toString(this.resourceType).toLowerCase());
		}
		if (hash != null) {
			sb.append("&").append(RESTConfig.RESOURCE_PARAM).append("=").append(hash);
		}		
	}

	@Override
	protected StringBuilder getLinkPrefix() {
		return new StringBuilder(this.getUrlRenderer().getApiUrl()).append(RESTConfig.TAGS_URL);
	}

	@Override
	protected List<Tag> getList() {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, regex, null, hash, this.getView().getOrder(), this.getView().getStartValue(), this.getView().getEndValue(), null, null);
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