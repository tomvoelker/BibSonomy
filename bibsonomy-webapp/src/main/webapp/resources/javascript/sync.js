function hideSubmitButtons(formData, form, options) {
	$(form).find(":submit").hide();
	$(form).find(".progressGif").show();
}
				
function successSyncForm(data, statusText, xhr, form) {
	$(form).find(":submit").show();
	$(form).find(".progressGif").hide();

	if (data.syncData) showSyncData(form, data.syncData);
	
	if (data.syncPlan) showSyncPlan(form, data.syncPlan);

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
	$(form).find(".synchronizeBtn").show();
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
	var method = form.find("input[name='_method']").val();
	if("POST" == method) {
		//error by synchronization -> set data to error and clear plan
		$(form).find(".resourceDiv").each(function(index, element){
			var dd = $(this).find("dd");
			dd.empty();
			$(dd).append(getString("error"));
			$(form).find(".syncPlan").empty();
		});
	} else if("GET" == method) {
		//error by get plan, do nothing special
	} else {
		alert("error on unknown method")
	}
	$(form).find(":submit").show();
	$(form).find(".synchronizeBtn").hide();
	$(form).find(".progressGif").hide();
}

$(document).ready(function() {
	$("form").each(function(index, elem) {
		var form = $(this);
	 	$(this).ajaxForm({
	 		dataType : "json",
	 		beforeSubmit : hideSubmitButtons,
	 		success : successSyncForm,
	 		error : function (jqXHR, textStatus, errorThrown) {
	 			errorSyncForm(jqXHR, textStatus, errorThrown, form);
	 		}
		 });
	});
});