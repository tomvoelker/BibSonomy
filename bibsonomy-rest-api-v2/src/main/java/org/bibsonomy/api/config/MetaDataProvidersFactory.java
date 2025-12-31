package org.bibsonomy.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.bibsonomy.database.managers.metadata.DistinctFieldProvider;
import org.bibsonomy.database.managers.metadata.MetaDataProvider;
import org.bibsonomy.database.managers.metadata.ProjectMetaDataAdapter;
import org.bibsonomy.database.managers.metadata.ResourceMetaDataProvider;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.GoldStandardPublication;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.services.searcher.ProjectSearch;
import org.bibsonomy.services.searcher.ResourceSearch;
/**
 * Helper to assemble the metaDataProviders map without fighting Kotlin's type inference.
 */
final class MetaDataProvidersFactory {

    private MetaDataProvidersFactory() {
    }

    static Map<Class<?>, MetaDataProvider<?>> build(
            final ProjectSearch projectSearch,
            final ResourceSearch<GoldStandardPublication> goldStandardPublicationSearch,
            final ResourceSearch<BibTex> publicationSearch,
            final Class<?> projectClass,
            final Class<?> goldStandardPublicationClass,
            final Class<?> publicationClass) {

        Objects.requireNonNull(projectSearch, "projectSearch must not be null");
        Objects.requireNonNull(goldStandardPublicationSearch, "goldStandardPublicationSearch must not be null");
        Objects.requireNonNull(publicationSearch, "publicationSearch must not be null");
        Objects.requireNonNull(projectClass, "projectClass must not be null");
        Objects.requireNonNull(goldStandardPublicationClass, "goldStandardPublicationClass must not be null");
        Objects.requireNonNull(publicationClass, "publicationClass must not be null");

        @SuppressWarnings({"rawtypes", "unchecked"})
        final Map providers = new HashMap();
        providers.put(projectClass, new ProjectMetaDataAdapter(projectSearch));
        providers.put(goldStandardPublicationClass, new ResourceMetaDataProvider(goldStandardPublicationSearch));
        providers.put(publicationClass, new ResourceMetaDataProvider(publicationSearch));

        @SuppressWarnings({"rawtypes", "unchecked"})
        final Map<Class<?>, MetaDataProvider<?>> metaDataProviders = new HashMap<>();
        metaDataProviders.put(DistinctFieldQuery.class, new DistinctFieldProvider(providers));
        return metaDataProviders;
    }
}
