package org.bibsonomy.database.managers.chain.bibtex.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.database.managers.chain.bibtex.BibTexChainElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;

/**
 * Returns a list of BibTex's for a given group (which is only viewable for
 * groupmembers excluded public option regarding setting a post).
 * 
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexViewable extends BibTexChainElement {

	@SuppressWarnings("hiding")
	private static final Log log = LogFactory.getLog(GetBibtexViewable.class);

	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		final Integer groupId = this.groupDb.getGroupIdByGroupNameAndUserName(param.getRequestedGroupName(), param.getUserName(), session);
		if (groupId == GroupID.INVALID.getId()) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found");
			return new ArrayList<Post<BibTex>>(0);
		}
		
		if (present(param.getTagIndex())) {
			return this.db.getPostsViewableByTag(groupId, param.getTagIndex(), HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), session);
		}
		
		return this.db.getPostsViewable(param.getRequestedGroupName(), param.getUserName(), groupId, HashID.getSimHash(param.getSimHash()), param.getLimit(), param.getOffset(), param.getSystemTags().values(), session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (present(param.getUserName()) &&
				!present(param.getBibtexKey()) &&
				param.getGrouping() == GroupingEntity.VIEWABLE &&
				present(param.getRequestedGroupName()) &&
				present(param.getRequestedUserName()) &&
				!present(param.getHash()) &&
				nullOrEqual(param.getOrder(), Order.ADDED) &&
				!present(param.getSearch()));
	}
}