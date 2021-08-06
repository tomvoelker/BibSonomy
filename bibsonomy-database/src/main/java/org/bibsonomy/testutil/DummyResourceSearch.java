/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 * University of Kassel, Germany
 * http://www.kde.cs.uni-kassel.de/
 * Data Mining and Information Retrieval Group,
 * University of Würzburg, Germany
 * http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 * L3S Research Center,
 * Leibniz University Hannover, Germany
 * http://www.l3s.de/
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
package org.bibsonomy.testutil;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * @author dzo
 */
public class DummyResourceSearch implements ResourceSearch<Resource> {

	@Override
	public List<Post<Resource>> getPosts(User loggedinUser, PostSearchQuery<?> postQuery) {
		return new LinkedList<>();
	}

	@Override
	public Statistics getStatistics(User loggedinUser, PostSearchQuery<?> postQuery) {
		return new Statistics();
	}

	@Override
	public List<Tag> getTags(User loggedinUser, PostSearchQuery<?> postQuery) {
		return new LinkedList<>();
	}

	@Override
	public <E> Set<E> getDistinctFieldCounts(FieldDescriptor<? extends Resource, E> fieldDescriptor) {
		return new HashSet<>();
	}
}
