package org.bibsonomy.webapp.controller;

import static org.bibsonomy.model.BibTex.ENTRYTYPE_FIELD_NAME;
import static org.bibsonomy.model.BibTex.YEAR_FIELD_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import lombok.Setter;
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
import org.bibsonomy.webapp.command.ExploreViewCommand;
import org.bibsonomy.webapp.util.ErrorAware;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.springframework.validation.Errors;

/**
 * Controller for explore pages
 *
 * /explore/group/GROUP
 * /explore/user/USER
 *
 * @author kchoong
 */

@Setter
public class ExplorePageController extends SingleResourceListController implements MinimalisticController<ExploreViewCommand>, ErrorAware {

    private static final String PRESET_FIELD_NAME = "preset";

    private LogicInterface logic;
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

    private User loggedInUser;

    /** the requested content type */
    private GroupingEntity entityType;

    /** the requested content name for user/group */
    private String requestedName;

    private User user;
    private Group group;

    private Errors errors;

    @Override
    public View workOn(ExploreViewCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

        // get details for requested entity
        this.requestedName = command.getRequestedName();

        // create filter list
        command.addFilters(ENTRYTYPE_FIELD_NAME, generateEntrytypeFilters());
        command.addFilters(YEAR_FIELD_NAME, generateFilters(YEAR_FIELD_NAME, 200, true));

        switch (this.entityType) {
            case USER:
                this.user = this.logic.getUserDetails(requestedName);
                command.setUser(this.user);
                return Views.USEREXPLOREPAGE;
            case GROUP:
                this.group = this.logic.getGroupDetails(requestedName, false);
                command.setGroup(this.group);
                // Add filters for group's preset tags
                command.addFilters(PRESET_FIELD_NAME, generatePresetTagFilters(group.getPresetTags()));
                return Views.GROUPEXPLOREPAGE;
            default:
                return Views.ERROR;
        }
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
        PostSearchQuery<BibTex> postsQuery = new PostSearchQuery<>();
        postsQuery.setGrouping(this.entityType);
        postsQuery.setGroupingName(this.requestedName);

        // get aggregated count by given field
        DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class, createFieldDescriptor(field));
        distinctFieldQuery.setPostQuery(postsQuery);
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
    public ExploreViewCommand instantiateCommand() {
        ExploreViewCommand command = new ExploreViewCommand();
        command.setEntityType(this.entityType);
        return command;
    }

    @Override
    public Errors getErrors() {
        return this.errors;
    }

    @Override
    public void setErrors(Errors errors) {
        this.errors = errors;
    }

}
