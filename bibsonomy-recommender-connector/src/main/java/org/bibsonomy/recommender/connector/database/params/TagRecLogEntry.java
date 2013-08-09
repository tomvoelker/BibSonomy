package org.bibsonomy.recommender.connector.database.params;

import java.sql.Timestamp;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bibsonomy.model.BibSonomyRecommendedTag;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;

/**
 * @author Lukas
 * @version $Id$
 */
public class TagRecLogEntry {

	private String userName;
	private Long qid;           /// query id
	private Long sid;           /// settings id
	private Long latency;
	private Double score;
	private Double confidence;
	private String tagName;
	private Timestamp timeStamp;
	private Post<? extends Resource> post;
	private byte[] metaData;
	private SortedSet<BibSonomyRecommendedTag> tags;
	private SortedSet<BibSonomyRecommendedTag> preset;	
	
	public void setQid(long qid) {
		this.qid = qid;
	}
	public long getQid() {
		return qid;
	}
	public void setSid(long sid) {
		this.sid = sid;
	}
	public long getSid() {
		return sid;
	}
	public void setLatency(long latency) {
		this.latency = latency;
	}
	public long getLatency() {
		return latency;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getScore() {
		return score;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public double getConfidence() {
		return confidence;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagName() {
		return tagName;
	}
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setPost(Post<? extends Resource> post) {
		this.post = post;
	}
	public Post<? extends Resource> getPost() {
		return post;
	}
	public void setMetaData(byte[] metaData) {
		this.metaData = metaData;
	}
	public byte[] getMetaData() {
		return metaData;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setPreset(List<BibSonomyRecommendedTag> preset) {
		if( this.preset==null )
			this.preset = new TreeSet<BibSonomyRecommendedTag>();
		else
			this.preset.clear();
		this.preset.addAll(preset);
	}
	public SortedSet<BibSonomyRecommendedTag> getPreset() {
		return preset;
	}
	public void setTags(List<BibSonomyRecommendedTag> tags) {
		if( this.tags==null )
			this.tags = new TreeSet<BibSonomyRecommendedTag>();
		else
			this.tags.clear();
		this.tags.addAll(tags);
	}
	public SortedSet<BibSonomyRecommendedTag> getTags() {
		return tags;
	}
	
}
