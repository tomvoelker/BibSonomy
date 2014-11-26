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
 * @author sdo
 * This SystemTag is used to create own tags that are hidden from other users
 * The tag is of the form sys:hidden:<MyHiddenArgument> where the sys|system prefix is optional
 * The argument cannot be empty
 */
public class HiddenTagSystemTag extends AbstractSystemTagImpl implements MarkUpSystemTag {

	public static final String NAME = "hidden";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public boolean isToHide() {
		return true;
	}


	@Override
	public HiddenTagSystemTag newInstance() {
		return new HiddenTagSystemTag();
	}

	@Override
	public boolean isInstance(final String tagName) {
		return SystemTagsUtil.hasTypeAndArgument(tagName) && NAME.equals(SystemTagsUtil.extractType(tagName));
	}

}