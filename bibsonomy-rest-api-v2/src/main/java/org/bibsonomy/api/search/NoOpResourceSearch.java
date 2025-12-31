package org.bibsonomy.api.search;

import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.statistics.Statistics;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.services.searcher.ResourceSearch;
import org.bibsonomy.util.object.FieldDescriptor;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Minimal no-op searcher that returns empty results. Used to satisfy legacy
 * search wiring without pulling in the legacy search stack.
 *
 * <p>All methods return non-null empty collections. This class is thread-safe
 * and can be used as a singleton.
 */
public class NoOpResourceSearch implements ResourceSearch<Resource> {

    /**
     * Returns an empty list of posts.
     * @return non-null empty list
     */
    @Override
    public List<Post<Resource>> getPosts(final User loggedinUser, final PostSearchQuery<?> postQuery) {
        return Collections.emptyList();
    }

    /**
     * Returns empty statistics.
     * Statistics is mutable (Lombok @Setter), so return a fresh instance each time
     * to prevent callers from mutating shared state.
     * @return non-null fresh Statistics instance with default values
     */
    @Override
    public Statistics getStatistics(final User loggedinUser, final PostSearchQuery<?> postQuery) {
        return new Statistics();
    }

    /**
     * Returns an empty list of tags.
     * @return non-null empty list
     */
    @Override
    public List<Tag> getTags(final User loggedinUser, final PostSearchQuery<?> postQuery) {
        return Collections.emptyList();
    }

    /**
     * Returns an empty set of field counts.
     * @return non-null empty set
     */
    @Override
    public <E> Set<E> getDistinctFieldCounts(final FieldDescriptor<? extends Resource, E> fieldDescriptor) {
        return Collections.emptySet();
    }
}
