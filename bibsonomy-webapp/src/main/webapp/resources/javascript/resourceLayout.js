$(document).ready(function() {

	$('.tag-popover').popover({
		html : true,
		content : function() {
			return $(this).parent().children(".popover-content-custom").html();
		}
	}).click(function(e) {
		e.preventDefault();
	});

	$('.ptags').each(function() {
		// +6 to avoid missmatch because of paddings
		if ($(this)[0].scrollHeight > ($(this).innerHeight() + 6)) {
			$('.all-tags-button', this).removeClass("hidden");
		}
	});
	
});