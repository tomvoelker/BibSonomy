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

	$("#post\\.resource\\.title").blur(function() {
		window.setTimeout(function() {
			$("#suggestionBox").hide();
		},
		140);
	});

	if(partialTitle.length < 2 || partialTitle.length%2 != 0) {
		$("#suggestionBox").hide();
	} else {
		var query = $.ajax({
			type: "GET",
			url: "/json/tag/sys:title:"+partialTitle+"*",
			dataType: "json",
			success: function(json){processResponse(json);}});
	}
}

/**
 * Process the JSON Data and make visible to the user
 * 
 * @param data
 * @return
 */
function processResponse(data) {
	var k = 1;
	// if there's no data cancel
	if(data.items.length == 0) {
		return;
	}

	var p = $("<div style=\"background-color: #006699; color: #FFFFFF; padding:3px;\">Suggestions</p>");
	$("#suggestionBox").html(p);
	
	$.each(data.items, function(i, item) {
		
		var element = $('<div style="background-color:'+
		(((k++)%2 == 0)?'#FFFFFF':'#AAAAAA')+ // change the background color every step
		'>'+item.label+'</div>');
		
		element.addClass("listEntry");
		element.click(
				// get title sepcific data
				// an set the forms accordingly
				function () {
					//post.resource.entrytype
					$("#post\\.resource\\.entrytype option[value='"+item["pub-type"]+"']");
					$("#post\\.resource\\.editor").val(concatEditors(item.editor));
					$("#post\\.resource\\.year").val(item.year);
					$("#post\\.resource\\.title").val(item.label);
					$("#post\\.resource\\.author").val(item.author);
				}
		).mouseover(
				function () {
					var editors = "";
					
					if(item.editor != 'undefined' && item.editor.length > 0) {
						editors = concatEditors(item.editor);
					}
					
					if(item.author.length >= 27) {
						item.author = item.author.substr(0, 27)+" ...";
					}

					if(item.editor.length >= 20) {
						item.editor = item.editor.substr(0, 20)+" ...";
					}

					//item.editor.replace(/ AND/g,',');
					p.html("["+item["pub-type"]+"]"+item.author+"("+item.year+"), "+editors).css("background-color","#222222");
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
	var pos = $("#post\\.resource\\.title").offset();
	var width = $("#post\\.resource\\.title").width();
	var top = parseInt(pos.top+$("#post\\.resource\\.title").height())+6;
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
 * create one-string representation of a list of editors
 * 
 * @param editors array of editors
 * @return one string, containing concatenation of all editors, separated by '\n'
 */
function concatEditors(editors) {
	var retVal = "";
	var editor;
	for(editor in editors) {
		retVal += editors[editor] + "\n";
	}

	return retVal;
}
