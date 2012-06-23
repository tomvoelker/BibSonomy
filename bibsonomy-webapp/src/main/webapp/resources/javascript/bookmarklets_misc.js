function installBookmarkletButton(options){
	var browser = $.browser.mozilla?"Firefox":$.browser.webkit&&navigator!=undefined&&navigator.userAgent.indexOf("Chrome")!=-1?"Chrome":"";
	if((browser=="Firefox" && parseInt($.browser.version, 10)>=4 && (extension="xpi")) 
			|| (browser=="Chrome" && (extension="crx"))) {
				if(options.emptyParent) {
					$("#"+options.buttonElementId).html('');
					$("#"+options.textElementId).html('');
				}
				var button = 
					$("<a></a>").attr(
							{
								"class":"post.resource."+browser.toLowerCase()+".installButton",
								"href":"/resources/extensions/BibSonomyBookmarklet."+extension
							}
					).html("<span class='post.resource.iconPlus'>"+getString("bookmarklet.buttonText"+browser)+"</span>");

   				if(options.sidebarInlay) 
   					button = $("<li></li>").append($("<p></p>").html(button));

		    	$("#"+options.buttonElementId).append(button);
	    		if(options.textElementId!=undefined)
	    			$("#"+options.textElementId).append(
	    					"<h3>"
	    					+getString("bookmarklet.title"+browser)
	    					+"</h3>"
	    					+getString("bookmarklet.sidebarNotice"));
	}
	return browser;
}
