/**
 * adds given relation to the login user
 * 
 * @param action one of 'add' or 'remove'
 * @param userName target user's name
 * @param relationName name of the relation 
 * @param ckey
 * @return
 */
function updateUserRelation(action, userName, relationName, ckey) {
	var actionString;
	if ('sys:network:bibsonomy-follower' == relationName) {
		// FIXME: move follower-table to friends table
		actionString = "&action=" + action + "Follower";
	} else {
		actionString = "&action=" + action + "Relation&relationTags="+encodeURIComponent(relationName);
	}
	
	handleUserRelation(actionString, userName, relationName, toggleSocializerButtons);
}


/**
 * toggles visibility of the socializer buttons
 * 
 * @return
 */
function toggleSocializerButtons(relationName) {
	var cleanedName = relationName.replace(/(:|\.)/g,'\\$1');
	$('#' + cleanedName + "_followLink").toggle();
	$('#' + cleanedName + "_removeLink").toggle();
}

/**
 * sends the requested user relation operation to the server
 * 
 * @param actionString determines which action to perform
 * @param userName name of the requested user
 * @param relationName name of the requested user relation
 * @param callback called on success 
 * @param referenced dom element
 * @param action one of 'add' or 'remove'
 * @return
 */
function handleUserRelation(actionString, userName, relationName, callback, element, action) {
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleUserRelation",  
		data: "requestedUserName="+ encodeURIComponent(userName) + actionString + "&ckey=" + ckey,
		success: function success(data) {
			callback(relationName, element, action, data);
		},
		error: function(jqXHR, data, errorThrown) {
			alert(jQuery.parseJSON(jqXHR.responseText).globalErrors[0].message);
		}
	});
}

/**
 * Function, to submit and add a given User as Friend.
 * It will be tested if Text-input is valid and then, 
 * if the given User Name exists in the Database.
 * 
 * @param textField
 * @returns {Boolean}
 */
function submitUsername(textField) {
	var userName = textField.val();
	// FIXME: this pattern is taken from the UserValidator, but 
	// there exist a small nr. of users (~1% ) which don't conform to it
	// (from the time before UserValidator existed)
	var regExp = new RegExp("^[a-zA-Z0-9\.\-\_]+$");

	if (regExp.test(userName)) {	
		handleUserRelation("&action=addFriend", userName, "sys:network:bibsonomy-friend", 
				function(relationName, element, action, data) {
					if (data.statusText === "error") {
						alert(getString("error.user.none_existing_user", [userName]));
					} else {
						textField.val('');
						location.reload();
					}					
				}, element, "add");
		
	} else {
		alert(getString("error.user.no_valid_username"));
	}
	return false;
}