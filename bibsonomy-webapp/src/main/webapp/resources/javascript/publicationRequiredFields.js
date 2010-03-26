var form_name = '#post\\.resource\\.title';

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

	$(form_name).blur(function() {
		window.setTimeout(function() {
			$("#suggestionBox").hide();
		},
		140);
	});

	if(partialTitle.length > 1) {
		var query = $.ajax({
			type: "GET",
			url: "/json/tag/sys:title:"+partialTitle+"*?items=10",
			dataType: "json",
			success: function(json){
			processResponse(json);
		}});
		return;
	}
	$("#suggestionBox").hide();	
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
	//style="background-color: #006699; color: #FFFFFF; padding:3px;"
	var p = $('<div class="suggBox" style="background-color: #006699; color: #FFFFFF; padding:3px;">'
			+getString('post.resource.suggested')
			+'</div>');
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
	    var tags = concatArray(item.tags, null, '+');
	    var k = m;
		var element = 
	    $('<div class="suggestion_entry" style="cursor:pointer; color:#006699;background-color:'
	  		+(((m++)%2 == 0)?'#FFFFFF':'#EEEEEE') // change the background color every step
	  		+'">'+formatLabel(item.label)
	      +'<br><span style="font-size:10px;">'
	      +author
	      +year
	      +'</span></div>');
	
		element.attr('url', '/editPublication?hash='+intraHash+'&user='+i.user+'&copytag='+tags);
		element.click(
				// get title specific data
				// and set the forms accordingly
				function () {
		          window.location.href = '/editPublication?hash='+intraHash+'&user='+data.user+'&copytag='+tags;
		          $("#suggestionBox").hide();
				}
		);
 
	 $("#suggestionBox").append(element);
	})
	var pos = $(form_name).offset();
	var width = $(form_name).width();
	var top = parseInt(pos.top+$(form_name).height());
		
	if($("#suggestionBox").width() < (width+2)){
		$("#suggestionBox").width(width+2);
	}
	
	$("#suggestionBox").css(
			{
				"left":(pos.left+1)+"px",
				"top":top+"px",
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
 * @return one string, containing concatenation of all strings, separated by either '\n' or the supplied delimeter
 */
 
function concatArray(data, max_len, delim) {
	var retVal = "";
	var entry;
	if(delim == null) {
		delim = "\n";
	}
	for(entry in data) {
		retVal += data[entry] + delim;
	}
	return ((max_len != null) && (retVal.length > max_len))?retVal.substr(0, max_len)+"...":retVal;
}

/**
 * go one entry up/down in the list
 * 
 * @param keyup event
 * @return 
 */
 
function getArrowKey(e) {
	if(e.keyCode == 38){
			if(!$('.suggestion_entry_selected').is('div')) {
				$('div').last('.suggestion_entry').attr('class','suggestion_entry_selected');
			} else {
				var selected = $('.suggestion_entry_selected').attr('class','suggestion_entry');
				selected.prev('.suggestion_entry').attr('class','suggestion_entry_selected');
			} 
	} else if(e.keyCode == 40){
			if(!$('.suggestion_entry_selected').is('div')) {
				$('div').first('.suggestion_entry').attr('class','suggestion_entry_selected');
			} else {
				var selected = $('.suggestion_entry_selected').attr('class','suggestion_entry');
				selected.next('.suggestion_entry').attr('class','suggestion_entry_selected');
			}
	} else if(e.keyCode == 13 && $('.suggestion_entry_selected').is('div')){
				window.location.href = $('.suggestion_entry_selected').attr('url');
		          $("#suggestionBox").hide();
	}
}

/**
 * format the matching part of a string bold
 * 
 * @param label what to match our partial string to
 * @return a formatted string
 */
function formatLabel (label) {
	var pos = label.toUpperCase().indexOf($(form_name).val().toUpperCase());
	
    return label.substr(0, pos)
    +'<b>'
    +label.substr(pos, $(form_name).val().length)
    +'</b>'
    +label.substr(pos + $(form_name).val().length);
}

