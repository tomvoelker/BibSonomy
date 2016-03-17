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
	
	
	var checkForPermission = true;
	
	$('.pickUnpickPostBtn').each(function(){
		var param  = "";
		var href = $(this).attr("href");
		param = unescapeAmp(href.replace(/^.*?\?/, ""));
		param = unescapeAmp(param.replace(/pick|unpick/,pickUnpick));
		
		//ask Permission if necessary
		var isUnpick = param.search(/action=unpick/) != -1;
		if (checkForPermission && isUnpick && !confirmDeleteByUser("clipboardpost")) {
			return false;
		} else {
			//only ask for Permission ONCE
			checkForPermission = false;
		}
		
		
		updateClipboard(null, param);
	});
	return false;
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
	
	//ask Permission if necessary
	var isUnpick = params.search(/action=unpick/) != -1;
	if (isUnpick && !confirmDeleteByUser("clipboardpost")) {
		return false;
	}
	
	
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