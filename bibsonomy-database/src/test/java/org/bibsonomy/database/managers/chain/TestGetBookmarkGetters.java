package org.bibsonomy.database.managers.chain;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksByConceptForUser;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

public class TestGetBookmarkGetters {

	/** for local test options */
	private GetBookmarksByConceptForUser get = new GetBookmarksByConceptForUser();

	@Test
	public void test1() {
		final List<String> taglist = new LinkedList<String>();
		taglist.add("semantic");

		// authUser,groupingEntity,groupingName,tags,hash,popular,added,start,end

		get.perform("jaeschke", GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);

		List<Post<? extends Resource>> test = get.perform("jaeschke", GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);
		// FIXME: test is null
//		System.out.println(test.size() + " in my FirstTest");
	}
}