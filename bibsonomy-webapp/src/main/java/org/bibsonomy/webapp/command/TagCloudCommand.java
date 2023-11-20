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
/*
 * Created on 14.10.2007
 */
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.Tag;

/**
 * bean for displaying a tag cloud
 * 
 * @author Dominik Benz
 */
@Getter
@Setter
public class TagCloudCommand extends BaseCommand {
	/** list of contained tags */
	private List<Tag> tags = new ArrayList<Tag>();

	/** threshold which tags to display */
	private int minFreq = 0;

	/** maximum occurrence frequency of all tags */
	private int maxFreq = 100;

	/**  used for set the value via URL */
	private int maxCount = 0;

	/** display mode */
	private TagCloudStyle style = TagCloudStyle.CLOUD;

	/** sorting mode */
	private TagCloudSort sort = TagCloudSort.ALPHA;
	private int maxTagCount = Integer.MIN_VALUE;
	private int minTagCount = Integer.MAX_VALUE;
	private int maxUserTagCount = Integer.MIN_VALUE;
	private int minUserTagCount = Integer.MAX_VALUE;
	
	/**
	 * find the max Tag Count
	 */
	private void calculateMinMaxTagCount() {
		maxTagCount = Integer.MIN_VALUE;
		maxUserTagCount = Integer.MIN_VALUE;
		for (final Tag tag : tags) {
			if (tag.getGlobalcount() > maxTagCount) {
				maxTagCount = tag.getGlobalcount();
			}
			if (tag.getUsercount() > maxUserTagCount) {
				maxUserTagCount = tag.getUsercount();
			}
			if (tag.getGlobalcount() < minTagCount) {
				minTagCount = tag.getGlobalcount();
			}
			if (tag.getUsercount() < minUserTagCount) {
				minUserTagCount = tag.getUsercount();
			}			
		}
	}

	/**
	 * @param tags a list of tags
	 */
	public void setTags(final List<Tag> tags) {
		this.tags = tags;
		calculateMinMaxTagCount();
	}
		
}
