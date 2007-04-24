package org.bibsonomy.database.managers;

import java.util.List;

import org.bibsonomy.database.params.UserParam;
import org.bibsonomy.model.Tag;
import org.junit.Test;

/**
 * This are simple tests because of the simple SQL
 * 
 * @author Christian Schenk
 */
public class TagDatabaseManagerTest extends AbstractDatabaseManagerTest {
	
	@Test
	public void getTagById() {
		this.tagDb.	getTagById(this.tagParam);
	}
	
	@Test
	public void getTagByCount() {
		this.tagDb.	getTagByCount(this.tagParam);
	}
	
	@Test
	public void getAllTags() {
		this.tagDb.	getAllTags(this.userParam);
	}
	
	@Test
	public void getTagsViewable() {
		this.tagDb. getTagsViewable(this.userParam);
	}
	
	@Test
	public void getTagsByUser() {
		this.tagDb. getTagsByUser(this.userParam);
	} 

	@Test
	public void getTagsByGroup() {
		this.tagDb. getTagsByGroup(this.userParam);
	}
	
	/*
	 * TODO not implemented
	 */
	
	/*@Test
	public void getTagsByExpression() {
		this.tagDb. getTagsByExpression(this.tagParam);
	}*/
	
}