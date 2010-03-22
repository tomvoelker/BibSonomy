package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;
/**
 * @author mwa
 * @version $Id$
 */
public class GetRelatedTagsByAuthorAndTags extends TagChainElement{
	
	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {

		if (this.db.isDoLuceneSearch()) {
			// FIXME: which parameters do we actually need?
			return this.db.getTagsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), null, null, null, param.getSimHash(), null, session);
		}
		
		return this.db.getRelatedTagsByAuthorAndTag(param, session);
	}

	@Override
	protected boolean canHandle(TagParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity())) &&
		   present(param.getTagIndex()) &&
		   !present(param.getBibtexKey()) &&
		   present(param.getSearch()) &&
		   nullOrEqual(param.getOrder(), Order.ADDED);
	}
}
