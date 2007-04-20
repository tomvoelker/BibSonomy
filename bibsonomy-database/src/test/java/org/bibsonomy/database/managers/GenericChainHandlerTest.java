package org.bibsonomy.database.managers;

import java.util.LinkedList;
import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.database.AbstractDatabaseTest;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.junit.Test;

public class GenericChainHandlerTest extends AbstractDatabaseTest {

	@Test
	public void perform() {
		final List<String> taglist = new LinkedList<String>();
		final List<String> taglistfriend = new LinkedList<String>();
		taglist.add("semantic");
		taglistfriend.add("DVD");
		// ByTagName
	    //final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.ALL, "jaeschke", taglist, null, false, false, 0, 10);
		 //ByTagNameForUser
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);
		//ByConceptForUser 
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.USER, "jaeschke", taglist, null, false,true, 0, 19);
		//ForUser
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.USER, "jaeschke", null, null, false, false, 0, 19);
		//ByHash
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.ALL, "jaeschke", null,"7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		//ByHashForUser geht noch nicht
		final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.USER, "jaeschke", null,"7d85e1092613fd7c91d6ba5dfcf4a044", false, false, 0, 19);
		//ByViewable
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.VIEWABLE, "jaeschke", null, null, false, false, 0, 19);
		//ForGroup
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.GROUP, "kde", null, null, false, false, 0, 19);
		//ForGroupByTag
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.GROUP, "kde", taglist, null, false, false, 0, 19);
		//ByFriendName
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.FRIEND, "ralfm", null, null, false, false, 0, 19);
		//ByFriendNameAndTag
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.FRIEND, "ralfm", taglistfriend, null, false, false, 0, 19);
        //Popular
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.ALL, "jaeschke", taglist, null, true, false, 0, 19);
		//Home
		//final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", null, "jaeschke", taglist, null, false, false ,0, 19);
		//System.out.println(test.size() + " in my FirstTest");
	}
}