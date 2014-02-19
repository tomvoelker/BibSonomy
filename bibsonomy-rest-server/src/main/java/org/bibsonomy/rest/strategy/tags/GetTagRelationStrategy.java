package org.bibsonomy.rest.strategy.tags;

import java.util.List;

import org.bibsonomy.common.enums.TagRelation;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.strategy.Context;

/**
 * Strategy for handling a request for related tags.
 * @author niebler
 */
public class GetTagRelationStrategy extends GetListOfTagsStrategy {
	
    /** The list of requested tags. */
	protected List<String> tags;
    /** The requested relation. */
	protected TagRelation relation;

	/**
	 * Creates the strategy object for handling a request for related tags.
	 * @param context the REST context.
	 * @param tags a list of requested tag names.
	 * @param relation the requested relation. This must not be null.
	 */
	public GetTagRelationStrategy(final Context context, final List<String> tags, final TagRelation relation) {
		super(context);
		
		this.tags = tags;
		if (relation == null) {
			throw new RuntimeException("relation must not be null!");
		}
		this.relation = relation;
	}
	
    /**
     * Returns a list of tags according to the requested relation. If <tt>relation</tt>
     * is something else than RELATED, SIMILAR, SUBTAGS, SUPERTAGS, all tags are
     * returned.
     * @return 
     */
	@Override
	protected final List<Tag> getList() {
        switch (this.relation) {
            case RELATED:
                return this.handleRelated();
            case SIMILAR:
                return this.handleSimilar();
            case SUBTAGS:
                return this.handleSubtags();
            case SUPERTAGS:
                return this.handleSupertags();
            default:
                return this.getLogic().getTags(resourceType, grouping, groupingValue, tags,
                        hash, null, regex, null, this.getView().getOrder(), null, null,
                        this.getView().getStartValue(), this.getView().getEndValue());
        }
	}
	
	/**
	 * Handling of the request for related tags. Also possible for more than one tag.
	 * @return a list of tags which are related to tagList.
	 */
	private List<Tag> handleRelated() {
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tags,
				hash, null, regex, TagSimilarity.COOC, this.getView().getOrder(),
				null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}

	/**
	 * Handling similar tags. Note that we cannot calculate similar tags for more than one tag!
	 * @return a list of similar tags.
	 */
	private List<Tag> handleSimilar() {
		if (this.tags.size() != 1) {
			return null;
		}
        
		return this.getLogic().getTags(resourceType, grouping, groupingValue, tags,
				hash, null, regex, TagSimilarity.COSINE, this.getView().getOrder(),
				null, null, this.getView().getStartValue(), this.getView().getEndValue());
	}
	
	/**
	 * Handling subtags. Only applicable for one tag yet.
	 * TODO: Think about a way of calculating subtags for more than one tag.
	 * @return a list of subtags for the first tag in tagList.
	 */
	private List<Tag> handleSubtags() {
		return this.getLogic().getTagDetails(tags.get(0)).getSubTags();
	}
	
    /**
     * Handle supertags. Only applicable for one tag yet.
     * TODO: Think about a way of calculating supertags for more than one tag.
     * @return a list of supertags for the first tag in tagList.
     */
	private List<Tag> handleSupertags() {
		return this.getLogic().getTagDetails(tags.get(0)).getSuperTags();
	}
}
