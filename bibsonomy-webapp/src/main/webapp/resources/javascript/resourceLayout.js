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

	$('.sort-info').popover({
		html: true,
		placement: 'top',
		trigger: 'manual',
		content: function() {
			return $(this).parent().children('.sort-info-content').html();
		}
	}).on("mouseenter", function() {
		var _this = this;
		$(this).popover("show");
		$(".popover").on("mouseleave", function() {
			$(_this).popover('hide');
		});
	}).on("mouseleave", function() {
		var _this = this;
		setTimeout(function() {
			if (!$(".popover:hover").length) {
				$(_this).popover("hide");
			}
		}, 300);
	}).click(function(e) {
		e.preventDefault();
	});

});