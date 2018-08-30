$(document).ready(function () {
    var budgetItems = document.getElementsByClassName("budget");
    for (var i = 0; i < budgetItems.length; i++) {
        budgetItems[i].style.display = "none";
    }
    displaySearch();
});

function displaySearch() {
    var filter = document.getElementById("search-project-input").value.toLowerCase();
    var list = document.getElementById("all-projects");
    var items = list.getElementsByClassName("show-project");
    var displayedItems = 0;
    var totalBudget = 0.0;
    for (var i = 0; i < items.length; i++) {
        var projectTitle = items[i].getElementsByClassName("name")[0];
        var projectSubtitle = items[i].getElementsByClassName("sub-title")[0];
        var projectBudget = items[i].getElementsByClassName("budget")[0];
        if (projectTitle.innerText.toLowerCase().indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText.slice(0, -2).replace(/,/g, ""));
            }
        } else if (typeof projectSubtitle !== 'undefined' && projectSubtitle.innerText.toLowerCase().indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText.slice(0, -2).replace(/,/g, ""));
            }
        } else {
            items[i].style.display = "none";
        }
    }
    document.getElementById("amount-projects").innerText = displayedItems.toLocaleString();
    document.getElementById("total-budget").innerText = totalBudget.toLocaleString() + " €";
}

function sortList(element) {
    var order, button;
    if (element === "start-date") {
        button = document.getElementById("sort-start-date");
    } else if (element === "end-date") {
        button = document.getElementById("sort-end-date");
    } else if (element === "type") {
        button = document.getElementById("sort-type");
    }
    var content = button.innerHTML;
    if (content.indexOf("▼") !== -1) {
        button.innerHTML = "▲" + content.substring(1);
        order = "ascending";
    } else if (content.indexOf("▲") !== -1) {
        button.innerHTML = "▼" + content.substring(1);
        order = "descending";
    } else {
        button.innerHTML = "▼" + content;
        order = "descending";
    }
    var list = document.getElementById("all-projects");
    var items = list.getElementsByClassName("show-project");
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