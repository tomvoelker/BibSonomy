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
	return updateBasket("action=" + pickUnpick + "&hash=" + unescapeAmp(encodeURIComponent(param)));
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
	return updateBasket(params);
}



/**
 * picks/unpicks publications in AJAX style
 * 
 * @param param
 * @return
 */
function updateBasket (param) {
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
		 * update the number of clipboard items
		 */
		if (location.pathname.startsWith("/clipboard") && !isUnpick) {
			// special case for the /clipboard page
			window.location.reload();
		} else {
			//$("#pickctr").empty().append(data);
			$("#basket-counter").html(data);
			updateCounter();
		}
	}
	});
	return false;
}

// TODO: maybe wrong place ?
function reportUser(a, userName){
	$.ajax({
		type: 'POST',
		url: $(a).attr("href")+ "?ckey=" + ckey,
		data: 'userName=' + userName,
		dataType: 'text',
		success: function(data) {
			$(a).parent().html('<span class="ilitem">' + data + '</span>');
		}
	});
	return false;
}