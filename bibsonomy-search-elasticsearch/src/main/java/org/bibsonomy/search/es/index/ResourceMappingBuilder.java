/**
 * BibSonomy Search Elasticsearch - Elasticsearch full text search module.
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
package org.bibsonomy.search.es.index;

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
public abstract class ResourceMappingBuilder<R extends Resource> implements MappingBuilder<String> {

	/** boost the field (search in _all field) */
	protected static final String BOOST_FIELD = "boost";

	/** include field in generated _all field ? */
	protected static final String INCLUDE_IN_ALL_FIELD = "include_in_all";

	/** type string */
	protected static final String STRING_TYPE = "string";
	
	/** date type */
	protected static final String DATE_TYPE = "date";
	
	/** the type field */
	protected static final String TYPE_FIELD = "type";
	
	/** the index field */
	protected static final String INDEX_FIELD = "index";
	
	/** e.g the date format field */
	protected static final String FORMAT_FIELD = "format";
	
	/** iso date format (optional time) */
	protected static final String FORMAT_DATE_OPTIONAL_TIME = "dateOptionalTime";
	
	/** iso date format */
	protected static final String DATE_TIME_FORMAT = "date_time";
	
	/** not analysed field */
	protected static final String NOT_ANALYZED = "not_analyzed";
	
	/** field should not be indexed */
	protected static final String NOT_INDEXED = "no";
	
	
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
	public Mapping<String> getMapping() {
		try {
			final String documentType = this.getDocumentType();
			XContentBuilder commonPostResourceFields = XContentFactory.jsonBuilder()
					.startObject()
						.startObject(documentType)
							/*
							 * set the date detection to false: we load the misc
							 * fields as field = value into es (=> dynamic mapping)
							 */
							.field("date_detection", false)
							.startObject("properties")
								.startObject(ESConstants.Fields.Resource.INTRAHASH)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(ESConstants.Fields.Resource.INTERHASH)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(ESConstants.Fields.TAGS)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
								.endObject()
								.startObject(ESConstants.Fields.USER_NAME)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(ESConstants.Fields.GROUPS)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								/*
								 * NOTE: we order our search requests by date
								 * => this field must be analyzed by es 
								 */
								.startObject(ESConstants.Fields.DATE)
									.field(TYPE_FIELD, DATE_TYPE)
									.field(FORMAT_FIELD, DATE_TIME_FORMAT)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(ESConstants.Fields.CHANGE_DATE)
									.field(TYPE_FIELD, DATE_TYPE)
									.field(INDEX_FIELD, NOT_INDEXED)
									.field(FORMAT_FIELD, DATE_TIME_FORMAT)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(Fields.SYSTEM_URL)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(INDEX_FIELD, NOT_ANALYZED)
									.field(INCLUDE_IN_ALL_FIELD, false)
								.endObject()
								.startObject(ESConstants.Fields.Resource.TITLE)
									.field(TYPE_FIELD, STRING_TYPE)
									.field(BOOST_FIELD, 2)
								.endObject();
			
			this.doResourceSpecificMapping(commonPostResourceFields);
			
			final XContentBuilder finalObject = commonPostResourceFields
							.endObject()
						.endObject()
					.endObject();
			final String info = finalObject.string();
			final Mapping<String> mapping = new Mapping<>();
			mapping.setMappingInfo(info);
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
