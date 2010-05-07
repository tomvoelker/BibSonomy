package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

/**
 * @author Stefan Stützer
 * @version $Id$
 */
public class GetPopularTags extends TagChainElement {
	
	@Override
	protected List<Tag> handle(TagParam param, DBSession session) {
		return this.db.getPopularTags(param, session);
	}
	
	@Override
	protected boolean canHandle(TagParam param) {
		return (GroupingEntity.ALL.equals(param.getGrouping()) && 
				Order.POPULAR.equals(param.getOrder()) &&
				!present(param.getRegex()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getHash()));
	}

}
