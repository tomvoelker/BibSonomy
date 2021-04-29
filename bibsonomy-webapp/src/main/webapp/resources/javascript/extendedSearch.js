const invalid_keys = [
    'entrytypes'
]

const AND = 'AND';
const OR = 'OR';
const NOT = 'NOT';

function resetExtendedSearch() {
    $('#extendedSearchInput').val('');
    $('#extendedSearchForm').submit();
}

function toggleExtendedSearch(focusTarget) {
    $('#search').toggleClass('hidden');
    $('#extendedSearch').toggleClass('hidden');
    // set focus
    var input = $(focusTarget);
    input.focus();

    // move cursor to end
    var inputValue = input.val();
    input.val('').val(inputValue);
}

function expandYearRange() {
    $('#inputGroupField').removeClass('col-md-8').addClass('col-md-6');
    $('#inputGroupYear').toggleClass('hidden');
    $('#filterValueYear').val('');
    $('#inputGroupFromYear').toggleClass('hidden');
    $('#inputGroupToYear').toggleClass('hidden');
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
    const fromYear = $('#filterValueFromYear').val();
    const toYear = $('#filterValueToYear').val();

    if (validateYear(year)) {
        // check if it's simple year input
        query = appendFilter(query, operator, 'year', year);
    } else {
        // else append year range
        if (validateYear(fromYear) || validateYear(toYear)) {
            query = appendFilter(query, operator, 'year', '[' + fromYear + ' TO ' + toYear + ']');
        }
    }

    const entrytype = $('#filterSelectionEntrytype').children().first().text();
    query = appendFilter(query, operator, 'entrytype', entrytype);


    const field = $('#filterSelectionField').children().first().text();
    const fieldValue = $('#filterValueField').val();
    query = appendFilter(query, operator, field, fieldValue);

    input.val(query);
}

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
    var keyIndex = query.indexOf(key + ':');
    if (keyIndex >= 0) {
        // replace filter term on this key, since using AND
        var nextSpaceIndex = query.indexOf(' ', keyIndex)
        if (nextSpaceIndex < 0) {
            nextSpaceIndex = query.length;
        }
        var oldTerm = query.substr(keyIndex, nextSpaceIndex - keyIndex);
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
