package org.bibsonomy.webapp.controller.ajax;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.bibsonomy.common.Pair;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Group;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.logic.query.statistics.meta.DistinctFieldQuery;
import org.bibsonomy.model.logic.querybuilder.PostQueryBuilder;
import org.bibsonomy.services.searcher.PostSearchQuery;
import org.bibsonomy.util.object.FieldDescriptor;
import org.bibsonomy.webapp.command.ListCommand;
import org.bibsonomy.webapp.command.ajax.AjaxGroupExploreCommand;
import org.bibsonomy.webapp.util.MinimalisticController;
import org.bibsonomy.webapp.util.View;
import org.bibsonomy.webapp.view.Views;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * AJAX controller to support clickable filters of publication lists for groups, while maintaining the search input.
 * Filtering supports: tags, authors, publication year, publication type
 *
 * @author kchoong
 */
public class GroupExploreAjaxController extends AjaxController implements MinimalisticController<AjaxGroupExploreCommand> {

    private static final String ENTRYTYPE_FILTER = "entrytype";
    private static final String YEAR_FILTER = "year";
    private static final String AUTHOR_FILTER = "author";
    
    private LogicInterface logic;
    private Map<Class<?>, Function<String, FieldDescriptor<?, ?>>> mappers;

    private User loggedInUser;

    /** the requested group */
    private String requestedGroup;
    private Group group;

    @Override
    public View workOn(AjaxGroupExploreCommand command) {
        this.loggedInUser = command.getContext().getLoginUser();

        // get group details
        this.requestedGroup = command.getRequestedGroup();
        this.group = this.logic.getGroupDetails(requestedGroup, false);

        PostQueryBuilder builder = new PostQueryBuilder()
                .setGrouping(GroupingEntity.GROUP)
                .setGroupingName(this.requestedGroup)
                .search(command.getSearch());

        if (command.isDistinctCount()) {
            PostSearchQuery<BibTex> distinctPostQuery = new PostSearchQuery<>(builder.createPostQuery(BibTex.class));
            try {
                JSONObject distinctCount = new JSONObject();
                distinctCount.put(ENTRYTYPE_FILTER, filtersToJSON(generateFilters(distinctPostQuery, ENTRYTYPE_FILTER, 20)));
                distinctCount.put(YEAR_FILTER, filtersToJSON(generateFilters(distinctPostQuery, YEAR_FILTER, 200)));

                command.setResponseString(distinctCount.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Views.AJAX_JSON;
        }

        // start + end
        final int postsPerPage = command.getPageSize();
        final int start = postsPerPage * command.getPage();
        builder.entriesStartingAt(postsPerPage, start);

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


    private Set<Pair<String, Long>> generateFilters(PostSearchQuery<BibTex> query, String field, int size) {
        // get aggregated count by given field
        DistinctFieldQuery<BibTex, ?> distinctFieldQuery = new DistinctFieldQuery<>(BibTex.class, createFieldDescriptor(field));
        distinctFieldQuery.setPostQuery(query);
        distinctFieldQuery.setSize(size);
        return (Set<Pair<String, Long>>) this.logic.getMetaData(this.loggedInUser, distinctFieldQuery);
    }

    private JSONObject filtersToJSON(Set<Pair<String, Long>> filters) throws JSONException {
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
