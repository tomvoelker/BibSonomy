package org.bibsonomy.webapp.controller;

import static org.bibsonomy.model.BibTex.ENTRYTYPE_FIELD_NAME;
import static org.bibsonomy.model.BibTex.YEAR_FIELD_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Tag;
import org.bibsonomy.model.User;
import org.bibsonomy.model.extra.SearchFilterElement;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.GroupExploreViewCommand;
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

    private static final String PRESET_FIELD_NAME = "preset";

    private LogicInterface logic;
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

    private User loggedInUser;

    /** the requested group */
    private String requestedGroup;
    private Group group;

    @Override
    public View workOn(GroupExploreViewCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

        // get group details
        this.requestedGroup = command.getRequestedGroup();
        this.group = this.logic.getGroupDetails(requestedGroup, false);
        command.setGroup(this.group);

        // create filter list
        command.addFilters(ENTRYTYPE_FIELD_NAME, generateEntrytypeFilters());
        command.addFilters(YEAR_FIELD_NAME, generateFilters(YEAR_FIELD_NAME, 200, true));
        command.addFilters(PRESET_FIELD_NAME, generatePresetTagFilters(group.getPresetTags()));

        return Views.GROUPEXPLOREPAGE;
    }

    private FieldDescriptor<BibTex, ?> createFieldDescriptor(String field) {
        return (FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply(field);
    }

    private List<SearchFilterElement> generatePresetTagFilters(List<Tag> presetTags) {
        List<SearchFilterElement> filters = new ArrayList<>();
        for (Tag tag : presetTags) {
            SearchFilterElement filterElement = new SearchFilterElement(tag.getName());
            filterElement.setField(PRESET_FIELD_NAME);
            filterElement.setTooltip(tag.getDescription());
            filters.add(filterElement);
        }

        return filters;
    }

    private List<SearchFilterElement> generateEntrytypeFilters() {
        List<SearchFilterElement> filters = generateFilters(ENTRYTYPE_FIELD_NAME, 20,false);
        for (SearchFilterElement element : filters) {
            element.setMessageKey(String.format("post.resource.entrytype.%s.title", element.getName()));
            element.setTooltipKey(String.format("post.resource.entrytype.%s.description", element.getName()));
        }

        return filters;
    }

    /**
     * Generate a list of search filter elements of the entire group posts.
     *
     * @param field the index field
     * @param size the bucket size
     * @param reverse reverse sort order by name
     * @return
     */
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
