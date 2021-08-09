/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index.converter.post;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Document;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.ResourcePersonRelation;
import org.bibsonomy.model.enums.PersonResourceRelationType;
import org.bibsonomy.search.es.ESConstants;

/**
 * converter for {@link GoldStandardPublication}
 *
 * @author dzo
 */
public class CommunityPublicationConverter extends PublicationConverter {

	/**
	 * @param systemURI
	 */
	public CommunityPublicationConverter(URI systemURI) {
		super(systemURI, null);
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.PublicationConverter#createNewResource()
	 */
	@Override
	protected BibTex createNewResource() {
		return new GoldStandardPublication();
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.PublicationConverter#convertDocuments(java.util.List)
	 */
	@Override
	public List<Map<String, String>> convertDocuments(List<Document> documents) {
		// nothing to do
		return null;
	}

	@Override
	protected void convertPostInternal(final Map<String, Object> source, final Post<BibTex> post) {
		// read the resource relations
		post.setResourcePersonRelations(readPersonResourceRelations(source));
	}

	private static List<ResourcePersonRelation> readPersonResourceRelations(final Map<String, Object> source) {
		final List<ResourcePersonRelation> resourcePersonRelations = new LinkedList<>();
		resourcePersonRelations.addAll(readPersonResourceRelations(source.get(ESConstants.Fields.Publication.EDITORS), PersonResourceRelationType.EDITOR));
		resourcePersonRelations.addAll(readPersonResourceRelations(source.get(ESConstants.Fields.Publication.AUTHORS), PersonResourceRelationType.AUTHOR));

		// FIXME: other relations are missing
		return resourcePersonRelations;
	}

	private static List<ResourcePersonRelation> readPersonResourceRelations(Object source, PersonResourceRelationType type) {
		final LinkedList<ResourcePersonRelation> relations = new LinkedList<>();
		if (source instanceof List) {
			@SuppressWarnings("unchecked")
			final List<Map<String, String>> personNamesList = (List<Map<String, String>>) source;
			int index = 0;

			for (final Map<String, String> personNameMap : personNamesList) {
				if (personNameMap.containsKey(ESConstants.Fields.Publication.PERSON_ID)) {
					final Person person = new Person();
					person.setPersonId(personNameMap.get(ESConstants.Fields.Publication.PERSON_ID));
					person.setCollege(personNameMap.get(ESConstants.Fields.Publication.PERSON_COLLEGE));

					final ResourcePersonRelation relation = new ResourcePersonRelation();
					relation.setPersonIndex(index);
					relation.setRelationType(type);
					relation.setPerson(person);
					relations.add(relation);
				}
				index += 1;
			}
		}

		return relations;
	}
	
	/* (non-Javadoc)
	 * @see org.bibsonomy.search.es.index.converter.post.ResourceConverter#fillIndexDocument(org.bibsonomy.model.Post, java.util.Map)
	 */
	@Override
	protected void fillIndexDocumentUser(Post<BibTex> post, Map<String, Object> jsonDocument) {
		// nothing to do
	}
}
