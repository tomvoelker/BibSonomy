/*
 * Created on 16.07.2007
 */
package org.bibsonomy.testutil;

import java.util.regex.Pattern;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Test;

public class ModelUtilsTest {
	
	@Test
	public void assertPropertyEquality() {
		Post<BibTex> postA = ModelUtils.generatePost(BibTex.class);
		Post<BibTex> postB = ModelUtils.generatePost(BibTex.class);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "date");
		postB.getTags().clear();
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null);
			Assert.fail();
		} catch (Throwable t) {
		}
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile(".ate"));
			Assert.fail();
		} catch (Throwable t) {
		}
		postB.setDate(postA.getDate());
		ModelUtils.assertPropertyEquality(postA, postB, 1, null);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, null, "tags");
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, Pattern.compile("t[ga]{2}s"));
	}
}
