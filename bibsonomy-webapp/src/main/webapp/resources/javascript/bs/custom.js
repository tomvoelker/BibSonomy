$(function() {
	
	$('.collapse').collapse();

	$('.toggleAdvanced').click(function(event){
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});
	
	/*
	 * 
	$('.post-popover, .help-popover').popover({ 
	    html : true,
	    trigger: "focus",
	    container: 'body',
	    placement: function() {
			return 'right';	    	
	    },
	    title: function() {
	      	return $(this).next().html();
	    },
	    content: function() {
	      	return $(this).next().next().html();
	    }
	});
	*/
	$('.post-popover, .help-popover').popover({ 
	    html : true,
	    trigger: "focus",
	    container: 'body',
	    placement: function() {
    		return ($(window).width() >= 990) ? 'right' : 'auto';
    	},	    	
	    title: function() {
	      	$(this).next().html();
	    },
	    content: function() {
	      	return $(this).next().next().html();
	    },
		delay: 0
	});

	$('.system-tags-link').popover({
		animation: false,
		html: true,
		trigger: 'manual',
		placement : 'right',
		delay: 0,
		title: function() {
			return $(this).next().find('.popover-title').html();
		},
		content: function() {
			return $(this).next().find('.popover-content').html();
		}
		
	});
	
	$('.system-tags-link').mouseenter(function(event){
		$(this).popover('show');
	});
	
	$('.popover-close').click(function(event){
		$(this).parent().parent().prev().popover('hide');
	});
	
	/**
	 * publication details abstract and description more link
	 */
	maxChar = 350;
    dots = "&hellip;";
    moretext = "";
    lesstext = "";
    $('.more').each(function() {
        var content = $(this).html();
        
        if(content.length > maxChar) {
 
            lesstext = content.substr(0, maxChar);
            lesstext += dots + ' <a href="#" class="morelink">(more)</a>';
            moretext = content + ' <a href="#" class="morelink less">(less)</a>';
            $(this).html(lesstext);
        }
    });
 
    $(".morelink").click(function(event){
    	event.preventDefault();
        if($(this).hasClass('less')) {
        	$(this).removeClass('less');
            $(this).parent().html(lesstext);
            
        } else {
            $(this).addClass("less");
            $(this).parent().html(moretext);
        }
        return false;
    });
});