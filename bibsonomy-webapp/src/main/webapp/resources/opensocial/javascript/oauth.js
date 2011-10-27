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
	// ensure that the template library is loaded exactly once
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
	    	var onOpen  = function() {
	    		$("#gadgetContent").html(
					$("#tpl_OAuth_Waiting").render(response, {projectHome: backref.projectHome})
				);
				document.getElementById('approvaldone').onclick = backref.popup.createApprovedOnClick();
	    	};
			var onClose = function() { backref.onLoad(); };
			backref.popup   = new gadgets.oauth.Popup(response.oauthApprovalUrl, null, onOpen, onClose);
	            				
			$("#gadgetContent").html(
				$("#tpl_OAuth_Approval").render(response, {projectHome: backref.projectHome})
			);
			document.getElementById('personalize').onclick = backref.popup.createOpenerOnClick();
			
	    } else if (response.data) {
			//
		    // Show Data
			//
			$("#gadgetContent").html(
				$("#tpl_postList").render(response.data, {projectHome: backref.projectHome})
			);
			
			gadgets.window.adjustHeight();
	    } else {
		   	//
			// Error-Handling
		    //
	    	
	    	// shorten error messages
	    	if (response.oauthErrorText && response.oauthErrorText.indexOf("===")>0) {
	    		response.oauthErrorText = response.oauthErrorText.substring(0, response.oauthErrorText.indexOf("==="));
	    	} else if (!response.oauthErrorText) {
	    		response.oauthErrorText = JSON.stringify(response);
	    	}

		    // render error message
			$("#gadgetContent").html(
				$("#tpl_Error_OAuth").render(response, {projectHome: backref.projectHome})
			);
			gadgets.window.adjustHeight();
	    }
	}, params);
};

//------------------------------------------------------------------------------------------------
// helper methods
//------------------------------------------------------------------------------------------------
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


