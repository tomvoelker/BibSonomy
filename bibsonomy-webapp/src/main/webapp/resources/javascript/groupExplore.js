const PUBLICATIONS_SELECTOR = '#groupExplorePublications';

$(function() {
    formAction();
    initFilterButtons();
    initResultPagination();
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

    $(PUBLICATIONS_SELECTOR).scrollPagination({
        'url': '/ajax/explore/group?requestedGroup=' + groupName, // The url you are fetching the results.
        'data': {
            // These are the variables you can pass to the request
            'page': 0, // Which page at the first time
            'size': 1000, // Number of pages FIXME: controller should get the actual number after fixing the controller
            'pageSize': 20,
            'search': query
        },
        'scroller': $(window), // Who gonna scroll? default is the full window
        'autoload': true, // Change this to false if you want to load manually, default true.
        'heightOffset': 250, // It gonna request when scroll is 10 pixels before the page ends
        'loading': "#loading", // ID of loading prompt.
        'loadingText': 'click to loading more.', // Text of loading prompt.
        'loadingNomoreText': 'No more.', // No more of loading prompt.
        'manuallyText': 'click to loading more.', // Click of loading prompt.
        'before': function(){
            $(".cust-loader").fadeIn();
        },
        'after': function(elementsLoaded){
            // After loading content, you can use this function to animate your new elements
            $(".cust-loader").fadeOut();
        }
    });
}

function generateFilterQuery() {
    var filterQuery = [];

    $('.searchFilterList').each(function() {
        var selectedFilters = [];
        $(this).find('.btn.active span.filter').each(function() {
            selectedFilters.push($(this).html());
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