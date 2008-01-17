var request = null;
var style_list = null;
var style_sort = new Array("alph", "freq");
var style_show = new Array("cloud", "list");

// var style_sort = new Array(getString("tagbox.sort.alpha"), getString("tagbox.sort.freq"));
// var style_show = new Array(getString("tagbox.style.cloud"), getString("tagbox.style.list"));

function init_tagbox(show, sort, minfreq, requUser) {

  style_list = document.createElement("ul");
  style_list.className= "floatul";

  style_list.appendChild(document.createElement("li"));
  style_list.appendChild(document.createElement("li"));
  style_list.appendChild(document.createElement("li"));

  style_list.replaceChild(getStyleItem(style_sort[sort], style_sort), style_list.childNodes[0]);
  style_list.replaceChild(getStyleItem(style_show[show], style_show), style_list.childNodes[1]);
  if (typeof disable_tagbox_minfreq != "undefined" && disable_tagbox_minfreq == "false") {
  	showMinfreq(minfreq, requUser);
  }

  var span = document.createElement("span");
  span.appendChild(style_list);  

  tagbox.parentNode.insertBefore(span, tagbox);
}

function attachChangeTagBox(mode) { return(function() {	changeTagBox(mode);	})}
function changeTagBox(mode) {
  var request = ajaxInit();

	if(mode == "list" || mode == "cloud"){
		sendStyleRequ("style", mode);
		tagbox.className = "tag" + mode;
		style_list.replaceChild(getStyleItem(mode, style_show), style_list.childNodes[1]);
	}else if(mode == "alph" || mode == "freq") {
		sendStyleRequ("sort", mode);
		style_list.replaceChild(getStyleItem(mode, style_sort), style_list.childNodes[0]);
		if (mode == "alph") {
			setTagBoxAlph();
		}else{
			setTagBoxFreq();
		}
	}
}



function getStyleItem(style, style_arr) {
	var style_sort = document.createElement("li");

	var node = document.createElement("a");
	node.style.cursor = "pointer";
	
	style_sort.appendChild(document.createTextNode(" ("));
	if(style == style_arr[0]) {
		style_sort.appendChild(document.createTextNode(getString("tagbox." + style_arr[0]) + " | "));

		node.onclick = attachChangeTagBox(style_arr[1]);
		node.appendChild(document.createTextNode(getString("tagbox." + style_arr[1])));
		style_sort.appendChild(node);
	} else {
		node.onclick = attachChangeTagBox(style_arr[0]);
		node.appendChild(document.createTextNode(getString("tagbox." + style_arr[0])));
		style_sort.appendChild(node);

		style_sort.appendChild(document.createTextNode(" | " + getString("tagbox." + style_arr[1])));
	}
	style_sort.appendChild(document.createTextNode(") "));
	
	return style_sort;
}
function attachMinUsertags(count, requUser) { return(function() { minUsertags(count,requUser);})}
function getMinUsertagsLink (count, requUser) {
	var node = document.createElement("a");
	node.onclick = attachMinUsertags(count,requUser);
	node.appendChild(document.createTextNode(count));
	node.style.cursor = "pointer";
	return node;
}

function showMinfreq(count, currUser) {
	var minfreqList = document.createElement("li")

	minfreqList.appendChild(document.createTextNode(" (" + getString("tagbox.minfreq") + "  "));
	
	if (count == 1) {
		minfreqList.appendChild(document.createTextNode("1 | "));
		minfreqList.appendChild(getMinUsertagsLink (2, currUser));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5, currUser));
	} else if(count == 2) {
		minfreqList.appendChild(getMinUsertagsLink (1, currUser));
		minfreqList.appendChild(document.createTextNode(" | 2 | "));
		minfreqList.appendChild(getMinUsertagsLink (5, currUser));
	} else if(count == 5) {
		minfreqList.appendChild(getMinUsertagsLink (1, currUser));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2, currUser));
		minfreqList.appendChild(document.createTextNode(" | 5"));
	} else {	
		minfreqList.appendChild(getMinUsertagsLink (1, currUser));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2, currUser));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5, currUser));		
	}
	minfreqList.appendChild(document.createTextNode(") "));
	
	style_list.replaceChild(minfreqList, style_list.childNodes[2]);
	
}



