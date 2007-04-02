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
		// TODO implement tests
		final List<String> taglist = new LinkedList<String>();
		taglist.add("semantic");
		final List<Post<? extends Resource>> test = this.chainHandler.perform("jaeschke", GroupingEntity.USER, "jaeschke", taglist, null, false, false, 0, 19);
		System.out.println(test.size() + " in my FirstTest");
	}
}