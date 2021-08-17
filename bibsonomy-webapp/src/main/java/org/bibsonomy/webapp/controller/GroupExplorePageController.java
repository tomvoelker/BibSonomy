package org.bibsonomy.webapp.controller;

import static org.bibsonomy.util.ValidationUtils.present;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.FirstValuePairComparator;
import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SortKey;
import org.bibsonomy.common.enums.SortOrder;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.services.searcher.PostSearchQuery;
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

    private static final String ENTRYTYPE_FILTER = "entrytype";
    private static final String YEAR_FILTER = "year";
    private static final String AUTHOR_FILTER = "author";

    private LogicInterface logic;
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

    private User loggedInUser;

    /** the requested group */
    private String requestedGroup;
    private Group group;

    private final int ENTRIES_PER_PAGE = 20;

    @Override
    public View workOn(GroupExploreViewCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

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

        List<Post<BibTex>> posts = this.logic.getPosts(builder.createPostQuery(BibTex.class));
        bibtexCommand.setList(posts);

        // create filter list
        command.setEntrytypeFilters(generateEntrytypeFilters());
        command.setYearFilters(generateFilters(YEAR_FILTER, 200, true));

        return Views.GROUPEXPLOREPAGE;
    }

    private FieldDescriptor<BibTex, ?> createFieldDescriptor(String field) {
        return (FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply(field);
    }

    private List<SearchFilterElement> generateEntrytypeFilters() {
        List<SearchFilterElement> filters = generateFilters(ENTRYTYPE_FILTER, 20,false);
        for (SearchFilterElement element : filters) {
            element.setMessageKey(String.format("post.resource.entrytype.%s.title", element.getName()));
        }

        return filters;
    }

    private List<SearchFilterElement> generateFilters(String field, int size, boolean reverse) {
        // build query for group posts to aggregate for counts
        PostSearchQuery<BibTex> groupPostsQuery = new PostSearchQuery<>();
        groupPostsQuery.setGrouping(GroupingEntity.GROUP);
        groupPostsQuery.setGroupingName(this.requestedGroup);

        // get aggregated count by given field
        DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class, createFieldDescriptor(field));
        distinctFieldQuery.setPostQuery(groupPostsQuery);
        distinctFieldQuery.setSize(size);
        final Set<?> distinctFieldCounts = this.logic.getMetaData(this.loggedInUser, distinctFieldQuery);

        List<SearchFilterElement> filters = new ArrayList<>();
        for (Pair<String, Long> filter : (Set<Pair<String, Long>>) distinctFieldCounts) {
            SearchFilterElement filterElement = new SearchFilterElement(filter.getFirst(), filter.getSecond());
            filterElement.setField(field);
            filterElement.setFilter(filterElement.getField() + ":" + filterElement.getName());
            filters.add(filterElement);
        }
        Collections.sort(filters);

        if (reverse) {
            Collections.reverse(filters);
        }

        return filters;
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
