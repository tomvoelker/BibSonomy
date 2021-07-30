package org.bibsonomy.search.es.index.generator.post;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;

/**
 * entity provider for community posts
 *
 * @author dzo
 */
public class CommunityPostEntityInformationProvider<R extends Resource> extends PostEntityInformationProvider<R> {
	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 * @param resourceType
	 */
	public CommunityPostEntityInformationProvider(final Converter converter, MappingBuilder mappingBuilder, Class resourceType) {
		super(converter, mappingBuilder, resourceType);
	}

	@Override
	public String getEntityId(final Post<R> entity) {
		return entity.getResource().getInterHash();
	}
}
