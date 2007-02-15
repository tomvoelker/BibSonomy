package org.bibsonomy.database.newImpl.general;

import java.util.List;

import org.bibsonomy.model.Tag;
import org.bibsonomy.rest.enums.GroupingEntity;

/*
 * TODO: implements tag specific methods of LogicInterface; method descriptions there
 */
public class TagDBManager {

	
	public List<Tag> getTags(String authUser, GroupingEntity grouping, String groupingName, String regex, int start, int end) {
		return null;
	}
	
	public Tag getTagDetails(String authUserName, String tagName) {
		return null;
	}
}
