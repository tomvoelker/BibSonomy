function pickAll() {
	return pickUnpickAll("pick");
}

function unpickAll() {
	return pickUnpickAll("unpick");
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
	return updateClipboard("action=" + pickUnpick + "&hash=" + encodeURIComponent(param));
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
	var params = $(element).attr("href").replace(/^.*?\?/, "");
	return updateClipboard(params);
}



/**
 * picks/unpicks publications in AJAX style
 * 
 * @param param
 * @return
 */
function updateClipboard (param) {
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
			$("#pickctr").empty().append(data);
		}
	}
	});
	return false;
}