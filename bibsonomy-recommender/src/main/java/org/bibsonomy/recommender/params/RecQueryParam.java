package org.bibsonomy.recommender.params;

import java.sql.Timestamp;
import java.util.List;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.RecommendedTag;
import org.bibsonomy.model.Resource;

/**
 * Parameter class for recommender queries.
 * @author fei
 * @version $Id$
 */
public class RecQueryParam {
	private Long qid;
	private Integer contentType;
	private String userName;
	private Timestamp timeStamp;
	private Post<? extends Resource> post;
	private List<RecommendedTag> tags;
	private List<RecommendedTag> preset;
	
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setQid(long qid) {
		this.qid = qid;
	}
	public long getQid() {
		return qid;
	}
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}
	public Post<? extends Resource> getPost() {
		return post;
	}
	public void setTags(List<RecommendedTag> tags) {
		this.tags = tags;
	}
	public List<RecommendedTag> getTags() {
		return tags;
	}
	public void setPreset(List<RecommendedTag> preset) {
		this.preset = preset;
	}
	public List<RecommendedTag> getPreset() {
		return preset;
	}
	public void setContentType(Integer content_type) {
		this.contentType = content_type;
	}
	public Integer getContentType() {
		return contentType;
	}
}
