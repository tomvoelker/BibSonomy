$(function() {
	
	$('.input-group.date').datepicker({autoclose: true});
	
	$('.collapse').collapse();

	$('.toggleAdvanced').click(function(event){
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});
	
	/*
	 * 
	 * $('.post-popover, .help-popover').popover({ html : true, trigger:
	 * "focus", container: 'body', placement: function() { return 'right'; },
	 * title: function() { return $(this).next().html(); }, content: function() {
	 * return $(this).next().next().html(); } });
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
    	
        var moreLink = $(document.createElement("a"));
        var contentContainer = $(this).children(".contentContainer")[0];
        moreLink.data("text", contentContainer.innerHTML).html(getString("more")).addClass("moreLink").click(function(event){
        	event.preventDefault();
        	var contentContainer = $(this.parentNode).children(".contentContainer")[0];
            if($(this).hasClass('less')) {
            	$(this).html("("+getString("more")+")").removeClass("less").addClass("more");
            } else {
            	$(this).html("("+getString("less")+")").removeClass("more").addClass("less");
            }
            shortenContent(contentContainer, moreLink.data("text"));
            return false;
        });
        
        
        this.appendChild(moreLink[0]);
        if(!shortenContent(contentContainer, moreLink.data("text"))) {
        	moreLink.hide();
        }
    });
    
    $('.rename-tags-btn').click(function(){
    	$(this).parent().prev().focus().next().show().hide();
    });
    
    $("#linkBox").click(function(e){
    	$(this).focus().select();
    });
    
    $('.remove-btn').click(function(e){
    	e.preventDefault();
    	var url = this.getAttribute("href");
    	var parent = this.parentNode.parentNode;
    	var el = this;
    	$.ajax({
    		url: url,
    		dataType: "xml",
    		success: function(data) {
    			handleDeleteResponse({parent:parent, data: data, el: el});
    			$(el).addClass("inputActionSuccess");
    		},
    		error: function(data) {
    			handleDeleteResponse({parent:parent, data: data, el: el});
    			$(el).addClass("inputActionError");
    		}
    	});
    	
    	return false;
    });
    
    function dummyHandler(e) {
    	e.preventDefault();
    	return false;
    }
    
    function handleDeleteResponse(o) {
    	
		o.parent.parentNode.removeChild(o.parent);
		if($("status", data).text()=="ok")
			$(el).addClass("inputActionSuccess");
		else
			$(el).addClass("inputActionError");
		setTimeout(borderTimeout(o.el), 2000);
    }
    
    function borderTimeout(el) {
    	$(el).removeClass("inputActionError").removeClass("inputActionSuccess");
    }
    
    function shortenContent (el, text) {
    	var shortened = false;
    	if(el.innerHTML.length > maxChar + dots.length) {
            text = text.substr(0, maxChar) + dots;
            shortened = true;
        }
        $(el).html(text);
        return shortened;
    }
});