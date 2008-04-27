package org.bibsonomy.testutil;

import java.util.regex.Pattern;

import static junit.framework.Assert.fail;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Test;

/**
 * @author Jens Illig
 * @version $Id$
 */
public class ModelUtilsTest {

	/**
	 * tests assertPropertyEquality
	 */
	@Test
	public void assertPropertyEquality() {
		final Post<BibTex> postA = ModelUtils.generatePost(BibTex.class);
		final Post<BibTex> postB = ModelUtils.generatePost(BibTex.class);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "date");
		postB.getTags().clear();
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null);
			fail();
		} catch (Throwable ignored) {
		}
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile(".ate"));
			fail();
		} catch (Throwable ignored) {
		}
		postB.setDate(postA.getDate());
		ModelUtils.assertPropertyEquality(postA, postB, 1, null);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "tags");
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile("t[ga]{2}s"));
	}
}