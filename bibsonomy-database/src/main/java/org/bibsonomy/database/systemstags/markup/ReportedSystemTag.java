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
package org.bibsonomy.database.systemstags.markup;

import org.bibsonomy.database.systemstags.AbstractSystemTagImpl;
import org.bibsonomy.database.systemstags.SystemTagsUtil;

/**
 * @author dzo
 */
public class ReportedSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	/**
	 * the name of the report system tag
	 */
	public static final String NAME = "reported";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isInstance(String tagName) {
		return SystemTagsUtil.hasTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

	@Override
	public boolean isToHide() {
		return true;
	}

	@Override
	public MarkUpSystemTag newInstance() {
		try {
			return (MarkUpSystemTag) super.clone();
		} catch (CloneNotSupportedException ex) {
			// never ever reached
			return null;
		}
	}

}
