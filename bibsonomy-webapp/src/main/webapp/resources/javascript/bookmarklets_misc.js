var BookmarkletButton = {
		getButtonForName: function(name) {
			for(var i = 0; this.browsers.length > i; i++) 
				if(this.browsers[i].name.toLowerCase()==name.toLowerCase())
					return this.browsers[i];
			return null;
		},
		getButtonForAgent: function(){
			for(var i = 0; this.browsers.length > i; i++) 
				if(this.browsers[i].satisfies()) 
					return this.browsers[i];
			return null;
		},
		browsers: [
		           {
		        	   	name: "Chrome",
		        	   	url:	"http://",
		        	   	satisfies: function(){
		        	   		return $.browser.webkit&&navigator!=undefined&&navigator.userAgent.indexOf("Chrome")!=-1;
		        	   	}
		           },
			       {
		        	   	name: "Firefox",
			        	url:	"http://",
			        	version: "4",
			        	satisfies: function(){
			        		return $.browser.mozilla&&parseInt($.browser.version, 10);
			        	}
			       }
		],
		getBrowser: function() {
			for(var i = 0; this.browsers.length > i; i++) 
				if(this.browsers[i].satisfies())
					return this.browsers[i]; 
			return null;
		},
		createButton: function(options) {
			var browser = null;
			
			if(options!=undefined&&options.name!=undefined)
				browser = this.getButtonForName(options.name);
			else
				browser = this.getButtonForAgent();
			
			if(browser==null)
				return null;

			return {
				browser:browser,
				button:$("<a></a>").attr(
					{
						"class":"post.resource."+browser.name.toLowerCase()+".installButton",
						"href":browser.url
					}
				).html("<span class='post.resource.iconPlus'>"+getString("bookmarklet.buttonText"+browser.name)+"</span>")
			};
		}
}

function installBookmarkletButton (options){
	var result = BookmarkletButton.createButton();
	if(result==null)
		return false;
	
	if(options.emptyParent) {
		$("#"+options.buttonElementId).html('');
		$("#"+options.textElementId).html('');
	}

	if(options.sidebarInlay) 
		button = $("<li></li>").append($("<p></p>").html(result.button));

    $("#"+options.buttonElementId).append(result.button);
   	if(options.textElementId!=undefined)
  		$("#"+options.textElementId).append(
			"<h3>"
			+getString("bookmarklet.title"+result.browser.name)
			+"</h3>"
			+getString("bookmarklet.sidebarNotice"));
   	return result.browser;
}