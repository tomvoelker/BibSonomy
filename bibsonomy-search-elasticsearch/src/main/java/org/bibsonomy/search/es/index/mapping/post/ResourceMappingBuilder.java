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
package org.bibsonomy.search.es.index.mapping.post;

import java.io.IOException;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.ESConstants;
import org.bibsonomy.search.es.ESConstants.Fields;
import org.bibsonomy.search.util.Mapping;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

/**
 * abstract resource mapping builder
 *
 * @author dzo
 * @param <R> 
 */
public abstract class ResourceMappingBuilder<R extends Resource> implements MappingBuilder<XContentBuilder> {


	private Class<R> resourceType;

	/**
	 * @param resourceType
	 */
	public ResourceMappingBuilder(Class<R> resourceType) {
		super();
		this.resourceType = resourceType;
	}

	/* (non-Javadoc)
	 * @see org.bibsonomy.search.util.MappingBuilder#getMapping()
	 */
	@SuppressWarnings("resource")
	@Override
	public Mapping<XContentBuilder> getMapping() {
		try {
			final String documentType = this.getDocumentType();
			XContentBuilder commonPostResourceFields = XContentFactory.jsonBuilder()
					.startObject()
							.field("date_detection", false)
							.startObject(ESConstants.IndexSettings.PROPERTIES)
								.startObject(ESConstants.Fields.Resource.INTRAHASH)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								.startObject(ESConstants.Fields.Resource.INTERHASH)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								.startObject(ESConstants.Fields.TAGS)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								.startObject(ESConstants.Fields.USER_NAME)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								.startObject(ESConstants.Fields.GROUPS)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								/*
								 * NOTE: we order our search requests by date
								 * => this field must be analyzed by es 
								 */
								.startObject(ESConstants.Fields.DATE)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.DATE_TYPE)
									.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
								.endObject()
								.startObject(ESConstants.Fields.CHANGE_DATE)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.DATE_TYPE)
									.field(ESConstants.IndexSettings.INDEX_FIELD, ESConstants.IndexSettings.NOT_INDEXED)
									.field(ESConstants.IndexSettings.FORMAT_FIELD, ESConstants.IndexSettings.DATE_TIME_FORMAT)
								.endObject()
								.startObject(Fields.SYSTEM_URL)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.KEYWORD_TYPE)
								.endObject()
								.startObject(ESConstants.Fields.Resource.TITLE)
									.field(ESConstants.IndexSettings.TYPE_FIELD, ESConstants.IndexSettings.TEXT_TYPE)
									.field(ESConstants.IndexSettings.BOOST_FIELD, 2)
								.endObject();
			
			this.doResourceSpecificMapping(commonPostResourceFields);
			
			final XContentBuilder finalObject = commonPostResourceFields
							.endObject()
						// .endObject()
					.endObject();
			final Mapping<XContentBuilder> mapping = new Mapping<>();
			mapping.setMappingInfo(finalObject);
			mapping.setType(documentType);
			return mapping;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param builder
	 * @throws IOException
	 */
	protected abstract void doResourceSpecificMapping(XContentBuilder builder) throws IOException;

	/**
	 * @return
	 */
	private String getDocumentType() {
		return ResourceFactory.getResourceName(this.resourceType);
	}
}
