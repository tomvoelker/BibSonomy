const PUBLICATIONS_SELECTOR = '#groupExplorePublications';

$(function() {
    formAction();
    initFilterButtons();
    initResultPagination();
    validateYearFilters();
});

function formAction() {
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateResultPagination();
    });
}

function initFilterButtons() {
    $('.searchFilterList .btn').click(function() {
        $(this).toggleClass('active');
        updateResultPagination();
    });
}

function updateResultPagination() {
    // empty shown publications
    $(PUBLICATIONS_SELECTOR).empty();
    // init new result pagination with updated query
    initResultPagination();
}

function initResultPagination() {
    var groupName = $('#requestedGroup').html();
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    var query = addFiltersToSearchQuery(search, filters);

    $.ajax({
        'url': '/ajax/explore/group?requestedGroup=' + groupName, // The url you are fetching the results.
        'data': {
            // These are the variables you can pass to the request
            'page': 0, // Which page at the first time
            'pageSize': 20,
            'search': query
        },
        success : function(data) {
            $(PUBLICATIONS_SELECTOR).html(data);
        }
    });
}

function generateFilterQuery() {
    var filterQuery = [];

    $('.searchFilterList').each(function() {
        var selectedFilters = [];
        $(this).find('.btn.active').each(function() {
            selectedFilters.push($(this).data('filter'));
        });
        var selectedFiltersQuery = selectedFilters.join(' OR ');
        if (selectedFiltersQuery) filterQuery.push('(' + selectedFiltersQuery + ')');
    });

    return filterQuery.join(' AND ');
}

function addFiltersToSearchQuery(search, filters) {
    var query = '';
    if (search) {
        query += search;
        if (filters) {
            query += ' AND ';
            query += filters;
        }
    } else {
        query += filters;
    }

    return query;
}

function validateYearFilters() {
    $('.searchFilterList').each(function() {
        if ($(this).data('field') === 'year') {
            $(this).children('button').each(function() {
                var element = $(this);
                if (isNaN(element.data('value'))) {
                    element.remove();
                }
            });
        }
    });
}

function switchSortKey(sortKey, element) {
    var element = $(element);
    var label = $('#labelSortKey');
    label.attr('data-sortkey', sortKey);
    label.html(element.html());
}

function switchSortOrder(sortOrder, element) {
    var element = $(element);
    var label = $('#labelSortOrder');
    label.attr('data-sortorder', sortOrder);
    label.html(element.html());
}