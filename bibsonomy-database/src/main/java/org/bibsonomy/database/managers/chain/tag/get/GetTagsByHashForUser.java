/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.database.managers.chain.tag.get;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.HashID;
import org.bibsonomy.common.exceptions.UnsupportedResourceTypeException;
import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.common.enums.ConstantID;
import org.bibsonomy.database.managers.chain.tag.TagChainElement;
import org.bibsonomy.database.params.TagParam;
import org.bibsonomy.model.Tag;

/**
 * @author Dominik Benz
 * @author Miranda Grahl
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