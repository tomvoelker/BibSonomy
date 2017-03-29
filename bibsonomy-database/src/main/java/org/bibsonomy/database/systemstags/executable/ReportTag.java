/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.systemstags.executable;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.database.systemstags.markup.ReportedSystemTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.services.information.InformationService;

/**
 * @author dzo
 */
public class ReportTag extends ForGroupTag {
	private static final String NAME = "report";
	
	private List<InformationService> services = Collections.emptyList();
	
	@Override
	protected <T extends Resource> boolean copyPostToGroup(Post<T> userPost, Set<Tag> userTags, DBSession session) {
		final boolean createdPost = super.copyPostToGroup(userPost, userTags, session);
		final String groupName = this.getArgument();
		// rename the tag report:<GROUP_NAME> to reported:<GROUP_NAME>
		Iterator<Tag> tagIterator = userTags.iterator();
		while (tagIterator.hasNext()) {
			final Tag tag = tagIterator.next();
			final String tagName = tag.getName();
			if (this.isInstance(tagName)) {
				if (groupName.equalsIgnoreCase(SystemTagsUtil.extractArgument(tagName))) {
					tagIterator.remove();
				}
			}
		}
		userTags.add(new Tag(ReportedSystemTag.NAME + SystemTagsUtil.DELIM + groupName));
		if (createdPost) {
			
			/*
			 * inform the user about the reported publication
			 */
			for (final InformationService service : this.services) {
				service.createdPost(groupName, userPost);
			}
		}
		
		return createdPost;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public ExecutableSystemTag clone() {
		return super.clone();
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(List<InformationService> services) {
		this.services = services;
	}
}
