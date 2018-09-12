package org.bibsonomy.search.es.index.generator;

/**
 * provides entity informations
 * @param <E>
 */
public interface EntityInformationProvider<E> {

	int getContentId(E e);

	String getEntityId(E entity);

	String getType();
}
