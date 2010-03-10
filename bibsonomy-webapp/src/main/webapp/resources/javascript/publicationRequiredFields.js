
/**
 * queries the titles and further details of publications by a partial title
 *
 * @param partialTitle prefix of a title word
 * @param autocompletion boolean flag - true if autocompletion is on
 * @return
 */
function getSuggestions(partialTitle, autocompletion) {
	if(!autocompletion) {
		return;
	}

	$("#title").blur(function() {
		window.setTimeout(function() {
			$("#suggestionBox").hide();
		},
		140);
	});

	if(partialTitle.length%2 == 0 && partialTitle.length < 2) {
		$("#suggestionBox").hide();
	} else {
		var query = $.ajax({
			type: "GET",
			url: "/json/tag/sys:title:"+partialTitle+"*",
			dataType: "json",
			success: function(json){
			processResponse(json);
      }});
	}
}

/**
 * Process the JSON Data and make it visible to the user
 * 
 * @param data contains the posts returned for the specific title entered
 * @return
 */
function processResponse(data) {
	var m = 1;
	var s = 0;
	// if there's no data abort
	if(data.items.length == 0) {
		return;
	}

	var p = $('<div style="background-color: #006699; color: #FFFFFF; padding:3px;">Suggestions</p>');
	$("#suggestionBox").html(p);
	
	$.each(data.items, function(i, item) {

		var editors = "";
		var author = "";
		var year = "";
		if(item.editor != 'undefined') {
			editors = concatArray(item.editor, 20);
		}
					
		if(item.author != 'undefined') {
			author = concatArray(item.author, 27);
		}

		if(item.year != 'undefined') {
			year = '('+item.year+')';
		}
    var intraHash = item.intraHash;
    var k = m;
		var element = 
    $('<div style="color:#006699;background-color:'
  		+(((m++)%2 == 0)?'#FFFFFF':'#EEEEEE') // change the background color every step
  		+'">'+formatLabel(item.label)
      +'<br><span style="font-size:10px;">'
      +author
      +year
      +'</span></div>');

		element.click(
				// get title specific data
				// and set the forms accordingly
				function () {
          window.open('/intrahash='+intraHash);
          $("#suggestionBox").hide();
				}
		);
 
	 $("#suggestionBox").append(element);
	})
	var pos = $("#title").offset();
	var width = $("#title").width();
	var top = parseInt(pos+$("#title").height())+6;
	$("#suggestionBox").css(
			{
				"left":(pos.left+1)+"px",
				"top":top+"px",
				"min-width":(width+2)+"px",
				"background-color":"#FFFFFF",
				"z-index":"999",
				"border":"1px solid #006699",
				"border-left":"3px solid #006699"
			}
	);
	$("#suggestionBox").show();
}

/**
 * create one-string representation of a list of strings
 * 
 * @param data array of strings
 * @param max_len return the representing string cut down to the size of max_len
 * @param delim  
 * @return one string, containing concatenation of all strings, separated by '\n'
 */
 
function concatArray(data, max_len, delim) {
	var retVal = "";
	var entry;
	for(entry in data) {
		retVal += data[entry] + "\n";
	}
	return ((max_len != null) && (retVal.length > max_len))?retVal.substr(0, max_len)+"...":retVal;
}

/**
 * format the matching part of a string bold
 * 
 * @param label what to match our partial string to
 * @return a formatted string
 */
function formatLabel (label) {
		var pos = label.toUpperCase().indexOf($("#title").val().toUpperCase());
    return label.substr(0, pos)
    +'<b>'
    +label.substr(pos, $("#title").val().length)
    +'</b>'
    +label.substr(pos + $("#title").val().length);
}

