const unique_keys = [
    'year:from',
    'year:to'
]

const invalid_keys = [
    'entrytypes'
]

function switchSelection(type, selection) {
    const id = 'filterSelection' + type;
    var filterSelection = document.getElementById(id);
    filterSelection.innerHTML = selection.innerText;
}

function addFilter() {

    // term operator
    const operator = $('#filterOperator label.active input').val();

    // search
    const input = document.getElementById('extendedSearchInput');
    var query = input.value;

    // filters
    const yearFrom = document.getElementById('filterValueYearFrom').value;
    if (validateYear(yearFrom)) {
        query = appendFilter(query, operator, 'year:from', yearFrom);
    }

    const yearTo = document.getElementById('filterValueYearTo').value;
    if (validateYear(yearFrom)) {
        query = appendFilter(query, operator, 'year:to', yearTo);
    }

    const entrytype = document.getElementById('filterSelectionEntrytype').innerHTML;
    query = appendFilter(query, operator, 'entrytype', entrytype);


    const field = document.getElementById('filterSelectionField').innerHTML;
    const fieldValue = document.getElementById('filterValueField').value;
    query = appendFilter(query, operator, field, fieldValue);

    input.value = query;
}

function appendFilter(query, operator, key, value) {

    if (key == null || value == null) {
        return query;
    }

    if (key === '' || value === '') {
        return query;
    }

    // don't add, when key is just a label from a unselected dropdown
    const unselected = '<span class="unselected">';
    if (key.includes(unselected) || value.includes(unselected)) {
        return query;
    }

    const term = key + ':' + value;
    // check, if term is already in the search
    if (query.includes(term)) {
        // returns previous search to avoid duplicate terms
        return query;
    }

    // check, if a unique key is already in query
    if (unique_keys.includes(key) && query.includes(key + ':')) {
        // TODO replace value
        return query;
    }

    if (operator === 'OR') {
        return '(' + query + ')' + ' ' +  operator + ' ' + term
    }

    return query + ' ' +  operator + ' ' + term;
}

function validateYear(year) {
    return !isNaN(year);
}