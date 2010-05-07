package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.TagSimilarity;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @version $Id$
 */
public class GetSimilarTags extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) { 
		return this.db.getSimilarTags(param.getTagIndex(), param.getGroups(), param.getLimit(), param.getOffset(), session);
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (param.getGrouping() == GroupingEntity.ALL &&
				present(param.getTagIndex()) &&
				present(param.getTagRelationType()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getSearch()) &&
				!present(param.getTitle()) &&
				!present(param.getAuthor()) &&
				param.getTagRelationType() == TagSimilarity.COSINE);
	}
}