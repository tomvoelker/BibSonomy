package org.bibsonomy.database;

import org.bibsonomy.database.managers.getpostsqueries.GetBookmarksByHashForUser;
import org.bibsonomy.rest.enums.GroupingEntity;

import junit.framework.TestCase;

public class MyFirstTest extends TestCase {

	GetBookmarksByHashForUser get = new GetBookmarksByHashForUser();
	
	public void test1 () {
		get.perform("jaeschke", GroupingEntity.USER, "jaeschke", null, "89e66a897c99ccfdd328f197f60625c8", false, true, 0, 1);
	}
	
}
