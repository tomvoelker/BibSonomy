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
	$(form).find(".syncPlan").empty();
	$(form).find(".synchronizeBtn").hide();
	var syncResult = $(form).find(".syncData");
	var syncData = "";
	syncResult.empty();
	
	for (key in data) {
		var error = data[key].error;
		var status = "";
		if (error != undefined) {
			alert(key + ": " + error);
			status = "ERROR";
		} else {
			status = data[key];
		}
		if(key == "BibTex") {
			resourceType = getString("publications");
		} else {
			resourceType = getString("bookmarks");
		}
		
		var contentDiv = $("<div class='fsRow'></div");
		var resultData = $("<dl><dt>" + resourceType +":</dt><dd>" + status + "</dd></dl>");

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

function confirmReset(t) {
	if(confirm(getString("synchronization.server.reset.confirm"))) {
		$(t).parents("form").find("input[name='_method']").val("DELETE");
	} else {
		return false;
	}
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