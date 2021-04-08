const invalid_keys = [
    'entrytypes'
]

function toggleExtendedSearch(focusTarget) {
    $("#search").toggleClass("hidden");
    $("#extendedSearch").toggleClass("hidden");
    // set focus
    var input = document.getElementById(focusTarget);
    input.focus();

    // move cursor to end TODO maybe there is a better way
    var val = input.value; //store the value of the element
    input.value = ''; //clear the value of the element
    input.value = val;
}

function expandYearRange() {
    $("#inputGroupField").removeClass("col-md-8").addClass("col-md-6");
    $("#inputGroupYear").toggleClass("hidden");
    $("#filterValueYear").val("")
    $("#inputGroupFromYear").toggleClass("hidden");
    $("#inputGroupToYear").toggleClass("hidden");
}

function switchSelection(type, value, selection) {
    const id = 'filterSelection' + type;
    var filterSelection = document.getElementById(id);
    filterSelection.innerHTML = '<span style="display:none;">' + value + '</span>' + selection.innerText;
}

function addFilter() {

    // term operator
    const operator = $('#filterOperator label.active input').val();

    // search
    const input = document.getElementById('extendedSearchInput');
    var query = input.value;

    // filters
    const year = document.getElementById('filterValueYear').value;
    const fromYear = document.getElementById('filterValueFromYear').value;
    const toYear = document.getElementById('filterValueToYear').value;

    if (validateYear(year)) {
        // check if it's simple year input
        query = appendFilter(query, operator, 'year', year);
    } else {
        // else append year range
        if (validateYear(fromYear) || validateYear(toYear)) {
            query = appendFilter(query, operator, 'year',  fromYear + '-' + toYear);
        }
    }

    const entrytype = document.getElementById('filterSelectionEntrytype').children[0].innerText;
    query = appendFilter(query, operator, 'entrytype', entrytype);


    const field = document.getElementById('filterSelectionField').children[0].innerText;
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

    if (operator === 'OR') {
        return '(' + query + ')' + ' ' +  operator + ' ' + term
    }

    return query + ' ' +  operator + ' ' + term;
}

function validateYear(year) {
    return year !== '' && !isNaN(year);
}