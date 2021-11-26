const invalid_keys = [
    'entrytypes'
]

const AND = 'AND';
const OR = 'OR';
const NOT = 'NOT';

function toggleFilters() {
    $('#extendedFilters').slideToggle();
    $('#expandFilterLink').toggle(0);
    $('#hideFilterLink').toggle(0);
}

function resetExtendedSearch() {
    $('#extendedSearchInput').val('');
    $('#extendedSearchForm').submit();
}

function toggleExtendedSearch(focusTarget) {
    $('#search').toggle();
    $('#extendedSearch').slideToggle();
    // set focus
    var input = $(focusTarget);
    input.focus();

    // move cursor to end
    var inputValue = input.val();
    input.val('').val(inputValue);
}

function toggleYearRange() {
    $('#inputGroupYear').toggleClass('hidden');
    $('#inputGroupYearRange').toggleClass('hidden');

    if ($('#inputGroupYear').is(':visible')) {
        $('#toggleYearRange').html(getString('search.extended.year.range.placeholder'));
    } else {
        $('#toggleYearRange').html(getString('search.extended.year.placeholder'));
    }
}

function switchSelection(type, value, selection) {
    const id = '#filterSelection' + type;
    var filterSelection = $(id);
    filterSelection.html('<span style="display:none;">' + value + '</span>' + selection.innerText);
}

function addFilter() {

    // term operator
    const operator = $('#filterOperator label.active input').val();

    // search
    const input = $('#extendedSearchInput');
    var query = input.val();

    // filters
    const year = $('#filterValueYear').val();
    var fromYear = $('#filterValueFromYear').val();
    var toYear = $('#filterValueToYear').val();

    // simple year filter
    if ($('#inputGroupYear').is(':visible') && validateYear(year)) {
        query = appendFilter(query, operator, 'year', year);
    }

    // year range filter
    if ($('#inputGroupYearRange').is(':visible') && (validateYear(fromYear) || validateYear(toYear))) {
        // set to * if no upper or lower limit set
        fromYear = (fromYear !== '') ? fromYear : '*';
        toYear = (toYear !== '') ? toYear : '*';
        query = appendFilter(query, operator, 'year', '[' + fromYear + ' TO ' + toYear + ']');
    }

    const entrytype = $('#filterSelectionEntrytype').children().first().text();
    query = appendFilter(query, operator, 'entrytype', entrytype);

    const field = $('#filterSelectionField').children().first().text();
    const fieldValue = $('#filterValueField').val();
    query = appendFilter(query, operator, field, fieldValue);

    input.val(query);
}

var escapedFields = ['title', 'author', 'editor', 'publisher', 'institution']

function appendFilter(query, operator, key, value) {

    // check, if empty key or value
    if (key === '' || value === '') {
        return query;
    }

    // don't add, when key is just a label from a unselected dropdown
    const unselected = '<span class="unselected">';
    if (key.includes(unselected) || value.includes(unselected)) {
        return query;
    }

    if (escapedFields.includes(key)) {
        value = '"' + value + '"';
    }

    const term = key + ':' + value;

    // just append filter term, if query is empty
    if (query === '') {
        return term;
    }

    // check, if term is already in the search
    if (query.includes(term)) {
        // returns previous search to avoid duplicate terms
        return query;
    }

    switch(operator) {
        case AND:
            return appendFilterAnd(query, term, key);
        case OR:
            return appendFilterOr(query, term);
        default:
            return query;
    }
}

function appendFilterAnd(query, term, key) {
    const keyIndex = query.indexOf(key + ':');
    if (keyIndex >= 0) {
        var oldTerm = '';
        // check if range input
        if (query.charAt(keyIndex + key.length + 1) === '[') {
            const nextRightBracketIndex = query.indexOf(']', keyIndex);
            oldTerm = query.substring(keyIndex, nextRightBracketIndex + 1);
        } else {
            // replace filter term on this key, since using AND
            var nextSpaceIndex = query.indexOf(' ', keyIndex)
            if (nextSpaceIndex < 0) {
                nextSpaceIndex = query.length;
            }
            oldTerm = query.substring(keyIndex, nextSpaceIndex);
        }

        return query.replace(oldTerm, term);
    }

    return query + ' ' + AND + ' ' + term;
}

function appendFilterOr(query, term) {
    return '(' + query + ')' + ' ' + OR + ' ' + term;
}

function validateYear(year) {
    return year !== '' && !isNaN(year);
}
