const PUBLICATIONS_SELECTOR = '#groupExplorePublications';

$(function() {
    // disable form action
    formAction();
    // add custom tags
    addTagFilters();
    addCustomFilters();
    // remove invalid year buttons
    validateYearFilters();
    // add action to filter buttons
    initFilterButtons();
    // add init results
    initResultPagination(0);
});

function formAction() {
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateCounters();
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
    initResultPagination(0);
}

function initResultPagination(page) {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    var query = addFiltersToSearchQuery(search, filters);

    $.ajax({
        url: '/ajax/explore/group', // The url you are fetching the results.
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'page': page, // Which page at the first time
            'pageSize': 20,
            'search': query
        },
        beforeSend: function() {
            $('.custom-loader').removeClass('hidden');
        },
        success : function(data) {
            $(PUBLICATIONS_SELECTOR).html(data);
        },
        complete: function() {
            $('.custom-loader').addClass('hidden');
        },
    });
}

function updateCounters() {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();

    $.ajax({
        url: '/ajax/explore/group', // The url you are fetching the results.
        dataType: 'json',
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'distinctCount': true,
            'search': search
        },
        error: function(xhr, status, error) {
            console.log(status);
            console.log(xhr.responseText);
        },
        success : function(data) {
            console.log("working!");
        },
        complete : function() {
            console.log("working?");
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
    var yearFilterList = $('.searchFilterList[data-field="year"]');
    yearFilterList.find('.searchFilterEntries > button').each(function() {
        var element = $(this);
        if (isNaN(element.data('value'))) {
            element.remove();
        }
    });
}

function createFilterButton(name, filter, description) {
    var element = '<button class="btn btn-default btn-block" ' +
        'data-filter="' + filter + '" ' +
        'data-value="' + name + '">' +
        description + '</button>';

    return element;
}

function addTagFilters() {
    var entries = $('.searchFilterList[data-field="tags"]').find('.searchFilterEntries');
    $.ajax({
        url: '/resources_puma/addons/explore/tags.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            $.each(data, function(index, tag) {
                entries.append(createFilterButton(tag.name, "tags:" + tag.name, tag.description));
            });
        }
    });
}

function addCustomFilters() {
    var entries = $('.searchFilterList[data-field="custom"]').find('.searchFilterEntries');
    $.ajax({
        url: '/resources_puma/addons/explore/custom.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            data.results.bindings.forEach(function(entity) {
                entries.append(createFilterButton(entity.label.value, "tags:" + entity.label.value, entity.facility.value));
            });
        }
    });
}

function searchCustomFilters() {
    var search = $('#searchCustomFilters').val().toLowerCase();
    var entries = $('.searchFilterList[data-field="custom"]').find('.searchFilterEntries');
    entries.children().each(function() {
        var value = $(this).html().toLowerCase();
        if (value.indexOf(search) > -1) {
            $(this).removeClass('hidden');
        } else {
            $(this).addClass('hidden');
        }
    });
}