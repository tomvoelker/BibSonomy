/*
 * Created on 26.01.2006
 */
package recommender.model;

import org.apache.log4j.Logger;

/**
 * Ein TagVector Tripel
 * 
 * @author Jens Illig
 */
public class TagVectorInfo {
	private static final Logger log = Logger.getLogger(TagVectorInfo.class);
	private TagVector contentTagVector, userTagVector;
	private Integer tagId = null;

	public TagVectorInfo( final TagVector content, final TagVector user) {
		if (content == null) {
			log.warn("contentTagVector=null");
		}
		if (user == null) {
			log.warn("userTagVector=null");
		}
		contentTagVector = content;
		userTagVector = user;
	}
	
	public Integer getTagId() {
		if (tagId == null) {
			if (userTagVector != null) {
				tagId = userTagVector.getTagID();
			} else if (contentTagVector != null) {
				tagId = contentTagVector.getTagID();
			}
		}
		return tagId;
	}
	
	public boolean isEmpty() {
		return (contentTagVector == null) && (userTagVector == null);
	}
	

	public TagVector getContentTagVector() {
		return contentTagVector;
	}
	

	public void setContentTagVector(TagVector contentTagVector) {
		this.contentTagVector = contentTagVector;
	}
	

	public TagVector getUserTagVector() {
		return userTagVector;
	}
	

	public void setUserTagVector(TagVector userTagVector) {
		this.userTagVector = userTagVector;
	}
	
}
