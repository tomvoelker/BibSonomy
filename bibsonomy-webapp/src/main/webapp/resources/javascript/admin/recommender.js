$(function() {
	$("#activateItemBtn").click(function() {
		activateReco("Item");
	});
	$("#deactivateItemBtn").click(function() {
		deactivateReco("Item");
	});
	$("#activateTagBtn").click(function() {
		activateReco("Tag");
	});
	$("#deactivateTagBtn").click(function() {
		deactivateReco("Tag");
	});
	$("#recActivationBtn").click(selectAll);
	$("#editItemRecoBtn").click(function() {
		openEditRecommender("Item");
	});
	$("#editTagRecoBtn").click(function() {
		openEditRecommender("Tag");
	});
});

function openEditRecommender(type) {

	var selectedReco = $("#deleteRec" + type + "Ids :selected");
	$("#editedRecurl").val(selectedReco.text());
	console.log($("#editRecoTitle").text());
	$("#editRecoTitle").text($("#editRecoTitle").text().replace(/Recommender/g, type + " Recommender"));
	$("#editRecoTitle").text($("#editRecoTitle").text().replace(/recommender/g, type.toLowerCase() + " recommender"));
	$("#editRecommenderForm").prop("action",
			"/admin/recommenders?action=edit" + type + "Recommender");
	$("#recommenderEditDiv").show();

}

function activateReco(tagOrItem) {
	var selectedItem = $("#disabled" + tagOrItem + "Select select :selected");

	if (selectedItem == undefined || selectedItem.length > 1) {
		return;
	}

	var selectedText = selectedItem.text();
	var selectedValue = selectedItem.val();

	var optionToAdd = "<option value='" + selectedValue + "'" + ">"
			+ selectedText + "</option>";

	$("#enabled" + tagOrItem + "Select select").append(optionToAdd);

	selectedItem.remove();

}

function deactivateReco(tagOrItem) {
	var selectedItem = $("#enabled" + tagOrItem + "Select select :selected");

	if (selectedItem == undefined || selectedItem.length > 1) {
		return;
	}

	var selectedText = selectedItem.text();
	var selectedValue = selectedItem.val();

	var optionToAdd = "<option value='" + selectedValue + "'" + ">"
			+ selectedText + "</option>";

	$("#disabled" + tagOrItem + "Select select").append(optionToAdd);

	selectedItem.remove();

}

function selectAll() {

	$("#disabledItemSelect select > option").each(function() {
		$(this).prop("selected", true);
	});

	$("#enabledItemSelect select > option").each(function() {
		$(this).prop("selected", true);
	});

	$("#disabledTagSelect select > option").each(function() {
		$(this).prop("selected", true);
	});

	$("#enabledTagSelect select > option").each(function() {
		$(this).prop("selected", true);
	});

	$("#newConfigSubmissionForm").submit();
}