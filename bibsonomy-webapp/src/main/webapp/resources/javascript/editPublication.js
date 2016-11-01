//methods for editPublication page
//setup jQuery to update recommender with form data

var tagRecoOptions = {
		type: "POST",
		url: '/ajax/getPublicationRecommendedTags',
		data: $('#postForm').serialize(),
		dataType: "json",
		success : function showResponse(responseText, statusText) {
			handleRecommendedTags(responseText);
		}
	}; 

var getFriends = null;
var fields = ["booktitle","journal","volume","number","pages","publisher","address",
		"month","day","edition","chapter","key","type","annote","note",
		"howpublished","institution","organization",
		"school","series","crossref","misc"];

var inproceedingsField = ["publisher","booktitle","volume","number","series","pages","address","month","organization","note"];

var requiredForType = {
        "article":["journal","volume","number","pages","month","note"],
        "book":["publisher","volume","number","series","address","edition","month","note"],
        "booklet":["howpublished","address","month","note"],
        "conference":inproceedingsField,
        "inbook":["chapter","pages","publisher","volume","number","series","type","address","edition","month","note"],
        "incollection":["publisher","booktitle","volume","number","series","type","chapter","pages","address","edition","month","note"],
        "inproceedings":inproceedingsField,
        "manual":["organization","address","edition","month","note"],
        "masterthesis":["school","type","address","month","note"],
        "misc":["howpublished","month","note"],
        "phdthesis":["school","address","type","month","note"],
        "proceedings":["publisher","volume","number","series","address","month","organization","note"],
        "techreport":["institution","number","type","address","month","note"],
        "unpublished":["note"]
}

