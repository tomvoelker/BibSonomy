function addUserRelation(name, relationName, ckey) {
	var actionString;
	if ('sys:network:bibsonomy-follower' == relationName) {
		// FIXME: move follower-table to friends table
		actionString = "&action=addFollower";
	} else {
		actionString = "&action=addRelation&relationTags="+encodeURIComponent(relationName);
	}
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleUserRelation",  
		data: "requestedUserName="+ encodeURIComponent(name) + actionString + "&ckey=" + ckey,
		complete: function changeText(){
			document.getElementById(relationName+"_followLink").style.visibility='hidden';
			document.getElementById(relationName+"_followLink").style.display="none";
			document.getElementById(relationName+"_removeLink").style.visibility='visible';
			document.getElementById(relationName+"_removeLink").style.display='';
		}
	});
}

function removeUserRelation(name, relationName, ckey){
	var actionString;
	if ('sys:network:bibsonomy-follower' == relationName) {
		// FIXME: move follower-table to friends table
		actionString = "&action=removeFollower";
	} else {
		actionString = "&action=removeRelation&relationTags="+encodeURIComponent(relationName);
	}
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleUserRelation",  
		data: "requestedUserName="+ encodeURIComponent(name) + actionString + "&ckey=" + ckey,
		complete: function changeText(){
			document.getElementById(relationName+"_removeLink").style.visibility='hidden';
			document.getElementById(relationName+"_removeLink").style.display="none";
			document.getElementById(relationName+"_followLink").style.visibility='visible';
			document.getElementById(relationName+"_followLink").style.display='';
	}
	});	
}

function removeFollowerFollowerPage(name, element, ckey){
	
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleUserRelation",  
		data: "requestedUserName="+ encodeURIComponent(name) +"&action=removeFollower&ckey=" + ckey,
		complete: function changeText(){
		if (document.getElementById("posts.refresh")) {
			document.getElementById("posts.refresh").style.visibility='visible';
			document.getElementById("posts.refresh").style.display='';
		}		
		element.setAttribute("href", "");
		element.parentNode.style.display='none';
		element.parentNode.style.visibility='hidden';
	}
	});		
}

function addFollowerFollowerPage(name, element, ckey){
	
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleUserRelation",  
		data: "requestedUserName="+ encodeURIComponent(name) +"&action=addFollower&ckey=" + ckey,
		complete: function changeText(){
		if (document.getElementById("posts.refresh")) {
			document.getElementById("posts.refresh").style.visibility='visible';
			document.getElementById("posts.refresh").style.display='';
		}
		element.setAttribute("href", "");
		element.parentNode.setAttribute("class", "");
		document.getElementById("followedUsers").appendChild(element.parentNode);
		element.parentNode.removeChild(element);
	}		
	});	
}