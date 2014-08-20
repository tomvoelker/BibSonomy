// methods for editBookmark page

function scraping() {
	var url = $("#post\\.resource\\.url").val();
	if (url.length > 'http://'.length) {
		$.ajax({
			url : '/scrapingservice?url=' + encodeURIComponent(url)
					+ '&format=bibtex&doIE=false&selection='
					+ encodeURIComponent($("#post\\.description").val()),
			success : function(data) {
				if (data != '') {
					$("#bib").val(data);
					var b = document.getElementById("scrapable");
					b.style.display = (b.value != '' ? '' : 'hidden');//showHide("scrapable");
				}
			},
			dataType : "text"
		});
	}
}

$(function() {
	scraping();
	checkUrlForTitle();
});

function checkUrlForTitle() {
	var req = new XMLHttpRequest();
	req.open("GET",
			'/generalAjax?action=getTitleForUrl&pageURL='
					+ encodeURIComponent(document
							.getElementById("post.resource.url").value), true); // Request
																				// starten
	req.onreadystatechange = function() {
		if (req.readyState == 4) {
			if (req.status == 200) {
				var result = eval("(" + req.responseText + ")");
				if ((result.pageTitle != "")
						&& (document.getElementById("post.resource.title").value == ""))
					addSuggestionLink('resource.title', result.pageTitle);
				if (result.pageDescription != "")
					addSuggestionLink('description', result.pageDescription);
			}
		}
	};

	req.send(null);
}

function addSuggestionLink(name, content) {
	var a = document.createElement("a");
	a.onclick = function() {
		document.getElementById('post.' + name).value = content;
		document.getElementById('suggestion.' + name).innerHTML = "";
	};
	a.appendChild(document.createTextNode(content));
	a.style.cursor = "pointer";
	document.getElementById("suggestion." + name).appendChild(a);
}

// setup jQuery to update recommender with form data
var tagRecoOptions = {
	url : '/ajax/getBookmarkRecommendedTags',
	success : function showResponse(responseText, statusText) {
		handleRecommendedTags(responseText);
	}
};
