package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.database.util.DBSession;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
 * @version $Id$
 */
public class GetTagsByHashForUser extends TagChainElement {

	@Override
	protected List<Tag> handle(final TagParam param, final DBSession session) {		
		final int contentType = param.getContentType();
		if (contentType == ConstantID.BIBTEX_CONTENT_TYPE.getId()) {
			return this.db.getTagsByBibtexHashForUser(	param.getUserName(), 
														param.getRequestedUserName(), 
														param.getHash(), HashID.getSimHash(param.getSimHash()), 
														param.getGroups(),
														param.getLimit(), 
														param.getOffset(), 
														session);
		}
		
		if (contentType == ConstantID.BOOKMARK_CONTENT_TYPE.getId()) {
			return this.db.getTagsByBookmarkHashForUser(param.getUserName(), 
														param.getRequestedUserName(), 
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
				param.getGrouping() == GroupingEntity.USER &&
				present(param.getHash()) &&
				!present(param.getBibtexKey()) &&
				present(param.getRequestedUserName()));
	}
}