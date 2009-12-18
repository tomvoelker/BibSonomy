var activeField = null;
var sidebar     = null;
var tagbox      = null;
var tags_toggle = 0;
var tags_filter = null;
var ckey        = null;
var currUser    = null;
var requUser	= null;
var projectName = null;

function init (tagbox_style, tagbox_sort, tagbox_minfreq, lrequUser, lcurrUser, lckey, lprojectName) {
  add_hints();
  sidebar = document.getElementById("sidebar");
  tagbox  = document.getElementById("tagbox");
  ckey = lckey;
  currUser = lcurrUser;
  
  if(lrequUser != "") {
  	requUser = lrequUser;
  }
  
  projectName = lprojectName;

  if (tagbox) {
    init_tagbox(tagbox_style, tagbox_sort, tagbox_minfreq, lrequUser);
  }
  
  if (!location.pathname.startsWith("/postPublication")){
	  if (sidebar) {  
	    add_filter();
	    init_sidebar();
	  }
  }
  
  add_tags_toggle();
}

function stopEvt () {
	return false;
}

function toggleSidebar() {
  if ( $('#sidebarroundcorner').css('visibility') == 'visible' ){
	  $('#sidebarroundcorner').css({
		  'visibility' : 'collapse'
	  });

	  $('#toggleSidebarButton').attr({
		  'src' : '${resdir}/image/open.png'
	  });
  } else {
	  $('#sidebarroundcorner').css({
		  'visibility' : 'visible'
	  });

	  $('#toggleSidebarButton').attr({
		  'src' : '${resdir}/image/close.png'
	  });
  }
}

function add_filter() {
  var f = document.createElement("form");
  f.onsubmit=stopEvt;
  f.title="filter sidebar";
  f.style.padding='5px';
  f.style.display = "inline";
  f.appendChild(document.createTextNode("filter: "));
  
  tags_filter = document.createElement("input");
  tags_filter.autocomplete="off";
  tags_filter.type="text";
  tags_filter.style.size="10ex";
  tags_filter.onkeyup = filter_tags;

  f.appendChild(tags_filter);
    
  var l = document.createElement("li");
  l.appendChild(f);
  
  sidebar.insertBefore(l, sidebar.childNodes[0]); // first child
}

function init_sidebar() {
  var childs = sidebar.childNodes;
  for (var i=0; i < childs.length; i++) {
    var elem = childs[i]
    
    if (elem.nodeName.toUpperCase() == "LI") {
      for (var j=0; j < elem.childNodes.length; j++) {
         var elem2 = elem.childNodes[j];
         if (elem2.className == "sidebar_h" ) {
            elem2.insertBefore(getToggler(), elem2.firstChild);
         }      
      }
    }
  }
}

function getToggler() {
  var toggle = document.createElement("span");
//  var text = document.createTextNode("-");//String.fromCharCode(9830));
  var text = document.createElement("img");
  text.src = "/resources/image/icon_collapse.png";
  text.border = 0;
  toggle.appendChild(text);
  toggle.className = "toggler";
  toggle.onclick = hideNextList; 
  
  return toggle;
}

// hide/unhide the next list following the element which called the method
function hideNextList() {
  var node = this.parentNode;
  var content = this.firstChild;
  
  while (node && node.nodeName.toUpperCase() != "UL") {
    node = node.nextSibling;
  }
  
  if (node) {
    if (node.style.display != "none") {
      node.style.display = "none";
      content.src="/resources/image/icon_expand.png";
    } else {
      node.style.display = "block";
      content.src="/resources/image/icon_collapse.png";
    }
  }
}

function confirmDelete(e) {
  var event = xget_event(e);
  // highlight post
  var li = getParent(event, "bm");
  li.style.background="#fdd";
  // get confirmation
  var del = confirm(getString("post.meta.delete.confirm"))
  li.style.background="transparent";
  return del;
}

function getParent(node, clazz) {
   if (node.className == clazz) return node;
   return getParent(node.parentNode, clazz);
}

// textarea resize
function sz(t) {
	a=t.value.split('\n');
	b=0;
	for (x=0; x<a.length; x++) {
		if (a[x].length >= t.cols) b+= Math.floor(a[x].length/t.cols);
	}
	b+= a.length;
	if (b>t.rows) t.rows=b;
}


//queries the titles and further details of publications by a partial title
function getSuggestions(text) {
	$("textarea[@id=post.resource.title]").blur(function() {
	      window.setTimeout(function() {
	    	  $("#suggestionBox").hide();
	      },
	      140);
 });
	
 if(text.length < 2 || text.length%2 != 0) {
	   $("#suggestionBox").hide();
 } else {
 	var query = $.ajax({
	        type: "GET",
	        url: "/ajax/getPublicationsByPartialTitle.json?title="+text,
	        dataType: "json",
	        success: function(json){processResponse(json);},
 	});
	}
}
/** Process the JSON Data and make visible to the user
* 
*/	
function processResponse(data) {
	// if there's no data cancel
	if(data.items.length == 0) {
		return;
	}
	
	var p = $("<div style=\"background-color: #006699; color: #FFFFFF; padding:3px;\">Suggestions</p>");
	$("#suggestionBox").html(p);
	$.each(data.items, function(i, item) {
		var element = $("<div>"+item.title+"</div>");
		element.addClass("listEntry");
		element.click(
			// get title sepcific data
			// an set the forms accordingly
			function () {
				post.resource.entrytype
				$("select[@id=post.resource.entrytype]").selectOptions(item.entry_type);
				$("textarea[@id=post.resource.editor]").val(item.editor);
				$("input[@id=post.resource.year]").val(item.year);
				$("textarea[@id=post.resource.title]").val(item.title);
				$("textarea[@id=post.resource.author]").val(item.author);
			}
		).mouseover(
			function () {
				if(item.author.length >= 27) {
					item.author = item.author.substr(0, 27)+" ...";
				}
				
				if(item.editor.length >= 20) {
					item.editor = item.editor.substr(0, 20)+" ...";
				}
				
				item.editor = item.editor.replace(/ AND/g,',');
				p.html("["+item.entry_type+"]"+item.author+"("+item.year+"), "+item.editor).css("background-color","#222222");
			}
		).mouseout(
			function () {
				p.html("Suggestions").css("background-color","#006699");
			}
		);
		$("#suggestionBox").append(element);
	});

	$(".listEntry").css("padding", "3px").mouseover(function() {
		$(this).css(
				{
					"background-color":"#006699",
					"color":"#FFFFFF",
					"cursor":"default"
				}
		);
	}).mouseout(
		function() {
			$(this).css(
					{
						"background-color":"transparent", 
						"color":"#000000"
					}
			);
		}
	);
	var pos = $("textarea[@id=post.resource.title]").offset();
	var width = $("textarea[@id=post.resource.title]").width();
	var top = parseInt(pos.top+$("textarea[@id=post.resource.title]").height())+6;
	$("#suggestionBox").css(
			{
				"left":(pos.left+1)+"px",
				"top":top+"px",
				"min-width":(width+2)+"px"
			}
	);
	$("#suggestionBox").show();
}

// if window is small, maximizes "general" div to 95%
function maximizeById(id) {
  if (window.innerWidth < 1200) {
    document.getElementById(id).style.width="95%";
  }
}

