/**
 * BibSonomy Recommendation - Tag and resource recommender.
 *
 * Copyright (C) 2006 - 2016 Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               http://www.kde.cs.uni-kassel.de/
 *                           Data Mining and Information Retrieval Group,
 *                               University of Würzburg, Germany
 *                               http://www.is.informatik.uni-wuerzburg.de/en/dmir/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               http://www.l3s.de/
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.recommender.tag.db.params;

import java.sql.Timestamp;

/**
 * Parameter used to insert tag recommender queries.
 */
public class BibRecQueryParam {
	
	private Long qid;
	/** ID for mapping posts to recommender queries */
	private int post_id;
	/** content type of {@link RecommendationEntity}, 1 for Bookmark, 2 for BibTex */
	private int contentType;
	private String userName;
	private Timestamp timeStamp;
	/** querie's timeout value */
	private int queryTimeout;
	
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
	public void setContentType(int content_type) {
		this.contentType = content_type;
	}
	public int getContentType() {
		return contentType;
	}
	public void setPost_id(int post_id) {
		this.post_id = post_id;
	}
	public int getPost_id() {
		return post_id;
	}
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public int getQueryTimeout() {
		return queryTimeout;
	}
}
