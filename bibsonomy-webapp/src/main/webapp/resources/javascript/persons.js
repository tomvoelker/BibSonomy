$(document).ready(function () {
	displaySearch();
});

function displaySearch() {
	var filter = $("#search-person-input").val().toLowerCase();
	var list = $("#all-persons");
	var items = $(list).find(".show-person");
	var displayedItems = 0;
	for (var i = 0; i < items.length; i++) {
		var publicationTitle = $(items[i]).children(".name");
		if ($(publicationTitle).text().toLowerCase().indexOf(filter) !== -1) {
			$(items[i]).show();
			$(items[i]).addClass("display-person");
			displayedItems = displayedItems + 1;
		} else {
			$(items[i]).hide();
			$(items[i]).removeClass("display-person");
		}
	}
	$("#amount-publications").text(displayedItems.toLocaleString());
	updatePagination(displayedItems);
}

function sortList(element) {
	var order;
	deleteArrows = function (button) {
		var content = $(button).text();
		if (content.indexOf("▼") !== -1)
			$(button).text(content.substring(1));
		if (content.indexOf("▲") !== -1)
			$(button).text(content.substring(1));
	};
	changeArrow = function (button) {
		var content = $(button).text();
		if (content.indexOf("▼") !== -1) {
			$(button).text("▲" + content.substring(1));
			order = "ascending";
		} else if (content.indexOf("▲") !== -1) {
			$(button).text("▼" + content.substring(1));
			order = "descending";
		} else {
			$(button).text("▼" + content);
			order = "descending";
		}
	};
	var buttons = ["name"];
	$(buttons).each(function () {
		if (element === this.toString()) {
			changeArrow($("#sort-" + this.toString()));
		} else {
			deleteArrows($("#sort-" + this.toString()));
		}
	});
	var list = $("#all-persons");
	var items = $(list).children(".show-person");
	[].slice.call(items).sort(function (a, b) {
		if (order === "descending") {
			if (a.getElementsByClassName(element)[0].innerText > b.getElementsByClassName(element)[0].innerText) return 1;
			if (a.getElementsByClassName(element)[0].innerText < b.getElementsByClassName(element)[0].innerText) return -1;
			return 0;
		}
		if (a.getElementsByClassName(element)[0].innerText < b.getElementsByClassName(element)[0].innerText) return 1;
		if (a.getElementsByClassName(element)[0].innerText > b.getElementsByClassName(element)[0].innerText) return -1;
		return 0;
	}).forEach(function (val) {
		list.append(val);
	});
}

function updatePagination(size) {
	var pageSize = 10;
	var pagecount = size / pageSize;
	var pagin = $("#pagin");
	pagin.empty();
	for (var i = 0; i < pagecount; i++) {
		pagin.append('<li><a href="#">' + (i + 1) + '</a></li>');
	}
	pagin.children().first().addClass("active");
	$("#show-amount").text("Showing " + pageSize + " of " + size.toString() + " persons. ");
	showCurrentPage = function (page) {
		var list = $(".display-person");
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

