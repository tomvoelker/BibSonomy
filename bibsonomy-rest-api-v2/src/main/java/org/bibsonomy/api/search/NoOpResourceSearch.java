package org.bibsonomy.api.search;

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
 */
@SuppressWarnings("rawtypes")
public class NoOpResourceSearch implements ResourceSearch<Resource> {
    @Override
    public List getPosts(final User loggedinUser, final PostSearchQuery postQuery) {
        return Collections.emptyList();
    }

    @Override
    private static final Statistics EMPTY_STATISTICS = new Statistics();

    @Override
    public Statistics getStatistics(final User loggedinUser, final PostSearchQuery postQuery) {
        return EMPTY_STATISTICS;
    }

    @Override
    public List<Tag> getTags(final User loggedinUser, final PostSearchQuery postQuery) {
        return Collections.emptyList();
    }

    @Override
    public Set getDistinctFieldCounts(final FieldDescriptor fieldDescriptor) {
        return Collections.emptySet();
    }
}
