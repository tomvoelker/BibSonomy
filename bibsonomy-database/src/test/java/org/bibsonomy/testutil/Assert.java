/**
 * BibSonomy-Database - Database for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
package org.bibsonomy.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.common.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * @author dzo
 */
public final class Assert {
	
	/**
	 * asserts that two tag collections are equal (are containing the same tags)
	 * only tag names are used for assertion (other properties are ignored!!!)
	 * 
	 * @param message  the assert fail message
	 * @param expected the expected tags
	 * @param actual   the current tags
	 */
	public static void assertTagsByName(final String message, final Collection<Tag> expected, final Collection<Tag> actual) {	
		if ((expected == null) && (actual == null)) {
			return;
		}
		
		/*
		 * get all string values of the tag collections separately
		 */
		final Set<String> expectedString = new HashSet<String>();
		final Set<String> actualString = new HashSet<String>();
		
		if (expected != null) {
			for (final Tag tag : expected) {
				expectedString.add(tag.getName());
			}
		}
		
		if (actual != null) {
			for (final Tag tag : actual) {
				actualString.add(tag.getName());
			}
		}
		
		/*
		 * compare the two string sets
		 */
		assertEquals(message, expectedString, actualString);		
	}
	
	/**
	 * see {@link Assert#assertTagsByName(String, Collection, Collection)}
	 * @param expected
	 * @param actual
	 */
	public static void assertTagsByName(final Collection<Tag> expected, final Collection<Tag> actual) {	
		Assert.assertTagsByName(null, expected, actual);
	}

	/**
	 * Searches in a list of posts for the requested tags from the bibtexParam.
	 * @param tagIndex 
	 * @param posts 
	 */
	public static void assertByTagNames(List<TagIndex> tagIndex, final List<Post<BibTex>> posts) {
		if (posts.size() == 0) return;
		for (final TagIndex requestedTag : tagIndex) {
			boolean foundTag = false;
			for (final Post<BibTex> post : posts) {
				for (final Tag tagFromOnePost : post.getTags()) {
					if (requestedTag.getTagName().equals(tagFromOnePost.getName())) {
						foundTag = true;
						break;
					}
				}
				if (foundTag) break;
			}
			assertTrue(foundTag);
		}
	}
}