/* update view when user selects another type of publication in list */
function changeView(showAll) {
	var requiredFields = requiredForType[document.getElementById('post.resource.entrytype').value];
	var message = getString('post.resource.fields.detailed.show.all');
	var noRequiredFields = (requiredFields === undefined); 
	var collpase = document.getElementById('collapse');
	
	if (showAll || noRequiredFields) {
		requiredFields = fields;
		if (noRequiredFields) {
			$(collapse).parent().addClass("hidden");
		} else {
			message = getString('post.resource.fields.detailed.show.required');
			$(collapse).parent().removeClass("hidden");
		}
	} else {
		$(collapse).parent().removeClass("hidden");
	}
	
	collapse.firstChild.nodeValue = message;
	
	for (var i = 0; i < fields.length; i++) {
		var bibtexField = fields[i];
		var field = $("#post\\.resource\\." + bibtexField);
		var parent = field.closest(".form-group");
		if (showAll || in_array(requiredFields, bibtexField)) {
			parent.show();
		} else {
			if (!field.val()) { //fix: don't hide not empty fields
				parent.hide();
			}
		}
	}
	
	if (showAll) {
		$("#miscDiv").show();
	} else {
		if (!$("#post\\.resource\\.misc").val()) {
			$("#miscDiv").hide();
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


/*  
 * change appearance of misc and transfer data
 */
$(document).ready(function() {
	/*
	 * add misc fields in the beginning, so they don't show up if js is disabled
	 */
	$("#miscDiv").append('<div id="allFieldsWrap"><div id="extraFieldsWrap" class="form-group"><label class="col-sm-3 control-label" for="">misc fields</label><div><div class="col-sm-4"><input class="form-control" type="text" path=""/></div><div class="col-sm-4"><input class="form-control" type="text" path=""/></div><div class="col-sm-1"><button class="btn btn-sm" type="button" id="add_field_button">+</button></div></div></div><div id="standardFieldsWrap" class="form-group"></div></div>');
	$("#miscCheckboxDiv").append('<label><input type="checkbox" id="expertView" /> expert view</label>');
	
	/*
	 * variables
	 */
	var wrapper = $("#extraFieldsWrap"); //Fields wrapper
	var add_button = $("#add_field_button"); //Add button ID
	var misc = $("#post\\.resource\\.misc");
	var miscFieldValues = [];
	// important fields that are often used, use ["labelName", "fieldName"]
	var standardMiscFields = [["DOI", "DOI"]];
	
	/*
	 * functions
	 */
	function addInputs(){
		$(wrapper).append('<div class="extraInputs"><label class="col-sm-3"></label><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-4"><input class="form-control" type="text"/></div><div class="col-sm-1"><button class="btn btn-sm remove_field" type="button">-</form:button></div></div>'); 
	};
	
	function transferMiscFieldValuesToOldField(){
		var fieldString = [];
		for (var i = 0; i < miscFieldValues.length; i+=2){
			if (miscFieldValues[i] != "undefined" || miscFieldValues[i+1] != "undefined"){
				fieldString.push("  " + miscFieldValues[i] + " = {" + miscFieldValues[i+1] + "}");
			}
		}
		$(misc).val(fieldString.join(", \n"));
	}
	
	function transferDataFromOldToNew(){
		// gets the data from misc
		var miscVal = $(misc).val();
		var pairs = miscVal.split(",");
		var values = [];
		
		//split the pairs and delete the characters not needed then save data 
		$.each(pairs, function(index, item){
			item = item.trim();
			var itemValues = item.split(" = {");
			if (itemValues.length === 2){ //should have 2 values
				if (itemValues[0].substr(0,2) === "  "){ //formatting
					itemValues[0] = itemValues[0].substr(2, itemValues[0].length);
				}
				if (itemValues[1].charAt(itemValues[1].length-1) === "}"){ //formatting
					itemValues[1] = itemValues[1].substr(0, itemValues[1].length-1);
				}
				//show an empty input instead of "undefined"
				for(var i = 0; i < itemValues.length; i++){
					if (itemValues[i] === "undefined"){
						itemValues[i] = "";
					}
				}
			}
			values = values.concat(itemValues);
		});

		//set length to 0, so no new line is added
		if (values.length === 1){
			values = [];
		}
		
		for(var i = 0; i < values.length; i+=2){
			var isStandardField = false;
			$("#standardFieldsWrap :input[type=text]").each(function(){
				if ($(this).attr("name") === values[i]){
					$(this).val(values[i+1]);
					isStandardField = true;
				}
			});
			if (!isStandardField){
				$("#extraFieldsWrap > div:last > div:eq(0) > input").val(values[i]);
				$("#extraFieldsWrap > div:last > div:eq(1) > input").val(values[i+1]);
				addInputs();
			}
		}
	}
	
	//adds fields, that have a special input
	function addStandardFields(){
		for (var i = 0; i < standardMiscFields.length; i++){
			var standardField = standardMiscFields[i];
			$("#standardFieldsWrap").append('<div class="standardInputs"><label class="col-sm-3 control-label">' + standardField[0] + '</label><div class="col-sm-9"><input name="' + standardField[1].toLowerCase() + '"class="form-control" type="text"/></div></div>');
		}
	}
	
	function refreshOldView(){
		miscFieldValues = [];
		
		//extra fields
		$("#extraFieldsWrap :input[type=text]").each(function(){
			/*
			 * pushes the value of each text-input if it is not empty after deleting every whitespace
			 * else pushes "undefined"
			 */
			if ($(this).val().replace(/\s+/g, '') != ""){
				miscFieldValues.push($(this).val());
			}else{
				miscFieldValues.push("undefined");
			}
		});
		
		//standard fields
		$("#standardFieldsWrap :input[type=text]").each(function(){
			/*
			 * pushes the name, then the value if not empty, else "undefined"
			 */
			miscFieldValues.push($(this).attr("name"));
			if ($(this).val().replace(/\s+/g, '') != ""){
				miscFieldValues.push($(this).val());
			}else{
				miscFieldValues.push("undefined");
			}
		});
		transferMiscFieldValuesToOldField();
	};
	
	
	/*
	 * after loading
	 */
	addStandardFields();
	//transfer Data after loading, so the values of the potentially filled old view are shown
	transferDataFromOldToNew();
	//hides old view
	$(misc).parent("div").parent("div").addClass("hidden");
	
	//on add input button click
	$(add_button).click(function(e){ 
		e.preventDefault();
		addInputs();
	});
	
	
	//user click on remove button
	$(wrapper).on("click",".remove_field", function(e){ 
		e.preventDefault();
		$(this).parent('div').parent('div').remove(); 
		
		//this refreshes the values in the array/old misc-field
		refreshOldView();
	});
	
	//transfer field values of new design to array
	$("#allFieldsWrap").focusout(refreshOldView);
	
	//change view to old or new
	$("#expertView").change(function(){
		if (this.checked){
			// old/expert view
			refreshOldView();
			$("#allFieldsWrap").addClass("hidden");
			$(misc).closest(".form-group").removeClass("hidden");
			$(".extraInputs").remove();
			$("#standardFieldsWrap :input[type=text]").each(function(){
				$(this).val("");
			});
		}else{
			// new/normal view
			transferDataFromOldToNew();
			
			//actually changes the view
			$(misc).closest(".form-group").addClass("hidden");
			$("#allFieldsWrap").removeClass("hidden");
		}
	});
	
});