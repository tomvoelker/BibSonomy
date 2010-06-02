package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.nullOrEqual;
import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.enums.Order;

/**
 * @author Christian Kramer
 * @version $Id$
 */
public class GetRelatedTags extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {
		if (Order.FOLKRANK.equals(param.getOrder())) return this.db.getRelatedTagsOrderedByFolkrank(param, session);
		return this.db.getRelatedTags(param, session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTagIndex()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				nullOrEqual(param.getTagRelationType(), TagSimilarity.COOC));
	}
}