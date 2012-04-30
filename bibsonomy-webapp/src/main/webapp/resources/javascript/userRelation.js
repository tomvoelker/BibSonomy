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
		actionString = "&action="+action+"Follower";
	} else {
		actionString = "&action="+action+"Relation&relationTags="+encodeURIComponent(relationName);
	}
	
	handleUserRelation(actionString, userName, relationName, toggleSocializerButtons);
}

/**
 * add follower on the follower page
 * 
 * @param userName
 * @param element
 * @param ckey
 * @return
 */
function updateFollowerFollowerPage(action, userName, element, ckey){
	handleUserRelation("&action="+action+"Follower", userName, "sys:network:bibsonomy-follower", moveFollwerLink, element, action);
}


/**
 * toggles visibility of the socializer buttons
 * 
 * @return
 */
function toggleSocializerButtons(relationName) {
	var cleanedName = relationName.replace(/(:|\.)/g,'\\$1');
	$('#'+cleanedName+"_followLink").toggle();
	$('#'+cleanedName+"_removeLink").toggle();
}

/**
 * moves the requested user from the 'similar' to the 'follower' list
 *  
 * @param relationName
 * @param element referenced dom element
 * @param action one of 'add' or 'remove'
 * @return
 */
function moveFollwerLink(relationName, element, action){
	$('#posts.refresh').show();
	element.setAttribute("href", "");
	
	if ("add" == action) {
		element.parentNode.setAttribute("class", "");
		document.getElementById("followedUsers").appendChild(element.parentNode);
		element.parentNode.removeChild(element);
	} else {
		element.parentNode.style.display='none';
		element.parentNode.style.visibility='hidden';
	}
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
		complete: function success(data) {
			callback(relationName, element, action, data);
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
	var regExp = new RegExp("^[a-zA-Z0-9]+$");

	if(regExp.test(userName)) {	
		handleUserRelation("&action=addFriend", userName, "sys:network:bibsonomy-friend", 
				function(relationName, element, action, data) {
					if(data.statusText === "error") {
						alert(getStringReplace("error.user.none_existing_user", [userName]));
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
