package org.bibsonomy.database.managers.metadata;

import java.util.function.Function;

import org.bibsonomy.model.logic.query.statistics.meta.MetaDataQuery;
import org.bibsonomy.util.object.FieldDescriptor;

public class MetaFieldDescriptor<T,R> extends FieldDescriptor<T,R> {

    private final MetaDataQuery<T> query;

    /**
     * default constructor
     *
     * @param fieldName
     * @param fieldSetter
     */
    public MetaFieldDescriptor(MetaDataQuery<T> query, String fieldName, Function<T, R> fieldSetter) {
        super(fieldName, fieldSetter);
        this.query = query;
    }

    public MetaDataQuery<T> getQuery() {
        return query;
    }

}
