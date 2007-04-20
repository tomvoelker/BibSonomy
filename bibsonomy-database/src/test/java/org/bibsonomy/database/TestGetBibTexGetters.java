package org.bibsonomy.database;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.managers.chain.bibtex.get.GetBibtexByConceptForUser;
import org.bibsonomy.model.Bookmark;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

public class TestGetBibTexGetters extends TestCase {

	
	/*******for local test options***********/
	GetBibtexByConceptForUser get =new GetBibtexByConceptForUser();

	public void testgetBookmark () {
		List <String> taglist=new LinkedList<String>();
		taglist.add("semantic");
		//authUser, groupingEntity,groupingName,tags,hash,popular,added,start,end
		
		get.perform("jaeschke", GroupingEntity.USER, "jaeschke",taglist,null,false, false, 0, 19);
		List<Post<? extends Resource>> test =get.perform("jaeschke", GroupingEntity.USER, "jaeschke",taglist,null,false, false, 0, 19);
		System.out.println(test.size()+" in my FirstTest");
		
	}
	
	
}
