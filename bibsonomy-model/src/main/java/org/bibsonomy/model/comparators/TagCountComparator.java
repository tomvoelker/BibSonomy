/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.comparators;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Comparator;

import org.bibsonomy.model.Tag;

/**
 * sorts tags according to their global count values
 * 
 * @author fei
 */
public class TagCountComparator implements Comparator<Tag> {

	/**
	 * compares two given tags based on the corresponding global counts
	 */
	@Override
	public int compare(Tag o1, Tag o2) {
		if( !present(o1) ) {
			if( present(o2) ) {
				return 1;
			}
				
			return 0;
		} else if( !present(o2) ) {
			return -1;
		} else {
			return o2.getGlobalcount() - o1.getGlobalcount();
		}
	}

}
