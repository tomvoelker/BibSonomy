// methods for postBookmark page

function checkUrlForTitle(  ){
	var req = new XMLHttpRequest();
	req.open("GET", '/generalAjax?action=getTitleForUrl&pageURL='+encodeURIComponent(document.getElementById("post.resource.url").value), true); // Request starten
	req.onreadystatechange = function() {
    	if ( req.readyState == 4 ) {
	    	if ( req.status == 200 ) {
			    var result = eval( "(" + req.responseText + ")" );
				if ( (result.pageTitle != "")&&(document.getElementById("post.resource.title").value=="") ) {
					var pageTitle = "<a href=\"javascript:setSuggestionFromUrlTitle('"+ result.pageTitle +"')\" tabindex=\"1\">" + result.pageTitle + "</a> ";
					document.getElementById( "suggestion.title" ).innerHTML = pageTitle;
				}
				if (result.pageDescription != "") {
					var pageDescription = "<a href=\"javascript:setSuggestionFromUrlDescription('"+ result.pageDescription +"')\" tabindex=\"2\">" + result.pageDescription + "</a> ";
					document.getElementById( "suggestion.description" ).innerHTML = pageDescription;
				}
        	}
    	}
	};
						
	req.send(null);
}

function setSuggestionFromUrlDescription(tagname){
	document.getElementById('post.description').value=tagname;
	document.getElementById('suggestion.description').innerHTML = "";
}

function setSuggestionFromUrlTitle(tagname){
	document.getElementById('post.resource.title').value=tagname;
	document.getElementById( "suggestion.title" ).innerHTML ="";
}

// setup jQuery to update recommender with form data
var tagRecoOptions = { 
   url:  '/ajax/getBookmarkRecommendedTags', 
   success: function showResponse(responseText, statusText) { 
	 handleRecommendedTags(responseText);
   } 
}; 