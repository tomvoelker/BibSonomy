package org.bibsonomy.database;

import java.util.List;

import org.bibsonomy.database.managers.getpostsqueriesForBookmark.GetBookmarksViewable;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.rest.enums.GroupingEntity;

import junit.framework.TestCase;

public class TestGetterBeforeURL extends TestCase {

	/*******for local test options***********/

	GetBookmarksViewable get=new GetBookmarksViewable();
	public void test1 () {
		
                //authUser, groupingEntity,groupingName,tags,hash,popular,added,start,end
		get.perform("mio", GroupingEntity.VIEWABLE, "mio", null, "acdcbe9350f5061732d0353a8deea172",false, false, 0, 19);
		
		List<Post<? extends Resource>> test =get.perform("mio", GroupingEntity.USER, "mio", null, "acdcbe9350f5061732d0353a8deea172",false, false, 0, 19);
		
		System.out.println(test.size()+" in my FirstTest");
		
		
		
		
		/*Post<? extends Resource> p =test.get(0);
		System.out.println("getInterHash() = " + p.getResource().getInterHash());
		System.out.println("getIntraHash() = " + p.getResource().getIntraHash());*/		
	}
	
}
