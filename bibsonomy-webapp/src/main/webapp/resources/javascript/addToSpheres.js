$(document).ready(function() {
	var timeout = null;
	var userSpheres = new Array();
	var newSphere = $("#newSphere");
	var list = $("#unsortedList");	

	//Database Command for adding/removing the User of the Sphere
	var callbackCheckbox = function(el, sphereName) {
		if(el.checked) {
			addUserRelation(requestedUser, "sys:relation:" + sphereName, ckey);   
		} else {
			removeUserRelation(requestedUser, "sys:relation:" + sphereName, ckey); 
		}
	};
	
	//Creates the Checkboxes
	var createChkBox = function(sphereName) {
		return $("<input></input>")
					.attr("type","checkbox")
					.attr("style","position: absolute; right: 17px")
					.change(function(){callbackCheckbox(this, sphereName);});
	};

	//Getter for the Timeout
	var getTo = function() {
		return timeout;
	};
	
	//Setter of the Timeout
	var setTo = function(t) {
		timeout = t;
	};
	
	//Function to hide the Spheres List
	var callbackHide = function() {
	    tO = getTo();
		setTo(setTimeout(function(){$(".addCircles").animate({height : 0});}, 500));
	};

	//Function to show the Spheres List
	var callbackShow = function() {
		if($(".addCircles").height() == 0) {
			$(".addCircles").animate({height : 250});
		}
		window.clearTimeout(getTo());
   };
   
   //Function to add a new Sphere by the name of the Input Field
   var addNewSphere = function() {
	   if(newSphere.val() != ""){
		   var name = newSphere.val();
		   var child = $("<li></li>").data("sphereName", name).text(name).append(createChkBox(name).attr("checked","checked"));
		   list.children().last().before(child);
		   newSphere.val("");
		   addUserRelation(requestedUser, "sys:relation:" + name, ckey); 
	   } else {
		   alert("Keine Sphere angegeben.");
	   }
   };

   //The AJAX Request to get the Spheres of the logged-in User
   var spheres = $.ajax({
	   url: "/json/spheres",
	   success:function(data){
	
	   		//Iterate over all Spheres and identify the Spheres, the requested User is attached to
	   		var temp = new Array();

			$.each(data.items, function(indx_Item, item) {

				var contains = false; 
				var temp = new Array();
				temp.push(item.name);

				$.each(item.members, function(indx_Member, member) {
					if(member.name == requestedUser) {
						contains = true;
					}
				});

	
				if(contains == true) {
					temp.push("checked");
				} else {
					temp.push("unchecked");
				}		
				userSpheres.push(temp);
			});

			//Sort the Spheres
			userSpheres = userSpheres.sort(function(s,t) {
				var a = String(s).toLowerCase();
				var b = String(t).toLowerCase();
				if(a < b) return -1;
				if(a > b) return 1;
				return 0;
			});
			userSpheres = userSpheres.reverse();

			//Put the Sphere Elements in HTML
			for (var i = 0; i < userSpheres.length; i++){
				if(userSpheres[i][1] == "checked") {
					var child = $("<li></li>").data("sphereName", userSpheres[i][0]).text(userSpheres[i][0]).append(createChkBox(userSpheres[i][0]).attr("checked","checked"));
				} else {
					var child = $("<li></li>").data("sphereName", userSpheres[i][0]).text(userSpheres[i][0]).append(createChkBox(userSpheres[i][0]));
				}
				list.prepend(child);
			}
		}
	});			
   
   //Mouseenter and Mouseleave Handler for the Friend Button
   $("#sys\\:network\\:bibsonomy-friend_followLink").mouseenter(function() {callbackShow();}).mouseleave(function() {callbackHide();});
   $("#sys\\:network\\:bibsonomy-friend_removeLink").mouseenter(function() {callbackShow();}).mouseleave(function() {callbackHide();});
   
   //Mouseenter and Mouseleave Handler for the Unsorted list of Spheres
   $( ".addCircles").mouseleave(function() {callbackHide();}).mouseenter(function() {callbackShow();});

   //Handler for the Input Field to add a new Sphere by Mouseclick on the Button
   $(".addCircles input[type=button]").click(function(e){
		addNewSphere();
   });	
   
   //Handler for the Input Field to add a new Sphere by Keypress (13 == Return Key) 
   $(".addCircles input[type=text]").keypress(function(event) {
	   if( event.which == 13 ) {
		   addNewSphere();
	   }
   });
});