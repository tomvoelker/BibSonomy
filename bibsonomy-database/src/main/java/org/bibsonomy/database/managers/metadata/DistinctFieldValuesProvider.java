package org.bibsonomy.database.managers.metadata;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldValuesQuery;
import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * field distinct values
 *
 * @param <E>
 * @author dzo
 */
public class DistinctFieldValuesProvider<E> implements MetaDataProvider<Set<E>> {

	private Map<Class<?>, Function<FieldDescriptor<?, E>, Set<E>>> providers;

	/**
	 * @param providers the providers
	 */
	public DistinctFieldValuesProvider(final Map<Class<?>, Function<FieldDescriptor<?, E>, Set<E>>> providers) {
		this.providers = providers;
	}

	@Override
	public Set<E> getMetaData(final MetaDataQuery<Set<E>> metaDataQuery) {
		final DistinctFieldValuesQuery<?, E> query = (DistinctFieldValuesQuery<?, E>) metaDataQuery;
		final Class<?> clazzForMetaData = query.getClazz();
		final FieldDescriptor<?, E> fieldDescriptor = query.getFieldDescriptor();
		return this.providers.get(clazzForMetaData).apply(fieldDescriptor);
	}
}
