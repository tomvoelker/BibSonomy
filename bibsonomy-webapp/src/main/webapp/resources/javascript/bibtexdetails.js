function updatePrivNote(button) {
	var textArea = button.siblings("div.resizable-textarea").find("textarea[name='privateNote']");
	var newVal = textArea.val();
	var oldVal = button.siblings("input[name='oldprivatenote']").val();
	if (newVal == oldVal) {
		textArea.css('background-color', '#D8EBAE').animate({backgroundColor : '#ffffff'}, 1000);
		return false;
	}
	var formData = button.parent("form").serialize();
	$.ajax({
		url : "/ajax/updateprivatenote",
		data : formData,
		success : function(data, textStatus, jqXHR) {
			textArea.css('background-color', '#D8EBAE').animate({backgroundColor : '#ffffff'}, 1000);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			alert("error: " + errorThrown);
		}
	});
	return false;
}
$(function() {
	
	/*
	 * tabs for citation style
	 */

	$('#citation').tabs().tabs('select', 1).tabs({
		disabled: [0],
		spinner: "",
		cache: true,
	    select: function(event, ui) {
		 	var $panel = $(ui.panel);
//		 	if ($panel.is(":empty")) {
		 		$panel.append(getString("bibtex.citation_format.loading"));
//		 	}
	    }
		           }); //{event: "mouseover"} // only nice with fixed height :(

    $( ".tabs, .tabs .tab > li" ).removeClass( "ui-corner-all ui-corner-top ui-corner-bottom" );
    
    /*
     * (un)folding of boxes
     */
//	$("a.foldUnfold").click(function(e){
//		e.preventDefault();
//		var content = $($(this).attr("href"));
//		if(content.is(":visible")){
//			$(this).find("img").replaceWith($("img#icon_expand").clone().removeAttr('id'));
//			content.hide();
//		} else {
//			$(this).find("img").replaceWith($("img#icon_collapse").clone().removeAttr('id'));
//			content.show();
//		}
//	});
});

$(function() {
	
	/*
	 * tabs for citation style
	 */
	$('#citation').tabs().tabs('select', 1).tabs({
		disabled: [0],
		spinner: "",
		cache: true,
	    select: function(event, ui) {
		 	var $panel = $(ui.panel);
//		 	if ($panel.is(":empty")) {
		 		$panel.append(getString("bibtex.citation_format.loading"));
//		 	}
	    }
		           }); //{event: "mouseover"} // only nice with fixed height :(

    $( ".tabs, .tabs .tab > li" ).removeClass( "ui-corner-all ui-corner-top ui-corner-bottom" );
    
    /*
     * (un)folding of boxes
     */
//	$("a.foldUnfold").click(function(e){
//		e.preventDefault();
//		var content = $($(this).attr("href"));
//		if(content.is(":visible")){
//			$(this).find("img").replaceWith($("img#icon_expand").clone().removeAttr('id'));
//			content.hide();
//		} else {
//			$(this).find("img").replaceWith($("img#icon_collapse").clone().removeAttr('id'));
//			content.show();
//		}
//	});
});

