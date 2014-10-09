$(function() {
	
	$('.input-group.date').datepicker({autoclose: true});
	
	$('.collapse').collapse();

	$('.toggleAdvanced').click(function(event){
		event.preventDefault();
		$(this).parents('li.media').find('.advanced').toggle();
	});
	
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
		content: function() {
			return $(this).next().find('.popover-content').html();
		}
		
	});
	
	
	$('.system-tags-link').click(function(event){
		event.preventDefault();
		$(this).popover('show');
	});

	/**
	 * publication details abstract and description more link
	 */
	maxChar = 350;
    dots = "&hellip;";
    moretext = "";
    lesstext = "";
    
    $('.show-more').each(function() {
    	
        var moreLink = $(document.createElement("a"));
        var contentContainer = $(this).children(".contentContainer")[0];
        
        if(contentContainer) {
        
	        moreLink.data("text", contentContainer.innerHTML).html("(" + getString("more") + ")").addClass("moreLink").click(function(event){
	        	event.preventDefault();
	        	var contentContainer = $(this.parentNode).children(".contentContainer")[0];
	        	
	            if($(this).hasClass('show-less')) {
	            	$(this)
	            	.html("(" + getString("more") + ")")
	            	.removeClass("show-less")
	            	.addClass("show-more");
	            } else {
	            	$(this)
	            	.html("(" + getString("less") + ")")
	            	.removeClass("show-more")
	            	.addClass("show-less");
	            }
	            shortenContent(contentContainer, moreLink.data("text"));
	            return false;
	        });
	        
	        this.appendChild(moreLink[0]);
	        if(!shortenContent(contentContainer, moreLink.data("text"))) {
	        	moreLink.hide();
	        }
        }
        

    });
    
    $('.rename-tags-btn').click(function(){
    	$(this).parent().prev().focus().next().show().hide();
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
    		},
    		error: function(data) {
    			handleDeleteResponse({parent:parent, data: data, el: el});
    		}
    	});
    	
    	return false;
    });
    
    if($('#sidebar').prev().hasClass('content')) {
    	var contentContainer = $('#sidebar').prev();
    	var contentHeight = contentContainer.height();
    	var sidebarHeight = $('#sidebar').height();
    	
    	if(contentHeight > sidebarHeight) {
    		$('#sidebar').css('height', contentHeight+20);
    	}
    	
    }
    
    function handleDeleteResponse(o) {
		if(o.data.getElementsByTagName("status")[0].innerHTML=="deleted" 
			|| o.data.getElementsByTagName("status")[0].innerHTML=="ok") o.parent.parentNode.removeChild(o.parent);
		else {
			$(o.el).removeClass("btn-stripped").addClass("btn-danger").popover({
					animation: false,
					trigger: 'manual',
					delay: 0,
					title: function() {
						return getString("post.resource.remove.error.title");
					},
					content: function() {
						return getString("post.resource.remove.error");
					}
			}).popover("show");
		}
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
    
    
    $('.community-page-user-list li a.show-less').click(function(event){
    	event.preventDefault();
    	$(this).parent().parent().find('.show').each(function() {
    		$(this).removeClass('show').addClass('hidden');
    	});
    });
    
    $('.community-page-user-list li a.show-more').click(function(event){
    	event.preventDefault();
    	$(this).parent().parent().find('.hidden').each(function() {
    		$(this).removeClass('hidden').addClass('show');
    	});
    	
    });
});

function dummyDownHandler(e) {
	var event = e || window.event;

	var character = String.fromCharCode((96 <= e.keyCode && e.keyCode <= 105)? e.keyCode-48 : e.keyCode);
	if(!(character.toLowerCase()=='c'&& event.ctrlKey)) {
    	e.preventDefault();
    	e.stopPropagation();
    	return false;
    }
}

function dummyHandler(e) {
	
	e.preventDefault();
	
	
	
	e.stopPropagation();
	return false;
}

function dummyUpHandler(e) {
	
	e.preventDefault();
	
	
	
	e.stopPropagation();
	return false;
}

function activateAffixEntry (el) {
	$(el).addClass("active").siblings().each(function(h, g){
			$(g).removeClass("active");
	});
}