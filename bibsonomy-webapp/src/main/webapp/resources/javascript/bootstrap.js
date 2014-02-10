$(function() {
	
	$('.collapse').collapse();
	
	$('.media-body .desc').ellipsis();
	$('.toggleAdvanced').click(function(event){
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});

	$('.post-popover, .help-popover').popover({ 
	    html : true,
	    trigger: "focus",
	    container: 'body',
	    placement: function() {
	    	console.log($(this));
			return 'right';	    	
	    },
	    title: function() {
	      	return $(this).next().html();
	    },
	    content: function() {
	      	return $(this).next().next().html();
	    }
	});
});