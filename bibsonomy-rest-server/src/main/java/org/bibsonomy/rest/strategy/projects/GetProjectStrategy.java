package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectMovedException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.model.cris.Project;
import org.bibsonomy.rest.ViewModel;
import org.bibsonomy.rest.exceptions.NoSuchResourceException;
import org.bibsonomy.rest.strategy.Context;
import org.bibsonomy.rest.strategy.Strategy;

import java.io.ByteArrayOutputStream;

/**
 * strategy to get a project by its id
 *
 * @author pda
 */
public class GetProjectStrategy extends Strategy {
    private final String projectId;

    /**
     * constructor
     *
     * @param context
     * @param projectId
     */
    public GetProjectStrategy(final Context context, final String projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    public void perform(final ByteArrayOutputStream outStream)
            throws InternServerException, NoSuchResourceException, ObjectMovedException, ObjectNotFoundException {
        final Project project = this.getLogic().getProjectDetails(projectId);
        if (project == null) {
            throw new NoSuchResourceException("The requested project with id '" + projectId + "' does not exist.");
        }
        this.getRenderer().serializeProject(writer, project, new ViewModel());
    }
}
