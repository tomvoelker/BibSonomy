//methods for editPublication page
//setup jQuery to update recommender with form data
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
		var comma = firstauthor.search(/,/);
		var lastname;
		if (comma < 0) {
			// no comma found - find last space
			var lastspace = firstauthor.search(/\s\S+$/);
			if (lastspace < 0) {
				lastname = firstauthor;
			} else {
				lastname = firstauthor.substring(lastspace + 1, firstauthor.length);
			}
		} else {
			lastname = firstauthor.substring(0, comma);
		}
		return lastname.trim().replace(/[^0-9A-Za-z]+/g, "");
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

$(window).load(function() {
	// load only, when extended fields are available                                                                                              
	if (document.getElementById("post.resource.publisher")) changeView();
});

$(document).ready(function() {
	addAutoCompleteSendTag($('#inpf'));

	var hash = window.location.href.match("[a-f0-9]{32}");
	if(hash == -1)
		return;
	$.ajax({
		url: '/json/bibtex/2'+hash + "?items=100",
		dataType: "jsonp",
		success: function (data) {
		if(data.items != undefined)
			$.ajax({
				url: '/json/bibtex/1'+data.items[0].interHash + "?items=100",
				dataType: "jsonp",
				success: function (data) {
				if(data.items != undefined) 
					buildGoodPostSuggestion(data);
			}
			});
	}
	});
});


/**
 * provide each input field with suggestions from multiple post values passed as json data 
 * 
 * @param json - posts as json data
 * @return
 */
function buildGoodPostSuggestion(json) {
	/* array of indices to sort */
	var sortIndices = function(j) {
		var h = new Array();
		while(j.length > h.length)
			h.push(h.length);
		for(i = 0; i < j.length; i++) {
			for(k = i+1; k < j.length; k++) 
				if(j[h[i]] < j[h[k]]) {
					h[i] = h[i]+h[k];
					h[k] = h[i]-h[k];
					h[i] = h[i]-h[k];
				}
		}
		return h;
	};
	/*
	 * constants
	 */
	var inputFields = $("textarea, #postForm > textarea, input");
	var postResource = "post.resource";
	var inputFieldMap = new Object();
	inputFieldMap["post.resource.title"] = "label";
	inputFieldMap["post.description"] = "description";
	var u = /(\s+|,)/g; // regex to remove whitespace and commas
	var v = /\s+$/; // regex to remove trailing whitespace

	var arrayOfPartialTags = new Array();

	/*
	 * loop over all input fields
	 */
	for(var x = 0; inputFields.length > x; x++) {
		var fieldValue = new Array();
		var inputField = inputFields[x];
		var inputFieldName = inputField.name;

		/*
		 * field name starts with "post.resource" or it appears in the inputFieldMap
		 */
		if (inputFieldName.substring(0, postResource.length) == postResource || inputFieldMap[inputFieldName] != undefined) {
			var g = inputField.value.replace(u, "");
			var suggestions = new Array();
			var occurrences = new Array();
			var k = -1;
			/*
			 * loop over posts
			 */
			for(var z = 0; json.items.length > z; z++) {
				var post = json.items[z];
				var fieldVal;
				if(((fieldVal = post[inputFieldName.substring(postResource.length+1, inputFieldName.length)]) != undefined 
						|| (fieldVal = post[inputFieldMap[inputFieldName]])) && fieldVal.length > 0) {
					var name = "";
					if(typeof fieldVal == "object") {
						var delimiter = " "; 
						/*
						 * special handling for person names: 
						 * - join them using ", "
						 * - prepend the prefix (last name) to our first name chain if present
						 * - remove whitespace trailing whitespaces
						 */
						if(inputFieldName == postResource + ".author" || inputFieldName == postResource + ".editor") {
							delimiter = ', ';
							for(var m = 0; m < fieldVal.length; m++) {
								var t = -1;
								var personName = fieldVal[m];
								var prepend = ((t = (personName.lastIndexOf(" ")+1)) > -1)?personName.substring(t):"";
								var postfix = personName.substring(0, personName.length-prepend.length).replace(v, "");
								name += ((prepend.length)?prepend+((postfix.length)?", ":""):"")+postfix+"\n";
							}
						}
						fieldVal = fieldVal.join(delimiter);
					}
					/* if a term has not been acquired yet,
					add a occurrence of 1 to our occurrence array and add the term to our suggestion array */
					if ((k = $.inArray(fieldVal, suggestions)) == -1) {
						suggestions.push(fieldVal); // add term to suggestions
						occurrences.push(1); // add initial occurrence count of 1
						/* if we encounter author or editor suggestions,
						 * label and field value are not the same, so we have to differentiate between
						 * field value and label by providing two different sets of values 							*/
						if(name.length) // if name has a length of > 0 the current input field is an author or editor field
							fieldValue.push(name);
					} else if(k > -1) { // if true we already have this term as a suggestion, so we just have to increment the occurrence of the term
						occurrences[k]++;
						k = -1;
					}
				}
			} // loop over posts
			/*
			 * no suggestions or the suggestion count is 1 AND field value is the same as the suggestion value 
			 * - skip in both cases 
			 */
			if (!suggestions.length || (suggestions.length == 1 
			&& g == ((name.length)?name.replace(u, ""):fieldVal.replace(u, "")))) continue;
			$(inputField).addClass("fsInputReco"); // show the user that suggestions are available
			/* we have a bijective mapping therefore (f:suggestion->occurrence) we sort our indices by descending order */
			var indices = sortIndices(occurrences);
			/* occurrences are sorted and aligned to the corresponding suggestions */
			/* if fieldvalue contains values, it is a different set of values we apply to the value label - this is true for the author and editor fields */
			var labels = $.map(suggestions, function(item, index) {
				var suggestion = suggestions[indices[index]];
				return {
					label: suggestion + " (" + occurrences[indices[index]] + ")",
					value: ((fieldValue.length == 0) ? suggestion : fieldValue[indices[index]])
				};
			});

			$(inputField).bind("focus", function(){$(this).autocomplete("search", "");}).autocomplete(
					{
						source : labels,
						minLength : 0,
						delay : 0
					}
			);
		}
	} // loop over input fields
}