function setTagBoxAlph(){
	collection_tagname = new Array();
	collection_li = new Object();

	/* store tagbox */
	var ultag = document.getElementById("tagbox");
	var litags = ultag.getElementsByTagName("li");
	for(x=0; x<litags.length; x++){
		var tags = litags[x].getElementsByTagName("a");
		if(tags.length==1){
			var tagname = tags[0].firstChild.nodeValue;
			collection_tagname.push(tagname);
			collection_li[tagname] = litags[x].cloneNode(true);
		}else if(tags.length>=2){
			var tagname = tags[2].firstChild.nodeValue;
			collection_tagname.push(tagname);
			collection_li[tagname] = litags[x].cloneNode(true);
		}
	}

	/* remove old tagbox */
	var litagslength = litags.length;
    for (x=0; x<litags.length; x++) {
	   	var taglinks = litags[x].getElementsByTagName("a");
	   	var taglinkslength = taglinks.length;
	   	for(y=0; y<taglinkslength; y++){
	   		litags[x].removeChild(taglinks[0]);
	   	}
	}
	for (x=0; x<litagslength; x++) {
	   	ultag.removeChild(litags[0]);
	}
	
	/* build new tagbox */
	collection_tagname.sort(unicodeCollation);
	for(x=0; x<collection_tagname.length; x++){
		/* build new li */
		var tagname = collection_tagname[x];
		var newli = collection_li[tagname];
		ultag.appendChild(newli);
	}
	
	/* aufr?umen */
	delete collection_tagname;
	delete collection_li;

}

function setTagBoxFreq(){
	collection_tagname = new Array();
	collection_li = new Object();
	collection_tagposts = new Object();
	collection_numberofposts = new Array();

	/* store tagbox */
	var ultag = document.getElementById("tagbox");
	var litags = ultag.getElementsByTagName("li");
	for(x=0; x<litags.length; x++){
		var tags = litags[x].getElementsByTagName("a");
		if(tags.length==1){
			var tagname = tags[0].firstChild.nodeValue;
			collection_tagname.push(tagname);
			collection_li[tagname] = litags[x].cloneNode(true);
			var title = tags[0].getAttribute("title");
			var titleParts = title.split(" ");
			var numberofpost = parseInt(titleParts[0]);
			// var numberofpost = title.substring(0, title.length-6);
			collection_tagposts[tagname] = numberofpost;
			var newnumberofposts = true;
			for(y=0; y<collection_numberofposts.length; y++){
				if(collection_numberofposts[y] == numberofpost){
					newnumberofposts = false;
				}					
			}
			if(newnumberofposts){
				collection_numberofposts.push(numberofpost);
			}
		}else if(tags.length>=2){
			var tagname = tags[2].firstChild.nodeValue;
			collection_tagname.push(tagname);
			collection_li[tagname] = litags[x].cloneNode(true);
			var title = tags[2].getAttribute("title");
			var titleParts = title.split(" ");
			var numberofpost = parseInt(titleParts[0]);			
			// var numberofpost = title.substring(0, title.length-6);
			collection_tagposts[tagname] = numberofpost;
			var newnumberofposts = true;
			for(y=0; y<collection_numberofposts.length; y++){
				if(collection_numberofposts[y] == numberofpost){
					newnumberofposts = false;
				}					
			}
			if(newnumberofposts){
				collection_numberofposts.push(numberofpost);
			}
		}	
	}
	/* remove old tagbox */
	var litagslength = litags.length;
    for (x=0; x<litags.length; x++) {
	   	var taglinks = litags[x].getElementsByTagName("a");
	   	var taglinkslength = taglinks.length;
	   	for(y=0; y<taglinkslength; y++){
	   		litags[x].removeChild(taglinks[0]);
	   	}
	}
	for (x=0; x<litagslength; x++) {
	   	ultag.removeChild(litags[0]);
	}
		
	/* build new tagbox */
	collection_numberofposts.sort(unicodeCollation);
	collection_numberofposts.reverse();//von gro? nach klein
	for(x=0; x<collection_numberofposts.length; x++){
		var tags = new Array();
		for(y=0; y<collection_tagname.length; y++){
			var tagname = collection_tagname[y];
			if(collection_tagposts[tagname] == collection_numberofposts[x]){
				tags.push(tagname);
			}
		}
		tags.sort(unicodeCollation);
		for(y=0; y<tags.length; y++){
			/* build new li */
			var tagname = tags[y];
			var newli = collection_li[tagname];
			ultag.appendChild(newli);
		}
		delete tags;
	}
	
	/* aufr?umen */	
	delete collection_tagname;
	delete collection_li;
	delete collection_tagposts;
	delete collection_numberofposts;
}

