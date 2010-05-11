/**
* Inits an XMLHTTPRequest Obejct
*/
function initRequest(){
   	var req;
    try{
      	if(window.XMLHttpRequest){
        		req = new XMLHttpRequest();
      	}else if(window.ActiveXObject){
        		req = new ActiveXObject("Microsoft.XMLHTTP");
      	}
      	if( req.overrideMimeType ) {
          	req.overrideMimeType("text/xml");
       }        	
   	} catch(e){
   	   	return false;
   	}
   	return req;
}

/**
 * flags user as spammers for evaluation
 */
function flagSpammerEvaluator(name, rowId, disable, evaluator) {
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	
	/* colorize */
	if (rowId != null && disable=='false') {			
		document.getElementById(rowId).className="spammer";
	} else {
		document.getElementById(rowId).style.display = "none";
	}
	
	/* add spammer to db via AJAX*/
	runAjax("userName=" + name +"&evaluator=" + evaluator, "flag_spammer_evaluator");	
}

/**
* flags a user as spammer
* name: name of the user
* rowId: table row id 
* disable:  if true row is removed else row is colorized red
*/	
function addSpammer(name, rowId, disable) {		
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	/* colorize */
	if (rowId != null && disable=='false') {			
		document.getElementById(rowId).className="spammer";
	} else {
		document.getElementById(rowId).style.display = "none";
	}
	
	/* add spammer to db via AJAX*/
	runAjax("userName=" + name , "flag_spammer");
}


function addSpammerInline(name) {
	/* remove items of this user from current view */
	var elements = document.getElementsByName(name);	
	for (i = 0; i < elements.length; i++) {
		elements[i].parentNode.parentNode.style.display='none';
	}
	/* add spammer to db via AJAX*/
	runAjax("userName=" + name, "flag_spammer");
	
}

/**
* unflags a user as spammer
* name: name of the user
* rowId: table row id 
* disable:  if true row is removed else row is colorized white
*/	
function unflagSpammer(name, rowId, disable) {		
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	
	/* colorize row */
	if (rowId != null && disable=='false') {			
		document.getElementById(rowId).className="spamflag";
	} else {
		document.getElementById(rowId).style.display = "none"; 
	}
		
	/* remove spammer from db via AJAX*/
	runAjax("userName=" + name, "unflag_spammer");
}

/**
* unflags a user as spammer
* name: name of the user
* rowId: table row id 
* disable:  if true row is removed else row is colorized white
*/	
function unflagSpammerEvaluator(name, rowId, disable, evaluator) {		
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	
	/* colorize row */
	if (rowId != null && disable=='false') {			
		document.getElementById(rowId).className="nonspammer";
	} else {
		document.getElementById(rowId).style.display = "none"; 
	}
	
	/* mark spammer in db via AJAX*/
	runAjax("userName="+name+"&evaluator=" + evaluator, "unflag_spammer_evaluator");
}

/**
* sets a user as an unsure spammer
* name: name of the user
* rowId: table row id 
* disable:  if true row is removed else row is colorized white
*/	
function markUncertainUser(name, rowId, disable) {		
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	
	/* colorize row */
	if (rowId != null && disable=='false') {			
		document.getElementById(rowId).className="uncertainUser";
	} else {
		document.getElementById(rowId).style.display = "none"; 
	}
	
	/* remove spammer from db via AJAX*/
	runAjax("userName=" + name, "mark_uncertainUser");
}

/** saves the settings on admin page */
function updateSettings(key, value) {
	if (key == null || value == null || key == "" || value == "") {
		addLogMessage("please enter a valid value");
		return;
	}
	runAjax("key=" + key + "&value=" + value,"update_settings");	
}

/** 
* Generates an API key for the specified user
*/
function generateApiKey(name) {
	if (name == null || name == "") {
		addLogMessage("please specify a user");
		return;
	}
	runAjax("userName=" + name, "gen_api_key");
}
	
/* function interacts with server via ajax */
function runAjax(parameter,action) {
	var request = initRequest(); 
	var url = "/ajax?" + parameter;	   
   	if (request) {    	   		
   		request.open('GET',url + "&action=" + action,true);	
   		var handle = ajax_updateLog(request); 	   		
   		request.onreadystatechange = handle;
   		request.send(null);		   		
   	}    	
}

/* handler function */
function ajax_updateLog(request) {   			
	return function() {			
		if (4 == request.readyState) {    	
	    	addLogMessage(request.responseText);	    			    	   
	    }
	}
}

/* add a message to log box */
function addLogMessage(msg) {
	var division = document.getElementById("log");
	if (division) {
		var li = document.createElement("LI");
		li.innerHTML = msg;
		division.insertBefore(li,division.firstChild);
	}
}	

/* resets input fields */
function clearFields() {		
	document.getElementsByName("user")[0].value = "";
	document.getElementsByName("user")[1].value = "";
}	
