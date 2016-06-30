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

import java.util.Comparator;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;

/**
 * Uses a {@link PostComparator} to sort {@link ResourcePersonRelation}s by their {@link Post}
 */
public class ResourcePersonRelationByPostComparator implements Comparator<ResourcePersonRelation> {
	private final Comparator<Post<? extends BibTex>> postComparator;
	
	/**
	 * @param postComparator 
	 */
	public ResourcePersonRelationByPostComparator(final Comparator<Post<? extends BibTex>> postComparator) {
		this.postComparator = postComparator;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(ResourcePersonRelation o1, ResourcePersonRelation o2) {
		return this.postComparator.compare(o1.getPost(), o2.getPost());
	}

}
