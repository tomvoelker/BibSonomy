package org.bibsonomy.database.managers.metadata;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldValuesQuery;
import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;

/**
 * field distinct values
 *
 * @author dzo
 * @param <E>
 */
public class DistinctFieldValuesProvider<E> implements MetaDataProvider<Set<E>> {

	private Map<Class<?>, Function<Function<?, E>, Set<E>>> providers;

	/**
	 * @param providers the providers
	 */
	public DistinctFieldValuesProvider(Map<Class<?>, Function<Function<?, E>, Set<E>>> providers) {
		this.providers = providers;
	}

	@Override
	public Set<E> getMetaData(MetaDataQuery<Set<E>> metaDataQuery) {
		final DistinctFieldValuesQuery<?, E> query = (DistinctFieldValuesQuery<?, E>) metaDataQuery;
		final Class<?> clazzForMetaData = query.getClazz();
		final Function<?, E> getter = query.getFieldGetter();
		return this.providers.get(clazzForMetaData).apply(getter);
	}
}
