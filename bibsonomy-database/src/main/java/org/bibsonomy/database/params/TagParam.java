package org.bibsonomy.database.params;

import java.util.List;

import org.bibsonomy.common.enums.ConstantID;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;

public class TagParam {

	private int id;
	private String name;
	private String stem; 
	private int count; 
	private int usercount;
	private List<Tag> superTags;
	private List<Tag> subTags;
    private List<Post<? extends Resource>> posts;
	private int offset;
	private int limit;
	private String userName;
	private ConstantID groupType;
	
	public int getGroupType() {
		return groupType.getId();
	}

	public void setGroupType(ConstantID groupType) {
		this.groupType = groupType;
	}
	
	public String getUserName() {
		return this.userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getCount() {
		return this.count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getId() {
		return this.id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Post<? extends Resource>> getPosts() {
		return this.posts;
	}
	public void setPosts(List<Post<? extends Resource>> posts) {
		this.posts = posts;
	}
	public String getStem() {
		return this.stem;
	}
	public void setStem(String stem) {
		this.stem = stem;
	}
	public List<Tag> getSubTags() {
		return this.subTags;
	}
	public void setSubTags(List<Tag> subTags) {
		this.subTags = subTags;
	}
	public List<Tag> getSuperTags() {
		return this.superTags;
	}
	public void setSuperTags(List<Tag> superTags) {
		this.superTags = superTags;
	}
	public int getUsercount() {
		return this.usercount;
	}
	public void setUsercount(int usercount) {
		this.usercount = usercount;
	}
	public int getLimit() {
		return this.limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getOffset() {
		return this.offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
}
