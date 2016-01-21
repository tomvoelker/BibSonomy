/**
 * BibSonomy-Database-Common - Helper classes for database interaction
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
package org.bibsonomy.database.common.params.beans;

/**
 * TODO: could be remove when using mybatis
 * 
 * This class holds the tagname and the corresponding index and join-index.
 * While the name of the class might be misleading, it can be used for tags as
 * well for concepts.
 * 
 * @author Christian Schenk
 */
public class TagIndex {

	/** This name can be both a name of a tag or concept. */
	private final String tagName;
	/** A index to produce a self-join, like t1...=t2..., t2...=t3..., etc. */
	private final int index;

	/**
	 * Creates a new instance with the given namen an start index.
	 * 
	 * @param tagName
	 * @param index
	 */
	public TagIndex(final String tagName, final int index) {
		this.tagName = tagName;
		this.index = index;
	}

	/**
	 * @return the tag's name
	 */
	public String getTagName() {
		return this.tagName;
	}

	/**
	 * @return current index
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Retrieves the join-index which is always the current index plus one.
	 * 
	 * Hint: a call to this function isn't idempotent, i.e. it changes the value
	 * of the index.
	 * 
	 * @return current index plus one
	 */
	public int getIndex2() {
		return (this.index + 1);
	}
}