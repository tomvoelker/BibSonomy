package org.bibsonomy.community.database.param;

import java.util.Date;

import org.bibsonomy.database.params.GenericParam;

/**
 * hopelessly over full parameter bean for passing database parameters
 * 
 * @author fei
 *
 */
public class CommunityParam extends GenericParam {
	private int algorithmID;
	private int blockID;
	private int runID;
	private int communityID;
	private int communityUID;
	private int topicID;
	private int contentID;
	
	private String userName;
	private String tagName;
	private String algorithmName;
	
	private double weight;
	private String algorithmMeta;
	private Date date;
	private int contentType;
	private int clusterCount;
	private int topicCount;
	private int globalcount;
	
	//------------------------------------------------------------------------
	// getter/setter
	//------------------------------------------------------------------------
	public void setRunID(int runID) {
		this.runID = runID;
	}
	public int getRunID() {
		return runID;
	}
	public void setAlgorithmID(int algorithmID) {
		this.algorithmID = algorithmID;
	}
	public int getAlgorithmID() {
		return algorithmID;
	}
	public void setContentID(int contentID) {
		this.contentID = contentID;
	}
	public int getContentID() {
		return contentID;
	}
	public void setTopicID(int topicID) {
		this.topicID = topicID;
	}
	public int getTopicID() {
		return topicID;
	}
	public void setBlockID(int blockID) {
		this.blockID = blockID;
	}
	public int getBlockID() {
		return blockID;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	public String getTagName() {
		return tagName;
	}
	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}
	public String getAlgorithmName() {
		return algorithmName;
	}
	public void setWeight(double weight) {
		this.weight = weight;
	}
	public double getWeight() {
		return weight;
	}
	public void setAlgorithmMeta(String algorithmMeta) {
		this.algorithmMeta = algorithmMeta;
	}
	public String getAlgorithmMeta() {
		return algorithmMeta;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public void setContentType(int contentType) {
		this.contentType = contentType;
	}
	public int getContentType() {
		return contentType;
	}
	public void setClusterCount(int clusterCount) {
		this.clusterCount = clusterCount;
	}
	public int getClusterCount() {
		return clusterCount;
	}
	public void setTopicCount(int topicCount) {
		this.topicCount = topicCount;
	}
	public int getTopicCount() {
		return topicCount;
	}
	public void setCommunityID(int communityID) {
		this.communityID = communityID;
	}
	public int getCommunityID() {
		return communityID;
	}
	public void setGlobalcount(int globalcount) {
		this.globalcount = globalcount;
	}
	public int getGlobalcount() {
		return globalcount;
	}
	public void setCommunityUID(int communityUID) {
		this.communityUID = communityUID;
	}
	public int getCommunityUID() {
		return communityUID;
	}

	
}
