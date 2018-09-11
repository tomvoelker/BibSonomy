$(document).ready(function () {
    search();
});

function search() {
    var filter = $("#search").val().toLowerCase();
    var list = $("#all-posts");
    var items = $(list).find(".post-item");
    var displayedItems = 0;
    for (var i = 0; i < items.length; i++) {
        var name = $(items[i]).children(".name");
        if ($(name).text().toLowerCase().indexOf(filter) !== -1) {
            $(items[i]).show();
            $(items[i]).addClass("display-project");
            displayedItems = displayedItems + 1;
        } else {
            $(items[i]).hide();
            $(items[i]).removeClass("display-project");
        }
    }
    $("#amount-projects").text(displayedItems.toLocaleString());
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
