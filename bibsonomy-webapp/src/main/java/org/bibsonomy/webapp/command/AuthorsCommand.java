/**
 * BibSonomy-Webapp - The web application for BibSonomy.
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
package org.bibsonomy.webapp.command;

import java.util.ArrayList;
import java.util.List;

import org.bibsonomy.common.enums.TagCloudSort;
import org.bibsonomy.common.enums.TagCloudStyle;
import org.bibsonomy.model.Author;

/**
 * @author Christian Claus
 */
public class AuthorsCommand extends BaseCommand {	
	private static final int DEFAULT_MAX_FREQ = 100;
	
	
	private List<Author> authorList = new ArrayList<Author>();
	private TagCloudStyle style = TagCloudStyle.CLOUD;
	private TagCloudSort sort = TagCloudSort.ALPHA;
	private int minFreq	 = 0;
	private int maxFreq	 = DEFAULT_MAX_FREQ;
	private int maxCount = 0;
	
	
	/**
	 * @return the authorList
	 */
	public List<Author> getAuthorList() {
		return this.authorList;
	}
	
	private void calculateMaxAuthorCount() {
		for (final Author a : authorList) {
			if (a.getCtr() > maxCount) {
				maxCount = a.getCtr();
			}
		}
	}
	
	/**
	 * @param authorList the authorList to set
	 */
	public void setAuthorList(final List<Author> authorList) {
		this.authorList = authorList;
		this.calculateMaxAuthorCount();
	}

	/**
	 * @return the style
	 */
	public TagCloudStyle getStyle() {
		return this.style;
	}

	/**
	 * @param style the style to set
	 */
	public void setStyle(final TagCloudStyle style) {
		this.style = style;
	}

	/**
	 * @return the sort
	 */
	public TagCloudSort getSort() {
		return this.sort;
	}

	/**
	 * @param sort the sort to set
	 */
	public void setSort(final TagCloudSort sort) {
		this.sort = sort;
	}

	/**
	 * @return the minFreq
	 */
	public int getMinFreq() {
		return this.minFreq;
	}

	/**
	 * @param minFreq the minFreq to set
	 */
	public void setMinFreq(final int minFreq) {
		this.minFreq = minFreq;
	}

	/**
	 * @return the maxFreq
	 */
	public int getMaxFreq() {
		return this.maxFreq;
	}

	/**
	 * @param maxFreq the maxFreq to set
	 */
	public void setMaxFreq(final int maxFreq) {
		this.maxFreq = maxFreq;
	}

	/**
	 * @return the maxCount
	 */
	public int getMaxCount() {
		return this.maxCount;
	}

	/**
	 * @param maxCount the maxCount to set
	 */
	public void setMaxCount(final int maxCount) {
		this.maxCount = maxCount;
	}
}
