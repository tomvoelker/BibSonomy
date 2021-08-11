package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.List;

import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ajax.AjaxGroupExploreCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;

/**
 * AJAX controller to support clickable filters of publication lists for groups, while maintaining the search input.
 * Filtering supports: tags, authors, publication year, publication type
 *
 * @author kchoong
 */
public class GroupExploreAjaxController extends AjaxController implements MinimalisticController<AjaxGroupExploreCommand> {

    private LogicInterface logic;

    private User loggedInUser;

    /** the requested group */
    private String requestedGroup;
    private Group group;

    private final int ENTRIES_PER_PAGE = 20;

    @Override
    public View workOn(AjaxGroupExploreCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

        // get group details
        this.requestedGroup = command.getRequestedGroup();
        this.group = this.logic.getGroupDetails(requestedGroup, false);
        command.setGroup(this.group);

        // start + end
        final int postsPerPage = command.getPageSize();
        final int start = postsPerPage * command.getPage();

        // get posts of the group
        ListCommand<Post<BibTex>> bibtexCommand = command.getBibtex();
        bibtexCommand.setEntriesPerPage(ENTRIES_PER_PAGE);
        PostQueryBuilder builder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.GROUP)
                .setGroupingName(this.requestedGroup)
                .entriesStartingAt(postsPerPage, start)
                .search(command.getSearch());

        List<Post<BibTex>> posts = this.logic.getPosts(builder.createPostQuery(BibTex.class));
        bibtexCommand.setList(posts);

        return Views.AJAX_BIBTEXS;
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
