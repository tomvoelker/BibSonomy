package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given search.
 * 
 * @author claus
 * @version $Id$
 */
public class GetBibtexSearchForGroup extends BibTexChainElement {

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupName(param.getRequestedGroupName(), session);
		
		if (groupId == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Post<BibTex>>(0);
		}
		param.setGroupId(groupId);
		
		return this.db.getPostsSearchForGroup(param.getGroupId(), param.getGroups(), param.getRawSearch(), param.getUserName(), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP &&
				SearchEntity.ALL.equals(param.getSearchEntity()) &&				
				!present(param.getBibtexKey()) &&
				present(param.getSearch()));
	}
}