$(document).ready(function () {
    var budgetItems = $(".budget");
    budgetItems.each( function () {
        $(this).hide();
    });
    displaySearch();
});

function displaySearch() {
    var filter = $("#search-project-input").val().toLowerCase();
    var list = $("#all-projects");
    var items = $(list).find(".show-project");
    var displayedItems = 0;
    var totalBudget = 0.0;
    for (var i = 0; i < items.length; i++) {
        var projectTitle = $(items[i]).children(".title");
        var projectSubtitle = $(items[i]).children(".sub-title");
        var projectBudget = $(items[i]).children(".budget");
        if ($(projectTitle).text().toLowerCase().indexOf(filter) !== -1) {
            $(items[i]).show();
            $(items[i]).addClass("display-project");
            displayedItems = displayedItems + 1;
            if (typeof $(projectBudget) !== 'undefined') {
                totalBudget = totalBudget + parseFloat($(projectBudget).text().slice(0, -2).replace(/,/g, ""));
            }
        } else if (typeof $(projectSubtitle) !== 'undefined' && $(projectSubtitle).text().toLowerCase().indexOf(filter) !== -1) {
            $(items[i]).show();
            $(items[i]).addClass("display-project");
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat($(projectBudget).text().slice(0, -2).replace(/,/g, ""));
            }
        } else {
            $(items[i]).hide();
            $(items[i]).removeClass("display-project");
        }
    }
    $("#amount-projects").text(displayedItems.toLocaleString());
    $("#total-budget").text(totalBudget.toLocaleString() + " €");
    updatePagination(displayedItems);
}

function updatePagination(size){
    var pageSize = 10;
    var pagecount = size / pageSize;
    var pagin = $("#pagin");
    pagin.empty();
    for (var i = 0; i < pagecount; i++) {
        pagin.append('<li><a href="#">' + (i + 1) + '</a></li>');
    }
    pagin.children().first().addClass("active");
    $("#show-amount").text("Showing " + pageSize + " of " + size.toString() + " projects. ");
    showCurrentPage = function(page) {
        var list = $(".display-project");
        $(list).hide();
        $(list).each(function (n) {
            if (n >= pageSize * (page - 1) && n < pageSize * page) {
                $(this).show();
            }
        });
    };
    showCurrentPage(1);
    $("#pagin li a").click(function () {
        $("#pagin li").removeClass("active");
        $(this).parent().addClass("active");
        showCurrentPage(parseInt($(this).text()))
    });
}

