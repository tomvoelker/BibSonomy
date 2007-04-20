package org.bibsonomy.database;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bookmark.get.GetBookmarksForHomePage;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

public class TestGetBookmarkGetters extends TestCase {

	/*******for local test options***********/

	GetBookmarksForHomePage get =new GetBookmarksForHomePage();
	
	public void test1 () {
		
		List <String> taglist=new LinkedList<String>();
		taglist.add("semantic");
		
		//authUser, groupingEntity,groupingName,tags,hash,popular,added,start,end
		
		get.perform("jaeschke", GroupingEntity.USER, "jaeschke",taglist,null,false, false, 0, 19);
		
		List<Post<? extends Resource>> test =get.perform("jaeschke", GroupingEntity.USER, "jaeschke",taglist,null,false, true, 0, 19);
		System.out.println(test.size()+" in my FirstTest");
	}
	
}
