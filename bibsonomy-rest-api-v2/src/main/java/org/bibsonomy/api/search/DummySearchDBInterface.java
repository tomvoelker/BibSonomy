package org.bibsonomy.api.search;

import org.bibsonomy.search.management.database.SearchDBInterface;
import org.bibsonomy.search.model.SearchIndexState;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collections;

/**
 * No-op implementation used to satisfy legacy search beans when the full search
 * DB layer is not available.
 */
@SuppressWarnings("rawtypes")
public class DummySearchDBInterface implements SearchDBInterface {

    @Override
    public List getPostsForUser(final String userName, final int limit, final int offset) { return Collections.emptyList(); }
    @Override
    public List getContentIdsToDelete(final Date lastLogDate) { return Collections.emptyList(); }
    @Override
    public List getNewPosts(final int lastTasId, final int limit, final int offset) { return Collections.emptyList(); }
    @Override
    public List getPredictionForTimeRange(final Date fromDate, final Date toDate) { return Collections.emptyList(); }
    @Override
    public List getPersonMainNamesByChangeIdRange(final long firstChangeId, final long toPersonChangeIdExclusive) { return Collections.emptyList(); }
    @Override
    public List getPersonByChangeIdRange(final long firstChangeId, final long toPersonChangeIdExclusive) { return Collections.emptyList(); }
    @Override
    public List getResourcePersonRelationsByPublication(final String interHash) { return Collections.emptyList(); }
    @Override
    public List getPostsForDocumentUpdate(final Date lastDocumentDate, final Date targetDocumentDate) { return Collections.emptyList(); }

    @Override
    public SearchIndexState getDbState() {
        return new SearchIndexState();
    }
}
