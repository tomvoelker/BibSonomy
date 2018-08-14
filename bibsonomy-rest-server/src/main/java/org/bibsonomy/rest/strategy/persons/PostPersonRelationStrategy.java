package org.bibsonomy.rest.strategy.persons;

import org.bibsonomy.rest.strategy.AbstractCreateStrategy;
import org.bibsonomy.rest.strategy.Context;

import java.io.Writer;

public class PostPersonRelationStrategy extends AbstractCreateStrategy {

    private final String userName;

    /**
     * @param context
     */
    public PostPersonRelationStrategy(Context context, String userName) {
        super(context);
        this.userName = userName;
    }

    @Override
    protected void render(Writer writer, String resourceID) {

    }

    @Override
    protected String create() {
        return null;
    }
}
