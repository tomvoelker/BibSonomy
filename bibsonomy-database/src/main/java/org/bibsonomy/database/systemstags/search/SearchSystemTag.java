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
package org.bibsonomy.database.systemstags.search;

import org.bibsonomy.database.params.GenericParam;
import org.bibsonomy.database.systemstags.SystemTag;
import org.bibsonomy.model.Resource;


/**
 * @author dzo
 */
public interface SearchSystemTag extends SystemTag {

	/**
	 * Creates a new instance of this kind of SearchSystemTag
	 * @return a new instance of a {@link SearchSystemTag} tag
	 */
	public SearchSystemTag newInstance();

	/**
	 * Sets or changes fields in a param according to the systemTags function
	 * @param param 
	 */
	public void handleParam(GenericParam param);

	/**
	 * Indicates whether resources of type resourceType can be fetched using this Systemtag
	 * @param resourceClass
	 * @return <code>true</code> iff the system tag allows the resource
	 */
	public boolean allowsResource(Class<? extends Resource> resourceClass);
}
