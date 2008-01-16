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
import org.bibsonomy.model.Post;

/**
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetBibtexForGroupAndTag extends BibTexChainElement {

	/**
	 * return a list of bibtex by a given group and common tags of a group.
	 */
	@Override
	protected List<Post<BibTex>> handle(final BibTexParam param, final DBSession session) {
		final Integer groupId = this.generalDb.getGroupIdByGroupName(param, session);
		if (groupId == GroupID.INVALID.getId()  || GroupID.isSpecialGroupId(groupId)) {
			log.debug("groupId " +  param.getRequestedGroupName() + " not found or special group" );
			return new ArrayList<Post<BibTex>>(0);			
		}
		param.setGroupId(groupId);	
		return this.db.getBibTexForGroupByTag(param, session);
	}

	@Override
	protected boolean canHandle(final BibTexParam param) {
		return (param.getGrouping() == GroupingEntity.GROUP) && present(param.getRequestedGroupName()) && !present(param.getRequestedUserName()) && present(param.getTagIndex())&& (param.getNumSimpleConcepts() == 0) && (param.getNumSimpleTags() > 0) && (param.getNumTransitiveConcepts() == 0) && !present(param.getHash()) && !present(param.getOrder()) && !present(param.getSearch());
	}
}