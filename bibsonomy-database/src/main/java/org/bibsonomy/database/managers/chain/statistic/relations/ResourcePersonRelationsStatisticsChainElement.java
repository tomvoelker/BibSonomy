package org.bibsonomy.database.managers.chain.statistic.relations;

import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.ChainElement;
import org.bibsonomy.model.logic.query.ResourcePersonRelationQuery;
import org.bibsonomy.model.statistics.Statistics;

public abstract class ResourcePersonRelationsStatisticsChainElement extends ChainElement<Statistics, ResourcePersonRelationQuery> {

    protected PersonDatabaseManager personDatabaseManager;

    public ResourcePersonRelationsStatisticsChainElement(PersonDatabaseManager personDatabaseManager) {
        this.personDatabaseManager = personDatabaseManager;
    }
}
