	/**
	* updates authorlist for selected tag in taglist
	**/
	function updateAuthorsForTags() {
		var authorsIdx 	= new Array()
		var tagList		= document.searchform.tags;
		var authorList 	= document.searchform.authors;		
		
		// no tag selected = all tags
		if (tagList.selectedIndex <= 0) {
			for (var i = 0; i<authors.length; i++) {
				authorsIdx[authorsIdx.length] = i;
			}		
		} else {
			var options = tagList.options;
			var found 	= false;
			
			for (var i=1; i<options.length; i++) {
				if (options[i].selected) {
					if (!found) {						
						authorsIdx = tagAuthor[options[i].value];
						found = true;
					} else {													
						if (getRadioValue("tag_op") == "and")
							authorsIdx = intersect(authorsIdx,tagAuthor[options[i].value]);
						else
							authorsIdx = union(authorsIdx,tagAuthor[options[i].value]);
					}
				}
			}							
		}	
		
		// fill list with authors

		authorsIdx = authorsIdx.sort(comparator);	
		authorList.length = 0;		
		authorList.options[0] = new Option(optionsAuthors,-1);
		
		for (var i=0; i<authorsIdx.length; i++) {
			authorList.options[i+1] = new Option(authors[authorsIdx[i]],authorsIdx[i]);		
		}		
		authorList.selectedIndex = 0;	
	}	
	
	/**
	* updates the title list 
	**/
	function updateTitles() {		
		var tagsTitles		= new Array();
		var authorsTitles 	= new Array();
		
		var tagList		= document.searchform.tags;
		var authorList 	= document.searchform.authors;			
		
		// no tags and authors selected --> all titles
		if (tagList.selectedIndex <= 0 && authorList.selectedIndex <= 0) {
			titlesIdx.length = 0;
			for (var i=0; i<titles.length; i++) {
				titlesIdx[titlesIdx.length] = i;
			}			
		} else {
			// titles from tags	
			if (tagList.selectedIndex <= 0) {
				for (var i=0; i<titles.length; i++) {
					tagsTitles[tagsTitles.length] = i;
				}
			} else {
				var options = tagList.options;
				var found = false;				
				for (var i=1; i<options.length; i++) {
					if (options[i].selected) {
						if (!found) {						
							tagsTitles = tagTitle[options[i].value];
							found = true;
						} else {													
							if (getRadioValue("tag_op") == "and")
								tagsTitles = intersect(tagsTitles,tagTitle[options[i].value]);
							else
								tagsTitles = union(tagsTitles,tagTitle[options[i].value]);
						}
					}
				}
			}
			
			// titles from authors
			if (authorList.selectedIndex <= 0) {
				for (var i=0; i<titles.length; i++) {
					authorsTitles[authorsTitles.length] = i;
				}
			} else {
				var options = authorList.options;
				var found = false;			
				for (var i=1; i<options.length; i++) {
					if (options[i].selected) {
						if (!found) {						
							authorsTitles = authorTitle[options[i].value];
							found = true;
						} else {													
							if (getRadioValue("author_op") == "and")
								authorsTitles = intersect(authorsTitles,authorTitle[options[i].value]);
							else
								authorsTitles = union(authorsTitles,authorTitle[options[i].value]);
						}
					}
				}
			}
			
			// intersection of both sets
			titlesIdx = intersect(tagsTitles,authorsTitles);			
		}			
		titlesIdx = titlesIdx.sort(comparator);	
	}			
		
	/**
	* shows titles in the list
	**/	
	function showTitleList() {		
		removeAllChildren('resultlist');
		
		var results = 0;	
		var k=0;
		for (var i=0;i<titlesIdx.length;i++) {
			var t = titles[titlesIdx[i]];
			var opt;
			var auth = titleAuthorStringArray[titlesIdx[i]];
			var my_tags = titleTag[titlesIdx[i]];
			if (filter(t + " " + auth + " " + my_tags)) {				
				appendRow(trim(t, 90), trim(auth, 80), contentIds[titlesIdx[i]], titleUrls[titlesIdx[i]]);
				k++;
				results++;
			}
		}		
		document.getElementById("numresult").innerHTML = results + " " + resultlang;		
	}	

	/**
	* checks the form before submitting
	**/		
	function checkForm(bibtexhash) {
		var url = "/bibtex/" + simHashID + bibtexhash + "/" + loginusername;	
		document.location.href = url;	
		return false;
	}
	
	/**
	* initialize the option boxes
	**/
	function initBoxes() {
		// creates array of authors seperated by "," for each publication
		createTitleAuthorStringArray();
		
		var taglist 	= document.searchform.tags;
		var authorlist 	= document.searchform.authors;
		var resultlist 	= document.searchform.results;
		
		taglist.options[0] 		= new Option(optionsTags, -1);//new Option("- all tags -",-1);
		authorlist.options[0] 	= new Option(optionsAuthors, -1);//new Option("- all authors -",-1);		
		
		// taglist
		for (var i=0; i<tags.length; i++) {
			var tag = new Option(tags[i],i);
			taglist.options[i+1] = tag;
		}
		
		// authorlist
		for (var i=0; i<authors.length; i++) {
			var author = new Option(authors[i],i);
			authorlist.options[i+1] = author;
		}
		
		// titlelist
		for (var i=0; i<titles.length; i++) {
			appendRow(trim(titles[i], 90), trim(titleAuthorStringArray[i], 80), contentIds[i], titleUrls[i]);
			titlesIdx[titlesIdx.length] = i;
		}
		
		// default select		
		taglist.selectedIndex 	 = 0;
		authorlist.selectedIndex = 0;	
		
		// update number of search results
		document.getElementById("numresult").innerHTML = titles.length + " " + resultlang;		
	}
	
		
	/******************************************************
	* some useful functions
	*******************************************************/
	
	/**
	* removes all children from given node
	**/
	function removeAllChildren(nodename) {
		var node = document.getElementById(nodename);

		if (node.hasChildNodes()) {
		    while (node.childNodes.length >= 1) {
		        node.removeChild( node.firstChild );       
		    } 
		}
	}

	/**
	* appends a row in search result list with given information 
	**/
	function appendRow(title, authors, bibtexhash, url) {		
		var table = document.getElementById("resultlist");
		var tr = document.createElement("tr");
		
		var titletd = document.createElement("td");			
		titletd.onclick = function() {checkForm(bibtexhash);}
		
		var div1 = document.createElement("div");	
		div1.style.fontSize = "90%";		
		
		var span1 = document.createElement("span");	
		span1.style.fontSize = "80%";
		
		var linktd = document.createElement("td");	
		linktd.style.borderBottom = "1px solid #BBBBBB";
		linktd.style.textAlign = "right";		
		linktd.style.fontSize = "70%";	
				
		var link = document.createElement("a");	
		link.className="litem";
		link.innerHTML = getString("bibtex.actions.bibtex");
		link.href = "/bib/bibtex/" + simHashID + bibtexhash + "/" + loginusername;
		link.title = getString("bibtex.actions.bibtex.title");
		
		
		var pick = document.createElement("a");
		pick.className="item pick";
		pick.onclick = function() {return pickUnpickPublication(this);};
		pick.href = "/ajax/pickUnpickPost?action=pick&hash=" + bibtexhash + "&user=" + encodeURIComponent(loginusername) + "&ckey=" + ckey;
		pick.title = getString("bibtex.actions.pick.title");
		
		if (url != "") {
			var urllink = document.createElement("a");
			urllink.className="litem";
			urllink.href = url;		
			urllink.title = getString("bibtex.actions.url.title");
			urllink.innerHTML = getString("bibtex.actions.url");
		} else {
			var urllink = document.createElement("span");
			urllink.className="ilitem";
			urllink.title = getString("bibtex.actions.url.inactive");
			urllink.innerHTML = getString("bibtex.actions.url");
		}
				
		div1.innerHTML = title;	
		span1.innerHTML = authors;				
		titletd.appendChild(div1);					
		titletd.appendChild(span1);
					
		linktd.appendChild(link);
		linktd.appendChild(pick);
//		if (url != "") {
			linktd.appendChild(urllink);			
//		}	
					
		tr.appendChild(titletd);	
		tr.appendChild(linktd);	
				
		table.appendChild(tr);
	}
	
	/**
	* compartor function for array sort
	**/
	function comparator(x,y) {
		return (x-y);
	}
	
	/**
	* returns the unionset of 2 arrays
	**/
	function union(x,y) {
		var result = new Array();
		
		var found = new Array();
		for (var i=0; i<x.length; i++) {
			if (found["a" + x[i]] != 1) {
				result[result.length] = x[i];
				found["a" + x[i]] = 1;			
			}			
		} 			
		for (var i=0; i<y.length; i++) {
			if (found["a" + y[i]] != 1) {
				result[result.length] = y[i];
				found["a" + y[i]] = 1;			
			}			
		} 		
		return result.sort(comparator);	
	}	
	
	/**
	* returns intersection of 2 sets (arrays)
	**/
	function intersect(x,y) {
		var result = new Array();
		
		var found = new Array();
		for (var i=0; i<x.length; i++) {
			for (var j=0; j<y.length; j++) {
				if (x[i] == y[j] && found["a" + x[i]] != 1) {
					result[result.length] = x[i];
					found["a" + x[i]] = 1;
				}
			}
		}		
		return result.sort(comparator);	
	}	
	
	/**	
	* clears the filter input field
	**/
	function clearFilter() {
		var filter_field = document.searchform.filter;
		filter_field.className = ((filter_field.className.length > 0)?filter_field.className+' ':'')+'descriptiveLabel';
		filter_field.value = getString("mysearch.option.filter.text");
	}		
	
	/**
	* returns the value of an radiobutton(group) in our form
	**/
	function getRadioValue(radioName) {
		for(i=0;i<document.searchform[radioName].length;i++){
			if (document.searchform[radioName][i].checked == true)
				return document.searchform[radioName][i].value;
		}	
	}
	
	/**
	* trim function for strings
	**/
	function trim(t, len) {
		if(t.length > len) {
			t = t.substring(0,len-2) + "...";
		}
		return t;
	}
	
	/**
	* filters resultset 
	**/ 
	function filter(str) {
		var filterstring = document.searchform.filter.value.toLowerCase();
	
		if (filterstring == "" || filterstring == getString("mysearch.option.filter.text").toLowerCase()) {
			return true;
		}
	
		var lower = str.toLowerCase();	
		var a = filterstring.split(" ");
		for (var i=0; i<a.length; i++) {
			if (a[i]!="" && lower.search(a[i]) == -1) {
				return false;
			}
		}
		return true;
	}	
	
	/**
	* creates an array of strings containing authornames separated by comma 
	**/
	function createTitleAuthorStringArray() {
		for (var i=0; i<titleAuthor.length; i++) {
			authorIds = titleAuthor[i];
			
			var temp = new Array();
			for (var j=0; j<authorIds.length; j++) {
				temp[temp.length] = authors[authorIds[j]];
			}
			titleAuthorStringArray[i] = temp.join(", ");			
		}		
	}	

