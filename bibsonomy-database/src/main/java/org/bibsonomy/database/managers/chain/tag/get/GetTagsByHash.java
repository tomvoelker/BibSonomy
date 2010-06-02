package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * Retrieve tags by Hash
 * 
 * @author Dominik Benz
 * @version $Id$
 */
public class GetTagsByHash extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {		
		final int contentType = param.getContentType();
		if (contentType == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getTagsByBibtexHash(param.getUserName(), 
											   param.getHash(), 
											   HashID.getSimHash(param.getSimHash()), 
											   param.getGroups(), 
											   param.getLimit(), 
											   param.getOffset(), 
											   session);
		}
		
		if (contentType == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return this.db.getTagsByBookmarkHash(param.getUserName(), 
												 param.getHash(), 
												 param.getGroups(), 
												 param.getLimit(), 
												 param.getOffset(), 
												 session);
		}
		
		throw new UnsupportedResourceTypeException();
	}

	@Override
	protected boolean canHandle(final TagParam param) {
		return (present(param.getGrouping()) &&
				param.getGrouping() == GroupingEntity.ALL &&
				present(param.getHash()) &&
				!present(param.getBibtexKey()) &&
				!present(param.getRequestedUserName()));
	}
}