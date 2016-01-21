function pickAll() {
	return pickUnpickAll("pick");
}

function unpickAll() {
	return pickUnpickAll("unpick");
}

function unescapeAmp(string) {
	return string.replace(/&amp;/g, "&");
}

/**
 * Pick or unpick all publications from the current post list.
 * 
 * @param pickUnpick
 * @return
 */
function pickUnpickAll(pickUnpick) {
	var param  = "";
	$("#publications_0 ul.posts li.post div.ptitle a").each(function(index) {
		var href = $(this).attr("href");
		if (!href.match(/^.*\/documents[\/?].*/)){
			param += href.replace(/^.*bibtex./, "") + " ";
		}
	}
	);
	return updateClipboard("action=" + pickUnpick + "&hash=" + unescapeAmp(encodeURIComponent(param)));
}

/**
 * pick or unpick a single publication
 * 
 * @param element
 * @return
 */
function pickUnpickPublication(element) {
	/*
	 * pick/unpick publication
	 */
	var params = unescapeAmp($(element).attr("href")).replace(/^.*?\?/, "");
	return updateClipboard(element, params);
}



/**
 * picks/unpicks publications in AJAX style
 * 
 * @param param
 * @return
 */
function updateClipboard (element, param) {
	var isUnpick = param.search(/action=unpick/) != -1;
	if (isUnpick && !confirmDeleteByUser("clipboardpost")) {
		return false;
	}
	
	$.ajax({
		type: 'POST',
		url: "/ajax/pickUnpickPost?ckey=" + ckey,
		data : param,
		dataType : "text",
		success: function(data) {
		
		/*
		 * special case for the /clipboard page
		 * remove the post from the resource list and update the post count
		 */
		if (location.pathname.startsWith("/clipboard") && isUnpick) {
			var post = $(element).parents('li.post');
			post.slideUp(400, function() {
				post.remove();
			});
			var postCountBadge = $('h3.list-headline .badge');
			var postCount = parseInt(postCountBadge.text());
			postCountBadge.text(postCount - 1);
		}
		
		/*
		 * update the number of clipboard items
		 */
		$("#clipboard-counter").show().html(data);
		updateCounter();
	}
	});
	return false;
}

// TODO: maybe wrong place ?
function reportUser(a, userName){
	$.ajax({
		type: 'POST',
		url: $(a).attr("href")+ "?ckey=" + ckey,
		data: 'requestedUserName=' + userName + '&userRelation=SPAMMER&action=addRelation',
		dataType: 'text',
		success: function(data) {
			$('a.report-spammer-link ').each(function(index, link) {
				if ($(link).data('username') == userName) {
					$(link).parent().append($("<span class=\"ilitem\"></span>").text(getString("user.reported")));
					$(link).remove();
				}
			});
		}
	});
	return false;
}