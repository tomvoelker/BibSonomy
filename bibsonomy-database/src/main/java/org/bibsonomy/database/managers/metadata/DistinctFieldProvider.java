package org.bibsonomy.database.managers.metadata;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * field distinct values
 *
 * @param <E>
 * @author dzo
 */
public class DistinctFieldProvider<E> implements MetaDataProvider<Set<E>> {

	private Map<Class<?>, Function<FieldDescriptor<?, E>, Set<E>>> providers;

	/**
	 * @param providers the providers
	 */
	public DistinctFieldProvider(final Map<Class<?>, Function<FieldDescriptor<?, E>, Set<E>>> providers) {
		this.providers = providers;
	}

	@Override
	public Set<E> getMetaData(final User loggedInUser, final MetaDataQuery<Set<E>> metaDataQuery) {
		final DistinctFieldQuery<?, E> query = (DistinctFieldQuery<?, E>) metaDataQuery;
		final Class<?> clazzForMetaData = query.getClazz();
		final MetaFieldDescriptor<?, E> fieldDescriptor = new MetaFieldDescriptor<>(query.getFieldDescriptor());
		fieldDescriptor.setQuery(metaDataQuery);
		fieldDescriptor.setLoggedInUser(loggedInUser);
		return this.providers.get(clazzForMetaData).apply(fieldDescriptor);
	}
}
