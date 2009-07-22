/**
*
* This small js file should catch clicks on "follow this user" links
* and send request per ajax to the controller.
*
* $author: Christian Kramer
*
*/

function addFollower(name, ckey){
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=addFollower&ckey=" + ckey,
		complete: function changeText(){
			document.getElementById("followLink").style.visibility='hidden';
			document.getElementById("followLink").style.display="none";
			document.getElementById("removeLink").style.visibility='visible';
			document.getElementById("removeLink").style.display='';
		}
	});
}

function removeFollower(name, ckey){
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=removeFollower&ckey=" + ckey,
		complete: function changeText(){
			document.getElementById("removeLink").style.visibility='hidden';
			document.getElementById("removeLink").style.display="none";
			document.getElementById("followLink").style.visibility='visible';
			document.getElementById("followLink").style.display='';
	}
	});	
}

function removeFollowerFollowerPage(name, element, ckey){
	
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=removeFollower&ckey=" + ckey,
		complete: function changeText(){
		element.parentNode.style.display='none';
		element.parentNode.style.visibility='hidden';
	}
	});	
}

function addFollowerFollowerPage(name, element, ckey){
	
	$.ajax({  
		type: "POST",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=addFollower&ckey=" + ckey,
		complete: function changeText(){
		element.parentNode.setAttribute("class", "");
		document.getElementById("followedUsers").appendChild(element.parentNode);
		element.parentNode.removeChild(element);
	}
	});	
}