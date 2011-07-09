// methods for editPublication page
// setup jQuery to update recommender with form data
var tagRecoOptions = { 
   dataType: "xml",
   url:  '/ajax/getPublicationRecommendedTags',
   success: function showResponse(responseText, statusText) { 
	 handleRecommendedTags(responseText);
   } 
}; 

var hide = true;
var getFriends = null;
var fields = new Array(	"booktitle","journal","volume","number","pages",
						"publisher","address","month","day","edition",
						"chapter","key","type","annote","note",
						"howpublished","institution","organization",
						"school","series","crossref","misc");

/* returns required and optional fields for given publication type*/ 
function getRequiredFieldsForType(type) {
	switch(type) {
		case "article":	
			return new Array("journal","volume","number","pages","month","note"); break;
		case "book": 
			return new Array("publisher","volume","number","series","address","edition","month","note"); break;
		case "booklet": 
			return new Array("howpublished","address","month","note"); break;
		case "inbook": 
			return new Array("chapter","pages","publisher","volume","number","series","type","address","edition","month","note"); break;
		case "incollection": 
			return new Array("publisher","booktitle","volume","number","series","type","chapter","pages","address","edition","month","note"); break;
		case "inproceedings": 
			return new Array("publisher","booktitle","volume","number","series","pages","address","month","organization","note"); break;
		case "manual": 
			return new Array("organization","address","edition","month","note"); break;
		case "mastersthesis": 
			return new Array("school","type","address","month","note"); break;
		case "misc": 
			return new Array("howpublished","month","note"); break;
		case "phdthesis": 
			return new Array("school","address","type","month","note"); break;
		case "proceedings": 
			return new Array("publisher","volume","number","series","address","month","organization","note"); break;
		case "techreport": 
			return new Array("institution","number","type","address","month","note"); break;
		case "unpublished": 
			return new Array("month","note"); break;
		default:		
			return fields; break;
	}
}	

/* update view when user selects another type of publication in list */
function changeView() {		
	if (hide == false)
		return;

	var requiredFields = getRequiredFieldsForType(document.getElementById('post.resource.entrytype').value);
	
    for (var i=0; i<fields.length; i++) {
        showHideElement(fields[i], in_array(requiredFields,fields[i]) ? '' : 'none');
    }
}	

/* toggle to show elements */
function showAll() {
	hide = false;
	document.getElementById('collapse').firstChild.nodeValue = getString('post.resource.fields.detailed.show.required');
	document.getElementById('collapse').href = 'javascript:hideElements();';
	for (i=0; i<fields.length; i++) {
		showHideElement(fields[i], '');			
	}		
}

/* toggle to hide elements */
function hideElements() {
	hide = true;
	document.getElementById('collapse').firstChild.nodeValue = getString('post.resource.fields.detailed.show.all');
	document.getElementById('collapse').href = 'javascript:showAll();';
	changeView();
}	

function showHideElement(id, display) {
    // get input field			
	var field = document.getElementById("post.resource." + id);			
	
	if (field.value == '') {
		// must find closest parent node with class 'fsRow'
		$(field).closest(".fsRow").css('display', display);
	}
}

/* checks if element is member of given array */
function in_array(array, element) {    	
 	for(var j = 0; j < array.length; j++) {
   		if(array[j] == element) {
     		return true;
   		}
 	}
 	return false;
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
    
	if (buffer.length == 0) {
		window.alert(getString("error.field.valid.bibtexKey.generation"));
	} else {
		document.getElementById("post.resource.bibtexKey").value = buffer.toLowerCase();
	}
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
        var lastspace = firstauthor.search(/\s\S+$/);
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

function addAutoCompleteSendTag(tagbox) {
	var friends = null;
	getFriends = function () {return friends;};
	$.ajax({
		url: '/json/friends?userRelation=FRIEND_OF',
		async: false,
		dataType: "jsonp",
		success: function (data) {
			friends = $.map( data.items, function( item ) {
				return item.name;
			});
		}
	});
	
	if(tagbox[0] == null)
		return;
	tagbox[0].onclick = tagbox[0].onblur = tagbox[0].onfocus = null;

	var c = null;	
	var inpfValue = function(t) {
		if(t == null)
			return c;
		return (c = t);
	};
	var suggestSendTo = function (partialName) {
		var x = 0;
		var regexp = new RegExp("^"+partialName);
		var friends = getFriends();
		delete sortedCollection;
		sortedCollection = new Array();
		clearSuggestion();
		while(x < friends.length) {
			if(("send:"+friends[x]).match(regexp) 
				&& tagbox.val().match(new RegExp("([ ]|^)send:"+friends[x]+"([ ]|$)")) == null)  
					sortedCollection.push("send:"+friends[x]);
			x++;
		}
		addToggleChild(sortedCollection);
		activeTag = partialName;
	};
	
	
	var evalKeyInput = function (e) {
		var keyCode = e.keyCode;
		switch( keyCode ) {
        case keyCode.ENTER:
        case keyCode.NUMPAD_ENTER:
        case keyCode.TAB:{
        	e.preventDefault();
        	break;
        }	
        default: {
        		if(e.type == 'keydown')
        			inpfValue(tagbox.val().split(" "));
        	}
        }
		return handler(e);
	};
	
	tagbox.keydown(function (e) {
		evalKeyInput(e);
	}).keypress(function (e) {
		return handler(e);
	}).keyup(function (e) {
		evalKeyInput(e);
		var t = inpfValue();
		var reverse = false;
		if(getFriends() != null) {
			var tagsNew = tagbox.val().split(" ");
			var x = 0;
			if(tagsNew.length < t.length) {
				tagsNew = tagsNew.reverse();t = t.reverse();reverse = true;
			}
			
			while( t.length > x ) {
				if(tagsNew[x] != undefined 
						&& tagsNew[x] != t[x]) {
					if(tagsNew[x].match(/^send:/) == null
							|| t[x].length == 0)
						break;
					return suggestSendTo(tagsNew[x]);
				}
				x++;
			}
		}
	});
}

$(window).load(function() {
	// load only, when extended fields are available                                                                                              
    if (document.getElementById("post.resource.publisher")) changeView();
});

$(document).ready(function() {
	addAutoCompleteSendTag($('#inpf'));
});