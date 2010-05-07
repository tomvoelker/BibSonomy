package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given group and common tags of a group.
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForGroupAndTag extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		// final Integer groupId = this.groupDb.getGroupIdByGroupName(param.getRequestedGroupName(), session);
		final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
		if (group == null || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Post<BibTex>>(0);
		}
		return this.db.getPostsForGroupByTag(group.getGroupId(), param.getGroups(), param.getUserName(), param.getTagIndex(), param.getFilter(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP &&
				!present(param.getBibtexKey()) &&
				present(param.getRequestedGroupName()) &&
				!present(param.getRequestedUserName()) &&
				present(param.getTagIndex()) &&
				param.getNumSimpleConcepts() == 0 &&
				param.getNumSimpleTags() > 0 &&
				param.getNumTransitiveConcepts() == 0 &&
				!present(param.getHash()) &&
				!present(param.getOrder()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()));
	}
}