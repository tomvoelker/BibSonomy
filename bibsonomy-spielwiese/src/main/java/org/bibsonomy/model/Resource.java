package org.bibsonomy.model;

import java.util.Date;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 * 
 * @author Christian Schenk
 */
public abstract class Resource {

	/** An Id for this resource; by default ConstantID.IDS_UNDEFINED_CONTENT_ID */
	private int contentId;
	/** The userName who tagged this resource */
	private String userName;
	private String groupName;
	/** The groupId of this resource; by default ConstantID.GROUP_PUBLIC */
	private int groupId;
	/** A timestamp for this resource */
	private Date date;
	private String url;
	private int count;

	// XXX: put them only in the model, if we really need them
	// private String group; FIXME was this meant to be groupName or groupId
	// private String title; FIXME belongs to BibTex?
	// private String privnote; FIXME belongs to BibTex?

	public Resource() {
		this.contentId = ConstantID.IDS_UNDEFINED_CONTENT_ID.getId();
		this.groupId = ConstantID.GROUP_PUBLIC.getId();
		// this.group = "public";
		// this.title = "";
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getGroupName() {
		return this.groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupId() {
		return this.groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public  Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}
}