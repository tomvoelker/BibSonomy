package org.bibsonomy.rest.strategy.projects;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.common.exceptions.ObjectNotFoundException;
import org.bibsonomy.common.exceptions.ResourceMovedException;
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

    public GetProjectStrategy(Context context, String projectId) {
        super(context);
        this.projectId = projectId;
    }

    @Override
    public void perform(ByteArrayOutputStream outStream)
            throws InternServerException, NoSuchResourceException, ResourceMovedException, ObjectNotFoundException {
        final Project project = getLogic().getProjectDetails(projectId);
        if (project.getExternalId() == null) {
            throw new NoSuchResourceException("The requested project with id '" + projectId + "' does not exist.");
        }
        getRenderer().serializeProject(writer, project, new ViewModel());
    }
}
