/**
 * on load
 */
$(function () {
    // only show allowed selection field in extended search
    disableSearchFields();
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
    // init sorting options
    initSortOptions('sorting-dropdown-menu', updateResults);
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

var lastSelectedFilter;

/**
 * Add action to all filter buttons in a section. On click set to active and update search results correspondingly.
 *
 * @param field the section identified by the field
 */
function initFilterButtons(field) {
    $('#filter-entries-' + field + ' > button').click(function () {
        lastSelectedFilter = this;
        $(this).toggleClass('active');
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
    var selectedSort = $('#sorting-dropdown-menu > .sort-selected');
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
        beforeSend: function () {
            $('#groupExplorePublications').empty();
            $('.custom-loader').show(0);
        },
        success: function (data) {
            $('#groupExplorePublications').html(data);
        },
        complete: function () {
            $('.custom-loader').hide(0);
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
        success: function (data) {
            updateFieldCounts('entrytype', data.entrytype);
            updateFieldCounts('year', data.year);
        },
        error: function (xhr, status, error) {
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
    $('#filter-entries-' + field + ' > button').each(function () {
        var value = $(this).data('value');
        if (value in counts) {
            $(this).find('.badge').html(counts[value]);
            $(this).show(0);
        } else {
            $(this).find('.badge').html(0);
            if ($('.filter-entry-counter.active').length < 2 && !$(this).hasClass('active')) {
                $(this).hide(0);
            }
        }
    });
}

function updateFieldCountsFailed(field) {
    $('#filter-entries-' + field + ' > button').each(function () {
        $(this).find('.badge').html('?');
        $(this).removeClass('active');
    });
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

/**
 * Initialize the show more functionality in the year filter section.
 * At the beginning show max. 10 entries and expand by clicking on the link below.
 */
function showRelevantYears() {
    var entries = $('#filter-entries-year button');
    var listSize = entries.size();
    entries.addClass('hidden');

    var num = 10;
    $('#filter-entries-year button:lt(' + num + ')').removeClass('hidden');
    $('#filter-more-year').click(function () {
        num = (num + 10 <= listSize) ? num + 10 : listSize;
        $('#filter-entries-year button:lt(' + num + ')').removeClass('hidden');
        if (num === listSize) {
            $('#filter-more-year').hide(0);
        }
    });
}

/**
 * Get the list of highlighted tags for the current PUMA system and add it as an additional filter section.
 */
function addTagFilters() {
    var entries = $('#filter-entries-tags');
    $.ajax({
        url: '/resources_puma/addons/explore/highlightTags.json', // The url you are fetching the results.
        dataType: 'json',
        success: function (data) {
            $.each(data, function (index, entity) {
                entries.append(createFilterButton(entity.tag, 'tags:' + entity.tag, entity.description));
            });
            initFilterButtons('tags');

            // show filter section
            $('#filter-list-tags').removeClass('hidden');
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
        success: function (data) {
            data.results.bindings.forEach(function (entity) {
                entries.append(createFilterButton(entity.label.value, 'tags:' + entity.label.value, entity.facility.value));
            });
            initFilterButtons('custom');

            // show filter section
            $('#filter-list-custom').removeClass('hidden');
        }
    });
}

/**
 * Get the list of custom tags (for example: subgroups/subentities identified by tags) for the current PUMA system and add it as an additional filter section.
 */
function addCustomTreeFilters() {
    var entries = $('#filter-entries-custom');
    $.ajax({
        url: '/resources_puma/addons/explore/customTagsTree.json', // The url you are fetching the results.
        dataType: 'json',
        success: function (data) {
            entries.append(createFilterTree(data));
            // initFilterButtons('custom');

            // show filter section
            $('#filter-list-custom').removeClass('hidden');
        }
    });
}

/**
 * Simple search for the custom tag list.
 */
function searchCustomFilters() {
    var search = $('#searchCustomFilters').val().toLowerCase();
    var entries = $('#filter-entries-custom');
    $(entries).children().each(function () {
        var value = $(this).html().toLowerCase();
        if (value.indexOf(search) > -1) {
            $(this).show(0);
        } else {
            $(this).hide(0);
        }
    });
}

function createFilterTree(items) {
    var tree = $('<ul/>');

    $.each(items, function (i) {
        var item = items[i];
        var li = $("<li/>", {
            'data-value': item.label,
            'data-cnr': item.cnr,
            'data-from': item.fromDate,
            'data-until': item.untilDate
        }).appendTo(tree);
        var label = $("<span/>")
            .text(item.facility)
            .appendTo(li);
        if (item.LowerTags) {
            var children = createFilterTree(item.LowerTags);
            children.appendTo(tree);
        }
    });

    return tree;
}

function resetFilterSelection() {
    $('.filter-entry').removeClass('active');
    updateCounters();
    updateResults(0);
}

/**
 * Extended search interface
 */

var allowedSearchFields = ['title', 'author', 'editor', 'publisher', 'institution', 'doi', 'isbn', 'issn'];

function disableSearchFields() {
    $('#dropdownSelectionField').children('li').each(function () {
        if (!allowedSearchFields.includes($(this).data('field'))) {
            $(this).hide(0);
        }
    })
}