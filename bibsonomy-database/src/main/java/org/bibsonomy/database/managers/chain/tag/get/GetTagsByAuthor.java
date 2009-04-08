package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

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
		return this.db.getTagsAuthor(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (SearchEntity.AUTHOR.equals(param.getSearchEntity()) &&
				!present(param.getBibtexKey()) &&
				present(param.getSearch()));
	}
}