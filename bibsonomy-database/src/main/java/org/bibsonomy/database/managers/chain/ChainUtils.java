/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.database.managers.chain;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.database.managers.PermissionDatabaseManager;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.database.systemstags.search.NotTagSystemTag;

/**
 * @author mba
 */
public class ChainUtils {

	public static boolean useResourceSearch(final GenericParam param) {
		final PermissionDatabaseManager pdm = PermissionDatabaseManager.getInstance();
		final List<TagIndex> tagIndex = param.getTagIndex();
	
		
		/*
		 * Are there Negation tags?
		 */
		boolean existsNegatedTags = false;
		for (final SystemTag sysTag: param.getSystemTags()) {
			if (sysTag instanceof NotTagSystemTag) {
				existsNegatedTags = true;
				break;
			}
		}
		
		/*
		 * Handle the request when:
		 * 1. There are TAGS in the query AND the lucene should be uses for the amount of tags
		 * OR
		 * 2. There are negated tags
		 */
		if ((present(tagIndex) && pdm.useResourceSearchForTagQuery(tagIndex.size())) ||	existsNegatedTags) {
			return true;
		}
		if ((param.getGrouping() == GroupingEntity.ALL) && (param.getNumSimpleConcepts() > 0)) {
			return true;
		}
		
		return false;
	}

}
