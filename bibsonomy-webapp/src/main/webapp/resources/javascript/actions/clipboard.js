$(function() {
	$('a.publ-export').click(function(e) {
		if (e.metaKey || e.ctrlKey) {
			return true;
		}
		var targetElement = $('#exportModalCitation');
		targetElement.html(getString("bibtex.citation_format.loading"));
		var exportModal = $('#exportModal');
		exportModal.modal('show');
		
		var postListItem = $(this).closest('li.post');
		var titleContainer = postListItem.find('.ptitle');
		var link = titleContainer.find('a');
		
		var publicationTitle = titleContainer.text();
		
		$('#exportModalLabel').text(publicationTitle);
		
		$(this).closest('div.btn-group').removeClass('open');
		
		loadExportLayout($(this), targetElement, link.attr('href'));
		
		return false;
	});
	
	var copyButton = $('#copyToLocalClipboard');
	var clipboard = new Clipboard(copyButton.get(0), {
		text: function(trigger) {
			var citationContainer = $('#exportModalCitation');
			var targetElement = citationContainer;
			var pre = citationContainer.find('pre');
			if (pre.length > 0) {
				targetElement = pre;
			}
			return targetElement.text();
		}
	});
	
	
	copyButton.mouseleave(function() {
		copyButton.tooltip('destroy');
	});
	
	clipboard.on('success', function(e) {
		copyButton.tooltip({
			placement: 'bottom',
			title: getString('export.copyToLocalClipboard.success')
		}).tooltip('show');
	});
});

function pickAll() {
	return pickUnpickAll(false);
}

function unpickAll() {
	return pickUnpickAll(true);
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
function pickUnpickAll(unpick) {
	var postsUI = $('#publications_0 ul.posts>li');
	var allPosts = "";
	postsUI.each(function() {
		var hash = $(this).data("intrahash");
		var user = $(this).data("user");
		var id = hash + "/" + user;
		
		allPosts += id + " ";
	});
	
	if (unpick && !confirmDeleteByUser("clipboardpost")) {
		return false;
	}
	
	var param = 'action=' + (unpick ? 'unpick' : 'pick') + '&hash=' + escape(allPosts);
	updateClipboard(null, param);
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
	
	// ask before deleting the pick
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
function updateClipboard(element, param) {
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
			var clipboardCounter = $(".clipboard-counter");
			var clipboardContainer = clipboardCounter.parent();
			clipboardCounter.text(data);
			clipboardCounter.css("display", "block !important").show();
			updateCounter();
		}
	});
	return false;
}

/*
 * update the counter at the navigation bar to reflect the amount of picked publications and unread messages
 */
function updateCounter() {
	var clipboardNum = $(".clipboard-counter:first");
	var inboxNum = $(".inbox-counter:first");
	var counter = $("#inbox-clipboard-counter");
	if (counter.length != 0) {
		var totalCount = 0;
		var clipboardCount = clipboardNum.length == 0 ? 0 : parseInt(clipboardNum.text());
		if (clipboardCount == 0) {
			clipboardNum.hide();
		}
		
		totalCount += clipboardCount;
		totalCount += inboxNum.length == 0 ? 0 : parseInt(inboxNum.text());
		counter.show().text(totalCount);
		if (totalCount == 0) {
			counter.hide();
		}
	}
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