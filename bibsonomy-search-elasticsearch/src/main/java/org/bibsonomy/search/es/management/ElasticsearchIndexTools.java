package org.bibsonomy.search.es.management;

import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.index.ResourceConverter;
import org.bibsonomy.search.es.index.ResourceMappingBuilder;

/**
 * TODO: add documentation to this class
 *
 * @author dzo
 * @param <R> 
 */
public class ElasticsearchIndexTools<R extends Resource> {
	private final Class<R> resourceType;
	
	private final ResourceConverter<R> converter;
	
	private final ResourceMappingBuilder<R> mappingBuilder;

	/**
	 * @param resourceType
	 * @param converter
	 * @param mappingBuilder
	 */
	public ElasticsearchIndexTools(Class<R> resourceType, ResourceConverter<R> converter, ResourceMappingBuilder<R> mappingBuilder) {
		super();
		this.resourceType = resourceType;
		this.converter = converter;
		this.mappingBuilder = mappingBuilder;
	}

	/**
	 * @return the resourceType
	 */
	public Class<R> getResourceType() {
		return this.resourceType;
	}
	
	/**
	 * @return the string representation of the resource type
	 */
	public String getResourceTypeAsString() {
		return ResourceFactory.getResourceName(this.resourceType);
	}

	/**
	 * @return the converter
	 */
	public ResourceConverter<R> getConverter() {
		return this.converter;
	}

	/**
	 * @return the mappingBuilder
	 */
	public ResourceMappingBuilder<R> getMappingBuilder() {
		return this.mappingBuilder;
	}
}
