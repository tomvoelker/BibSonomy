/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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
package org.bibsonomy.database.plugin.plugins;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.params.discussion.DiscussionItemParam;
import org.bibsonomy.database.plugin.AbstractDatabasePlugin;
import org.bibsonomy.model.DiscussionItem;

/**
 * @author dzo
 */
public class DiscussionPlugin extends AbstractDatabasePlugin {
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.database.plugin.AbstractDatabasePlugin#onCommentUpdate(java.lang.String, org.bibsonomy.model.Comment, org.bibsonomy.model.Comment, org.bibsonomy.database.common.DBSession)
	 */
	@Override
	public void onDiscussionUpdate(final String interHash, final DiscussionItem discussionItem, final DiscussionItem oldDiscussionItem, final DBSession session) {
		final DiscussionItemParam<DiscussionItem> param = new DiscussionItemParam<>();
		param.setInterHash(interHash);
		param.setOldParentHash(oldDiscussionItem.getHash());
		param.setNewParentHash(discussionItem.getHash());
		
		this.update("updateParentHash", param, session);
	}
}
