/**
 * Yiven
 * 
 * jQuery ScrollPagination
 * 2020/02/17
 */
(function($){
    const defaults = {
        'url': null, // The url you are fetching the results.
        'autoload': true, // Change this to false if you want to load manually, default true.
        'data': {
            // These are the variables you can pass to the request, for example: which page you are
            'page': 1, // Which page at the firsttime
            'size': 10, // Number of pages
        },
        'before': function () {
            // Before load function, you can display a preloader div
            $(this.loading).fadeIn();
        },
        'after': function (elementsLoaded) {
            // After loading content, you can use this function to animate your new elements
            $(this.loading).fadeOut();
            $(elementsLoaded).fadeInWithDelay();
        },
        'scroller': $(window), // Who gonna scroll? default is the full window
        'heightOffset': 20, // It gonna request when scroll is 10 pixels before the page ends
        'loading': '#loading', // ID of loading prompt.
        'loadingText': 'Loading!', // Text of loading prompt.
        'loadingNoMoreText': 'No more.', // No more of loading prompt.
        'manuallyText': 'click to loading more.',
    };

    let requestInProgress = false;

    $.fn.scrollPagination = function(options) {
        const opts = $.extend(defaults, options);
        const target = opts.scroller;
        return this.each(function() {
            $.fn.scrollPagination.init($(this), opts);
        });
    };

    $.fn.stopScrollPagination = function(obj=null, opts=null){
        if(obj == null){
            return this.each(function() {
                $(this).attr('scrollPagination', 'disabled');
            });
        }else{
            $(opts.loading).text(opts.loadingNoMoreText).fadeIn();
            $(obj).attr('scrollPagination', 'disabled');
        }
    };

    $.fn.scrollPagination.loadContent = function(obj, opts){
		if (requestInProgress) {
			return;
		} else {
			requestInProgress = true;
		}

        const target = opts.scroller;
        // do before
        if (opts.before != null){
            opts.before();
        }
        $(obj).children().attr('rel', 'loaded');
        $.ajax({
            type: 'POST',
            url: opts.url,
            data: opts.data,
            dataType: 'html',
            success: function(data){
                let html = "";
                //alert(JSON.stringify(data));
                let dataCount;
                if (data !== "") {
                    $(opts.loading).text(opts.loadingText);
                    html = data;
                    dataCount = opts.data.pagesize * opts.data.page;
                    $(obj).append(html);
                    if (dataCount < opts.data.size) {
                        opts.data.page++;
                    } else {
                        $.fn.stopScrollPagination(obj, opts);
                    }
                } else {
                    $.fn.stopScrollPagination(obj, opts);
                }
                var objectsRendered = $(obj).children('[rel!=loaded]');
                // do after
                if (opts.after != null){
                    opts.after(objectsRendered);
                }

			requestInProgress = false;
            }
        });
    };

    $.fn.scrollPagination.init = function(obj, opts){
        const target = opts.scroller;
        $(obj).attr('scrollPagination', 'enabled');
        if($(obj).children().length === 0){
            $.fn.scrollPagination.loadContent(obj, opts);
        }
        if(opts.autoload === true){
            $(target).scroll(function(event){
                if($(obj).attr('scrollPagination') === 'enabled'){
                    const mayLoadContent = (Math.ceil($(target).scrollTop()) + opts.heightOffset) >= ($(document).height() - $(target).height());
                    if(mayLoadContent){
                        $.fn.scrollPagination.loadContent(obj, opts);
                    }
                }else{
                    event.stopPropagation(obj, opts);
                }
            });
            // $.fn.scrollPagination.loadContent(obj, opts);
        }else{
            opts.loadingText = opts.manuallyText;
            $(opts.loading).text(opts.loadingText).fadeIn().on('click', function(event){
                if($(obj).attr('scrollPagination') === 'enabled'){
                    $.fn.scrollPagination.loadContent(obj, opts);
                }else{
                    event.stopPropagation(obj, opts);
                }
            });
            // $.fn.scrollPagination.loadContent(obj, opts);
        }
    };
    
    // code for fade in element by element
    $.fn.fadeInWithDelay = function(){
        let delay = 0;
        return this.each(function(){
            $(this).delay(delay).animate({opacity:1}, 200);
            delay += 100;
        });
    };
})(jQuery);
