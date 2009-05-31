/**
*
* This small js file should catch clicks on "follow this user" links
* and send request per ajax to the controller.
*
* $author: Christian Kramer
*
*/

function addFollower(name, infoText, linkText){
	$.ajax({  
		type: "GET",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=addFollower",
		complete: function changeText(){
			document.getElementById("followLink").style.visibility='hidden';
			document.getElementById("followLink").style.display="none";
			document.getElementById("removeLink").style.visibility='visible';
			document.getElementById("removeLink").style.display='';
		}
	});
}

function removeFollower(name, infoText, linkText){
	$.ajax({  
		type: "GET",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=removeFollower",
		complete: function changeText(){
			document.getElementById("removeLink").style.visibility='hidden';
			document.getElementById("removeLink").style.display="none";
			document.getElementById("followLink").style.visibility='visible';
			document.getElementById("followLink").style.display='';
	}
	});	
}

function removeFollowerFollowerPage(name, element){
	
	$.ajax({  
		type: "GET",  
		url: "/ajax/handleFollower",  
		data: "requestedUserName="+ name +"&action=removeFollower",
		complete: function changeText(){
		element.parentNode.style.display='none';
		element.parentNode.style.visibility='hidden';
	}
	});	
}