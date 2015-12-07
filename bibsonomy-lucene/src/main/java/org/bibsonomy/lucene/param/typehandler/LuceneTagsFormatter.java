/**
 * BibSonomy - A blue social bookmark and publication sharing system.
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package org.bibsonomy.lucene.param.typehandler;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.bibsonomy.model.Tag;

/**
 * convert date objects to a standardized string representation
 * 
 * @author fei
 */
public class LuceneTagsFormatter extends LuceneCollectionFormatter<Tag> {

	@Override
	protected Collection<Tag> createCollection() {
		return new LinkedHashSet<Tag>();
	}

	@Override
	protected Tag createItem(String token) {
		return new Tag(token);
	}

	@Override
	protected String convertItem(Tag item) {
		return item.getName();
	}
}