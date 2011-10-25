var dateFormatString = "MMM dd, yyyy, hh:mm a"; //Dec 10, 1815, 12:00 AM

/**
  Onload Handler
*/
function onLoad() {
    var prefs = new _IG_Prefs();
    var apiUrl    = "http://www.bibsonomy.org/api";
    var apiquery  = prefs.getString("query");

    fetchData(apiUrl + apiquery);
}

function $(x) {
    return document.getElementById(x);
}

function showOneSection(toshow) {
    var sections = [ 'main', 'approval', 'waiting' ];
    for (var i=0; i < sections.length; ++i) {
	var s = sections[i];
	var el = $(s);
	if (s === toshow) {
	    el.style.display = "block";
	} else {
	    el.style.display = "none";
	}
    }
}

// Concatenate the tags for one post
function concatTags(tags) {
    var copyTags = "";
    for(i=0; i<tags.length; i++) {
        copyTags += tags[i].name;
        if (i != tags.length -1) {
           copyTags += "+";
        }
    }
    return copyTags;
}

// parse and format a date
function parsePostDate(dateString) {
    if (!dateString) {
	dateString = "0";
    }
    // parseDate doesn't know milliseconds
    dateString = dateString.substring(0,19);
    var testDate = new Date(getDateFromFormat(dateString, 'yyyy-MM-ddTHH:mm:ss'));
    return formatDate(testDate, dateFormatString);
}

// does some data manipulation which is not possible using templates
// as the template library does not support function calls in the 
// library's expression language
function preparePosts(posts) {
    var i;
    for (i=0; i<posts.length; i++) {
        // beautify dates
	posts[i].changedate  = parsePostDate(posts[i].changedate);
	posts[i].postingdate = parsePostDate(posts[i].postingdate);

        // prepare Copy-tags
        posts[i].copytags = concatTags(posts[i].tag);
    }
}

function fetchData(url) {
    clearView('main');
    os.Loader.loadUrl('http://www.bibsonomy.org/resources/opensocial/templates/opensociallib.xml', function(){});
    os.Loader.loadUrl('http://www.bibsonomy.org/resources/opensocial/templates/bibsonomypostlib.xml', function(){});

    var params = {};
    params[gadgets.io.RequestParameters.CONTENT_TYPE]       = gadgets.io.ContentType.JSON;
    params[gadgets.io.RequestParameters.AUTHORIZATION]      = gadgets.io.AuthorizationType.OAUTH;
    params[gadgets.io.RequestParameters.METHOD]             = gadgets.io.MethodType.GET;
    params[gadgets.io.RequestParameters.OAUTH_SERVICE_NAME] = "BibSonomy";

    gadgets.io.makeRequest(url, function (response) {

	    // Approval needed
	    if (response.oauthApprovalUrl) { 
		var onOpen  = function() { showOneSection('waiting'); };
		var onClose = function() { onLoad(); };
		var popup   = new gadgets.oauth.Popup(response.oauthApprovalUrl,null, onOpen, onClose);
            				
		$('personalize').onclick = popup.createOpenerOnClick();
		$('approvaldone').onclick = popup.createApprovedOnClick();
		showOneSection('approval');

	    // Show Data
	    } else if (response.data) {
		preparePosts(response.data.posts.post);

		var template = os.getTemplate('postList');
		template.renderInto(document.getElementById('gadgetContent'), response.data);
				
		showOneSection('main');
		gadgets.window.adjustHeight();

	    // Error-Handling
	    } else {
		var whoops   = document.createTextNode("Whoops, something went wrong! You can try to reload this gadget or if this doesn't help, maybe there is a problem with your settings.");
		var br = document.createElement('br');

		var reloadLink = document.createElement('a');
                reloadLink.setAttribute('href', '#');
                reloadLink.setAttribute('onclick', 'onLoad()');
		var reloadLinkName = document.createTextNode('Reload this gadget.');
		reloadLink.appendChild(reloadLinkName);

		$('main').appendChild(whoops);
		$('main').appendChild(br);
		$('main').appendChild(reloadLink);
		showOneSection('main');
	    }
	}, params);
}

// Delete old dom tree if exists
function clearView(view) {
    var oldView = $(view);
    if (oldView && oldView.hasChildNodes) {
	while (oldView.childNodes.length >= 1) {
	    oldView.removeChild( oldView.firstChild );       
	} 
    }
}	
