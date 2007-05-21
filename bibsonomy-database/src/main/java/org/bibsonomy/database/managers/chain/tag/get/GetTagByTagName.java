package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.Transaction;
import org.bibsonomy.model.Tag;

/**
 * @author dbenz
 *
 */
public class GetTagByTagName extends TagChainElement {

	/* 
	 * This class an handle the request if the tag name is set and not sub- or supertags are included
	 * 
	 * (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElementForTag#canHandle(java.lang.String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean, int, int, org.bibsonomy.database.util.Transaction)
	 */
	@Override
	protected boolean canHandle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, String tagName, int start, int end, final Transaction session) {		
		return  tagName != null && tagName != "" && subTags != null && superTags != null && subTags == false && superTags == false;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.database.managers.chain.ChainElementForTag#handle(java.lang.String, org.bibsonomy.common.enums.GroupingEntity, java.lang.String, java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.Boolean, int, int, org.bibsonomy.database.util.Transaction)
	 */
	@Override
	protected List<Tag> handle(String authUser, GroupingEntity grouping, String groupingName, String regex, Boolean subTags, Boolean superTags, Boolean subSuperTagsTransitive, String tagName, int start, int end, final Transaction session) {

		final TagParam param = new TagParam();
		param.setName(tagName);
				
		List<Tag> tags = db.getTagByTagName(param, session);
		if (tags.size() != 0) {
			System.out.println("GetTagByTagName");
		}
		return tags;				
	}
}
