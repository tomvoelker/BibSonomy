package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.sf.json.JSONArray;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.GroupExploreViewCommand;
import org.bibsonomy.webapp.command.ListCommand;
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
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

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

        List<Pair<String, Integer>> entrytypes = new ArrayList<>();
        entrytypes.add(new Pair<>("article", 6));
        entrytypes.add(new Pair<>("newspaper", 35));
        command.setEntrytypeFilters(entrytypes);


        final Set<?> values = this.logic.getMetaData(new DistinctFieldQuery<>(BibTex.class, createFieldDescriptor("entrytype")));

        final JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(values);

        return Views.GROUPEXPLOREPAGE;
    }

    private FieldDescriptor<BibTex, ?> createFieldDescriptor(String field) {
        return (FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply(field);
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
     * @param mappers the mappers to set
     */
    public void setMappers(Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers) {
        this.mappers = mappers;
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
