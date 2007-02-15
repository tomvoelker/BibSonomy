package org.bibsonomy.database;

import java.util.List;

import org.bibsonomy.database.managers.getpostsqueries.GetBookmarksByHashForUser;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

import junit.framework.TestCase;

public class MyFirstTest extends TestCase {

	GetBookmarksByHashForUser get = new GetBookmarksByHashForUser();
	
	public void test1 () {
		get.perform("jaeschke", GroupingEntity.USER, "jaeschke", null, "89e66a897c99ccfdd328f197f60625c8", false, false, 0, 1);
		List<Post<? extends Resource>> test =get.perform("jaeschke", GroupingEntity.USER, "jaeschke", null, "89e66a897c99ccfdd328f197f60625c8", false, false, 0, 1);
		System.out.println(test.size());
		Post<? extends Resource> p =test.get(0);
		System.out.println("getInterHash() = " + p.getResource().getInterHash());
		System.out.println("getIntraHash() = " + p.getResource().getIntraHash());		
	}
	
}
