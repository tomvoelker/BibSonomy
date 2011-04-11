var request = null;
var style_list = null;
var style_sort = new Array("alph", "freq");
var style_show = new Array("cloud", "list");
var userMinFreq = 1;

//var style_sort = new Array(getString("tagbox.sort.alpha"), getString("tagbox.sort.freq"));
//var style_show = new Array(getString("tagbox.style.cloud"), getString("tagbox.style.list"));

function init_tagbox(show, sort, minfreq, requUser) {
	style_list = document.createElement("ul");
	style_list.className= "floatul";

	style_list.appendChild(document.createElement("li"));
	style_list.appendChild(document.createElement("li"));
	style_list.appendChild(document.createElement("li"));

	style_list.replaceChild(getStyleItem(style_sort[sort], style_sort), style_list.childNodes[0]);
	style_list.replaceChild(getStyleItem(style_show[show], style_show), style_list.childNodes[1]);
	if (typeof tagbox_minfreq_style != "undefined") {
		if (tagbox_minfreq_style == "user") {
			showUserMinfreq(minfreq, requUser);
		}
		else if (tagbox_minfreq_style == "default") {
			showMinfreq();  
		}
		else if (tagbox_minfreq_style == "none"){
			// do nothing
		}
	}

	var span = document.createElement("span");
	span.appendChild(style_list);  

	changeTagBox(style_show[show]);
	changeTagBox(style_sort[sort]);

	tagbox.parentNode.insertBefore(span, tagbox);
}

function attachChangeTagBox(mode) { return(function() {	changeTagBox(mode);	});}

