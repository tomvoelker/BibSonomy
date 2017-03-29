/**
 * BibSonomy-Model - Java- and JAXB-Model.
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
package org.bibsonomy.model;

import java.io.Serializable;


/**
 * Wraps a {@link Post} and adds score and confidence.
 * 
 * @author lukas
 * 
 * @param <T> the resourcetype
 */
public class RecommendedPost<T extends Resource> implements Serializable {

private static final long serialVersionUID = -1872430526599241544L;
	
	private double score;
	private double confidence;
	private Post<T> post;
	
	/**
	 * for bean-compatibility
	 * @param post the post
	 */
	public RecommendedPost(Post<T> post) {
		this.post = post;
	}
	
	/**
	 * @return the post
	 */
	public Post<T> getPost() {
		return this.post;
	}
	
	/**
	 * @param post the post
	 */
	public void setPost(Post<T> post) {
		this.post = post;
	}
	
	/**
	 * @return the score
	 */
	public double getScore() {
		return this.score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(double score) {
		this.score = score;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return this.confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
}
