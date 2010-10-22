var oaBaseUrl = "/ajax/checkOpenAccess";

/* open access check */
/* TODO: add error handling, check apicontrol and outcome in response. */
function checkOpenAccess () {
	var container = $("#openAccess");	
	container.hide(); // TODO: add progress animation	

	var url;
	if ($("#post\\.resource\\.entrytype").val() == "article")
		url = oaBaseUrl + "?jTitle=" + $("#post\\.resource\\.journal").val();
	else
		url = oaBaseUrl + "?publisher=" + $("#post\\.resource\\.publisher").val();
	
	$.ajax({
		url: url,
		dataType: 'json',
		success: function(data) {
			/*
			 * build list with publishers
			 */
			var ul = document.createElement("ul");
			ul.className = "oa-publishers";
			$.each(data.publishers, function(index, publisher) {
				var li = document.createElement("li");
				li.className = "oa-" + publisher.colour;
				var span = document.createElement("span");
				span.appendChild(document.createTextNode(publisher.name));
				span.className = "oa-publisher";
				li.appendChild(span);
				var ulCond = document.createElement("ul");
				ulCond.className = "oa-conditions";
				$.each(publisher.conditions, function(index, condition) {
					var liCond = document.createElement("li");
					liCond.appendChild(document.createTextNode(condition));
					ulCond.appendChild(liCond);
				});
				li.appendChild(ulCond);
				ul.appendChild(li);
			});
			container.append(ul);
			container.fadeIn();
		},
		error: function(req, status, e) {
			alert("check open access: " + status);
		}
	});
}