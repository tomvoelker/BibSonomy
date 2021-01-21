function switchSelection(type, selection) {
    let id = 'filterSelection' + type;
    let filterSelection = document.getElementById(id);
    filterSelection.value = selection.value;
}

function addFilter() {

    // term operator
    const operator = "AND";

    // search
    let input = document.getElementById('extendedSearchInput');
    let search = input.value;

    // filters
    let yearFrom = document.getElementById('filterValueYearFrom').value;
    let yearTo = document.getElementById('filterValueYearTo').value;
    let entrytype = document.getElementById('filterSelectionEntrytype').value;
    let entity = document.getElementById('filterSelectionEntity').value;
    let entityValue = document.getElementById('filterValueEntity').value;
    let additional = document.getElementById('filterSelectionAdditional').value;
    let additionalValue = document.getElementById('filterValueAdditional').value;

    // append filters to search
    if (entrytype != null) {
        search = appendFilter(search, operator, 'entrytype', entrytype);
    }

    if (entityValue != null) {
        search = appendFilter(search, operator, entity, entityValue);
    }

    if (additionalValue != null) {
        search = appendFilter(search, operator, additional, additionalValue);
    }

    input.value = search;
}

function appendFilter(search, operator, key, value) {
    let term = key + ':' + value;
    // check, if term is already in the search
    if (search.includes(term)) {
        // returns previous search to avoid duplicate terms
        return search;
    }
    return search + ' ' +  operator + ' ' + term;
}