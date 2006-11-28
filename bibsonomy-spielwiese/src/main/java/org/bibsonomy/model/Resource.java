package org.bibsonomy.model;

import java.util.Date;

import org.bibsonomy.ibatis.enums.ConstantID;

/**
 * Everything, which can be tagged in BibSonomy, is derived from this class.
 * 
 * @author Christian Schenk
 */
public abstract class Resource {

	public int contentId;
	private Date date;

	// FIXME: put them only in the model, if we really need them
	// private int groupid;
	// private String group;
	// private String title;
	// private String privnote;

	public Resource() {
		this.contentId = ConstantID.IDS_UNDEFINED_CONTENT_ID.getId();
		// this.groupid = ConstantID.GROUP_PUBLIC.getId();
		// this.group = "public";
		// this.title = "";
	}

	public int getContentId() {
		return this.contentId;
	}

	public void setContentId(int contentId) {
		this.contentId = contentId;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}