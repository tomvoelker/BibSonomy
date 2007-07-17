/*
 * Created on 16.07.2007
 */
package org.bibsonomy.testutil;

import junit.framework.Assert;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.junit.Test;

public class ModelUtilsTest {
	
	@Test
	public void assertPropertyEquality() {
		Post<BibTex> postA = ModelUtils.generatePost(BibTex.class);
		Post<BibTex> postB = ModelUtils.generatePost(BibTex.class);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, "date");
		postB.getTags().clear();
		try {
			ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE);
			Assert.fail();
		} catch (Throwable t) {
		}
		postB.setDate(postA.getDate());
		ModelUtils.assertPropertyEquality(postA, postB, 1);
		ModelUtils.assertPropertyEquality(postA, postB, Integer.MAX_VALUE, "tags");
	}
}
