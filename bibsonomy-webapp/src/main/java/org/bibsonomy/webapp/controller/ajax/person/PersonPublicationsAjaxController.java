package org.bibsonomy.webapp.controller.ajax.person;

import org.bibsonomy.layout.citeproc.renderer.AdhocRenderer;
import org.bibsonomy.layout.csl.CSLFilesManager;
import org.bibsonomy.services.URLGenerator;
import org.bibsonomy.webapp.command.ajax.AjaxPersonPageCommand;
import org.bibsonomy.webapp.controller.ajax.AjaxController;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

public class PersonPublicationsAjaxController extends AjaxController implements MinimalisticController<AjaxPersonPageCommand> {

    private AdhocRenderer renderer;
    private CSLFilesManager cslFilesManager;
    private URLGenerator urlGenerator;

    @Override
    public View workOn(AjaxPersonPageCommand command) {
        return null;
    }

    @Override
    public AjaxPersonPageCommand instantiateCommand() {
        return new AjaxPersonPageCommand();
    }

    public void setRenderer(AdhocRenderer renderer) {
        this.renderer = renderer;
    }

    public void setCslFilesManager(CSLFilesManager cslFilesManager) {
        this.cslFilesManager = cslFilesManager;
    }

    public void setUrlGenerator(URLGenerator urlGenerator) {
        this.urlGenerator = urlGenerator;
    }
}