$(function() {
	$('td[data-url]').each(function(){
		var countContainer = $(this);
		var urlToCall = countContainer.data('url');
		$.ajax({
			url: urlToCall,
			success : function(data) {
				countContainer.text(data);
			}
		});
	});
});