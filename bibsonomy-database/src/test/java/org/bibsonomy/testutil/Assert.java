package org.bibsonomy.testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bibsonomy.database.params.beans.TagIndex;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Tag;

/**
 * @author dzo
 * @version $Id$
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
