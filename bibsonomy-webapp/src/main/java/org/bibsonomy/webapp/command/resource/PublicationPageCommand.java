/**
 * BibSonomy-Webapp - The web application for BibSonomy.
 *
 * Copyright (C) 2006 - 2021 Data Science Chair,
 *                               University of Würzburg, Germany
 *                               https://www.informatik.uni-wuerzburg.de/datascience/home/
 *                           Information Processing and Analytics Group,
 *                               Humboldt-Universität zu Berlin, Germany
 *                               https://www.ibi.hu-berlin.de/en/research/Information-processing/
 *                           Knowledge & Data Engineering Group,
 *                               University of Kassel, Germany
 *                               https://www.kde.cs.uni-kassel.de/
 *                           L3S Research Center,
 *                               Leibniz University Hannover, Germany
 *                               https://www.l3s.de/
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

import lombok.Getter;
import lombok.Setter;
import org.bibsonomy.model.BibTex;

/**
 * @author dzo
 */
@Setter
@Getter
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

}
