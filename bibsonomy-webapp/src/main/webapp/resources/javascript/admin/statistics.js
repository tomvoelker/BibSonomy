$(function() {
	$('td[data-url] a').click(function(){
		var countContainer = $(this).parent();
		countContainer.addClass('loading');
		countContainer.text("loading …")
		var urlToCall = countContainer.data('url');
		$.ajax({
			url: urlToCall,
			async: false,
			success : function(data) {
				countContainer.text(data);
				countContainer.removeClass('loading');
			}
		});
		return false;
	});
});