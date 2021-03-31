/**
 * BibSonomy-Model - Java- and JAXB-Model.
 *
 * Copyright (C) 2006 - 2021 Knowledge & Data Engineering Group,
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

package org.bibsonomy.model.enums;

import java.util.HashMap;
import java.util.Map;

public enum PersonPostsStyle {
    GOLDSTANDARD(0),
    MYOWN(1);

    private final int value;
    private static Map<Integer, PersonPostsStyle> map;

    private PersonPostsStyle(int value) {
        this.value = value;
    }

    static {
        map = new HashMap<>();
        for (PersonPostsStyle style : PersonPostsStyle.values()) {
            map.put(style.value, style);
        }
    }

    public static PersonPostsStyle valueOf(int pageType) {
        return map.get(pageType);
    }

    public int getValue() {
        return value;
    }

}
