package org.bibsonomy.rest.strategy.tags;

import java.util.Arrays;
import java.util.List;

import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.Context;

/**
 * @author niebler
 */
public class GetTagRelationStrategy extends GetListOfTagsStrategy {
	
	protected List<String> tagList;
	protected TagRelation relation;

	/**
	 * 
	 * @param context the REST context.
	 * @param tags a list of requested tag names.
	 * @param relation the requested relation.
	 */
	public GetTagRelationStrategy(final Context context, final List<String> tags, final TagRelation relation) {
		super(context);
		
		this.tagList = tags;
		if (relation == null) {
			throw new RuntimeException("relation must not be null!");
		}
		this.relation = relation;
	}
	
	@Override
	protected final List<Tag> getList() {
		if (this.relation != null)
			switch (this.relation) {
				case RELATED:
					return this.handleRelated();
				case SIMILAR:
					return this.handleSimilar();
				case SUBTAGS:
					return this.handleSubtags();
				case SUPERTAGS:
					return this.handleSupertags();
			}
		
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tagList,
				hash, null, regex, null, this.getView().getOrder(), null, null,
				this.getView().getStartValue(), this.getView().getEndValue());
	}
	
	/**
	 * Handling of the request for related tags. Also possible for more than one tag.
	 * @return a list of tags which are related to tagList.
	 */
	private final List<Tag> handleRelated() {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tagList,
				hash, null, regex, TagSimilarity.COOC, this.getView().getOrder(),
				null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}

	/**
	 * Handling similar tags. Note that we cannot calculate similar tags for more than one tag!
	 * @return a list of similar tags.
	 */
	private final List<Tag> handleSimilar() {
		// TODO: Wird das schon in getTags abgehandelt?
		if (this.tagList.size() > 1 || this.tagList.isEmpty()) {
			return null;
		}
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tagList,
				hash, null, regex, TagSimilarity.COSINE, this.getView().getOrder(),
				null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}
	
	/**
	 * Handling subtags. Only applicable for one tag yet.
	 * 
	 * TODO: Think about a way of calculating subtags for more than one tag.
	 * 
	 * @return a list of subtags for the first tag in tagList.
	 */
	private final List<Tag> handleSubtags() {
		return this.getLogic().getTagDetails(tagList.get(0)).getSubTags();
	}
	
	private final List<Tag> handleSupertags() {
		return this.getLogic().getTagDetails(tagList.get(0)).getSuperTags();
	}
}