function sendMinfreqRequ(minfreq, currUser) {
 	var request = ajaxInit();
 	if(request) {
 		if(minfreq == null)	minfreq = 1;

		request.open('GET', "/ajax/subTagList.jsp?requUser=" + currUser + "&minfreq=" + minfreq + "&ckey=" + ckey, true);
		request.onreadystatechange = handleMinfreqResponse(request);
		request.send(null);
	}
}

function sendStyleRequ(mode, style) {
 	var request = ajaxInit();
 	if(request){
 		if(style == null) style = 0;

		request.open('GET', "?" + mode + "=" + style + "&ckey=" + ckey, true);
		request.send(null);
	}
}


function handleMinfreqResponse(request) {
    return function(){
    // window.alert(request.responseText);
	if(request.readyState == 4) {
		// dirty workaround for failed innerHTML-ajax functionality in ie6+7

		if(window.navigator.userAgent.indexOf("MSIE ") > -1) {
			window.location.reload();
		} else {
			replaceTags(request);
		}
	}	
	}
}

function replaceTags (request) {
	
	var pListStartTag = '<taglist>';
	var pListEndTag = '</taglist>';
	
	var text = request.responseText;
	var start = text.indexOf(pListStartTag) + pListStartTag.length;
	var end = text.indexOf(pListEndTag);	

	tagbox.innerHTML = text.slice(start, end);
}

function minUsertags(minfreq, currUser) {
	sendMinfreqRequ(minfreq, currUser)
	showMinfreq(minfreq, currUser);
}

