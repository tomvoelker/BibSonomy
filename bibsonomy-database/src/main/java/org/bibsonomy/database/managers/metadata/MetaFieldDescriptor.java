package org.bibsonomy.database.managers.metadata;

import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;
import org.bibsonomy.util.object.FieldDescriptor;

/**
 * A field descriptor to get meta statistics in the search index.
 *
 * @param <T>
 * @param <R>
 *
 * @author kchoong
 */
public class MetaFieldDescriptor<T,R> extends FieldDescriptor<T,R> {

    private MetaDataQuery<Set<R>> query;
    private User loggedInUser;

    /**
     *
     */
    public MetaFieldDescriptor(FieldDescriptor<T,R> fieldDescriptor) {
        super(fieldDescriptor.getFieldName(), fieldDescriptor.getFieldSetter());
    }

    /**
     * default constructor
     *
     * @param fieldName
     * @param fieldSetter
     */
    public MetaFieldDescriptor(MetaDataQuery<Set<R>> query, String fieldName, Function<T, R> fieldSetter) {
        super(fieldName, fieldSetter);
        this.query = query;
    }

    public MetaDataQuery<Set<R>> getQuery() {
        return query;
    }

    public void setQuery(MetaDataQuery<Set<R>> query) {
        this.query = query;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }
}
