/**
 * @author Bernd Terbrack
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
	return false;
}

function submitWiki(renderOptions) {
	$.ajax({
		type : "GET",
		url : "/ajax/cv",
		data : {
			ckey : $('#ckey').val(),
			wikiText : $('#wikiTextArea').val(),
			renderOptions : renderOptions
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
	return false;
}

function formatPublications(self) {
	var layout = $(self).val();
	var tags = $(self).next().val();
	var reqUser = $('#reqUser').val();
	$(self).parent().parent().parent().next().empty();
	$.get("/layout/"+layout+"/user/"+reqUser+"/"+tags, function(data){
		$(self).parent().parent().parent().next().html(data);
	});
	return false;
}

function clearCVTextField() {
	var wikiTextArea = $('#wikiTextArea');
	wikiTextArea.val("");
	return false;
}

function toggleDetails(element) {
	var details = $(element).next();
	details.toggle();
	return false;
}