// adds hints to input fields
function add_hints() {
  // for search input field
  var el = document.getElementById("se");
  if (el && (el.value == "" || el.value == getString("navi.search.hint"))) {
    // add hint
    el.value       = getString("navi.search.hint");
    el.style.color = "#aaaaaa";
    el.onmousedown = clear_input;
    el.onkeypress  = clear_input;
  }
  // for tag input field
  el = document.getElementById("inpf");
  if (el != null && (el.name == "tag" || el.name == "tags") && (el.value == "" || el.value == getString("navi.tag.hint"))) {
    el.value = getString("navi.tag.hint");
    el.style.color = "#aaaaaa";
    el.onmousedown = clear_input;
    el.onkeypress  = clear_input;
  }
  // specialsearch (tag, user, group, author, relation)
  if (el != null && el.name == "search" && (el.value == "" || el.value == getString("navi.author.hint") || el.value == getString("navi.tag.hint") 
  		|| el.value == getString("navi.user.hint") || el.value == getString("navi.group.hint") || el.value == getString("navi.concept.hint") || getString("navi.bibtexkey.hint")) || (el != null && el.value == getString("navi.search.hint"))) {
    var scope = document.getElementById("scope");
    // add call to this method to dropdown box, so that hint changes, when box changes
    if (scope) {
	    scope.onmouseup = add_hints;
	    scope.onkeyup   = add_hints;
	    if (scope.value == "tag") {
	      el.value = getString("navi.tag.hint");
	    } else if (scope.value == "user") {
	      el.value = getString("navi.user.hint");
	    } else if (scope.value == "group") {
	      el.value = getString("navi.group.hint");
	    } else if (scope.value == "author") {
	      el.value = getString("navi.author.hint");
	    } else if (scope.value == "concept/tag") {
	      el.value = getString("navi.concept.hint");
	    } else if (scope.value == "bibtexkey") {
	      el.value = getString("navi.bibtexkey.hint");
	    } else if (scope.value.indexOf("user") != -1 || scope.value == "search") {
	      el.value = getString("navi.search.hint");
	    }    
	    el.style.color = "#aaaaaa";
	    el.onmousedown = clear_input;
	    el.onkeypress  = clear_input;
    }        
  }  
}

/* removes hint and function from node */
function clear_node(node) {
  node.value       = "";
  node.style.color = "#000000";
  node.onmousedown = "";
  node.onkeypress  = "";
  node.focus();  
}

/* clear_node for events (if input field gets clicked) */
function clear_input (event) {
  clear_node(xget_event(event));
}

/* clear_node for inpf (tags) - is toggled, when submit button gets pressed */
function clear_tags () {
  var tag = document.getElementById("inpf");
  if (tag.value == getString("navi.tag.hint")) {
    clear_node(tag);
  }
}

/* sets the focus to the element with the given id */
function focus(id) {
  var el = document.getElementById(id);
  if (el) {
    el.focus();
  }
}


/*
 * functions to toggle background color for required bibtex fields
 */ 
function toggle_required_author_editor () {
  if (document.post_bibtex.author.value.search(/^\s*$/) == -1) {
    /* author field not empty */
    document.post_bibtex.editor.style.backgroundColor = "#ffffff";
    document.post_bibtex.author.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;
  } else {
    /* author field empty */
    if (document.post_bibtex.editor.value.search(/^\s*$/) == -1) {
      /* editor not empty */
      document.post_bibtex.author.style.backgroundColor = "#ffffff";
      document.post_bibtex.editor.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;
    } else {
      /* both empty */
      document.post_bibtex.author.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;    
      document.post_bibtex.editor.style.backgroundColor = document.post_bibtex.title.style.backgroundColor;      
    }
  }
}


function filter_tags(e) {
  var key;

  if (!e) e = window.event;
  if (e.which) {
    key = e.which;
  } else {
    key = e.keyCode;
  }
  var search = tags_filter.value.toLowerCase();
  returnPressed = key == 13;

    var merkeziel=0;
    var uls = sidebar.getElementsByTagName("ul");
    for (i=0; i<uls.length; i++) {
    	if (uls[i] != style_list) {
	        var links = uls[i].getElementsByTagName("li");
    	    for (x=0; x<links.length; x++) {
        		var taglinks = links[x].getElementsByTagName("a");
	        	for (xx = 0; xx < taglinks.length ; xx++) {
    	           var tagName = taglinks[xx].childNodes[0].nodeValue;
    	           if (taglinks[xx].className != "bmaction") {
        	       		if (tagName.toLowerCase().search(search) == -1) {
            	      		links[x].style.display="none";
	               		} else {
    	              		links[x].style.display='';
        	          		if (links[x].getElementsByTagName("a")[0].getAttribute("href")) {
	        	         		if (returnPressed && merkeziel==0) {
	            	      			merkeziel=links[x].getElementsByTagName("a")[0].getAttribute("href");
	                	 		}
		              		} else {
		                 		if (returnPressed && merkeziel==0) {
  	    	                		merkeziel=links[x].getElementsByTagName("a")[0].getAttribute("text");
	        	         		}
		           	   		}
    	           		}
    	        	}
        	    }
			}
        }
    }
    if (merkeziel != 0) {window.location.href=merkeziel;}
}

/*
 * Setting the current input Field
 * and clears the suggestions
 */
function setActiveInputField(id) {
	
	activeField = id;
	
	var sg = document.getElementById("suggested");
	if(sg){
		while(sg.hasChildNodes())
			sg.removeChild(sg.firstChild);
	}
}

/*
 * functions for copytag handling
 */
function toggle(event) {
	clear_tags(); // remove getString("navi.tag.hint") 

	var tagn = xget_event(event);
    var tag = tagn.childNodes[0].nodeValue;
		
	if(activeField) {
		var eingabe = document.getElementById(activeField);
	} else {
		var eingabe = document.getElementById('inpf');
	}

	toggleTag(eingabe, tag);
}
      
function add_toggle() {
  tags_toggle = 1;
}

function add_tags_toggle() {
  if (tags_toggle == 1) {
    var links = tagbox.getElementsByTagName("li");
    for (x=0; x<links.length; x++) {
         var aNode = links[x].getElementsByTagName("a")[0];
         aNode.onclick=toggle;
         aNode.setAttribute('text',aNode.getAttribute("href"));
         aNode.removeAttribute("href");
         aNode.style.cursor = "pointer";
    }
    var ul = document.getElementById("copytag");
    if (ul!=null) {
	    var links = ul.getElementsByTagName("li");
    	for (x=0; x<links.length; x++) {
	         var aNode = links[x];
    	     aNode.onclick=toggle;
	    }
	 }
  }
}

    /* ********************************** *
       clickable relations for edit_tags
     * ********************************** */
    function add_toggle_relations() {
	    var relation_list  = document.getElementById("relations");
	    var relation_items = relation_list.childNodes;
	    var counter = 0;
	    // iterate over supertags
    	for (x=0; x<relation_items.length; x++) {
        	var node = relation_items[x];
        	if (node.nodeName == "LI") {
        	    counter++;
             	// supertag found
        	 	var aNode = node.getElementsByTagName("a")[0];
		        aNode.onclick = add_supertag_to_input;
    		    aNode.removeAttribute("href");
        	 	aNode.style.cursor = "pointer";
        	 	aNode.setAttribute("title", "add as supertag");
        	 	// iterate over subtags
        	 	var sub_list = relation_items[x].getElementsByTagName("ul")[0];
        	    var sub_items = sub_list.getElementsByTagName("li");
                for (y=0;y<sub_items.length; y++) {
	        		var bNode = sub_items[y].getElementsByTagName("a")[0];
		        	bNode.onclick = add_subtag_to_input;
    		    	bNode.removeAttribute("href");
        			bNode.style.cursor = "pointer";
	        		bNode.setAttribute("title", "add as subtag");
        	 	}
        	}
	    }
	}
	
	function add_supertag_to_input(event) {
	   var node  = xget_event(event);
	   var value = node.childNodes[0].nodeValue;
	   document.getElementById("delete_up").value = value;
  	   document.getElementById("insert_up").value = value;
	}
	
	function add_subtag_to_input(event) {
	  	var node = xget_event(event);
	  	var value = node.childNodes[0].nodeValue;
	  	var delete_lo = document.getElementById("delete_lo");
		
		// -----------   new -------------------------------------------------
        value = value.replace(/ /,"");
		
        var subtags = delete_lo.value.split(" ");

        if (subtags[0] == "") {
          subtags.splice(0,1);
        }

        var drin = 0
        var neuetags = new Array();

        for (var i = 0; i < subtags.length; i++) {
			eintag = subtags[i];
            if (eintag == value) {
				drin = 1;
            } else {
				neuetags.push(eintag);
            }
        } 

        if (!drin) {
            neuetags.push(value)
        }
         
        var neueeingabe = neuetags.join(" ");
        delete_lo.value = neueeingabe;
        delete_lo.focus();
	}
	
	
	

