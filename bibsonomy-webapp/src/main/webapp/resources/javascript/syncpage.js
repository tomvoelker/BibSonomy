function validateSyncForm(formData, form, options) {
	$(form).find(":submit").hide();
	$(form).find(".progressGif").show();
}
				
function successSyncForm(data, statusText, xhr, form) {
	$(form).find(":submit").show();
	$(form).find(".progressGif").hide();
	var syncResult = $(form).find(".syncData");
	var syncData = "";
	syncResult.empty();
	
	for (key in data) {
		var error = data[key].error;
		var status = "";
		var info = "";
		if (error != "no") {
			alert(key + ": " + error);
			status = "ERROR";
			info = "error";
		} else {
			status="DONE";
			info = data[key].info;
		}
		var date = new Date(data[key].date);
		var formattedDate = formatDate(date, "MMM dd, yyyy hh:mm a");
		var resourceType = "";
		if(key == "BibTex") {
			resourceType = getString("publications");
		} else {
			resourceType = getString("bookmarks");
		}
		
		var contentDiv = $("<div class='fsRow'></div");
		var label = $("<span class='fsLabel'>" + resourceType + "</span>");
		var resultData = $("<span class='fsInput'>" + formattedDate + " " + getString("synchronization.result") + " " + status + " " + info + "</span>");
		
		label.appendTo(contentDiv);
		resultData.appendTo(contentDiv);
		contentDiv.appendTo(syncResult);
		
	}
	// submit the form
								 	     
    // return false to prevent normal browser submit and page navigation 
    return false; 
}
				
function errorSyncForm(jqXHR, textStatus, errorThrown, form) {
	alert("error: " + errorThrown);
	$(form).find(":submit").show();
	$(form).find(".progressGif").hide();
}

$(document).ready(function() {
	$("form").each(function(index, elem) {
	 	$(this).ajaxForm({
	 		dataType : "json",
	 		beforeSubmit : validateSyncForm,
	 		success : successSyncForm,
	 		error : errorSyncForm
		 });
	});
});