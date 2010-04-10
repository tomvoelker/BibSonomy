package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchEntity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * Returns a list of tags.
 * 
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsBySearchString extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		return this.db.getTagsBySearchString(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return ( present(param.getGrouping())                      &&
				 param.getGrouping() == GroupingEntity.ALL         &&
			     nullOrEqual(param.getSearchEntity(), SearchEntity.ALL) &&
				 present(param.getSearch())                        &&
				!present(param.getRegex())                         &&
				!present(param.getTagIndex())                      &&
				!present(param.getHash())                          &&
		//  	!present(param.getTagRelationType())       &&
				!present(param.getBibtexKey()) );
	}
}