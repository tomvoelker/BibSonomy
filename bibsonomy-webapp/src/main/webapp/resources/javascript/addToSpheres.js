function initSpheres(requestedUser, ckey) {
	var timeout = null;
	var userSpheres = new Array();
	var newSphere = $("#newSphere");
	var list = $("#sphereList");	

	//function, to pulsate the loading text
	for(var i = 0; i < 10; i++) {
		$("#loader_Value").animate({opacity: 0.3}, 1000, 'linear').animate({opacity: 1}, 1000, 'linear');
	}
	
	//Database Command for adding/removing the User of the Sphere
	var callbackCheckbox = function(el, sphereName) {
		if(el.checked) {
			updateUserRelation("add", requestedUser, "sys:relation:" + sphereName, ckey);   
		} else {
			updateUserRelation("remove", requestedUser, "sys:relation:" + sphereName, ckey); 
		}
	};
	
	//Creates the Checkboxes
	var createChkBox = function(sphereName) {
		return $("<input></input>")
					.attr("type","checkbox")
					.attr("style","position: absolute; right: 13px;")
					.change(function(){callbackCheckbox(this, sphereName);});
	};
	
	//Creates the Text for Sphere Names
	var createText = function(sphereName) {
		var shortSphereName = sphereName;
		//Cut the Sphere-names, if the names are to long
		if(sphereName.length > 15) {
			shortSphereName = sphereName.slice(0,15);
			shortSphereName = shortSphereName + " ...";
		}
		return $("<a></a>").attr("href","/sphere/" + encodeURIComponent(sphereName)).text(shortSphereName);
	};
	
	//Creates the user Count of Spheres
	var createUserCount = function(sphereUsers) {
		var user;
		
		if(sphereUsers > 1) {
			user = getString("spheres.sphere.menu.user");
		} else {
			user = getString("spheres.sphere.menu.users");
		}
		
		return $("<strong></strong>").text(sphereUsers + " " + user).css("padding-left", 5).css("right", 35).css("position","absolute").attr("data-userCount",sphereUsers);
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
		setTo(setTimeout(function(){$(".addSpheres").slideUp('slow');}, 500));
	};

	//Function to show the Spheres List
	var callbackShow = function() {
		//jQuery.fx.off = false;
		if(!$(".addSpheres").is(":visible"))
			$(".addSpheres").each(function() {$(this).slideDown('slow');});
		window.clearTimeout(getTo());
   };
   
   //Function to add a new Sphere by the name of the Input Field
   var addNewSphere = function() {
	   var name = newSphere.val();
	   var re = new RegExp("^[a-zA-Z0-9_-]+$");
	   
	   if(re.test(name)){
		   var child = $("<li></li>").data("sphereName", name);
		   child.append(createText(name));
		   child.append(createUserCount(1));
		   child.append(createChkBox(name).attr("checked","checked"));
		   list.children().last().before(child);
		   newSphere.val("");
		   updateUserRelation('add', requestedUser, "sys:relation:" + name, ckey); 
	   } else {
		   alert(getString("error.field.valid.spheres"));
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
	
				if(contains) {
					temp.push("checked");
				} else {
					temp.push("unchecked");
				}		
				temp.push(item.members.length);
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
			
			//remove the pulsating "loading spheres" 
			$("#loader").remove();
			
			//Put the Sphere Elements in HTML
			for (var i = 0; i < userSpheres.length; i++){
				var child = $("<li></li>").data("sphereName", userSpheres[i][0]);
				child.append(createText(userSpheres[i][0]));
				child.append(createUserCount(userSpheres[i][2]));

				if(userSpheres[i][1] == "checked") {
					child.append(createChkBox(userSpheres[i][0]).attr("checked","checked"));
				} else {
					child.append(createChkBox(userSpheres[i][0]));
				}
				list.prepend(child);
			}
		}
	});			
   
   //Mouseenter and Mouseleave Handler for the Friend Button
   $("#sys\\:network\\:bibsonomy-friend_followLink").mouseenter(function() {callbackShow();}).mouseleave(function() {callbackHide();});
   $("#sys\\:network\\:bibsonomy-friend_removeLink").mouseenter(function() {callbackShow();}).mouseleave(function() {callbackHide();});
   
   //Mouseenter and Mouseleave Handler for the Unsorted list of Spheres
   $( ".addSpheres").mouseleave(function() {callbackHide();}).mouseenter(function() {callbackShow();});

   //Handler for the Input Field to add a new Sphere by Mouseclick on the Button
   $(".addSpheres input[type=button]").click(function(e){
		addNewSphere();
   });	
   
   //Handler for the Input Field to add a new Sphere by Keypress (13 == Return Key) 
   $(".addSpheres input[type=text]").keypress(function(event) {
	   if( event.which == 13 ) {
		   addNewSphere();
	   }
   });
}

/**
 * class for rendering spheres
 */
function SphereControl(projectHome) {
	this.projectHome = projectHome;
};


/**
 * Retrieves JSONized user recommendations from the recommendation service
 *  
 * @param callBack function(json) to call on success with recommendations data given in json
 * @param callError
 * 
 * @return JSONized user recommendations 
 */
SphereControl.prototype.renderSpheres = function() {
	var backref = this;
	
	$("#spheresTemplates").load(backref.projectHome + '/resources/templates/spherestemplates.xml', function(data, textStatus) {
			backref.getSpheres(function(data){backref["cb_handleSpheres"](data)});
	});
};

/**
 * Retrieves JSONized list of spheres
 *  
 * @param callBack function(json) to call on success with recommendations data given in json
 * @param callError
 * 
 * @return JSONized user recommendations 
 */
SphereControl.prototype.getSpheres = function(callBack, callError) {
	this.fetchData(this.projectHome + '/json/spheres', null, callBack, callError);
};

/**
 * ajax request handler 
 * 
 * @param url the REST-Url to query 
 * @param queryParam string containing the query parameters
 * @param callBack function(json) to call on success with talk data given in json
 * @param callError
 * @return
 */
SphereControl.prototype.fetchData = function(url, queryParam, callBack, callError) {
	$.ajax( {
		type : "GET",
		url : url,
		data : queryParam,
		dataType : "jsonp",
		jsonp : "callback",
		success : callBack,
		error : callError
	});	
};

/**
 * render spheres
 */
SphereControl.prototype.cb_handleSpheres = function(data) {
    var response = new Object();
    data.projectHome = this.projectHome;
    
    // defined in 'spherestemplates.xml'
    this.preprocessSpheres(data);
    
    $("#spheresCloudElement").html(
			$("#tplSpheresCloud").render(data, data)
	);
};
