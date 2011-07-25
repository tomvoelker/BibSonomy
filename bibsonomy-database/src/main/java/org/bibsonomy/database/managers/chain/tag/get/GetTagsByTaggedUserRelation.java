package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.search.UserRelationSystemTag;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given tagged user relation of a given user.
 * 
 * @author fmi
 */
public class GetTagsByTaggedUserRelation extends TagChainElement {
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsByTaggedUserRelation(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.FRIEND &&
				present(param.getRequestedUserName()) && 
				// discriminate from the friendOf and ofFriend queries
				present(param.getRelationTags()) &&
				SystemTagsUtil.containsSystemTag(param.getRelationTags(), UserRelationSystemTag.NAME) &&
				!present(param.getRegex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getHash()));
	}
}