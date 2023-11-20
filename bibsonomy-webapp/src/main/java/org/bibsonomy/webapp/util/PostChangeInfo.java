/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.util;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about changed posts.
 *
 * @author niebler
 */
@Getter
@Setter
public class PostChangeInfo {
	
	// FIXME: should be of type Set<Tag>
	/** the old tags before the post changes */
	private String oldTags;
	// FIXME: should be of type Set<Tag>
	/** the new tags after the post changes */
	private String newTags;

	/** flag, if posted has been checked/selected */
	private boolean checked;

	/** flag, if the post should be normalized */
	private boolean normalize;

	/** flag, if the visibility of the post should be updated */
	private boolean updateVisibility;

	/** flag, if the post should be deleted*/
	private boolean delete;

}
