package org.bibsonomy.webapp.controller.ajax;

import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.webapp.command.ajax.AjaxGroupExploreCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;

/**
 * AJAX controller to support clickable filters of publication lists for groups, while maintaining the search input.
 * Filtering supports: tags, authors, publication year, publication type
 *
 * @author kchoong
 */
public class GroupExploreAjaxController extends AjaxController implements MinimalisticController<AjaxGroupExploreCommand> {

    private LogicInterface logic;

    @Override
    public View workOn(AjaxGroupExploreCommand command) {
        return null;
    }

    @Override
    public AjaxGroupExploreCommand instantiateCommand() {
        return new AjaxGroupExploreCommand();
    }

    /**
     * @param logic the logic to set
     */
    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }

}
