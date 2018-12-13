/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index.generator.post;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.bibsonomy.util.Sets;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;
import java.util.Set;

/**
 * implementation of the {@link EntityInformationProvider} interface for posts
 *
 * @param <R>
 * @author dzo
 */
public class PostEntityInformationProvider<R extends Resource> extends EntityInformationProvider<Post<R>> {

	public static final Set<String> PRIVATE_FIELDS = Sets.asSet(
					ESConstants.Fields.Publication.PRIVNOTE,
					ESConstants.Fields.Publication.ALL_DOCS
	);

	// TODO: maybe we should separate bookmark and publication fields
	public static final Set<String> PUBLIC_FIELDS = Sets.asSet(
					ESConstants.Fields.TAGS,
					ESConstants.Fields.DESCRIPTION,
					ESConstants.Fields.Resource.TITLE,
					ESConstants.Fields.Bookmark.URL,
					ESConstants.Fields.Publication.ALL_AUTHORS,
					ESConstants.Fields.Publication.MISC_FIELDS_VALUES,
					ESConstants.Fields.Publication.SCHOOL,
					ESConstants.Fields.Publication.YEAR,
					ESConstants.Fields.Publication.BIBTEXKEY,
					ESConstants.Fields.Publication.ADDRESS,
					ESConstants.Fields.Publication.ENTRY_TYPE,
					ESConstants.Fields.Publication.ANNOTE,
					ESConstants.Fields.Publication.KEY,
					ESConstants.Fields.Publication.ABSTRACT,
					ESConstants.Fields.Publication.BOOKTITLE,
					ESConstants.Fields.Publication.CHAPTER,
					ESConstants.Fields.Publication.CROSSREF,
					ESConstants.Fields.Publication.DAY,
					ESConstants.Fields.Publication.EDITION,
					ESConstants.Fields.Publication.HOWPUBLISHED,
					ESConstants.Fields.Publication.INSTITUTION,
					ESConstants.Fields.Publication.JOURNAL,
					ESConstants.Fields.Publication.MONTH,
					ESConstants.Fields.Publication.NOTE,
					ESConstants.Fields.Publication.NUMBER,
					ESConstants.Fields.Publication.ORGANIZATION,
					ESConstants.Fields.Publication.PAGES,
					ESConstants.Fields.Publication.PUBLISHER,
					ESConstants.Fields.Publication.SERIES,
					ESConstants.Fields.Publication.TYPE,
					ESConstants.Fields.Publication.URL,
					ESConstants.Fields.Publication.VOLUME
	);

	static {
		PUBLIC_FIELDS.addAll(ESConstants.Fields.Publication.SPECIAL_MISC_FIELDS);
	}

	private Class<R> resourceType;

	/**
	 * the entity information provider
	 *  @param converter
	 * @param mappingBuilder
	 * @param resourceType
	 */
	public PostEntityInformationProvider(Converter<Post<R>, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder, final Class<R> resourceType) {
		super(converter, mappingBuilder);

		this.resourceType = resourceType;
	}

	@Override
	public int getContentId(Post<R> post) {
		return post.getContentId();
	}

	@Override
	public String getEntityId(Post<R> entity) {
		return String.valueOf(entity.getContentId());
	}

	@Override
	public String getType() {
		return ResourceFactory.getResourceName(this.resourceType);
	}

	@Override
	public Set<String> getPublicFields() {
		return PUBLIC_FIELDS;
	}

	@Override
	public Set<String> getPrivateFields() {
		return PRIVATE_FIELDS;
	}
}
