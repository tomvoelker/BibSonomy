const HIDDEN_CLASS = 'hidden';
const ACTIVE_CLASS = 'active';

/**
 * on load
 */
$(function() {
    // disable form action
    formAction();
    // add tag filters
    initFilterButtons('entrytype');
    initFilterButtons('year');
    addTagFilters();
    addCustomFilters();
    // remove invalid year buttons
    validateYearFilters();
    // init 'show more' for year list
    showRelevantYears();
    // add action to filter buttons
    initFilterButtons();
    // init sorting options
    initSortOptions();
    // add init results
    updateResults(0);
});

/**
 * input controls
 */
function formAction() {
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateCounters();
        updateResults(0);
    });
}

function initFilterButtons(field) {
    $('#filter-entries-' + field + ' > button').click(function() {
        $(this).toggleClass(ACTIVE_CLASS);
        updateResults(0);
    });
}

/**
 * AJAX updates
 */
function updateResults(page) {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    var query = addFiltersToSearchQuery(search, filters);
    var selectedSort =  $('#sorting-dropdown-menu > .sort-selected');
    var sortPage = selectedSort.data('key');
    var sortPageOrder = selectedSort.data('asc') ? 'asc' : 'desc';

    $.ajax({
        url: '/ajax/explore/group', // The url you are fetching the results.
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'search': query,
            'sortPage': sortPage,
            'sortPageOrder': sortPageOrder,
            'page': page, // Which page at the first time
            'pageSize': 20,
        },
        beforeSend: function() {
            $('#groupExplorePublications').empty();
            $('.custom-loader').removeClass(HIDDEN_CLASS);
        },
        success : function(data) {
            $('#groupExplorePublications').html(data);
        },
        complete: function() {
            $('.custom-loader').addClass(HIDDEN_CLASS);
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
            var distinctCounts = JSON.parse(xhr.responseText);
            console.log(JSON.stringify(distinctCounts));
            updateFieldCounts('entrytype', distinctCounts.entrytype);
            updateFieldCounts('year', distinctCounts.year);
        },
        success : function(data) {
            console.log("success?");
        }
    });
}

function updateFieldCounts(field, counts) {
    $('#filter-entries-' + field + ' > button').each(function() {
        var value = $(this).data('value');
        if (value in counts) {
            $(this).find('.badge').html(counts[value]);
            $(this).removeClass(HIDDEN_CLASS);
        } else {
            $(this).addClass(HIDDEN_CLASS);
            $(this).removeClass(ACTIVE_CLASS);
        }
    });
}

/**
 * query and filter builder
 */
function generateFilterQuery() {
    var filterQuery = [];

    $('.filter-list').each(function() {
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
    $('#filter-entries-year > button').each(function() {
        var element = $(this);
        if (isNaN(element.data('value'))) {
            element.remove();
        }
    });
}

function showRelevantYears() {
    var listSize = $('#filter-entries-year button').size();
    var num = 10;
    $('#filter-entries-year button:lt(' + num + ')').removeClass(HIDDEN_CLASS);
    $('#filter-more-year').click(function () {
        num = (num + 10 <= listSize) ? num + 10 : listSize;
        $('#filter-entries-year button:lt(' + num + ')').removeClass(HIDDEN_CLASS);
        if(num === listSize){
            $('#filter-more-year').addClass(HIDDEN_CLASS);
        }
    });
}

function createFilterButton(name, filter, description) {
    var element = '<button class="btn btn-default btn-block" ' +
        'title="' + description + '" ' +
        'data-filter="' + filter + '" ' +
        'data-value="' + name + '">' +
        description + '</button>';

    return element;
}

function addTagFilters() {
    var entries = $('#filter-entries-tags');
    $.ajax({
        url: '/resources_puma/addons/explore/tags.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            $.each(data, function(index, entity) {
                entries.append(createFilterButton(entity.tag, 'tags:' + entity.tag, entity.description));
            });
            initFilterButtons('tags');
        }
    });
}

function addCustomFilters() {
    var entries = $('#filter-entries-custom');
    $.ajax({
        url: '/resources_puma/addons/explore/custom.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            data.results.bindings.forEach(function(entity) {
                entries.append(createFilterButton(entity.label.value, 'tags:' + entity.label.value, entity.facility.value));
            });
            initFilterButtons('custom');
        }
    });
}

function searchCustomFilters() {
    var search = $('#searchCustomFilters').val().toLowerCase();
    var entries = $('.filter-list[data-field="custom"]').find('.filter-entries');
    entries.children().each(function() {
        var value = $(this).html().toLowerCase();
        if (value.indexOf(search) > -1) {
            $(this).removeClass(HIDDEN_CLASS);
        } else {
            $(this).addClass(HIDDEN_CLASS);
        }
    });
}

/**
 * sorting
 */

function initSortOptions() {
    var SELECTED_CLASS = 'sort-selected';
    $('#sorting-dropdown-menu > .sort-selection').click(function (e) {
        e.preventDefault();

        // hide all sorting order arrows
        $('.sort-order').addClass(HIDDEN_CLASS);

        // remove all elements as selected and selected the current element
        if ($(this).hasClass(SELECTED_CLASS)) {
            $(this).data('asc', !$(this).data('asc'))
        } else {
            $('#sorting-dropdown-menu > .sort-selection').removeClass(SELECTED_CLASS);
            $(this).addClass(SELECTED_CLASS);
        }

        // show the correct sorting arrow to display the order
        if ($(this).data('asc')) {
            $(this).find('.sort-asc').removeClass(HIDDEN_CLASS);
        } else {
            $(this).find('.sort-desc').removeClass(HIDDEN_CLASS);
        }

        // refresh results
        updateResults(0);
    });
}