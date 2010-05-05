package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags for a given author.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByAuthor extends TagChainElement {
	
	
	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		
		if (this.db.isDoLuceneSearch()) {
			// FIXME: which parameters do we actually need?
			return this.db.getTagsByAuthorLucene(param.getRawSearch(), GroupID.PUBLIC.getId(), param.getRequestedUserName(), param.getRequestedGroupName(), null, null, null, param.getSimHash(), null, param.getLimit(), session);
		}
		
		return this.db.getTagsByAuthor(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity()) &&
		        ( (param.getGrouping() == GroupingEntity.ALL  )|| 
		          (param.getGrouping() == GroupingEntity.GROUP && present(param.getRequestedGroupName())) ) &&
				!present(param.getTagIndex()) &&
				!present(param.getBibtexKey()) &&
				present(param.getSearch()));
	}
}