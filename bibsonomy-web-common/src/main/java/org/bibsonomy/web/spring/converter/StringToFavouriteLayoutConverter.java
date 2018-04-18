/**
 * BibSonomy-Web-Common - Common things for web
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
package org.bibsonomy.web.spring.converter;

import org.bibsonomy.model.user.settings.FavouriteLayout;
import org.springframework.core.convert.converter.Converter;

/**
 * converts a string to a {@link FavouriteLayout}
 *
 * @author jp
 */
public class StringToFavouriteLayoutConverter implements Converter<String, FavouriteLayout> {
	
	/* (non-Javadoc)
	 * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
	 */
	@Override
	public FavouriteLayout convert(String value) {
		// splitting at "/". Now sourceAndStyle[0] = source and sourceAndStyle[1] = style
		final String sourceAndStyle[] = value.split("/");
		if (sourceAndStyle.length != 2) {
			throw new IllegalArgumentException("Format has to be source/style");
		}
		return new FavouriteLayout(sourceAndStyle[0], sourceAndStyle[1]);
	}
}
