/**
 * @author Bernd
 */
function changeCVLayout(name) {
	$.ajax({
		type : "GET",
		url : "/ajax/cv",
		data : {
			layout : name,
			ckey : $('#ckey').val()
		},
		success : function(data) {
			var status = $("status", data).text();
			if ("ok" == status) {
				var wikiText = $("wikitext", data).text();
				var renderedWikiText = $("renderedwikitext", data).text();
				var wikiTextArea = $('#wikiTextArea');
				if ("" != renderedWikiText) {
					var wikiArea = $('#wikiArea');
					wikiTextArea.val(wikiText);
					wikiArea.empty();
					wikiArea.append(renderedWikiText);
				} else {
					wikiTextArea.val(wikiText);
				}
			} else {
				alert(data.globalErrors[0].message);
			}
		}
	});
}

function submitWiki(isSave) {
	$.ajax({
		type : "GET",
		url : "/ajax/cv",
		data : {
			ckey : $('#ckey').val(),
			wikiText : $('#wikiTextArea').val(),
			isSave : isSave
		},
		beforeSend : function() {
		},
		complete : function() {
		},
		success : function(data) {
			var status = $("status", data).text();
			if ("ok" == status) {
				var wikiArea = $('#wikiArea');
				var renderedWikiText = $("renderedwikitext", data).text();
				wikiArea.empty();
				wikiArea.append(renderedWikiText);
			} else {
				alert(data.globalErrors[0].message);
			}
		}
	});
}

function formatPublications() {
	$.ajax({
		type : "GET",
		url : "/ajax/cv",
		data : {
			ckey : $('#ckey').val(),
			pubFormat : $('#layout').val()
		},
		beforeSend : function() {
		},
		complete : function() {
		},
		success : function(data) {
			var status = $("status", data).text();
			if ("ok" == status) {
				var wikiArea = $('#wikiArea');
				var renderedWikiText = $("renderedwikitext", data).text();
				wikiArea.empty();
				wikiArea.append(renderedWikiText);
			} else {
				alert(data.globalErrors[0].message);
			}
		}
	});
}

function clearCVTextField() {
	var wikiTextArea = $('#wikiTextArea');
	wikiTextArea.val("");
}