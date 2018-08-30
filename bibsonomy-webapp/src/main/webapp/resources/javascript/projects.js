$(document).ready(function () {
    displaySearch();
    var budgetItems = document.getElementsByClassName("budget");
    for (var i = 0; i < budgetItems.length; i++) {
        budgetItems[i].style.display = "none";
    }
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
                totalBudget = totalBudget + parseFloat(projectBudget.innerText);
            }
        } else if (typeof projectSubtitle !== 'undefined' && projectSubtitle.innerText.toLowerCase().indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText);
            }
        } else {
            items[i].style.display = "none";
        }
    }
    document.getElementById("amount-projects").innerText = displayedItems.toLocaleString();
    document.getElementById("total-budget").innerText = totalBudget.toLocaleString() + " €";
}