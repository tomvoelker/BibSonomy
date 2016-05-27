/**
 * Little helper JS for the admin/group page
 */
$(document).ready(function() {
	$('.declineGroupCancel').click(function() {
		$(this).closest("tr").hide();
	});	
})

function declineGroup(element) {
	nextTR = $(element).closest("tr").next();
	nextTR.show();
	$("textarea#declineMessage", nextTR).val("");
	return false;
}


//Have this working properly, like tag recommendation
//autocomplete user info box

function fetchGroupPermissions(id) {
	groupname=$(id)[0].value
	$.ajax({
		url: "../admin/ajax",
				data: {groupname:groupname, action:'fetch_group_with_permissions'},
				async: false,
			    contentType: "application/json;charset=utf-8",
			    dataType: "json",
				success: function (data) {
					if (data.hasOwnProperty("groupLevelPermissions")) {
						for (var i=0; i< data.groupLevelPermissions.length; i++) {
							permission = data.groupLevelPermissions[i];
							var permissionCheckboxId="#".concat(permission);
							var permissionCheckbox = $(permissionCheckboxId)[0];
							if (permissionCheckbox) {
								permissionCheckbox.checked=true;
							}
						}
					}
					var permissionCheckboxes=$(".permissionCheckbox");
					for (var i=0; i<permissionCheckboxes.length; i++) {
						permissionCheckboxes[i].disabled=false;
					}
				}
	});
}
