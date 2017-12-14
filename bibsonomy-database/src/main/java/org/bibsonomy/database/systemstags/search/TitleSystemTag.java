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
package org.bibsonomy.database.systemstags.search;

import static org.bibsonomy.util.ValidationUtils.present;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.TagUtils;

/**
 * @author sdo
 */
public class TitleSystemTag extends AbstractSearchSystemTagImpl {

	/**
	 * the name of the title system tag
	 */
	public static final String NAME = "title";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public TitleSystemTag newInstance() {
		return new TitleSystemTag();
	}

	@Override
	public boolean handleParam(final GenericParam param) {
		if (!present(param.getTitle())) {
			param.setTitle(this.getArgument());
		} else {
			// we append the new title part
			param.setTitle( param.getTitle() + TagUtils.getDefaultListDelimiter() + this.getArgument() );
		}
		param.setGrouping(GroupingEntity.ALL);
		log.debug("set title to " + param.getTitle() + " after matching for title system tag");
		return true;
	}
	
	@Override
	public boolean allowsResource(final Class<? extends Resource> resourceType) {
		return true;
	}
}
