/**
 * BibSonomy-OpenAccess - Check Open Access Policies for Publications
 *
 * Copyright (C) 2006 - 2015 Knowledge & Data Engineering Group,
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
package de.unikassel.puma.openaccess.classification;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author philipp
 */
public class Classification {
	
	private final String delimiter;
	
	private final String className;
	
	private final Map<String , ClassificationObject> classifications;
	
	public Classification(final String className, final Map<String , ClassificationObject> classifications, final String delimiter) {
		this.delimiter = delimiter;
		this.className = className;
		this.classifications = classifications;
	}
	
	public String getClassName() {
		return this.className;
	}
	
	private String getNextToken(final String actualToken) {
		if (present(this.delimiter)) {
			final int delimiter = actualToken.indexOf('.') +1;
			
			if(delimiter != 0) {
				return actualToken.substring(0, delimiter);
			}
			return actualToken;
		}
		return actualToken.charAt(0) + "";
	}
	
	private String getRestToken(final String token) {
		if (present(this.delimiter)) {
			final int delimiter = token.indexOf('.') +1;
			
			if (delimiter != 0) {
				return token.substring(delimiter, token.length());
			}
			
			return "";
		}
		return token.substring(1);
	}
	
	public final List<PublicationClassification> getChildren(final String name) {
		final List<PublicationClassification> erg = new ArrayList<PublicationClassification>();
		
		String actual, tempName = name;
		
		Map<String , ClassificationObject> children = this.classifications;
		ClassificationObject actualObject = null;
		
		while(!tempName.isEmpty()) {			
			actual = this.getNextToken(tempName);
			tempName = this.getRestToken(tempName);
			actualObject = children.get(actual);
			
			children = actualObject.getChildren();
		}

		final Set<String> keys = children.keySet();
		for (final String s : keys) {
			final PublicationClassification co = new PublicationClassification(s, this.getDescription(name +s));
			erg.add(co);
		}
		return erg;
	}
	
	public String getDescription(String name) {
		String actual = this.getNextToken(name);
		name = this.getRestToken(name);
		
		Map<String , ClassificationObject> children = this.classifications;
		ClassificationObject actualObject = null;
		
		while (!children.isEmpty()) {
			if (!actual.isEmpty()) {
				actualObject = children.get(actual);
			} else {
				if (present(actualObject)) {
					return actualObject.getDescription();
				}
				return "";
			}
			
			if (!name.isEmpty()) {
				actual = this.getNextToken(name);
				name = this.getRestToken(name);
			} else {
				actual = "";
			}
			
			if (present(actualObject)) {
				children = actualObject.getChildren();
			}
		}
		if (present(actualObject)) {
			return actualObject.getDescription();
		}
		return "";
	}
}
