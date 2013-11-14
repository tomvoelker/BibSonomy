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
		        	   	satisfies: function(){
		        	   		return $.browser.webkit&&navigator!=undefined
		        	   		&&navigator.userAgent.indexOf("Epiphany")==-1
		        	   		&&navigator.userAgent.indexOf("Chrome")!=-1;
		        	   	}
		           },
			       {
		        	   	name: "Firefox",
			        	version: "8",
			        	satisfies: function(){
			        		return $.browser.mozilla
			        		&&parseInt($.browser.version, 10)>=this.version;
			        	}
			       }
		],
		getBrowser: function() {
			for(var i = 0; this.browsers.length > i; i++) 
				if(this.browsers[i].satisfies())
					return this.browsers[i]; 
			return null;
		},
		showButton: function(options) {
			var browser = null;
			
			if(options!=undefined&&options.name!=undefined)
				browser = this.getButtonForName(options.name);
			else
				browser = this.getButtonForAgent();
			
			if(browser==null)
				return null;

			return browser;
			};
		}
}