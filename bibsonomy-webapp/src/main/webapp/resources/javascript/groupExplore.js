/**
 * on load
 */
$(function () {
    initExtendedSearch();
    initFilters();
    initResourceMenu();

    // add initial results
    updateResults(0);
});

function initExtendedSearch() {
    /**
     * Prevent default of the search interface and now triggers the update of the counters and the post results.
     */
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateCounters();
        updateResults(0);
    });

    /**
     * Only show allowed selection field in extended search
     */
    var allowedSearchFields = ['title', 'author', 'editor', 'publisher', 'institution', 'doi', 'isbn', 'issn'];
    $('#dropdownSelectionField').children('li').each(function () {
        if (!allowedSearchFields.includes($(this).data('field'))) {
            $(this).hide(0);
        }
    });
}

function initFilters() {
    // add tag filters
    initFilterButtons('entrytype');
    initFilterButtons('year');
    // add highlight tag filter
    // addHighlightTagFilters();
    // add custom tag filters
    addCustomFilters();
    // remove invalid year buttons
    validateYearFilters();
    // init 'show more' for year list
    showRelevantYears();
}

function initResourceMenu() {
    // init sorting options
    initSortOptions('sorting-dropdown-menu', updateResults);

    // add parameters for export and batch edit, when clicked
    $('#batchEditButton, #export-dropdown-menu > li > a').click(function (e) {
        e.preventDefault();

        // append search query as a parameter to URL
        var href = $(this).attr('href');
        var query = buildSearchQuery();
        var hrefWithParams = href + '?search=' + query;

        // open URL with query in new tab
        window.open(hrefWithParams, '_blank').focus();
    })
}

/**
 * input controls
 */

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
    var query = buildSearchQuery();
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
    var query = buildSearchQuery();

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
 *
 * IMPORTANT: This requested JSON file has to be overwritten in the individual PUMA instances to use.
 */
function addHighlightTagFilters() {
    var entries = $('#filter-entries-tags');
    $.ajax({
        url: '/resources/explore/highlightTags.json', // The url you are fetching the results.
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
 *
 * IMPORTANT: This requested JSON file has to be overwritten in the individual PUMA instances to use.
 */
function addCustomFilters() {
    var entries = $('#filter-entries-custom');
    $.ajax({
        url: '/resources/explore/customTags.json', // The url you are fetching the results.
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

function buildSearchQuery() {
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    return addFiltersToSearchQuery(search, filters);
}

function resetFilterSelection() {
    $('.filter-entry').removeClass('active');
    updateCounters();
    updateResults(0);
}