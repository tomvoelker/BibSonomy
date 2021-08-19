const PUBLICATIONS_SELECTOR = '#groupExplorePublications';

$(function() {
    // disable form action
    formAction();
    // add custom tags
    addTagFilters();
    addCustomFilters();
    // remove invalid year buttons
    validateYearFilters();
    // add action to filter buttons
    initFilterButtons();
    // add init results
    initResultPagination(0);
});

function formAction() {
    $('#extendedSearchForm').on('submit', function (e) {
        e.preventDefault();
        updateCounters();
        updateResultPagination();
    });
}

function initFilterButtons() {
    $('.searchFilterList .btn').click(function() {
        $(this).toggleClass('active');
        updateResultPagination();
    });
}

function updateResultPagination() {
    // empty shown publications
    $(PUBLICATIONS_SELECTOR).empty();
    // init new result pagination with updated query
    initResultPagination(0);
}

function initResultPagination(page) {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();
    var filters = generateFilterQuery();
    var query = addFiltersToSearchQuery(search, filters);

    $.ajax({
        url: '/ajax/explore/group', // The url you are fetching the results.
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'page': page, // Which page at the first time
            'pageSize': 20,
            'search': query
        },
        beforeSend: function() {
            $('.custom-loader').removeClass('hidden');
        },
        success : function(data) {
            $(PUBLICATIONS_SELECTOR).html(data);
        },
        complete: function() {
            $('.custom-loader').addClass('hidden');
        },
    });
}

function updateCounters() {
    var groupName = $('#requestedGroup').data('group');
    var search = $('#extendedSearchInput').val();

    $.ajax({
        datatype: 'json',
        url: '/ajax/explore/group', // The url you are fetching the results.
        data: {
            // These are the variables you can pass to the request
            'requestedGroup': groupName,
            'distinctCount': true,
            'search': search
        },
        success : function(data) {
            console.log(data);
        },
        complete : function() {
            console.log("working?");
        }
    });
}

function generateFilterQuery() {
    var filterQuery = [];

    $('.searchFilterList').each(function() {
        var selectedFilters = [];
        $(this).find('.btn.active').each(function() {
            selectedFilters.push($(this).data('filter'));
        });
        var selectedFiltersQuery = selectedFilters.join(' OR ');
        if (selectedFiltersQuery) filterQuery.push('(' + selectedFiltersQuery + ')');
    });

    return filterQuery.join(' AND ');
}

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

function validateYearFilters() {
    var yearFilterList = $('.searchFilterList[data-field="year"]');
    yearFilterList.find('.searchFilterEntries > button').each(function() {
        var element = $(this);
        if (isNaN(element.data('value'))) {
            element.remove();
        }
    });
}

var tags = [
    {
        'name': 'OA',
        'description': 'Open Access'
    },
    {
        'name': 'OAFONDS',
        'description': 'Open-Access-Publikationsfonds'
    },
    {
        'name': 'DFG',
        'description': 'Deutsche Forschungsgemeinschaft'
    },
];

function createFilterButton(name, filter, description) {
    var element = '<button class="btn btn-default btn-block" ' +
        'data-filter="' + filter + '" ' +
        'data-value="' + name + '">' +
        description + '</button>';

    return element;
}

function addTagFilters() {
    var entries = $('.searchFilterList[data-field="tags"]').find('.searchFilterEntries');
    tags.forEach(function(tag) {
        entries.append(createFilterButton(tag.name, "tags:" + tag.name, tag.description));
    });
}

var custom = {
    "head": {
        "vars": ["label", "cnr", "facility", "fromDate", "untilDate"]
    },
    "results": {
        "bindings": [
            {
                "label": {"type": "literal", "value": "ubs_10001"},
                "cnr": {"type": "literal", "value": "010000"},
                "facility": {"type": "literal", "value": "Fakultät für Architektur und Stadtplanung"},
                "fromDate": {
                    "type": "literal",
                    "datatype": "http://www.w3.org/2001/XMLSchema#date",
                    "value": "2016-02-18"
                }
            },
            {
                "label": {"type": "literal", "value": "ubs_10002"},
                "cnr": {"type": "literal", "value": "020000"},
                "facility": {"type": "literal", "value": "Fakultät für Bau- und Umweltingenieurwissenschaften"},
                "fromDate": {
                    "type": "literal",
                    "datatype": "http://www.w3.org/2001/XMLSchema#date",
                    "value": "2016-02-19"
                }
            },
            {
                "label": {"type": "literal", "value": "ubs_10003"},
                "cnr": {"type": "literal", "value": "030000"},
                "facility": {"type": "literal", "value": "Fakultät für Chemie"},
                "fromDate": {
                    "type": "literal",
                    "datatype": "http://www.w3.org/2001/XMLSchema#date",
                    "value": "2016-02-19"
                }
            }
        ]
    }
};

function addCustomFilters() {
    var entries = $('.searchFilterList[data-field="custom"]').find('.searchFilterEntries');
    custom.results.bindings.forEach(function(entity) {
        entries.append(createFilterButton(entity.label.value, "tags:" + entity.label.value, entity.facility.value));
    });
}