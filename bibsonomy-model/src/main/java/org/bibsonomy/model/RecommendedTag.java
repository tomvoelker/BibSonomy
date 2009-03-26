/**
 *  
 *  BibSonomy-Model - Java- and JAXB-Model.
 *   
 *  Copyright (C) 2006 - 2008 Knowledge & Data Engineering Group, 
 *                            University of Kassel, Germany
 *                            http://www.kde.cs.uni-kassel.de/
 *  
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.bibsonomy.model;


/** Adds scores and confidence to {@link Tag}.
 * 
 * TODO: move this to org.bibsonomy.model?
 * 
 * @author rja
 * @version $Id$
 */
public class RecommendedTag extends Tag implements Comparable<Tag> {

	private double score;
	private double confidence;
	
	// 2008/12/10,fei: added standard constructor for bean-compatibility
	public RecommendedTag() {
	}
	
	public RecommendedTag(String name, double score, double confidence) {
		super(name);
		this.score = score;
		this.confidence = confidence;
	}
	
	public double getScore() {
		return this.score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getConfidence() {
		return this.confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	
	@Override
	public String toString() {
		return super.toString() + " (score=" + score + ", confidence=" + confidence + ")";
	}
	

}
