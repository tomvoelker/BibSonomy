//queries the titles and further details of publications by a partial title
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
		        url: "/ajax/getPublicationsByPartialTitle.json?title="+partialTitle,
		        dataType: "json",
		        success: function(json){processResponse(json);}});
}
}
/** Process the JSON Data and make visible to the user
* 
*/	
function processResponse(data) {

	// if there's no data cancel
	if(data.items.length == 0) {
		return;
	}

	var p = $("<div style=\"background-color: #006699; color: #FFFFFF; padding:3px;\">Suggestions</p>");
	$("#suggestionBox").html(p);
	$.each(data.items, function(i, item) {
		var element = $("<div>"+item.title+"</div>");
		element.addClass("listEntry");
		element.click(
			// get title sepcific data
			// an set the forms accordingly
			function () {
				//post.resource.entrytype
				$("#post\\.resource\\.entrytype option[value='"+item.entry_type+"']");
				$("#post\\.resource\\.editor").val(item.editor);
				$("#post\\.resource\\.year").val(item.year);
				$("#post\\.resource\\.title").val(item.title);
				$("#post\\.resource\\.author").val(item.author);
			}
		).mouseover(
			function () {
				if(item.author.length >= 27) {
					item.author = item.author.substr(0, 27)+" ...";
				}
				
				if(item.editor.length >= 20) {
					item.editor = item.editor.substr(0, 20)+" ...";
				}
				
				item.editor = item.editor.replace(/ AND/g,',');
				p.html("["+item.entry_type+"]"+item.author+"("+item.year+"), "+item.editor).css("background-color","#222222");
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