function set_cookie(name, value) {
  var now = new Date();
  var out = new Date(now.getTime() + (1000 * 60 * 60 * 24 * 365));
  document.cookie = name + "=" + value + "; expires=" + out.toGMTString() + ";";
}


/* returns the node of an event */
function xget_event (event) {
  if (!event) event = window.event;
  if (event.srcElement) {
    // Internet Explorer1
	return event.srcElement;
  } else if (event.target) {
	// Netscape and Firefox
	return event.target;
  }
}

 function checkBrowser() {
  var str_browser = "";

	if (navigator.appName.indexOf("Opera") != -1)	{
    str_browser = "opera";
	}
	else if (navigator.appName.indexOf("Explorer") != -1)	{
    str_browser = "ie";
	}
	else if (navigator.appName.indexOf("Netscape") != -1)	{
    str_browser = "ns";
	}
	else {
    str_browser = "undefined";
	}

	return str_browser;
} 

    var maxTagFreq = 0;                    // maximal tag frequency in tag cloud
	var list = new Array();
	var listElements = new Array();
	var nodeList = new Array();
	var copyListElements = new Array();
	var copyList = new Array();
	var savTag = "";
	var activeTag = "";
	var sortedCollection;
	var collect;
	var copyCollect;
	var pos = 0;

	//38,39,40 von 86
	
	function enableHandler() {
		document.onkeydown = document.onkeypress = document.onkeyup = handler;
	}
	
	function disableHandler() {
		if(checkBrowser() == "ie" || checkBrowser() == "opera") {
			document.onkeydown = document.onkeypress = document.onkeyup;
		} else {
			document.onkeydown = document.onkeypress = document.onkeyup = disHandler;
		}
	}
	
	function Suggestion(tagname, wighting)	{
		this.tagname = tagname;
		this.wighting = wighting;
	}
	
	function disHandler(event) {	}
	
	function handler(event) {
	var inputValue = activeField ? document.getElementById(activeField).value 
															 : document.getElementById("inpf").value;

		var e = (event || window.event || event.shiftkey);

		if (e.type == 'keyup') {
			switch (e.keyCode) {
				case 8: {	//backspace
					if(sortedCollection) {
						delete sortedCollection;
						sortedCollection = new Array();
					}

					if(inputValue == "") {
						savTag = "";
						activeTag = "";
					}
					else {
						getActiveTag(true);
						clearSuggestion();
						if(activeTag != "")	
							suggest();
					}
					break;
				}
				case 9:	{	//tab
					if(inputValue != "" && activeTag) {
						clearSuggestion();
						completeTag();
						activeTag = "";
					}
					if(sortedCollection) {
						delete sortedCollection;
						sortedCollection = new Array();
					}
					break;
				}
				case 38: // up
				case 40: {	break;	}	//down
				case 35: //end
				case 36: //home
				case 37: // left
				case 39: // right
				case 32: {	// space
					if(sortedCollection) {
						delete sortedCollection;
						sortedCollection = new Array();
					}
					clearSuggestion();
					break;
				}
				case 13: {	//enter
					getActiveTag(false);
					if(activeTag != "") {
						clearSuggestion();
						completeTag();
					}
					break;
				}
				default: {
					getActiveTag(false);
					clearSuggestion();
					if(activeTag != "")	
						suggest();
					break;
				}
			}
		}
		else if(e.type == "keypress") {
			switch(e.keyCode) {
				case 9: { 
				
					if(inputValue != "" && activeTag && e.preventDefault()) {
						e.preventDefault();
					}
					
					break;
				}
				case 8: {	//backspace
					clearSuggestion();
					savTag = getTags(inputValue);
					break;
				}
				case 38:
				case 40: {
					if(e.preventDefault && e.originalTarget)
						e.preventDefault()
					break;
				}
				default: {
					savTag = getTags(inputValue);
					break;
				}
			}
		}
		else if(e.type == "keydown") {
			switch(e.keyCode) {
				case 8: {	//backspace
					clearSuggestion();
					savTag = getTags(inputValue);
					break;
				}
				case 38: { // oben
					if(inputValue != "") {
						if(pos < sortedCollection.length - 1 && pos < 2)	
							pos++;
						else
							pos = 0;

						clearSuggestion();
						addToggleChild(sortedCollection);
					}
					break;
				}
				case 40: { // unten
					if(inputValue != "") {
						if(pos > 0)
							pos--;
						else {
							if(sortedCollection.length < 3)
								pos = sortedCollection.length - 1;
							else
								pos = 2;
						}
						
						clearSuggestion();
						addToggleChild(sortedCollection);
					}
					break;
				}
				default: {
					savTag = getTags(inputValue);
					break;
				}
			}
		}
	}

	/*
	 * Wechselt das InputField, insofern atm kein Tag in der Vorschlagsliste steht
	 */

	function switchField(source,target) {
		if(activeTag != "") {
			document.getElementById(source).focus();
		}
		else if(activeTag == "") {
			document.getElementById(target).focus();
		}
	}
	
	function deleteCache() {
		clearSuggestion();

		/*
		 *	weiss nicht, warum ich das statement drin hatte.... war aber bestimmt nich ohne grund :(
		 */
 
		// activeTag = "";
	}

	/**
	 * Parses tag clound from json text into list of objects, each containing 
	 * 'label' and 'count'. 
	 * 
	 * For using the old suggestion functions we update global variables:
	 *    - array 'list' containing the tag names
	 *    - "associative" array 'nodeList' containing objects O with attributes
	 *      O.title = tagFrequency (as String), O.count = tagFrequency
	 *   
	 * @return result list
	 */
	function populateSuggestionsFromJSON(json) {
		// parse JSON into 'associative array'-object
		var data = eval( "(" + json + ")" );
		var tagCloud = data.items;
		
		// construct array from object such that sorting functions can be applied 
		var tagList = [];
		for( i in tagCloud ) {
			tagList.push(tagCloud[i]);
		}
		// now sort this array
		tagList.sort(tagCompare);
		
		// set global tag list, storing maximal tag frequency in maxTagFreq
		for( var i=0; i<tagList.length; i++ ) {
			var label = tagList[i].label;
			var count = tagList[i].count;
			if(count>maxTagFreq) 
				maxTagFreq = count;
			
			list.push(label);
			var node = new Object;
			node.title=count+" ";
			node.count=count;
			nodeList[label] = node;
		}

		// all done.
		return tagList;
	}
	
	/**
	 * Append recommended tags to list of potential tag suggestions.
	 *  
	 * Input: list of objects with attributes:
	 *    - title = tag weight
	 *    - label = tagName
	 *    - score, confidence
	 * 
	 * For using the old suggestion functions we update global variables:
	 *    - array 'list' containing the tag names
	 *    - "associative" array 'nodeList' containing objects O with attributes
	 *      O.title = tagFrequency (as String), O.count = tagFrequency
	 *   
	 * @return result list
	 */
	function populateSuggestionsFromRecommendations(tagList) {
		// update global tag list
		for( var i=0; i<tagList.length; i++ ) {
			var label = tagList[i].label.replace(/^\s+|\s+$/g, '').replace(/ /g,"_");;
			list.push(label);
			
			var node = tagList[i];
			node.title = Math.ceil(node.score*(maxTagFreq/2)+(maxTagFreq/2))+ " ";
			nodeList[label] = tagList[i];
		}
		// sort the list
		list.sort(stringCompare);
	}

	/**
	 * Compares two tag cloud entries.
	 * 
	 * @param a object containing properties 'label' and 'count'
	 * @param b object containing properties 'label' and 'count'
	 * @return -1 if a.label<b.label, 0 if a.label=b.label, 1 if a.label>b.label
	 */
	function tagCompare(a, b) {
		return stringCompare(a.label, b.label);
	}
	
	/**
	 * Compares two strings, ignoring case.
	 * 
	 * @param a string
	 * @param b string
	 * @return -1 if a < label, 0 if a=b, 1 if a>b
	 */
	function stringCompare(a, b) {
		if( a.toLowerCase()<b.toLowerCase() )
			return -1;
		else if( a.toLowerCase()==b.toLowerCase() )
			return 0;
		else
			return 1;
	}	
	
	/*
	 * Hier werden die Tags aus der Tagwolke, Copytags und Recommendations in Listen gepackt
	 */
	function setOps() {
		var ul = document.getElementById("tagbox");
		var rows = new Array();
		rows = ul.getElementsByTagName("li");
		for(var i = 0; i < rows.length; ++i) {
			if(rows[i].getElementsByTagName("a").length < 2) {
				var tmp = rows[i].getElementsByTagName("a")[0];
				var tmpData = tmp.firstChild.data.trim();
				listElements[tmpData] = i;
				nodeList[tmpData] = tmp;
				//list[i] = tmpData;	wegen recommending pfeilen
				list.push(tmpData);
            } else {
                var tmp = rows[i].getElementsByTagName("a")[2];
                var tmpData = tmp.firstChild.data.trim();
                listElements[tmpData.trim()] = i;
                nodeList[tmpData] = tmp;
                list.push(tmpData);
            }
		
		}

		if(document.getElementById("recommendtag")) {
			var recomm = document.getElementById("recommendtag");
			var recommRows = recomm.getElementsByTagName("li");
			for(var i = 0; i < recommRows.length; ++i) {
				var tmp = recommRows[i].getElementsByTagName("a")[0];
				var tmpData = tmp.firstChild.data;
				listElements[tmpData] = rows.length + i;
				nodeList[tmpData] = tmp;
				list.push(tmpData.trim());
			}
		}
		
		
		if(document.getElementById("copytag")) {
			copyTag = document.getElementById("copytag");
			copyRows = copyTag.getElementsByTagName("li");
			for(var i = 0; i < copyRows.length; ++i) {
				copyListElements[copyRows[i].firstChild.data] = i;
				copyList[copyRows[i].firstChild.data] = copyRows[i];
			}
		}
		
		list.sort(unicodeCollation);		
	}
	
	/*
	 * Gibt eine Liste aus Tags zur?ck. Bei Relationen werden die Tags gesplittet.
	 */
	
	function getTags(s) {
		var tmpInput = s.split(" ");
	 	var input = new Array();
		
		if(s.match(/->/) || s.match(/<-/)) {
			for(i in tmpInput) {
				if(tmpInput[i].match(/->/)) {
					var parts = tmpInput[i].split("->");
					input.push(parts[0]);
					input.push(parts[1]);
				} else if(tmpInput[i].match(/<-/)) {
					var parts = tmpInput[i].split("<-");
					input.push(parts[0]);
					input.push(parts[1]);
				} else {
					input.push(tmpInput[i]);
				}
			}
		} else {
			input = tmpInput;
		}		

		return input;

	}
	
	/*
	 *  Findet das Wort, welches gerade editiert wird
	 */
	
	function getActiveTag(backspace) {
		var tmpInput = activeField ? document.getElementById(activeField).value.toLowerCase().split(" ") 
								: document.getElementById("inpf").value.toLowerCase().split(" ");

		var inputValue = activeField ? document.getElementById(activeField).value 
	 								 : document.getElementById("inpf").value;

	 	var input = new Array();
	 	input = getTags(inputValue);

		for(var n in input) {
			if(typeof savTag != "undefined") {
				if(input[n] > savTag[n] && !backspace) {
					activeTag = input[n];
					break;
				} else if(input[n] < savTag[n] && backspace && input[n] > "") {
					activeTag = input[n];
					
					break;
				} else {
					activeTag = "";
				}
			}
		}
		delete input;
	}
	
	/*
	 * Vorschlagsfunktion
	 */
	
	function suggest() {
		delete collect;
		if(sortedCollection)
		delete sortedCollection;
		delete copyCollect;
		collect = new Array();
		sortedCollection = new Array();
		copyCollect = new Array();
		var searchString = activeTag.toLowerCase();
		var searchLength = searchString.length;
		var success = false;
		var counter = 0;
		var firstElement = 0;
		var lastElement = list.length - 1;
		var midElement = 0;

		/*
		 * if the following lines are activated, it's allowed to post duplicates in relations
		 */
		var tags = activeField ? document.getElementById(activeField).value.toLowerCase().split(" ")
												   : document.getElementById("inpf").value.toLowerCase().split(" ");
							   
		/*
		 * if the following lines are activated, it's not allowed to post duplicates in relations.
		 * var tagString = activeField ? document.getElementById(activeField).value.toLowerCase()
		 *  					       : document.getElementById("inpf").value.toLowerCase();
		 * var tags = getTags(tagString);
		 */
		
		pos = 0;
		
		/*
		 * Bin?re Suche ?ber die Listenelemente, in denen die Tags stehen
		 */
		while(!success && firstElement <= lastElement) {
			midElement = Math.floor((firstElement + lastElement) / 2);
			if(list[midElement].substring(0,searchLength).toLowerCase() == searchString) {
				var i = midElement - 1;
				var j = midElement + 1;
				while(i >= 0 && list[i].substring(0,searchLength).toLowerCase() == searchString) {
					collect.push(new Suggestion(list[i], nodeList[list[i]].title.split(" ")[0]));
					i--;
				}
				while(j <= list.length - 1 && list[j].substring(0,searchLength).toLowerCase() == searchString) {
					collect.push(new Suggestion(list[j], nodeList[list[j]].title));
					j++;
				}
				collect.push(new Suggestion(list[midElement], nodeList[list[midElement]].title.split(" ")[0]));
				success == true;
				break;
				//alert(collect);
			}
			else if(list[midElement].substring(0,searchLength).toLowerCase() > searchString)
				lastElement = midElement - 1;
			else
				firstElement = midElement + 1;
		}
		collect.sort(byWight);
		
		if(document.getElementById("copytags") != null) {
			copyTag = document.getElementById("copytags");
			copyRows = copyTag.getElementsByTagName("a");
			for(var i = 0; i < copyRows.length; ++i) {
				copyListElements[copyRows[i].firstChild.data.replace(/^\s+/, '').replace(/\s+$/, '')] = i;
				copyList[copyRows[i].firstChild.data.replace(/^\s+/, '').replace(/\s+$/, '')] = copyRows[i];
			}
		}
		
		/*	collects suggested entrys inside copytag elemens	*/
		for(copyTag in copyListElements) {
			var duplicate = false;
			var tmpTag = "";
			valid = true;
			if(searchLength <= copyTag.length)
				tmpTag = copyTag.substring(0,searchLength).toLowerCase();
			else
				valid = false;
	
			if(tmpTag.match(activeTag) && valid) {
				for(var z in tags) {
					duplicate = false;
					
					if(copyTag.toLowerCase() == tags[z].toLowerCase()) {
						duplicate = true;
						break;
					}
				}
				if(!duplicate)
					copyCollect.push(copyTag);
			}
			i++
		}
		
		for(var i in copyCollect)
			sortedCollection.push(copyCollect[i]);

		for(var i in collect) {
			for(var z in tags) {
				duplicate = false;
				if(collect[i].tagname.toLowerCase() == tags[z].toLowerCase()) {
					duplicate = true;
					break;
				}
			}
			var j = 0;
			while(j < sortedCollection.length && !duplicate) {
				if(collect[i].tagname.toLowerCase() == sortedCollection[j].toLowerCase()) {
					duplicate = true;
					break;
				}
				j++;
			}				
			if(!duplicate) {
				sortedCollection.push(collect[i].tagname);
			}
		}
		
		addToggleChild(sortedCollection);
	}
	
	/*
	 * Sortiert 2 Tags nach Gewichtung
	 */
	
	function byWight(a, b) {
		if(b.wighting == a.wighting) {
			if(b.tagname < a.tagname)
				return -1;
			else if(b.tagname > a.tagname)
				return 1;
			else
				return 0;
		}
		else
			return b.wighting - a.wighting;
	}
	
	/*	adds list elements to the suggested list	*/
	function addToggleChild(sortedCollection) {
		var sg = document.getElementById("suggested");

		for(var i in sortedCollection) {
			var newTag = document.createElement("a");
			sg.appendChild(document.createTextNode(" "));
			newTag.className = "tagone";
			newTag.appendChild(document.createTextNode(sortedCollection[i]))
			newTag.removeAttribute("href");
			newTag.style.cursor = "pointer";
			newTag.setAttribute('href','javascript:completeTag("'+sortedCollection[i].replace(/"/g,'\\"')+'")');

			if(i == pos) {
				newTag.style.color = "white";
				newTag.style.backgroundColor = "#006699";
			}
			
			if(i == 3)
				break;

			sg.appendChild(newTag)
		}
	}

	function clearSuggestion() {

		var sg = document.getElementById("suggested");
		
		if(document.getElementById("copytag")) {
			for(var i = 0; i < copyRows.length; ++i) {
				copyRows[i].style.color = "";
				copyRows[i].style.backgroundColor = "";
			}
		}
		
		while(sg.hasChildNodes())
			sg.removeChild(sg.firstChild);
	}
	
	
	function getRelations(input) {
		var relList = new Array();
		
		for(i in input) {
			if(input[i].match(/->/)) {
				relList.push(1);
				relList.push(1);
			} else if(input[i].match(/<-/)) {
				relList.push(2);
				relList.push(2);
			}
			else
				relList.push(0);
		}
		
		return relList;
	}
	
	/*	completes the inquiry	
	 *	tab -> sortedCollection[0]
	 *	mouseclick -> parameter value (tag)
	 */
	function completeTag(tag) {
   		var inpf = activeField ? document.getElementById(activeField)
							   : document.getElementById("inpf");		

		var tags = getTags(inpf.value);
		var val_tags = getTags(inpf.value.toLowerCase());
		var relList = getRelations(inpf.value.split(" "));
		var tmpTag = "";
		var mergedList = new Array();
		var counter = 0;
		var reset = false;
		var relation = false;
		

		
		for(var i in val_tags) {
			if(val_tags[i] == activeTag.toLowerCase()) {
				if(tag) {
					reset = true;
					tags[i] = tag + " ";
					break;
				}
				else if(sortedCollection) {
					if(sortedCollection[pos] != "") {
						// tag found in collection
						reset = true;
						var tag = sortedCollection[pos];
						tags[i] =  tag + " ";
						//
						// 2009/03/30, fei: treat tag completion as mouseclick
						//                  (that way we get this information via
						//                   clicklog)
						// FIXME: relations not tested!
						var target = lookupRecommendedTag(tag);
						// send clicklog-event
						if( target!=null )
							simulateClick(target);
					}
					if(!sortedCollection[pos]) {
						reset = false
						break;
					}
				}
			}
		}
		
		if(reset) {
			for(var i = 0; i < tags.length; i++) {
				relation = false;

				if(relList[i] == 1) {
					tmpTag = tags[i] + "->" + tags[i+1];
					i++;
				} else if(relList[i] == 2) {
					tmpTag = tags[i] + "<-" + tags[i+1];
					i++;
				} else {
					tmpTag = tags[i];
				}
				
				mergedList.push(tmpTag);
			}
			inpf.value = mergedList.join(" ");
			
			delete mergedList;
		}

		activeTag = "";
		clearSuggestion();
		
		inpf.focus();

		if(window.opera)
			inpf.select(); 

		inpf.value = inpf.value;

	}
	
/**
 * returns link from tagfield if given tagname was recommended
 */
function lookupRecommendedTag(tag) {
	var tagField = document.getElementById("tagField");
	var links = tagField.getElementsByTagName("a");
	var tag_name = tag.replace(/^\s+|\s+$/g, '');
	var retVal = null;
	
	for (var i = 0; i < links.length; i++) {
		var text = links[i].firstChild.nodeValue.replace(/^\s+|\s+$/g, '');
		if( tag_name==text ) {
			retVal = links[i];
			break;
		}
	}
	return retVal;
}

function simulateClick(target) {
	var evt;
	var el = target;
	if (document.createEvent){
		evt = document.createEvent("MouseEvents");
		if (evt.initMouseEvent){
			evt.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
		} else {
			evt = false;
		}
	}
	(evt)? el.dispatchEvent(evt):(el.click && el.click());
} 

function setButton() {
	var tNode = document.getElementById("privnote");
	
	if(tNode.firstChild) {
		var div = document.getElementById("note");
		var button = document.getElementById("makeP");

		div.removeChild(button);

		var newButton = document.createElement("input");
		newButton.setAttribute("id","makeP");
		newButton.setAttribute("type","button");
		newButton.setAttribute("value","update");
		newButton.setAttribute("onClick","makeParagraph()");
		div.appendChild(newButton);
	}
}

function makeParagraph() {
	var button = document.getElementById("makeP");
	var tNode = document.getElementById("privnote");
	var div = document.getElementById("note");
	var newParagraph = document.createElement("p");
	var note = "";
	
	if(tNode.firstChild)
		note = tNode.firstChild.data;
		
	var textNode = document.createTextNode(note);
	newParagraph.setAttribute("id","pText");
	newParagraph.appendChild(textNode);
	
	if(note != "") {
		tNode.style.display = "none";
		div.appendChild(newParagraph);
		button.setAttribute("onClick","makeText()");
		button.setAttribute("value","edit");
		div.appendChild(button);
	}
}

function makeText() {
	var button = document.getElementById("makeP");
	var pText = document.getElementById("pText");
	var div = document.getElementById("note");

	var tNode = document.getElementById("privnote");
	tNode.style.display = "inline";
	
	var newButton = document.createElement("input");
	newButton.setAttribute("type","submit");
	newButton.setAttribute("value","update");
	
	div.removeChild(pText);
	div.removeChild(button);	
	div.appendChild(newButton);	
}

function unicodeCollation(ersterWert, zweiterWert){
	var result;
	if(isNaN(ersterWert) == false && isNaN(zweiterWert) == false){
		result = ersterWert - zweiterWert;// vergleich von 2 Zahlen
	}else if(isNaN(ersterWert) == false && isNaN(zweiterWert) == true){
		result = -1;// vergleich erster Wert ist eine Zahl und zweiter Wert ist ein String
	}else if(isNaN(zweiterWert) == false && isNaN(ersterWert) == true){
		result = 1;// vergleich zweiter Wert ist eine Zahl und erster Wert ist ein String
	}else if(ersterWert.toLowerCase() < zweiterWert.toLowerCase()){
		result = -1;// vergleich zweier Strings
	}else if(zweiterWert.toLowerCase() < ersterWert.toLowerCase()){
		result = 1;// vergleich zweier Strings
	}else if(zweiterWert.toLowerCase() == ersterWert.toLowerCase()){
		// vergleiche zwei gleiche Strings(im toLower Fall)
		if(ersterWert < zweiterWert){
			result = -1;
		}else if(zweiterWert < ersterWert){
			result = 1;
		}else{
			result = 0;
		}
	}else{
		result = 0;
	}
	return result;
}





/* ********************************************************
 * AJAX functions
 * ********************************************************/



    function ajaxInit(){
    	var req;
      	try{
        	if(window.XMLHttpRequest){
          		req = new XMLHttpRequest();
        	}else if(window.ActiveXObject){
          		req = new ActiveXObject("Microsoft.XMLHTTP");
        	}
        	if( req.overrideMimeType ) {
            	req.overrideMimeType("text/xml");
	        }        	
     	} catch(e){
     	   	return false;
     	}
     	return req;
    }

    // this shows or hides a relation by clicking the arrow in the tag cloud
    function showOrHideConcept(evt, action){
    	// get concept name
	    var link = xget_event(evt);
		var concept = link.parentNode.getElementsByTagName("a")[2].firstChild.nodeValue;
		// update relation list
		updateRelations(evt, action, concept);
	}
	
	// removes a relation from the list of shown relations
    function hideConcept(evt){
    	// get concept name
	    var link = xget_event(evt);
	    var concept = link.parentNode.getElementsByTagName("a")[1].firstChild.nodeValue;
	    // update relations list, hide concept
	    updateRelations(evt, "hide", concept);
    } 
    
    // updates the relations in AJAX style
	function updateRelations (evt, action, concept) {
		// do AJAX stuff	    	
    	var request = ajaxInit();
    	if(request){
	    	// build URL for AJAX request
			var url = "/ajax/showOrHideConcept?" + action + "=" + encodeURIComponent(concept);
			request.open('GET', url, true);
			request.setRequestHeader("Content-Type", "text/xml");
			request.setRequestHeader('If-Modified-Since', 'Sat, 1 Jan 2000 00:00:00 GMT');
			// attach function which handles the request
			var handler = ajax_updateRelations(request);
			request.onreadystatechange = handler;
			request.send(null);

			// break link
			if (evt.stopPropagation) {
			    evt.stopPropagation();
			    evt.preventDefault();
			} else if (window.event){
			    window.event.cancelBubble = true;
			    window.event.returnValue = false;
			}
		}
    } 
    
 
    
        

                        
	// updates the relations list 
    function ajax_updateRelations(request) {
    	return function(){
	   	  	if( 4 == request.readyState ) {
	          	if( 200 == request.status ) {
					
					// get surrounding <ul>
	           		var relations_list = document.getElementById("relations");
					var relations  = new Array();
	           		
					// remove relations from list
	           		for(x=0; x<relations_list.childNodes.length; x++){
	           			if(relations_list.childNodes[x].nodeName == "LI")
		           			relations.push(relations_list.childNodes[x]);
	           		}
	           		for(x=0; x<relations.length; x++){
	           			relations_list.removeChild(relations[x]);
	           		}
					
	           		// parse XML input
	       		    var xml = request.responseXML.documentElement;
	         		var conceptnames = new Array();					
					
					if(xml) {
						var currUser = xml.getAttribute("user");
								
						// get all relations
						var requestrelations = xml.getElementsByTagName("relation");


	         		    // iterate over the relations
		       		    for(x=0; x<requestrelations.length; x++){       		    
		       		    	// one relation
							var rel = requestrelations[x];		    
							// the upper tag of the relation
		         		    var upper = rel.getElementsByTagName("upper")[0].firstChild.nodeValue;
		         		    // new list item for this supertag
		         		    var rel_item = document.createElement("li");
		         		    rel_item.className = "box_upperconcept";
		         		    // store upper tag in array
		         		    conceptnames.push(upper);
								         		    	         		    
		         		    // add the symbol to hide the relation
		         		    var linkupperx = document.createElement("a");
		         		    var linkupperxhref = document.createAttribute("href");
		         		    linkupperxhref.nodeValue = "/RelationsHandler?hide=" + upper;
		         		    linkupperx.setAttributeNode(linkupperxhref);
		         		    // changed from 215 (&times;) to 8595 (&darr;)
		         		    var linkupperxtext = document.createTextNode(String.fromCharCode(8595));
		         		    linkupperx.appendChild(linkupperxtext);
		         		    rel_item.appendChild(linkupperx);
		         		    rel_item.appendChild(document.createTextNode(" "));
		         		    
		         		    // attach function to onlick event
	       		    		if (linkupperx.attachEvent) {
							    linkupperx.attachEvent("onclick", hideConcept);
							} else if (linkupperx.addEventListener) {
							    linkupperx.addEventListener("click", hideConcept , true);
							} else {
							    linkupperx.onclick = hideConcept;
							}
							
		         		    
		         		    // add link for upper tag
		         		    var linkupper = document.createElement("a");
		         		    var linkupperhref = document.createAttribute("href");
		         		    linkupperhref.nodeValue = "/concept/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(upper);
		         		    linkupper.setAttributeNode(linkupperhref);
		         		    var linkuppertext = document.createTextNode(upper);
		         		    linkupper.appendChild(linkuppertext);
		         		    rel_item.appendChild(linkupper);

		         		    // add arrow
		         		    rel_item.appendChild(document.createTextNode(" " + String.fromCharCode(8592) + " "));
		         		    
		         		    
		         		    // add lower tags
		         		    var lowers = rel.getElementsByTagName("lower");
		         		    var lowerul = document.createElement("ul");
		         		    lowerul.className = "box_lowerconcept_elements";
		         		    var lowerulid = document.createAttribute("id")
		         		    lowerulid.nodeValue = upper;
		         		    lowerul.setAttributeNode(lowerulid);
		         		
							// iterate over lower tags
		         		    for(y=0; y<lowers.length; y++) {
		         		    	var lower = lowers[y].firstChild.nodeValue;
		         		    	
		         		    	// create new list item for lower tag
		         		    	var lowerli = document.createElement("li");
		         		    	lowerli.className = "box_lowerconcept";

								// add link
		         		    	var lowerlink = document.createElement("a");
		         		    	var lowerlinkhref = document.createAttribute("href");
		         		    	lowerlinkhref.nodeValue = "/user/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(lower);
		         		    	lowerlink.setAttributeNode(lowerlinkhref);
		         		    	var lowerlinktext = document.createTextNode(lower + " ");
		         		    	lowerlink.appendChild(lowerlinktext);
		         		    	lowerli.appendChild(lowerlink);
		         		    	
		         		    	// add item
		         		    	lowerul.appendChild(lowerli);
		         		    }
		         		    
		         		    // append list of lower tags to supertag item
	   	         		    rel_item.appendChild(lowerul);

			         		// insert relations_list for this supertag
			         		relations_list.appendChild(rel_item);
							

		         		}
					}

					// set arrows of supertags in tag cloud depending on if supertag is shown or not
	         		var ultag = document.getElementById("tagbox");
	         		var taglis = ultag.getElementsByTagName("li");

	         		for(x=0; x<taglis.length; x++){
	         		  	var links = taglis[x].getElementsByTagName("a");

	         		   	if(links.length == 3){
	         		   		var tagname = links[2].firstChild.nodeValue;
	         		   		var addArrow = true;
							
	         		   		for(y=0; y<conceptnames.length; y++){
	         		   			if(tagname == conceptnames[y]){
	         		   				addArrow=false;
	         		   			}
	         		   		}
	         		  		if(addArrow){
	         		   			links[0].style.display = "none";
	         		   			links[1].style.display = "inline";
	         		   		}else{
	         		   			links[0].style.display = "inline";
	         		   			links[1].style.display = "none";
	         		   		}
							if(x == 0){alert("done");}
	         		   	}
	         		}
					
	         		delete conceptnames;
	         	}
	        }
	    }
    } 
    
    
    
    function pickAll(evt) {
       pickUnpickAll(evt, "pickAll");
    }
    
    function unpickAll(evt) {
       pickUnpickAll(evt, "unpickAll");
    }

    function pickUnpickAll(evt, pickUnpick) {
    	// get user names/hashes to pick
    	var bibtex = document.getElementById("bibtex")
    	var lis    = bibtex.getElementsByTagName("li");
    	var param  = "";
    	   	
    	for(x=0; x<lis.length; x++) {
    	    var divs = lis[x].getElementsByTagName("div");
    	    for (y=0; y<divs.length; y++) {
    	       if (divs[y].className == "bmtitle") {
    	          var spans = divs[y].getElementsByTagName("a");
    	          for (z=0; z<spans.length; z++) {
    	        	if (spans[z].getAttribute("href").match(/^.*\/documents\/.*/) == null){
    					var post = spans[z].getAttribute("href").replace(/^.*bibtex./, "");
    					param += post + " ";
    	        	}
    	          }
    	       }
    	    }
    	}
    	updateCollector("action=" + pickUnpick + "&hash=" + encodeURIComponent(param));
    	
    	breakEvent(evt);    	
    }    

	function breakEvent(evt) {
		// break link
		if (evt.stopPropagation) {
		    evt.stopPropagation();
		    evt.preventDefault();
		} else if (window.event){
		    window.event.cancelBubble = true;
		    window.event.returnValue = false;
		}
	}
    
    // this picks or unpicks a publication
    function pickUnpickPublication(evt){
    	// get parameters from URL
	    var action = xget_event(evt).getAttribute("href").replace(/^.*?\?/, "");
		
		// pick/unpick publication
		updateCollector(action);
		
		/*
		 * devide which page will be processed
		 * -> on the /basket page we have to remove the listitems
		 * -> on other we have to change the pick <-> unpick link (not yet implemented)
		 */
		if (location.pathname.startsWith("/basket")){
			var li = evt.currentTarget.parentNode.parentNode.parentNode;
			var parent = li.parentNode;
			parent.removeChild(li);
		
			document.getElementById("ttlctr").childNodes[0].nodeValue = "(" + document.getElementById("pickctr").childNodes[0].nodeValue + ")";
		} else {
			/*
			 * some pre consideration how to switch between pick and unpick
			 * for all other pages than /basket 
			 */
//			for (var i = 0; i < evt.currentTarget.attributes.length; i++){
//				if (evt.currentTarget.attributes[i].nodeName == "href"){
//					var href = evt.currentTarget.attributes[i].nodeValue;
//					var regex = /(\/ajax\/pickUnpickPost\?)(action=\w{4,6})(.*)/;
//					regex.exec(href);
//					var url = RegExp.$1;
//					var action  = RegExp.$2;
//					var restUrl = RegExp.$3;
//					var newAction = "";
//					
//					if (action == "action=pick"){
//						newAction = "action=unpick";
//					} else if (action == "action=unpick"){
//						newAction = "action=pick";
//					}
//					
//					var newUrl = url + newAction + restUrl;
//					
//					evt.currentTarget.setAttribute("href", newUrl);
//				}
//			}
			
		}
				   	            
   		breakEvent(evt);
	}
	   

	   
    // picks/unpicks publications in AJAX style
	function updateCollector (param) {
		// do AJAX stuff
    	var request = ajaxInit();
    	if(request){
	    	// build URL for AJAX request
			var url = "/ajax/pickUnpickPost?ckey=" + ckey;
			request.open('POST', url, true);
			request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
			request.setRequestHeader("Content-length", param.length);
      		request.setRequestHeader("Connection", "close");
			request.onreadystatechange = ajax_updateCollector(request);
			request.send(param);
		}
    } 
    
    // updates the shown number of documents for download 
    function ajax_updateCollector(request) {
    	return function(){
	   	  	if( 4 == request.readyState ) {
	          	if( 200 == request.status ) {
	          		// get counter
	           		var pickctr = document.getElementById("pickctr").childNodes[0];
	           		
	           		// parse XML input
	       		    var xml = request.responseText;
	       		    
					// update counter   	           		
   	           		pickctr.nodeValue = xml;
   	           		
   	           		// special case for the /basket page
   	           		if (location.pathname.startsWith("/basket")){
   	           			document.getElementById("ttlctr").childNodes[0].nodeValue = "("+xml+")";
   	           		}
	         	}
	        }
	    }
    }
    
/*
 * 
 */
function sendEditTags(obj, type, ckey, link) {
	var tags = obj.childNodes[0].value;
	var hash = obj.childNodes[0].name;
	var ckey = obj.childNodes[1].value;
	var targetChild = 0;
	
	$.ajax( {
		type :"POST",
		url :"/batchEdit_new?newTags['" + hash + "']=" + encodeURIComponent(tags.trim())
				+ "&ckey=" + ckey,
		dataType :"html",
		global :"false"
	});

	var parent = obj.parentNode;
	var nodeText = "edit ";

	if (type == "bibtex") {
		targetChild = 2;
		parent.removeChild(parent.childNodes[targetChild]);
		parent.removeChild(parent.childNodes[targetChild]);
		nodeText = " edit ";
	} else {
		parent.removeChild(parent.firstChild);
		parent.removeChild(parent.firstChild);
	}
	
	var edit = document.createElement("a");
	edit.setAttribute("onclick", "editTags(this, '" + ckey + "'); return false;");
	edit.setAttribute("tags", tags.trim());
	edit.setAttribute("href", link);
	edit.appendChild(document.createTextNode(nodeText));
	
	parent.insertBefore(edit, parent.childNodes[targetChild]);
	parent = parent.parentNode.previousSibling.childNodes[1];

	while (parent.hasChildNodes()) {
		parent.removeChild(parent.firstChild);
	}

	var tagList = tags.split(" ");

	for (i in tagList) {
		var tag = document.createElement("a");
		tags = encodeURIComponent(tagList[i]);
		tag.setAttribute("href", "/user/" + currUser + "/" + tags);
		tag.appendChild(document.createTextNode(tagList[i] + " "));
		parent.appendChild(tag);
	}

	return false;
}
    
    
    
/*
 * edit tags in place
 */	
function editTags(obj, ckey) {
	var tags = obj.getAttribute("tags");
    var link = obj.getAttribute("href");
    var hash = obj.getAttribute("name");
    var targetChild = 0;
    var parent = obj.parentNode;
    var type = "bookmark";

    if (link.search(/^\/editPublication/) != -1)	{
    	type = "bibtex";
    	targetChild = 2;
    	parent.removeChild(parent.childNodes[targetChild]);
    }

	// remove the other childnodes
	// FIXME: this is a bad heuristic!
	parent.removeChild(parent.childNodes[targetChild]);
	
	// creates Form Element
	var form = document.createElement("form");
	form.className = "tagtextfield";
	form.setAttribute('onsubmit', 'sendEditTags(this, \'' 
			+ type + '\',  \'' + ckey + '\', '
			+ '\'' + link + '\'); return false;');
	
	// creates an input Field
	var input = document.createElement("input");
	input.setAttribute('name',hash);
	input.setAttribute('size','30');
	input.value = tags;
	
	var hidden = document.createElement("input");
	hidden.type="hidden";
	hidden.setAttribute("name", "ckey");
	hidden.value = ckey;
		
	// creates the link to detail-editing
	var details = document.createElement("a");
	details.setAttribute('href', link);
	details.appendChild(document.createTextNode(" details "));

	// append all the created elements
	form.appendChild(input);
	form.appendChild(hidden);
	
	if(type == "bibtex") {
		var pipe = document.createTextNode(" | ");
		parent.insertBefore(pipe, parent.childNodes[targetChild]);
		parent.insertBefore(details, parent.childNodes[targetChild]);
		parent.insertBefore(form, parent.childNodes[targetChild]);
	} else {
		parent.insertBefore(details, parent.firstChild);
		parent.insertBefore(form, parent.firstChild);
	}
}
 
function setTut(tutName) {
	
	var w;
	
	w = window.open("http://" + window.location.host + "/tutorial/" + tutName + '.htm', "_blank", "width=915, height=735, scrollbars=no");
	w.focus();

}

String.prototype.startsWith = function(s) { 
	return this.indexOf(s) == 0; 
}

function getString( key ) {
  if ( typeof LocalizedStrings == "undefined" ) return "???"+key+"???"; 
  var s = LocalizedStrings[key];
  if( !s ) return "???"+key+"???";
  return s;
}

function expandBookmarkList(){
	$("#bibtexList").animate({ 
	width: 0, opacity: 0.0
	}, "slow" ).hide("1");

	$("#bookmarkList").animate({ 
	width: "97%", opacity: 1.0
	}, "slow", function() {
		$(this).css("position","static");
	}).show("1");
	
	$("#optionExpandBookmark").hide();
	$("#optionShowBoth").show();
	$("#optionExpandBibtex").show();

}

function expandBibTexList(){
	$("#bookmarkList").animate({ 
		width: 0, opacity: 0.0
	}, "slow", function() {
		$(this).css("position","absolute");
	}).hide("1");

	$("#bibtexList").animate({ 
		width: "97%", opacity: 1.0
	}, "slow").show("1");
				   
	$("#optionExpandBookmark").show();
	$("#optionShowBoth").show();
	$("#optionExpandBibtex").hide();						   
}

function showBothLists(){
	$("#bibtexList").animate({ 
		width: "47%", opacity: 1.0
	}, "slow", function() {
		$(this).css("position","static");
	}).show("1");

	$("#bookmarkList").animate({ 
		width: "47%", opacity: 1.0
	}, "slow", function() {
		$(this).css("position","static");
	} ).show("1");
	
	$("#optionExpandBookmark").show();
	$("#optionShowBoth").hide();
	$("#optionExpandBibtex").show();
}

//----------------------------------------------------------------------------
//new functions for adding tags from tag-cloud to a field
//----------------------------------------------------------------------------

//the old tag toggler: add or remove tagname tagn to/from input field target 
function toggleTag(target, tagname) {
	clear_tags(); // remove getString("navi.tag.hint") 
	var tag     = tagname.replace(/^\s+|\s+$/g, '').replace(/ /g,"_");
	var eingabe = target;
	var tags    = eingabe.value.split(" ");

	if (tags[0] == "") {
		tags.splice(0,1);
	}

	var drin = 0
	var neuetags = new Array();

	for (var i = 0; i < tags.length; i++) {
		eintag = tags[i];
		if (eintag == tag) {
			drin = 1;
		} else {
			neuetags.push(eintag);
		}
	} 1

	if (!drin) {
		neuetags.push(tag)
	}

	var neueeingabe = neuetags.join(" ");
	eingabe.value = neueeingabe;

	activeTag = "";
	if(sortedCollection) {
		sortedCollection[0] = "";
		clearSuggestion();
	}

	eingabe.focus();
}

//add/remove tagname to/from target field 
function copytag(target, tagname){
	var targetNode = document.getElementById(target) 
	if( targetNode ){
		toggleTag(targetNode, tagname);
	}
}

/** FUNCTIONS USED IN THE POSTING VIEWS **/

// hide and show the tagsets in the relevant for field
function showTagSets(select) {
    for (var i = 0; i < select.options.length; i++) {
      var op = select.options[i];
      var field = document.getElementById("field_" + op.value);
      if (field != null) {
        if (op.selected) {
          field.style.display = '';
        } else {
          field.style.display = 'none';
        }
      }   
    }
}

//functions checks if a group in the relevant for field is selected and adds its name to the hidden field 
//systemtags 
function addSystemTags(){
	var counter = 0;
	clear_tags();
	var tags = document.getElementById("inpf").value;
	while(document.getElementById("relgroup"+counter) != null){
		if(document.getElementById("relgroup"+counter).selected == true){
			var value = document.getElementById("relgroup"+counter).value;
			// only write the systemtag if it doesn't exist in the tagfield
			if(tags.match(":"+value) == null){
				if(systemtags == null){
					systemtags = "sys:relevantFor:"+value;
				}else{
					systemtags +=" "+"sys:relevantFor:"+value;
				}
			}
		}
		counter++;
	}
	//if a systemtag was build, add it to the tag field
	if(systemtags != null){
		//add systemtags to the tag field
		copytag("inpf",systemtags);
	}
	
}

function generateBibTexKey(obj) {
    var buffer  = "";

    /* get author */
    buffer += getFirstPersonsLastName(document.getElementById("post.resource.author").value);

    /* the year */ 
    var year = document.getElementById("post.resource.year").value;
    if (year != null) {
        buffer += year.trim();
    }

    /* first relevant word of the title */
    var title = document.getElementById("post.resource.title").value;
	if (title != null) {
		buffer += getFirstRelevantWord(title).toLowerCase();
	}
    
    document.getElementById("post.resource.bibtexKey").value = buffer.toLowerCase();
}

function getFirstPersonsLastName(person) {
    if (person != null) {
        var firstauthor;
        /*
		 * check, if there is more than one author
		 */
        var firstand = person.indexOf("\n");
        if (firstand < 0) {
            firstauthor = person;
        } else {
            firstauthor = person.substring(0, firstand);				
        }
        /*
         * first author extracted, get its last name
         */
        var lastspace = firstauthor.lastIndexOf(' ');
        var lastname;
        if (lastspace < 0) {
            lastname = firstauthor;
        } else {
            lastname = firstauthor.substring(lastspace + 1, firstauthor.length);
        }
        return lastname;
    }
    return "";
}

function getFirstRelevantWord(title) {
	split = title.split(" ");
	for (i in split) {
		var regex = new RegExp("[^a-zA-Z0-9]", "g");
		ss = split[i].replace(regex, "");
		if (ss.length > 4) {
			return ss;
		}
	}
	return "";
}

//get the value of a node by a windowevent
function getNodeValueByEvent(event){
	node = xget_event(event);
	return node.getAttributeNode("value").value;
}

//copy a value from a option field to the target
function copyOptionTags(target, event){
	var value = getNodeValueByEvent(event);
	copytag(target, value);
}

//trim
String.prototype.trim = function () {
    return this.replace(/^\s+/g, '').replace(/\s+$/g, '');
}

