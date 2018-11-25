package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

/**
 * @author dzo
 * @param <T>
 * @param <M>
 */
public abstract class OneToManyEntityInformationProvider<T, M> extends EntityInformationProvider<T> {

	private EntityInformationProvider<M> toManyEntityInformationProvider;

	/**
	 * the entity information provider
	 *
	 * @param converter
	 * @param mappingBuilder
	 */
	protected OneToManyEntityInformationProvider(Converter<T, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder, final EntityInformationProvider<M> toManyEntityInformationProvider) {
		super(converter, mappingBuilder);

		this.toManyEntityInformationProvider = toManyEntityInformationProvider;
	}

	/**
	 * returns the entity provider of the tomany entity
	 * @return
	 */
	public EntityInformationProvider<M> getToManyEntityInformationProvider() {
		return this.toManyEntityInformationProvider;
	}

}
