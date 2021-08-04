package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.Calendar;
import java.util.List;

import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.webapp.command.GroupExploreViewCommand;
import org.bibsonomy.webapp.command.GroupResourceViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.SearchViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for group explore pages
 *
 * /explore/group/GROUP
 *
 * @author kchoong
 */
public class GroupExplorePageController extends SingleResourceListController implements MinimalisticController<GroupExploreViewCommand>, ErrorAware {

    private LogicInterface logic;

    /** the requested group */
    private String requestedGroup;
    private Group group;

    private final int ENTRIES_PER_PAGE = 20;

    @Override
    public View workOn(GroupExploreViewCommand command) {
        // get group details
        this.requestedGroup = command.getRequestedGroup();
        this.group = this.logic.getGroupDetails(requestedGroup, false);
        command.setGroup(this.group);

        // get posts of the group
        ListCommand<Post<BibTex>> bibtexCommand = command.getBibtex();
        bibtexCommand.setEntriesPerPage(ENTRIES_PER_PAGE);
        PostQueryBuilder builder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.GROUP)
                .setGroupingName(this.requestedGroup)
                .entriesStartingAt(bibtexCommand.getEntriesPerPage(), bibtexCommand.getStart())
                .searchAndSortCriteria(command.getSearch(), new SortCriteria(SortKey.PUBDATE, SortOrder.DESC));

        if (!present(command.getSearch())) {
            /*
             * If there is no search given, for example when the page is viewed for the first time.
             * Show latest publications to current year without textual years like: to appear, submitted
             */
            final Calendar calendar = Calendar.getInstance();
            builder.search(String.format("year:[* TO %s]", calendar.get(Calendar.YEAR)));
        }

        List<Post<BibTex>> posts = this.logic.getPosts(builder.createPostQuery(BibTex.class));
        bibtexCommand.setList(posts);

        return Views.GROUPEXPLOREPAGE;
    }

    @Override
    public GroupExploreViewCommand instantiateCommand() {
        return new GroupExploreViewCommand();
    }

    /**
     * @param logic the logic to set
     */
    public void setLogic(LogicInterface logic) {
        this.logic = logic;
    }

    /**
     *
     * @param requestedGroup the requested group to set
     */
    public void setRequestedGroup(String requestedGroup) {
        this.requestedGroup = requestedGroup;
    }

    /**
     * @param group the actual requested group to set
     */
    public void setGroup(Group group) {
        this.group = group;
    }

    @Override
    public Errors getErrors() {
        return null;
    }

    @Override
    public void setErrors(Errors errors) {

    }

}
