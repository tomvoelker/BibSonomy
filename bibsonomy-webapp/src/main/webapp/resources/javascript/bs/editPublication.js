//methods for editPublication page
//setup jQuery to update recommender with form data

var tagRecoOptions = {
		type: "POST",
		url: '/ajax/getPublicationRecommendedTags',
		data: $('#postForm').serialize(),
		dataType: "json"
	}; 

var getFriends = null;
var fields = ["booktitle","journal","volume","number","pages","publisher","address",
        "month","day","edition","chapter","key","type","annote","note",
		"howpublished","institution","organization",
		"school","series","crossref","misc"];

var requiredForType = {
        "article":["journal","volume","number","pages","month","note"],
        "book":["publisher","volume","number","series","address","edition","month","note"],
        "booklet":["howpublished","address","month","note"],
        "inbook":["chapter","pages","publisher","volume","number","series","type","address","edition","month","note"],
        "incollection":["publisher","booktitle","volume","number","series","type","chapter","pages","address","edition","month","note"],
        "inproceedings":["publisher","booktitle","volume","number","series","pages","address","month","organization","note"],
        "manual":["organization","address","edition","month","note"],
        "masterthesis":["school","type","address","month","note"],
        "misc":["howpublished","month","note"],
        "phdthesis":["school","address","type","month","note"],
        "proceedings":["publisher","volume","number","series","address","month","organization","note"],
        "techreport":["institution","number","type","address","month","note"],
        "unpublished":["month","note"]
}

/* update view when user selects another type of publication in list */
function changeView(showAll) {	
	
	var requiredFields = requiredForType[document.getElementById('post.resource.entrytype').value];
	var message = getString('post.resource.fields.detailed.show.all');
	var noRequiredFields = (requiredFields === undefined); 
	var collpase = document.getElementById('collapse');
	
	if(showAll || noRequiredFields) {
		requiredFields = fields;
		if(noRequiredFields)
			$(collapse).parent().addClass("hidden");
		else {
			message = getString('post.resource.fields.detailed.show.required');
			$(collapse).parent().removeClass("hidden");
		}
	} else {
		$(collapse).parent().removeClass("hidden");
	}
	
	collapse.firstChild.nodeValue = message;
	
	for (var i=0; i<fields.length; i++) {
		
		var field = $("#post\\.resource\\." + fields[i]);
		var parent = field.closest(".form-group");
		if(showAll || in_array(requiredFields,fields[i])) {
			parent.show();
		} else {
			if (!field.val()) { //fix: don't hide not empty fields
				parent.hide();
			}
		}
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

function toggleView() {
	 
	var collapse = $("#collapse");
	var showAll = collapse.data("showAll");
	collapse.data("showAll", !showAll);
	changeView(collapse.data("showAll"));
}

function activateAffixEntry (el) {
	$(el).addClass("active").siblings().each(function(h, g){
			$(g).removeClass("active");
	});
}

$(function() {
	$("#post\\.resource\\.entrytype").change(function(e) {
		changeView($("#collapse").data("showAll"));	
	});
	
	$("#collapse").click(function(e){
		toggleView();
	});
	// load only, when extended fields are available                                                                                              
	//if (document.getElementById("post.resource.publisher")) toggleView();
	toggleView();
	
	var hash = $("#post\\.resource\\.interHash").val();
	if(hash == -1 || hash == undefined)
		return;
	$.ajax({
		url: '/json/bibtex/1' + hash + "?items=100",
		dataType: "jsonp",
		success: function (data) {
		if(data.items != undefined) 
			buildGoodPostSuggestion(data);
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
	var postResource = "post.resource.";
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
		var inputField = $(inputFields[x]);
		var inputFieldName = inputField[0].name;

		/*
		 * field name starts with "post.resource" or it appears in the inputFieldMap
		 */
		if (inputFieldName.substring(0, postResource.length) == postResource || inputFieldMap[inputFieldName] != undefined) {
			var g = inputField[0].value.replace(u, "");
			var suggestions = new Array();
			var occurrences = new Array();
			var k = -1;
			/*
			 * loop over posts
			 */
			var fieldVal;
			
			for(var z = 0; json.items.length > z; z++) {
				var post = json.items[z];
				if(((fieldVal = post[inputFieldName.substring(postResource.length, inputFieldName.length)]) != undefined 
						|| (fieldVal = post[inputFieldMap[inputFieldName]])) && fieldVal.length > 0) {
					var name = "";
					if(typeof fieldVal == "object") {
						var delimiter = " "; 
						/*
						 * special handling for person names: 
						 * - join them using ", "
						 * - prepend the prefix (last name) to our first name chain if present
						 */
						if(inputFieldName == postResource + "author" || inputFieldName == postResource + "editor") {
							delimiter = ', ';
							var namePair = post[inputFieldName.replace(postResource, '')+"s"];
							for(var m = 0; m < namePair.length; m++) {
								name += ((namePair[m].last.length > 0)?namePair[m].last+((namePair[m].first.length > 0)?", ":""):"")+namePair[m].first+"\n";
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
			if (!suggestions.length || fieldVal === undefined || (suggestions.length == 1 
			&& g == ((name.length)?name.replace(u, ""):fieldVal.replace(u, "")))) continue;
			
			inputField.tooltip({
				trigger : 'focus',
				placement : 'top',
				title: getString('post.resource.suggestion.hint')
			}).tooltip('show');
			
			inputField.after('<span class="autocompletion fa fa-caret-down form-control-feedback"><!-- --></span>');
			inputField.popover('destroy');  //remove popover help
			
			
			/* we have a bijective mapping therefore (f:suggestion->occurrence) we sort our indices by descending order */
			var indices = sortIndices(occurrences);
			/* occurrences are sorted and aligned to the corresponding suggestions */
			/* if fieldvalue contains values, it is a different set of values we apply to the value label - this is true for the author and editor fields */
			var labels = $.map(suggestions, function(item, index) {
				var suggestion = suggestions[indices[index]];
				return {
					label : suggestion + " (" + occurrences[indices[index]] + ")",
					value : ((fieldValue.length == 0) ? suggestion : fieldValue[indices[index]])
				};
			});
			inputField.autocomplete(
					{
						source : labels,
						minLength : 0,
						delay : 0
					}
			).bind("focus", 
				function(){
					$(this).autocomplete("search", "");
				}
			);
			applyKeyDownHandler(inputField);
		}
	} // loop over input fields
}
/* ugly hack to keep the up and down key working if no suggestions are shown */
function applyKeyDownHandler(element) {
	var widget = element.autocomplete("widget");
	var keyHandler = function (e) {
		var p = widget.hasClass("ui-autocomplete-disabled");
		if((e.keyCode == 38 || e.keyCode == 40)
		&& !p
		&& widget[0].style.display == 'none') {
			element.autocomplete( "disable" );
		} else if(p)
			element.autocomplete( "enable" );
	};
	element
	.bind( "autocompleteopen", function(event, ui) {
		widget.prepend("<li class='fsInputRecoHelp'>" + getString("post.resource.suggestion.help") + "</li>");
	})
	.bind("keydown",function(e) {keyHandler(e);});
}
