package org.bibsonomy.database.managers.chain.tag.get;

import java.util.List;

import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;
/**
 * @author mwa
 * @version $Id$
 */
public class GetRelatedTagsByAuthorAndTags extends TagChainElement{
	
	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {
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