function changeTagBox(mode) {
	var request = ajaxInit();

	if(mode == "list" || mode == "cloud"){
		tagbox.className = "tag" + mode;
		style_list.replaceChild(getStyleItem(mode, style_show), style_list.childNodes[1]);
	}else if(mode == "alph" || mode == "freq") {
		style_list.replaceChild(getStyleItem(mode, style_sort), style_list.childNodes[0]);
		mode == "alph" ? setTagBoxAlph() : setTagBoxFreq();
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
function attachMinUsertags(count) { return(function() { minUsertags(count);});}

function getMinUsertagsLink (count) {
	var node = document.createElement("a");
	node.onclick = attachMinUsertags(count);
	node.appendChild(document.createTextNode(count));
	node.style.cursor = "pointer";
	return node;
}

function getMinTagsLink (count) {
	var node = document.createElement("a");
	if (userMinFreq != count) {
		node.onclick = attachMinUsertags(count);
		node.style.cursor = "pointer";
	}

	node.appendChild(document.createTextNode(count));

	return node;
}

//create minfreq links for users (which are loaded via AJAX)
function showUserMinfreq(count, currUser) {

	var minfreqList = document.createElement("li");

	minfreqList.appendChild(document.createTextNode(" (" + getString("tagbox.minfreq") + "  "));

	if (count == 1) {
		minfreqList.appendChild(document.createTextNode("1 | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5));
	} else if(count == 2) {
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | 2 | "));
		minfreqList.appendChild(getMinUsertagsLink (5));
	} else if(count == 5) {
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | 5"));
	} else {	
		minfreqList.appendChild(getMinUsertagsLink (1));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (2));
		minfreqList.appendChild(document.createTextNode(" | "));
		minfreqList.appendChild(getMinUsertagsLink (5));		
	}
	minfreqList.appendChild(document.createTextNode(") "));

	style_list.replaceChild(minfreqList, style_list.childNodes[2]);

}

//create default minfreq links
function showMinfreq() {
	var minfreqList = document.createElement("li");

	minfreqList.appendChild(document.createTextNode(" (" + getString("tagbox.minfreq") + "  "));

	minfreqList.appendChild(getMinTagsLink(1));
	minfreqList.appendChild(document.createTextNode(" | "));
	minfreqList.appendChild(getMinTagsLink(2));
	minfreqList.appendChild(document.createTextNode(" | "));
	minfreqList.appendChild(getMinTagsLink(5));

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
	var space = document.createTextNode(" ");
	collection_tagname.sort(unicodeCollation);
	for(x=0; x<collection_tagname.length; x++){
		/* build new li */
		var tagname = collection_tagname[x];
		var newli = collection_li[tagname];
		newli.appendChild(space.cloneNode(true));
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
		var space = document.createTextNode(" ");
		for(y=0; y<tags.length; y++){
			/* build new li */
			var tagname = tags[y];
			var newli = collection_li[tagname];
			newli.appendChild(space.cloneNode(true));
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

		request.open('GET', "?tagcloud.minFreq=" + minfreq + "&ckey=" + ckey + "&tagstype=default&format=tagcloud", true);
		userMinFreq = minfreq;
		request.onreadystatechange = handleMinfreqResponse(request);
		request.send(null);
	}
}

function handleMinfreqResponse(request) {
	return function(){
		//window.alert(request.responseText);
		//alert(request.readyState);
		if(request.readyState == 4) {
			// dirty workaround for failed innerHTML-ajax functionality in ie6+7

			if(window.navigator.userAgent.indexOf("MSIE ") > -1) {
				window.location.reload();
			} else {
				replaceTags(request);
			}
		}
	};
}

function replaceTags (request) {

	//ensure that we look in the correct list (the tagCloud / list)
	var pListStartTag = "<li class=\"tag";
	var pListEndTag = "</ul>";

	var text = request.responseText;
	var start = text.indexOf(pListStartTag);
	var end = text.indexOf(pListEndTag,start);

	tagbox.innerHTML = text.slice(start, end);
	var sListStartTag = "<span>";
	var sListEndTag = "</span>";
	start = text.indexOf(sListStartTag) + (sListEndTag.length - 1);
	end = text.indexOf(sListEndTag);

	// re-order tags per js
	if (text.slice(start, end) == "ALPHA") {
		setTagBoxAlph();
	} else{
		setTagBoxFreq();
	}
}

function minUsertags(minfreq) {
	sendMinfreqRequ(minfreq);
	showMinfreq(minfreq);
}

var gOptions = new Array();

//switches page default path to full navigation path
function naviSwitchSpecial(target) {

	var username = null;

	if(requUser != null) {
		username = requUser;
	} else if(currUser != null) {
		username = currUser;
	}

	// obtain fundamental informations
	var body = document.getElementsByTagName("body")[0];	
	var bar = document.getElementById("heading").parentNode;
	if(bar == null) { 
		// backwards compatibility
		bar = document.getElementById("path");
	}

	// create headline node as container for following stuff 
	var headlineNode = document.createElement("h1");
	headlineNode.setAttribute("id", "path");

	// create a node with projectname default values
	var pN = document.createElement("a");
	pN.setAttribute("href", "/");
	pN.setAttribute("rel", "Start_js");
	pN.appendChild(document.createTextNode(projectName));
	headlineNode.appendChild(pN);
	headlineNode.appendChild(document.createTextNode(" :: "));

	// create form as container for dropdown- and textfields
	var fN = document.createElement("form");
	fN.setAttribute("id", "specialsearch");
	fN.setAttribute("method", "get");
	fN.setAttribute("action", "/redirect");

	// create dropdown box
	var sN = document.createElement("select");
	sN.setAttribute("name", "scope");
	sN.setAttribute("size", "1");
	sN.setAttribute("id", "scope");

	// select options
	var options = new Array("tag", "user", "group", "author", "concept/tag", "bibtexkey", "search", "explicit_user", "explicit_group");

	// hint for input field
	var hint = "";

	// fill select dropdown box with options
	for(var i = 0; i < options.length; i++) {

		// exception for 'search' case
		if(options[i] == "search") {

			var oN = document.createElement("option");
			oN.setAttribute("value", options[i]);
			oN.appendChild(document.createTextNode(getString("navi.search") + ":" + getString("navi.all")));

			if(options[i] == target) {
				oN.setAttribute("selected", "");
				hint = getString("navi.search.hint");
			}

			sN.appendChild(oN);

		} else if(options[i] == "concept/tag") {

			var oN = document.createElement("option");
			oN.setAttribute("value", options[i]);
			oN.appendChild(document.createTextNode(getString("navi.concept")));			

			if(options[i] == target) {
				oN.setAttribute("selected", "");
				hint = getString("navi.concept.hint");
			}

			sN.appendChild(oN);

		} else if(options[i] == "explicit_user") {

			if(username != "" && username != null) {
				var oN = document.createElement("option");
				oN.setAttribute("value", "user:" + username);
				oN.appendChild(document.createTextNode(getString("navi.search") + ":" + username));							

				if(options[i] == target) {
					oN.setAttribute("selected", "");
					hint = getString("navi.search.hint");	
				}

				sN.appendChild(oN);
			}

		} else if(options[i] == "explicit_group") {
			for(var j = 0; j < gOptions.length; ++j) {
				var oN = document.createElement("option");
				oN.setAttribute("value", "group:" + gOptions[j]);
				oN.appendChild(document.createTextNode(getString("navi.search") + ":" + gOptions[j]));		

				if(gOptions[j] == target) {
					oN.setAttribute("selected", "");
					hint = getString("navi.search.hint");	
				}

				sN.appendChild(oN);
			}
		} else {

			var oN = document.createElement("option");
			oN.setAttribute("value", options[i]);
			oN.appendChild(document.createTextNode(getString("navi." + options[i])));			

			if(options[i] == target) {
				oN.setAttribute("selected", "");
				hint = getString("navi." + options[i] + ".hint");
			}

			sN.appendChild(oN);
		}		
	}

	// append dropdown box and spacer
	fN.appendChild(sN);
	fN.appendChild(document.createTextNode(" :: "));

	// create and append textfield and spacer
	var iN = document.createElement("input");
	iN.setAttribute("type", "text");
	iN.setAttribute("id", "inpf");
	iN.setAttribute("name", "search");
	iN.setAttribute("size", "30");

	if(document.getElementById("inpf") != null) {
		var iV = document.getElementById("inpf").value;

		if(iV == getString("navi.search.hint") 
				|| iV == getString("navi.tag.hint")
				|| iV == getString("navi.user.hint") 
				|| iV == getString("navi.group.hint")
				|| iV == getString("navi.author.hint") 
				|| iV == getString("navi.concept.hint")
				|| !iV) {

			iV = hint;
			iN.style.color = "#aaaaaa";
			iN.onmousedown = clear_input;
			iN.onkeypress  = clear_input;					
		}

		iN.value = iV;
	}

	fN.appendChild(iN);
	headlineNode.appendChild(fN);
	headlineNode.appendChild(document.createTextNode(" "));

	// clone old navigation path and save it in a global variable for later restoring
	curr_navi = bar.cloneNode(true);

	// display new path element
	bar.replaceChild(headlineNode, document.getElementById("heading"));
	headlineNode.id="heading";

	// set focus to the input field
	iN.focus();

	// if the user uses opera, there is a workaround to set the cursor position
	if(window.opera)
		iN.select();

	// unselect text
	iN.value = iN.value;

}