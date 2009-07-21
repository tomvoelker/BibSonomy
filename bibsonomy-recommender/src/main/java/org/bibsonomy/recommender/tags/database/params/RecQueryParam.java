package org.bibsonomy.recommender.tags.database.params;

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
	/** ID for mapping posts to recommender queries */
	private int pid;
	/** content typ: 1 for bookmar, 2 for bibtex */
	private Integer contentType;
	private String userName;
	private Timestamp timeStamp;
	/** querie's timeout value */
	private int queryTimeout;
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
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getPid() {
		return pid;
	}
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public int getQueryTimeout() {
		return queryTimeout;
	}
}
