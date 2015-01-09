
$(document).ready(function() {
	$(".btnLinkPerson").on("click", function() {
		var e = $(this);
		if(e.attr("data-action") == "addRole") {
			$.post("/person",
					{ 	formAction: "addRole",
						formInterHash: e.attr("data-resource-simhash1"),
						formIntraHash: e.attr("data-resource-simhash2"),
						formPersonId: e.attr("data-person-id") ,
						formUser: e.attr("data-pub-owner"),
						formPersonRole: "AUTHOR",
						formPersonNameId: e.attr("data-person-name-id")
					}
			).done(function(data) {
				e.attr("data-rpr-id", data);
				e.attr("data-action", "deleteRole");
				$("#icon_"+e.attr("data-person-name-id")).removeClass("glyphicon-plus");
				$("#icon_"+e.attr("data-person-name-id")).addClass("glyphicon-remove");
				$("#icon_"+e.attr("data-person-name-id")).attr("style", "color: darkred");
			});	
		} else if(e.attr("data-action") == "deleteRole") {
			$.post("/person",
					{ 	formAction: "deleteRole",
						formRPRId: e.attr("data-rpr-id")
					}
			).done(function(data) {
				e.attr("data-rpr-id", "");
				e.attr("data-action", "addRole");
				$("#icon_"+e.attr("data-person-name-id")).removeClass("glyphicon-remove");
				$("#icon_"+e.attr("data-person-name-id")).addClass("glyphicon-plus");
				$("#icon_"+e.attr("data-person-name-id")).attr("style", "");
			});
		}
		
	});
});