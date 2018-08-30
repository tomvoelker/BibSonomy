$(document).ready(function () {
    displaySearch();
});

    function displaySearch() {
    var filter = document.getElementById("search-project-input").value.toLowerCase();
    var list = document.getElementById("all-projects");
    var items = list.getElementsByClassName("show-project");
    var displayedItems = 0;
    var totalBudget = 0.0;
    for (var i = 0; i < items.length; i++) {
        var projectName = items[i].getElementsByClassName("name")[0];
        var projectType = items[i].getElementsByClassName("type")[0];
        var projectBudget = items[i].getElementsByClassName("budget")[0];
        if (projectName.innerText.toLowerCase().indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText);
            }
        } else if (typeof projectType !== 'undefined' && projectType.innerText.toLowerCase().indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText);
            }
        } else if (typeof projectBudget !== 'undefined' && projectBudget.innerText.indexOf(filter) !== -1) {
            items[i].style.display = "";
            displayedItems = displayedItems + 1;
            if (typeof projectBudget !== 'undefined') {
                totalBudget = totalBudget + parseFloat(projectBudget.innerText);
            }
        } else {
            items[i].style.display = "none";
        }
    }
    document.getElementById("amount-projects").innerText = displayedItems.toString();
    document.getElementById("total-budget").innerText = totalBudget.toString();
}