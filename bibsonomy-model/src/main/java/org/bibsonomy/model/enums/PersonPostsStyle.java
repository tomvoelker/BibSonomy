/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model.enums;

/**
 * FIXME: rename! has nothing to do with the style of the site,
 * only the source what publications are display on the person page
 *
 * @author kchoong
 */
public enum PersonPostsStyle {
    GOLDSTANDARD(0),
    MYOWN(1);

    private final int value;

    PersonPostsStyle(int value) {
        this.value = value;
    }

    /**
     * @param pageType the pageType
     * @return
     */
    public static PersonPostsStyle valueOf(final int pageType) {
        for (final PersonPostsStyle personPostsStyle : PersonPostsStyle.values()) {
            if (personPostsStyle.getValue() == pageType) {
                return personPostsStyle;
            }
        }
        throw new IllegalArgumentException("no person posts style with id " + pageType);
    }

    /**
     * @return the value
     */
    public int getValue() {
        return value;
    }
}
