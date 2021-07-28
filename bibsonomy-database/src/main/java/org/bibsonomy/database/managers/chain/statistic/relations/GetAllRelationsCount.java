package org.bibsonomy.database.managers.chain.statistic.relations;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.statistics.Statistics;

import static org.bibsonomy.util.ValidationUtils.present;

/**
 * chain element that returns the total count of all relations by person id
 *
 * @author dzo
 */
public class GetAllRelationsCount extends ResourcePersonRelationsStatisticsChainElement {

    public GetAllRelationsCount(final PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected Statistics handle(final ResourcePersonRelationQuery param, final DBSession session) {
        return new Statistics(this.personDatabaseManager.countResourcePersonRelationsWithPosts(param.getPersonId(), session));
    }

    @Override
    protected boolean canHandle(final ResourcePersonRelationQuery param) {
        return present(param.getPersonId());
    }
}
