package org.bibsonomy.database.managers.metadata;

import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.Resource;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.util.object.FieldDescriptor;

public class ResourceMetaDataProvider<E> implements Function<FieldDescriptor<? extends Resource, E>, Set<E>>  {

    private final ResourceSearch<? extends Resource> resourceSearch;

    /**
     * @param resourceSearch
     */
    public ResourceMetaDataProvider(ResourceSearch<? extends Resource> resourceSearch) {
        this.resourceSearch = resourceSearch;
    }

    @Override
    public Set<E> apply(FieldDescriptor<? extends Resource, E> fieldDescriptor) {
        return this.resourceSearch.getDistinctFieldCounts(fieldDescriptor);
    }

}
