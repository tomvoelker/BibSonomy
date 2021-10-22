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
 * @param name
 * @param filter
 * @param description
 * @returns {string}
 */
function createFilterButton(name, filter, description) {
    var element = '<button class="btn btn-default btn-block filter-entry" ' +
        'title="' + description + '" ' +
        'data-value="' + name + '">' +
        description + '</button>';

    return element;
}

/**
 * sorting
 */

/**
 * Initialize sorting buttons to update selection and the post results.
 */
function initSortOptions() {
    var SELECTED_CLASS = 'sort-selected';
    var HIDDEN_CLASS = 'hidden';

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