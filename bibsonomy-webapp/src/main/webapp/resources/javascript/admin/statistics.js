$(function() {
	$('td[data-url]').each(function(){
		var countContainer = $(this);
		countContainer.addClass('loading');
		var urlToCall = countContainer.data('url');
		$.ajax({
			url: urlToCall,
			async: false,
			success : function(data) {
				countContainer.text(data);
				countContainer.removeClass('loading');
			}
		});
	});
});