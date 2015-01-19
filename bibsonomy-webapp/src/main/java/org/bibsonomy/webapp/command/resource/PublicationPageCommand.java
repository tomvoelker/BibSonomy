/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2014 Knowledge & Data Engineering Group,
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
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bibsonomy.webapp.command.resource;

import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 */
public class PublicationPageCommand extends ResourcePageCommand<BibTex> {
	/**
	 * additional metadata for bibtex resource
	 * e.g.
	 * <code>
	 * additionalMetadataMap
	 *  {
	 *  	DDC=[010, 050, 420, 422, 334, 233], 
	 *  	post.resource.openaccess.additionalfields.additionaltitle=[FoB], 
	 *  	post.resource.openaccess.additionalfields.phdreferee2=[Petra Musterfrau], 
	 *  	post.resource.openaccess.additionalfields.phdreferee=[Peter Mustermann], 
	 *  	ACM=[C.2.2], 
	 *  	JEL=[K12], 
	 *  	post.resource.openaccess.additionalfields.sponsor=[DFG, etc..], 
	 *  	post.resource.openaccess.additionalfields.phdoralexam=[17.08.2020], 
	 *  	post.resource.openaccess.additionalfields.institution=[Uni KS tEST ]
	 *  }
	 *  </code>
	 */
	private Map<String, List<String>> additionalMetadata;

	/**
	 * @return the additionalMetadata
	 */
	public Map<String, List<String>> getAdditionalMetadata() {
		return this.additionalMetadata;
	}

	/**
	 * @param additionalMetadata the additionalMetadata to set
	 */
	public void setAdditionalMetadata(final Map<String, List<String>> additionalMetadata) {
		this.additionalMetadata = additionalMetadata;
	}
}
