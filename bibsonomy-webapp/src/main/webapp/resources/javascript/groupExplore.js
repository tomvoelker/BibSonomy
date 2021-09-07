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

/**
 * Prevent default of the search interface and now triggers the update of the counters and the post results.
 */
function formAction() {
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateCounters();
        updateResults(0);
    });
}

/**
 * Add action to all filter buttons in a section. On click set to active and update search results correspondingly.
 *
 * @param field the section identified by the field
 */
function initFilterButtons(field) {
    $('#filter-entries-' + field + ' > button').click(function() {
        $(this).toggleClass(ACTIVE_CLASS);
        updateCounters();
        updateResults(0);
    });
}

/**
 * AJAX updates
 */

/**
 * Get all inputs for the search query and update the posts lists via AJAX.
 *
 * @param page the selected pagination
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

/**
 * Get all inputs for the search query and update the counters on the filters accordingly via AJAX.
 */
function updateCounters() {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    var query = addFiltersToSearchQuery(search, filters);

    $.ajax({
        url: '/ajax/explore/group', // The url you are fetching the results.
        dataType: 'json',
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'distinctCount': true,
            'search': query
        },
        success : function(data) {
            updateFieldCounts('entrytype', data.entrytype);
            updateFieldCounts('year', data.year);
        },
        error: function(xhr, status, error) {
            updateFieldCountsFailed('entrytype');
            updateFieldCountsFailed('year');
        }
    });
}

/**
 * Update a filter section's counters.
 *
 * @param field the filter section name
 * @param counts the list of updated counts
 */
function updateFieldCounts(field, counts) {

    $('#filter-entries-' + field + ' > button').each(function() {
        var value = $(this).data('value');
        if (value in counts) {
            $(this).find('.badge').html(counts[value]);
            $(this).removeClass(HIDDEN_CLASS);
        } else {
            $(this).find('.badge').html(0);
            if (!$(this).hasClass(ACTIVE_CLASS)) {
                $(this).addClass(HIDDEN_CLASS);
            }
        }
    });
}

function updateFieldCountsFailed(field) {
    $('#filter-entries-' + field + ' > button').each(function() {
        $(this).find('.badge').html('?');
        $(this).removeClass(ACTIVE_CLASS);
    });
}

/**
 * query and filter builder
 */
function generateFilterQuery() {
    var filterQuery = [];

    $('.filter-list').each(function() {
        var selectedFiltersQuery = getFilterQuery(this)
        if (selectedFiltersQuery) filterQuery.push(selectedFiltersQuery);
    });

    return filterQuery.join(' AND ');
}

/**
 * query and filter builder for ONLY custom tags
 */
function generateTagsFilterQuery() {
    var filterQuery = [];

    var customQuery = getFilterQuery($('#filter-list-custom'));
    if (customQuery) filterQuery.push(customQuery);

    var tagsQuery = getFilterQuery($('#filter-list-tags'));
    if (tagsQuery) filterQuery.push(tagsQuery);

    return filterQuery.join(' AND ');
}

function getFilterQuery(filterList) {
    var selectedFilters = [];
    $(filterList).find('.btn.active').each(function() {
        selectedFilters.push($(this).data('value'));
    });

    var field = $(filterList).data('field');
    var matchValues = selectedFilters.join(' OR ');

    if (matchValues) {
        return field + ':(' + matchValues + ')';
    }

    return '';
}

/**
 * Concat the search query and the selected filters
 *
 * @param search the search
 * @param filters the selected filters
 * @returns the combined query
 */
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

/**
 * Removes all non-numeric filters in the year filter section.
 */
function validateYearFilters() {
    $('#filter-entries-year > button').each(function() {
        var element = $(this);
        if (isNaN(element.data('value'))) {
            element.remove();
        }
    });
}

/**
 * Initialize the show more functionality in the year filter section.
 * At the beginning show max. 10 entries and expand by clicking on the link below.
 */
function showRelevantYears() {
    var entries = $('#filter-entries-year button');
    var listSize = entries.size();
    entries.hide();

    var num = 10;
    $('#filter-entries-year button:lt(' + num + ')').show();
    $('#filter-more-year').click(function () {
        num = (num + 10 <= listSize) ? num + 10 : listSize;
        $('#filter-entries-year button:lt(' + num + ')').show();
        if(num === listSize){
            $('#filter-more-year').hide();
        }
    });
}

/**
 * Create a button HTML-Element for a filter.
 *
 * @param name
 * @param filter
 * @param description
 * @returns {string}
 */
function createFilterButton(name, filter, description) {
    var element = '<button class="btn btn-default btn-block" ' +
        'title="' + description + '" ' +
        'data-value="' + name + '">' +
        description + '</button>';

    return element;
}

/**
 * Get the list of highlighted tags for the current PUMA system and add it as an additional filter section.
 */
function addTagFilters() {
    var entries = $('#filter-entries-tags');
    $.ajax({
        url: '/resources_puma/addons/explore/highlightTags.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            $.each(data, function(index, entity) {
                entries.append(createFilterButton(entity.tag, 'tags:' + entity.tag, entity.description));
            });
            initFilterButtons('tags');

            // show filter section
            $('#filter-list-tags').removeClass(HIDDEN_CLASS);
        }
    });
}

/**
 * Get the list of custom tags (for example: subgroups/subentities identified by tags) for the current PUMA system and add it as an additional filter section.
 */
function addCustomFilters() {
    var entries = $('#filter-entries-custom');
    $.ajax({
        url: '/resources_puma/addons/explore/customTags.json', // The url you are fetching the results.
        dataType: 'json',
        success : function(data) {
            data.results.bindings.forEach(function(entity) {
                entries.append(createFilterButton(entity.label.value, 'tags:' + entity.label.value, entity.facility.value));
            });
            initFilterButtons('custom');

            // show filter section
            $('#filter-list-custom').removeClass(HIDDEN_CLASS);
        }
    });
}

/**
 * Simple search for the custom tag list.
 */
function searchCustomFilters() {
    var search = $('#searchCustomFilters').val().toLowerCase();
    var entries = $('#filter-entries-custom');
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

/**
 * Initialize sorting buttons to update selection and the post results.
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