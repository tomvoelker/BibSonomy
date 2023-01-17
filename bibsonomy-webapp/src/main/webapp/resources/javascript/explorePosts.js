/**
 * query and filter builder
 */
function generateFilterQuery() {
    var filterQuery = [];

    $('.filter-list').each(function () {
        var selectedFiltersQuery = getFilterQuery(this)
        if (selectedFiltersQuery) filterQuery.push(selectedFiltersQuery);
    });

    return filterQuery.join(' AND ');
}

function getFilterQuery(filterList) {
    var selectedFilters = [];
    $(filterList).find('.btn.active').each(function () {
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
    $('#filter-entries-year > button').each(function () {
        var element = $(this);
        if (isNaN(element.data('value'))) {
            element.remove();
        }
    });
}

/**
 * Create a button HTML-Element for a filter.
 *
 * @param tag
 * @param label
 * @returns {string}
 */
function createFilterButton(tag, label) {
    var element = '<button class="btn btn-default btn-block filter-entry" ' +
        'title="' + label + '" ' +
        'data-value="' + tag + '">' +
        label + '</button>';

    return element;
}

/**
 * sorting
 */

/**
 * Initialize sorting buttons to update selection and the post results.
 */
function initSortOptions(menuId, updateResults) {
    var SELECTED_CLASS = 'sort-selected';

    var menu = $('#' + menuId);

    $(menu).children('.sort-selection').click(function (e) {
        e.preventDefault();

        // hide all sorting order arrows
        $('.sort-order').addClass('hidden');

        // remove all elements as selected and selected the current element
        if ($(this).hasClass(SELECTED_CLASS)) {
            $(this).data('asc', !$(this).data('asc'))
        } else {
            $(menu).children('.sort-selection').removeClass(SELECTED_CLASS);
            $(this).addClass(SELECTED_CLASS);
        }

        // show the correct sorting arrow to display the order
        if ($(this).data('asc')) {
            $(this).find('.sort-asc').removeClass('hidden');
        } else {
            $(this).find('.sort-desc').removeClass('hidden');
        }

        // refresh results
        updateResults(0);
    });
}