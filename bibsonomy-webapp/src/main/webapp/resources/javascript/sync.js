function hideSubmitButtons(formData, form, options) {
	$(form).find(":submit").hide();
	$(form).find(".progressGif").show();
}
				
function successSyncForm(data, statusText, xhr, form) {
	$(form).find(":submit").show();
	$(form).find(".progressGif").hide();
	
	if (data.syncPlan) showSyncPlan(form, data.syncPlan);

	if (data.syncData) showSyncData(form, data.syncData);

	// return false to prevent normal browser submit and page navigation 
    return false; 
}

function showSyncPlan(form, plan) {
	var div = $(form).find(".syncPlan");
	div.empty();

	var serviceName = $(form).find("input[name='serviceName']").val();
	var projectName = location.hostname;
		
	var dl = document.createElement("dl");

	for (var resourceType in plan) {
		var dt = document.createElement("dt");
		dl.appendChild(dt);
		dt.appendChild(document.createTextNode(getString("resourceType." + resourceType + ".plural")));
		var dd = document.createElement("dd");
		dl.appendChild(dd);
		var ul = document.createElement("ul");
		dd.appendChild(ul);
		var actions = plan[resourceType];
		
		var liClient = document.createElement("li");
		liClient.appendChild(document.createTextNode(actions["CLIENT"]));
		ul.appendChild(liClient);
		
		var liServer = document.createElement("li");
		liServer.appendChild(document.createTextNode(actions["SERVER"]));
		ul.appendChild(liServer);
		
		var liOther  = document.createElement("li"); 
		liOther.appendChild(document.createTextNode(actions["OTHER"]));
		ul.appendChild(liOther);
	
	}
	div.append(dl);
}

function showSyncData(form, data) {
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
}

function getSyncPlan(t) {
	$(t).parents("form").find("input[name='_method']").val("GET");
}

function doSync(t) {
	$(t).parents("form").find("input[name='_method']").val("POST");
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
	 		beforeSubmit : hideSubmitButtons,
	 		success : successSyncForm,
	 		error : errorSyncForm
		 });
	});
});