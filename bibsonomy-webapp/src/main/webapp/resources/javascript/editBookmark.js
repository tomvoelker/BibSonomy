// methods for editBookmark page

function checkUrlForTitle(  ){
	var req = new XMLHttpRequest();
	req.open("GET", '/generalAjax?action=getTitleForUrl&pageURL='+encodeURIComponent(document.getElementById("post.resource.url").value), true); // Request starten
	req.onreadystatechange = function() {
    	if ( req.readyState == 4 ) {
	    	if ( req.status == 200 ) {
			    var result = eval( "(" + req.responseText + ")" );
				if ( (result.pageTitle != "")&&(document.getElementById("post.resource.title").value=="") ) {
					var pageTitle = "<a href=\"javascript:setSuggestionFromUrl('title', '"+ result.pageTitle +"')\" tabindex=\"2\">" + result.pageTitle + "</a> ";
					document.getElementById( "suggestion.title" ).appendChild(document.createTextNode(pageTitle));
				}
				if (result.pageDescription != "") {
					var pageDescription = "<a href=\"javascript:setSuggestionFromUrl('description', '"+ result.pageDescription +"')\" tabindex=\"2\">" + result.pageDescription + "</a> ";
					document.getElementById( "suggestion.description" ).innerHTML = pageDescription;
				}
        	}
    	}
	};
						
	req.send(null);
}

function setSuggestionFromUrl(element, tagname){
	document.getElementById('post.' + element).value = tagname;
	document.getElementById('suggestion.' + element).innerHTML = "";
}

// setup jQuery to update recommender with form data
var tagRecoOptions = { 
   url:  '/ajax/getBookmarkRecommendedTags', 
   success: function showResponse(responseText, statusText) { 
	 handleRecommendedTags(responseText);
   } 
}; 