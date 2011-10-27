/**
 * class for managinig javascript part of BibSonomy's OpenSocial gadget
 * 
 * @param projectHome base url of the BibSonomy 
 * @param templateDomElement id of the dom element where the template definitions will be loaded
 * @param prefs container preferences
 */
function GadgetControl(projectHome, templateDomElement, prefs) {
	this.projectHome = projectHome;

    this.prefs = prefs; 
    this.apiUrl = projectHome+"api";
    this.apiquery = prefs.getString("query");
    
    // set to true, when templates are loaded
    this.isReady = false;
    
};

/**
 * onload handler
 */
GadgetControl.prototype.onLoad = function() {
	if (!this.isReady) {
	    var backref = this;
		$("#templates").load(this.projectHome+'resources/opensocial/templates/bibsonomylib.xml', function(data, textStatus) {
			backref.isReady = true;
		    backref.onLoad();
		});
	}
    this.fetchData(this.apiUrl + this.apiquery);
};

/**
 * fetches and displays data via oauth
 * 
 * @param url
 * @return
 */
GadgetControl.prototype.fetchData = function(url) {
    this.clearView('main');
    
    var params = {};
    params[gadgets.io.RequestParameters.CONTENT_TYPE]       = gadgets.io.ContentType.JSON;
    params[gadgets.io.RequestParameters.AUTHORIZATION]      = gadgets.io.AuthorizationType.OAUTH;
    params[gadgets.io.RequestParameters.METHOD]             = gadgets.io.MethodType.GET;
    params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = "BibSonomy";

    var backref = this;
    gadgets.io.makeRequest(url, function (response) {
    	// check the state of the oauth dance
	    if (response.oauthApprovalUrl) { 
	    	//
		    // Approval needed
	    	//
	    	var onOpen  = function() { backref.showOneSection('waiting'); };
			var onClose = function() { backref.onLoad(); };
			var popup   = new gadgets.oauth.Popup(response.oauthApprovalUrl, null, onOpen, onClose);
	            				
			document.getElementById('personalize').onclick = popup.createOpenerOnClick();
			document.getElementById('approvaldone').onclick = popup.createApprovedOnClick();
			backref.showOneSection('approval');
	    } else if (response.data) {
			//
		    // Show Data
			//
			$("#gadgetContent").html(
				$("#tpl_postList").render(response.data, {projectHome: backref.projectHome})
			);
			
			backref.showOneSection('main');
			gadgets.window.adjustHeight();
	    } else {
		   	//
			// Error-Handling
		    //

		    // render error message
			$("#gadgetContent").html(
				$("#tpl_Error_OAuth").render(response, {projectHome: backref.projectHome})
			);
			backref.showOneSection('main');
	    }
	}, params);
};

//------------------------------------------------------------------------------------------------
// helper methods
//------------------------------------------------------------------------------------------------
/**
 * switches between the different views 'main', 'approval' and 'waiting'
 * which are used to model the oauth authorization process
 * 
 * @param toshow
 * @return
 */
GadgetControl.prototype.showOneSection = function(toshow) {
    var sections = [ 'main', 'approval', 'waiting' ];
    for (var i=0; i < sections.length; ++i) {
		var s = sections[i];
		var el = document.getElementById(s);
		if (s === toshow) {
		    el.style.display = "block";
		} else {
		    el.style.display = "none";
		}
    }
};

/**
 * Delete old dom tree if exists
 */
GadgetControl.prototype.clearView = function(view) {
    var oldView = document.getElementById(view);
    if (oldView && oldView.hasChildNodes) {
		while (oldView.childNodes.length >= 1) {
		    oldView.removeChild( oldView.firstChild );       
		} 
    }
};


