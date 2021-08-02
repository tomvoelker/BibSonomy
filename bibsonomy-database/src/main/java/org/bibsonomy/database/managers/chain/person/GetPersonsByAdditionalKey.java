package org.bibsonomy.database.managers.chain.person;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Collections;
import java.util.List;

import org.bibsonomy.database.common.DBSession;
import org.bibsonomy.database.managers.PersonDatabaseManager;
import org.bibsonomy.database.managers.chain.util.QueryAdapter;
import org.bibsonomy.model.Person;
import org.bibsonomy.model.extra.AdditionalKey;
import org.bibsonomy.model.logic.query.PersonQuery;

public class GetPersonsByAdditionalKey extends PersonChainElement {

    /**
     * default constructor
     *
     * @param personDatabaseManager
     */
    public GetPersonsByAdditionalKey(final PersonDatabaseManager personDatabaseManager) {
        super(personDatabaseManager);
    }

    @Override
    protected List<Person> handle(QueryAdapter<PersonQuery> param, DBSession session) {
        final AdditionalKey additionalKey = param.getQuery().getAdditionalKey();
        final Person personByAdditionalKey = this.getPersonDatabaseManager()
                .getPersonByAdditionalKey(additionalKey.getKeyName(), additionalKey.getKeyValue(), session);
        if (present(personByAdditionalKey)) {
            return Collections.singletonList(personByAdditionalKey);
        }

        return Collections.emptyList();
    }

    @Override
    protected boolean canHandle(QueryAdapter<PersonQuery> param) {
        return present(param.getQuery().getAdditionalKey());
    }
}
