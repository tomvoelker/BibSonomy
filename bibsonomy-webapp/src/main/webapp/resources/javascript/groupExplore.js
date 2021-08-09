$(function() {
    initFilterButtons();
});

function initFilterButtons() {
    $('.searchFilterList .btn').click(function() {
        generateFilterQuery();
    });
}

function generateFilterQuery() {
    var filterQuery = [];

    $('.searchFilterList').each(function() {
        var selectedFilters = [];
        $(this).find('.btn.active span.filter').each(function() {
            selectedFilters.push($(this).html());
        });
        var selectedFiltersQuery = selectedFilters.join(' OR ');
        if (selectedFiltersQuery) filterQuery.push('(' + selectedFiltersQuery + ')');
    });

    console.log(filterQuery.join(' AND '));
}