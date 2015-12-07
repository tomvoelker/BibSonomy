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
package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;
import org.bibsonomy.model.Resource;

/**
 * System tag for representing a tagged user relation, e.g., sys:relation:football
 * 
 * @author fmi
 */
public class UserRelationSystemTag extends AbstractSystemTagImpl implements SearchSystemTag {

	public static final String NAME = "relation";
	
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return true;
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasPrefixTypeAndArgument(tagName) && SystemTagsUtil.extractType(tagName).equals(this.getName());
	}

	//------------------------------------------------------------------------
	// SearchSystemTag interface
	//------------------------------------------------------------------------
	@Override
	public UserRelationSystemTag newInstance() {
		return new UserRelationSystemTag();
	}
	
	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}

	@Override
	public void handleParam(final GenericParam param) {
		final String tagName = SystemTagsUtil.buildSystemTagString(this.getName(), this.getArgument());
		param.addRelationTag(tagName);
	}

}
