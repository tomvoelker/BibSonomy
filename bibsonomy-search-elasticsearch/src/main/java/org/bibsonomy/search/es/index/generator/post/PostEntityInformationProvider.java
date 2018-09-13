package org.bibsonomy.search.es.index.generator.post;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.factories.ResourceFactory;
import org.bibsonomy.search.es.index.generator.EntityInformationProvider;
import org.bibsonomy.search.es.management.util.ElasticsearchUtils;
import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * implementation of the {@link EntityInformationProvider} interface for posts
 * @param <R>
 */
public class PostEntityInformationProvider<R extends Resource> extends EntityInformationProvider<Post<R>> {

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
		return ElasticsearchUtils.createElasticSearchId(entity.getContentId());
	}

	@Override
	public String getType() {
		return ResourceFactory.getResourceName(this.resourceType);
	}
}
