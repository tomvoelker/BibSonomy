
/*
 * Register listeners on document ready.
 */
$(function() {
	var bookmarkRegex = /editBookmark\?hash=([^&]+)&user=([^&]+)/;
	var publicationRegex = /[^\/]+\/([^\/]+)\/([^\/]+)/;
	
	$(".bookmarksContainer .ptitle").click(function() {
		sendAjaxRecommenderFeedback(bookmarkRegex, $(this).closest('.bookmarksContainer').find("a.copy").attr("href"), "bookmark");
	});
	$(".bookmarksContainer a.copy").click(function() {
		sendAjaxRecommenderFeedback(bookmarkRegex, $(this).attr("href"), "bookmark");
	});
	$(".publicationsContainer .ptitle").click(function() {
		sendAjaxRecommenderFeedback(publicationRegex, $(this).children("a").attr("href"), "bibtex");
	});
	$(".publicationsContainer a.copy, .publicationsContainer a.pick").click(function() {
		var publicationUrl = $(this).closest('.publicationsContainer').find(".ptitle a").attr("href");
		sendAjaxRecommenderFeedback(publicationRegex, publicationUrl, "bibtex");
	});
});

/*
 * Extract username and intrahash of post via regex out of the extractionstring
 * and send the data via ajax to the ItemRecommenderFeedbackController.
 * Action specifies whether a bookmark or a publication was clicked.
 * 
 */
function sendAjaxRecommenderFeedback(regex, extractionString, action) {
	
	var data = regex.exec(extractionString);
	
	var intraHash = data[1];
	var userName = data[2];
	
	$.ajax({
	      async: false,
	      type: "POST", 
	      url: "/ajax/addItemRecommenderFeedback", 
	      data: "userName="+userName+"&intraHash="+intraHash+"&action=" +action
	}); 
}