// switches page default path to full navigation path
function naviSwitchSpecial(target) {
	
	var username = null;
	
	if(requUser != null) {
		username = requUser;
	} else if(currUser != null) {
		username = currUser;
	}
	
	// obtain fundamental informations
	var body = document.getElementsByTagName("body")[0];	
	var bar = document.getElementById("heading");
	if(bar == null) { 
		// backwards compatibility
		bar = document.getElementById("path");
	}

	// create headline node as container for following stuff 
	var headlineNode = document.createElement("h1");
	headlineNode.setAttribute("id", "path");

	// create a node with projectname default values
	var projectNode = document.createElement("a");
	projectNode.setAttribute("href", "/");
	projectNode.setAttribute("rel", "Start_js");
	projectNode.appendChild(document.createTextNode(projectName));
	headlineNode.appendChild(projectNode);
	headlineNode.appendChild(document.createTextNode(" :: "));

	// create form as container for dropdown- and textfields
	var formNode = document.createElement("form");
	formNode.setAttribute("id", "specialsearch");
	formNode.setAttribute("method", "get");
	formNode.setAttribute("action", "/specialsearch");

	// create dropdown box
	var selectNode = document.createElement("select");
	selectNode.setAttribute("name", "scope");
	selectNode.setAttribute("size", "1");
	selectNode.setAttribute("id", "scope");
	
	// select options
	var options = new Array("tag", "user", "group", "author", "concept", "bibtexkey", "all", "explicit_user");
	
	// hint for input field
	var hint = "";
	
	// fill select dropdown box with options
	for(var i = 0; i < options.length; i++) {
						
		// exception for 'all' case
		if(options[i] == "all") {
			
			var optionNode = document.createElement("option");
			optionNode.setAttribute("value", options[i]);
			optionNode.appendChild(document.createTextNode(getString("navi.search") + ":" + getString("navi.all")));
						
			if(options[i] == target) {
				optionNode.setAttribute("selected", "");
				hint = getString("navi.search.hint");
			}
		
			selectNode.appendChild(optionNode);
			
		} else if(options[i] == "bibtexkey") {
			
			var optionNode = document.createElement("option");
			optionNode.setAttribute("value", options[i]);
			optionNode.appendChild(document.createTextNode(getString("navi.bibtexKey")));			
			
			if(options[i] == target) {
				optionNode.setAttribute("selected", "");
				hint = getString("navi.bibtexkey.hint");
			}
		
			selectNode.appendChild(optionNode);
			
		} else if(options[i] == "explicit_user") {
			
			if(username != "" && username != null) {
				var optionNode = document.createElement("option");
				optionNode.setAttribute("value", "user:" + username);
				optionNode.appendChild(document.createTextNode(getString("navi.search") + ":" + username));							
			
				if(options[i] == target) {
					optionNode.setAttribute("selected", "");
					hint = getString("navi.search.hint");	
				}
		
				selectNode.appendChild(optionNode);
			}
			
		} else {
			
			var optionNode = document.createElement("option");
			optionNode.setAttribute("value", options[i]);
			optionNode.appendChild(document.createTextNode(getString("navi." + options[i])));			
			
			if(options[i] == target) {
				optionNode.setAttribute("selected", "");
				hint = getString("navi." + options[i] + ".hint");
			}
		
			selectNode.appendChild(optionNode);
		}		
	}
	
	// append dropdown box and spacer
	formNode.appendChild(selectNode);
	formNode.appendChild(document.createTextNode(" :: "));
	
	// create and append textfield and spacer
	var inpfNode = document.createElement("input");
	inpfNode.setAttribute("type", "text");
	inpfNode.setAttribute("id", "inpf");
	inpfNode.setAttribute("name", "q");
	inpfNode.setAttribute("size", "30");
	
	if(document.getElementById("inpf") != null) {
		inpfValue = document.getElementById("inpf").value;
						
//		if(inpfValue != getString("navi.search.hint") && inpfValue != getString("navi.tag.hint")
//			&& inpfValue != getString("navi.user.hint") && inpfValue != getString("navi.group.hint")
//			&& inpfValue != getString("navi.author.hint") && inpfValue != getString("navi.concept.hint")) {

		if(inpfValue == getString("navi.search.hint") || inpfValue == getString("navi.tag.hint")
			|| inpfValue == getString("navi.user.hint") || inpfValue == getString("navi.group.hint")
			|| inpfValue == getString("navi.author.hint") || inpfValue == getString("navi.concept.hint")
			|| !inpfValue) {
			
			inpfValue = hint;
    		inpfNode.style.color = "#aaaaaa";
    		inpfNode.onmousedown = clear_input;
    		inpfNode.onkeypress  = clear_input;					
		}
			
		inpfNode.value = inpfValue;
		inpfNode.value = inpfNode.value;
	}
	
   	formNode.appendChild(inpfNode);
	headlineNode.appendChild(formNode);
	headlineNode.appendChild(document.createTextNode(" "));

	// insert new navi
	body.insertBefore(headlineNode, bar);

	// clone old navigation path and save it in a global variable for later restoring
	curr_navi = bar.cloneNode(true);

	// remove old navigation path
	body.removeChild(bar);
	
	// set focus to the input field
	inpfNode.focus();
	
	// if the user uses opera, there is a workaround to set the cursor position
	if(window.opera)
		inpfNode.select();
	
	// unselect text
	inpfNode.value = inpfNode.value;
	
}