package org.bibsonomy.webapp.controller.ajax;

import static org.bibsonomy.model.BibTex.ENTRYTYPE_FIELD_NAME;
import static org.bibsonomy.model.BibTex.YEAR_FIELD_NAME;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.SortCriteria;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.SortUtils;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ajax.AjaxGroupExploreCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.simple.JSONObject;

/**
 * AJAX controller to support clickable filters of publication lists for groups, while maintaining the search input.
 * Filtering supports: tags, authors, publication year, publication type
 *
 * @author kchoong
 */
public class GroupExploreAjaxController extends AjaxController implements MinimalisticController<AjaxGroupExploreCommand> {
    
    private LogicInterface logic;
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

    private User loggedInUser;


    @Override
    public View workOn(AjaxGroupExploreCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

        // get group details
        final String requestedGroup = command.getRequestedGroup();
        final Group group = this.logic.getGroupDetails(requestedGroup, false);

        PostQueryBuilder builder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.GROUP)
                .setGroupingName(requestedGroup)
                .search(command.getSearch());

        // check, if only the distinct counts of the query should be retrieved
        if (command.isDistinctCount()) {
            return workOnDistinctCounts(command, builder);
        }

        return workOnPublications(command, builder);
    }

    public View workOnDistinctCounts(AjaxGroupExploreCommand command, PostQueryBuilder builder) {
        PostSearchQuery<BibTex> distinctPostQuery = new PostSearchQuery<>(builder.createPostQuery(BibTex.class));

        final JSONObject response = new JSONObject();
        JSONObject distinctCount = new JSONObject();
        distinctCount.put(ENTRYTYPE_FIELD_NAME, filtersToJSON(generateFilters(distinctPostQuery, ENTRYTYPE_FIELD_NAME, 20)));
        distinctCount.put(YEAR_FIELD_NAME, filtersToJSON(generateFilters(distinctPostQuery, YEAR_FIELD_NAME, 200)));
        response.put("data", distinctCount);
        response.put("success", true);

        // returns a JSON response
        command.setFormat(Views.FORMAT_STRING_JSON);
        command.setResponseString(response.toString());

        return Views.AJAX_JSON;
    }

    public View workOnPublications(AjaxGroupExploreCommand command, PostQueryBuilder builder) {
        // start + end
        final int postsPerPage = command.getPageSize();
        final int start = postsPerPage * command.getPage();
        builder.entriesStartingAt(postsPerPage, start);

        // sort criteria
        List<SortCriteria> sortCriteria = SortUtils.generateSortCriteria(SortUtils.parseSortKeys(command.getSortPage()), SortUtils.parseSortOrders(command.getSortPageOrder()));
        builder.setSortCriteria(sortCriteria);

        // get posts of the group
        ListCommand<Post<BibTex>> bibtexCommand = command.getBibtex();
        bibtexCommand.setEntriesPerPage(postsPerPage);
        bibtexCommand.setStart(start);

        List<Post<BibTex>> posts = this.logic.getPosts(builder.createPostQuery(BibTex.class));
        bibtexCommand.setList(posts);

        return Views.AJAX_BIBTEXS;
    }


    private FieldDescriptor<BibTex, ?> createFieldDescriptor(String field) {
        return (FieldDescriptor<BibTex, ?>) mappers.get(BibTex.class).apply(field);
    }


    /**
     * Retrieve a distinct count of a field with a given post search query.
     *
     * @param query the post search query
     * @param field the index field
     * @param size the size of buckets
     * @return
     */
    private Set<Pair<String, Long>> generateFilters(PostSearchQuery<BibTex> query, String field, int size) {
        // get aggregated count by given field
        DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class, createFieldDescriptor(field));
        distinctFieldQuery.setPostQuery(query);
        distinctFieldQuery.setSize(size);
        return (Set<Pair<String, Long>>) this.logic.getMetaData(this.loggedInUser, distinctFieldQuery);
    }

    /**
     * Create JSON object for a distinct count
     *
     * @param filters list of pairs with name and count
     * @return JSONObject JSON representation of the count
     */
    private JSONObject filtersToJSON(Set<Pair<String, Long>> filters) {
        JSONObject res = new JSONObject();
        for (Pair<String, Long> filter : filters) {
            res.put(filter.getFirst(), filter.getSecond());
        }
        return res;
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

    public void setMappers(Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers) {
        this.mappers = mappers;
    }
}
