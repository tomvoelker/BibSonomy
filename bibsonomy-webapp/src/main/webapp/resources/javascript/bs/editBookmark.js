// methods for editBookmark page

function scraping() {
	var url = $("#post\\.resource\\.url").val();
	var selection = $("#post\\.description").val();
	if (url.length > 'http://'.length) {
		$.ajax({
			url : '/scrapingservice?url=' + encodeURIComponent(url)
					+ '&format=bibtex&doIE=false&selection='
					+ encodeURIComponent(selection),
			success : function(data) {
				if (data != '') {
					var f = document.createElement('form');
					var form = $(f)
					.attr('action', '/editPublication')
					.attr('method', 'POST');
					
					var input = $('<input />').attr('type', 'hidden')
					.attr('name', 'url')
					.val(url);
					
					var selectionInput = $('<input />').attr('type', 'hidden')
					.attr('name', 'selection')
					.val(selection);
					
					var content = $('#publication-found-form-placeholder').html();
					
					form.append(input)
					.append(selectionInput)
					.append(content);
					
					$('#publication-found-form-placeholder').html(form);
					
					$('#post\\.resource\\.url').popover('show');
				}
			},
			dataType : "text"
		});
	}
}

$(function() {

	$('#post\\.resource\\.url').popover({ 
	    html : true,
	    trigger: 'manual',
	    container: 'body',
	    placement: 'top',
	    title: function() {
	      	var title = $(this).parent().parent().find('.publication-found-title');
	      	/*
	      	var div = document.createElement('div');
	      	$(div).html(title.html() + '<button type="button" class="close" onclick="$(&quot;#url-resource-title&quot;).popover(&quot;hide&quot;);"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>');
	      	return $(div);
	      	*/
	      	return title;
	    },
	    content: function() {
	    	return $(this).parent().parent().find('.publication-found-content');
	    },
		delay: 0
	});
	/* popover show is triggered in scraping() */
	//$('#post\\.resource\\.url').popover('show');
	scraping();
	checkUrlForTitle();
	setFocus();
});

function setFocus() {
	var emptyFields = $(".content > input:text").filter(function() { 
		return $(this).val() == ""; 
	});
	if (emptyFields.length > 0) {
		emptyFields.first().focus();
	} else {
		$("#inpf_tags").focus();
	}
}

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

function toggleGroupBox() {
	document.getElementById("post.groups.other2").disabled = !document.getElementById("post.groups.other").checked;
}
