package org.bibsonomy.search.es.index.generator;

import org.bibsonomy.search.util.Converter;
import org.bibsonomy.search.util.MappingBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * provides entity informations
 * @param <E>
 */
public abstract class EntityInformationProvider<E> {

	private final Converter<E, Map<String, Object>, ?> converter;

	private final MappingBuilder<XContentBuilder> mappingBuilder;

	/**
	 * the entity information provider
	 * @param converter
	 * @param mappingBuilder
	 */
	protected EntityInformationProvider(Converter<E, Map<String, Object>, ?> converter, MappingBuilder<XContentBuilder> mappingBuilder) {
		this.converter = converter;
		this.mappingBuilder = mappingBuilder;
	}

	public abstract int getContentId(E e);

	public abstract String getEntityId(E entity);

	public abstract String getType();

	/**
	 * @return the converter
	 */
	public Converter<E, Map<String, Object>, ?> getConverter() {
		return this.converter;
	}

	public MappingBuilder<XContentBuilder> getMappingBuilder() {
		return this.mappingBuilder;
	}

	/**
	 * returns all public fields of this type
	 */
	public Set<String> getPublicFields() {
		return Collections.emptySet();
	}

	/**
	 * @return a set of all private fields that should only be queryied by the owner of the entity
	 */
	public Set<String> getPrivateFields() {
		return Collections.emptySet();
	}

	/**
	 * the routing information for this entity
	 * @param entity
	 * @return
	 */
	public String getRouting(E entity) {
		return null;
	}
}
