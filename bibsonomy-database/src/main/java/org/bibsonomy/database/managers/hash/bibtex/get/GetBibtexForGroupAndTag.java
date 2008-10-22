package org.bibsonomy.database.managers.hash.bibtex.get;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.hash.bibtex.BibTexHashElement;
import org.bibsonomy.database.params.BibTexParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;

/**
 * Returns a list of BibTex's for a given group and common tags of a group.
 * 
 * @author Andreas Koch
 * @version $Id$
 */
public class GetBibtexForGroupAndTag extends BibTexHashElement {

	public GetBibtexForGroupAndTag() {
		setTagIndex(true);
		setNumSimpleTagsOverNull(true);
		setGroupingEntity(GroupingEntity.GROUP);
	}

	/**
	 * return a list of bibtex by a given group and common tags of a group.
	 */
	@Override
	public List<Post<BibTex>> perform(final BibTexParam param, final DBSession session) {
		// final Integer groupId =
		// this.groupDb.getGroupIdByGroupName(param.getRequestedGroupName(),
		// session);
		final Group group = this.groupDb.getGroupByName(param.getRequestedGroupName(), session);
		if (group == null || group.getGroupId() == GroupID.INVALID.getId() || GroupID.isSpecialGroupId(group.getGroupId())) {
			log.debug("groupId " + param.getRequestedGroupName() + " not found or special group");
			return new ArrayList<Post<BibTex>>(0);
		}
		param.setGroupId(group.getGroupId());
		return this.db.getBibTexForGroupByTag(param, session);
	}
}