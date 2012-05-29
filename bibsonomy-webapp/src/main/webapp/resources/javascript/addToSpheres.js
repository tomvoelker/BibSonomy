/**
 * Function to generate the Sphere Dialog Window under a friendship Button
 * @param requestedUser - The sphere member
 * @param ckey			- ckey for the function updateUserRelation(...)
 * @param button		- The name of the friendship Button (resp. friendship Link's)
 * @param singleButton	- One or more Buttons on the page ? True for One Button.
 * @param sphereRow		- The position of the row, to adress the opening sphereDialog on a Page with more Buttons
 * @param dialogPosition- The parameter, which defines the Sphere Dialog Position. Particulars Information in the description
 * 						  of the setSphereDialogPosition() function.
 */
function generateSphereDialog(requestedUser, ckey, button, singleButton, rowCounter, dialogPosition) {
	
	/**
	 * Test, if the requested User is set.
	 * If there's no User, we need no Spheres.
	 */
	if(requestedUser == undefined) {
		return;
	}
	
	var requestedUser 		= requestedUser;
	var data				= null;
	var pulsateID			= null;
	var timeout 			= null;
	var newSphereText 		= null;
	var list 				= null;
	var friendshipButton 	= $('[name='+button+']');
	var loadingSpheres		= getString("spheres.sphere.menu.load");
	var addContainerHTML	= 	'<div class="addContainer">' + 
									'<div class="addSpheres initiallyHidden">' + 
										'<ul id="sphereList">' 	+ 
										   '<li id="loader">'	+
										   		'<span id="loader_Value">' + loadingSpheres + '</span>'	+
										   '</li>'	+	
										   '<li>'	+
										   		'<input type="text" name="sphereName" id="newSphere" size="27%" autocomplete="off" placeholder="Sphere"/>'	+
										   		'<input type="button" id="addNewSphere" value="add"/>'	+
										   '</li>'	+
										'</ul>'	+
									'</div>'	+
								'</div>';
	
	
	/**
	 * Function to set the JSON data of the Spheres
	 * @param	the JSON data
	 */
	function setData(parameterData) {
		data = parameterData;
	}


	/**
	 * The AJAX Request to get the Spheres of the logged-in User
	 * @returns - The Json File of the spheres
	 */
	var getSphereData = function(buildingSpheres) {
		var spheres = $.ajax({
			url: 		"/json/spheres",
			async:		true,
			success:	function(data){
				setData(data);
				if(buildingSpheres) {
					buildSpheres();	
				}
			}
		});	
	}


	/**
	 * Function to build the Spheres of the Sphere Dialog
	 */
	var buildSpheres = function() {   	
		
		var temp 		= null;
		var contains 	= null; 
		var userSpheres = new Array();
		
		//Put the Spheres in an Array, check if the requestedUser is in the Sphere
		$.each(data.items, function(indx_Item, item) {
			contains = false;
			temp = new Array();
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
		
		//Remove the pulsating "loading spheres" 
		$("#loader").remove();
	   
		//Stop Pulsating Function
		clearInterval(pulsateID);
	
		//Put the Sphere Elements in HTML
		for (var i = 0; i < userSpheres.length; i++) {
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
	};


	/**
	 * Creates the Checkboxes
	 */
	var createChkBox = function(sphereName) {
		return $("<input></input>")
					.attr("type","checkbox")
					.attr("style","position: absolute; right: 13px; margin-top: -2px;")
					.change(function(){callbackCheckbox(this, sphereName);});
	};
	
					
	/**
	 * Function, to cut the Spheres which are to long.
	 */
	var cutText = function (sphereName) {
		
		var text = sphereName;
		$(".addContainer").append($("<div><div/>")
									.css("position", "absolute")
									.css("visibility", "hidden")
									.css("height", "auto")
									.css("width", "auto")
									.attr("id", "textLengthTester")
									.text(text));
		
		var tempDiv = document.getElementById("textLengthTester");
		var width = tempDiv.clientWidth + 1;
		
		if(width <= 125) {
			$("#textLengthTester").remove();
			return text;
		}
		
		while(width > 125) {
			$("#textLengthTester").text(text);
		
			tempDiv = document.getElementById("textLengthTester");
			width = tempDiv.clientWidth + 1;

			text = text.slice(0, text.length - 1);
		}
		
		$("#textLengthTester").remove();
		return text + "...";
	}
	

	/**
	 * Creates the Text for Sphere Names
	 */
	var createText = function(sphereName) {
		var shortSphereName = cutText(sphereName);
		return $("<a></a>").attr("href","/sphere/" + encodeURIComponent(sphereName)).text(shortSphereName);
	};
	
	
	/**
	 * Creates the user Count of Spheres
	 */
	var createUserCount = function(sphereUsers) {
		var user;
		
		if(sphereUsers > 1) {
			user = getString("spheres.sphere.menu.user");
		} else {
			user = getString("spheres.sphere.menu.users");
		}
		
		return $("<strong></strong>").text(sphereUsers + " " + user).css("padding-left", 5).css("right", 35).css("position","absolute").attr("data-userCount",sphereUsers);
	};


   /**
    * Function, to add a new Sphere by the name of the Input Field in the Sphere Dialog List
    */
	var addNewSphere = function() {
	   var name = newSphereText.val();
	   var re = new RegExp("^[a-zA-Z0-9_-]+$");
	   if(re.test(name)) {
		   var child = $("<li></li>").data("sphereName", name);
		   child.append(createText(name));
		   child.append(createUserCount(1));
		   child.append(createChkBox(name).attr("checked","checked"));
		   list.children().last().before(child);
		   newSphereText.val("");
		   updateUserRelation('add', requestedUser, "sys:relation:" + name, ckey);
		   callbackShow(); 
	   } else {
		   alert(getString("error.field.valid.spheres"));
	   }
   };


   	/**
	 * Database Command for adding/removing the User of the Sphere
	 */
	var callbackCheckbox = function(el, sphereName) {
		if(el.checked) {
			updateUserRelation("add", requestedUser, "sys:relation:" + sphereName, ckey);   
		} else {
			updateUserRelation("remove", requestedUser, "sys:relation:" + sphereName, ckey); 
		}
	};


	/**
	 * Function, to pulsate the loader_Value in the div
	 */
	var pulsate = function() {
		$("#loader_Value").animate({opacity: 0.3}, 1000, 'linear').animate({opacity: 1}, 1000, 'linear');
	}


	/**
	 * Getter for the Timeout
	 */
	var getTo = function() {
		return timeout;
	};
	
	
	/**
	 * Setter for the Timeout
	 */
	var setTo = function(t) {
		timeout = t;
	};
	
	
	/**
	 * Function to Hide the Sphere Dialog
	 */
	var callbackHide = function() {
	    tO = getTo();
		setTo(setTimeout(function(){$(".addSpheres").slideUp('slow');}, 400));
	};

	/**
	 * Function, to add the Listener:
	 * - mouseleave of the addContainer div
	 * - the add new Sphere Button in the Sphere Dialog (klick)
	 * - the add new Sphere text Field (keypress = return)
	 */
	var addListener = function() {
		//Mouseenter and Mouseleave Handler for the Unsorted list of Spheres
		$(".addContainer").mouseleave(function() {callbackHide();}).mouseenter(function() {callbackShow();});
	
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
	 * Function, to set the Position of the Add to Spheres Dialog.
	 * 
	 * @Param dialogPosition
	 * 
	 * 
	 * dialogPosition = "position1"
	 * 
	 * 			|------------|BUTTON
	 * 			|			 |
	 * 			|------------|
	 * 
	 * dialogPosition = "position2"
	 * 
	 * 					BUTTON
	 *  		|------------|
	 * 			|			 |
	 * 			|------------|
	 *
	 */
	var setSphereDialogPosition = function(dialogPosition) {
		
		var xpos 		= friendshipButton.position();
		var xtop 		= xpos.top;
		var xleft 		= xpos.left;
		var currentTag 	= friendshipButton.offsetParent();
		var p 			= null;
		
		while(currentTag[0].tagName!='BODY') {
			p			 = currentTag.position();
			xtop		+= p.top;
			xleft		+= p.left;
			currentTag 	 = currentTag.offsetParent();
		}

		/**
		 * Function to check which Browser is used.
		 */
		function checkBrowserName(name){  
			var agent = navigator.userAgent.toLowerCase();  
				if (agent.indexOf(name.toLowerCase()) > -1) {  
				return true;  
			}  
			return false;  
		}  
			
		//TODO: Browserswitcher for Internet Explorer
		
		//SAFARI and CHROME
		if(checkBrowserName("safari" || "chrome")) {
			switch(dialogPosition) {
	            case "position1": 	{
	           		$(".addContainer").css("left", xleft - 280);		
					$(".addContainer").css("top",  xtop);
					$(".addContainer").css("width",  282);
	            }; break;
	            
	            case "position2": 	{
					$(".addContainer").css("top",  214);
					$(".addContainer").css("width",  282);
	            }; break;
	            
	            default: console.error("No or bad Parameter for Sphere Dialog Position.");
        	}
        	return;
		}
		
		//FIREFOX and OPERA
		if(checkBrowserName("firefox" || "opera")) {
			switch(dialogPosition) {
	            case "position1": 	{
	           		$(".addContainer").css("left", xleft - 260);		
					$(".addContainer").css("top",  xtop);
					$(".addContainer").css("width",  270);
	            }; break;
	            
	            case "position2": 	{
					$(".addContainer").css("top",  214);
					$(".addContainer").css("width",  270);
	            }; break;
	            
	            default: console.error("No or bad Parameter for Sphere Dialog Position.");
        	}
        	return;
		}
				
		//OTHER BROWSERS
		switch(dialogPosition) {
            case "position1": 	{
           		$(".addContainer").css("left", xleft - 260);		
				$(".addContainer").css("top",  xtop);
				$(".addContainer").css("width",  272);
            }; break;
            
            case "position2": 	{
				$(".addContainer").css("top",  214);
				$(".addContainer").css("width",  282);
            }; break;
            
            default: console.error("No or bad Parameter for Sphere Dialog Position.");
    	}
	}

	/**
	 * Function to show the Sphere Dialog
	 */
	var callbackShow = function() {		
		
		if(!$(".addSpheres").is(":visible")) {

			if(!singleButton) {
				//Remove all divs with the ID addContainer
				$(".addContainer").remove();
				
				//Add a new addContainer div
				friendshipButton.parent().append(addContainerHTML);
				
				list 			= $("#sphereList");
				newSphereText 	= $("#newSphere");
				requestedUser 	= getUserSelection(rowCounter);	
				
				//Add Mouse/Keyboard Listener to the new div
				addListener();
				
				//Let the loader pulsate 
				pulsate();	
				pulsateID = setInterval(pulsate,1999);

				//Get the Sphere data (JSON) and build them
				getSphereData(true);				
			}
			
			setSphereDialogPosition(dialogPosition);

			$(".addSpheres").each(function() {$(this).slideDown('slow');});			
		}
		
		window.clearTimeout(getTo());
   };	

	
	/**
	 * If there is only one Button:
	 * - Add the HTML Code
	 * - Let the loader_Value pulsate
	 * - Set the variables of the List and newSphereText
	 * - add the Listeners of the Button/TextField and addContainer Div
	 * - load the Spheres via AJAX and call the buildSpheres Function one Time
	 */
	if(singleButton) {
//		friendshipButton.parent().append(addContainerHTML);
		$("#sidebox").append(addContainerHTML);
		pulsate();	
		pulsateID = setInterval(pulsate,1999);
		list 			= $("#sphereList");
		newSphereText 	= $("#newSphere");
		addListener();	   	
		getSphereData(true);
	}

	
   /**
    * Add the mouseover and mouseleave of the friendship Button
    */
   friendshipButton.mouseover(function() {callbackShow();}).mouseleave(function() {callbackHide();});
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
	
	$("#spheresTemplates").load(backref.projectHome + 'resources/templates/spherestemplates.xml', function(data, textStatus) {
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
	this.fetchData(this.projectHome + 'json/spheres', null, callBack, callError);
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
