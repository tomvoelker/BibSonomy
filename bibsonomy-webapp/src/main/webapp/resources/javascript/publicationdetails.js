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
	
	$.getScript('/resources/jquery/plugins/form/jquery.form.js');
	
	/*
	 * tabs for citation style
	 */
	$('#citation').tabs().tabs('select', 1).tabs({
		disabled: [0],
		spinner: "",
		cache: true,
	    select: function(event, ui) {
		 	var $panel = $(ui.panel);
		 	if ($panel.is(":empty")) {
		 		$panel.append(getString("bibtex.citation_format.loading"));
		 	}
	    }
		           }); //{event: "mouseover"} // only nice with fixed height :(

    $( ".tabs, .tabs .tab > li" ).removeClass( "ui-corner-all ui-corner-top ui-corner-bottom" );
    
    /*
     * (un)folding of boxes
     */
	$("a.foldUnfold").click(function(e){
		e.preventDefault();
		var content = $($(this).attr("href"));
		if(content.is(":visible")){
			$(this).find("img").replaceWith($("img#icon_expand").clone().removeAttr('id'));
			content.hide();
		} else {
			$(this).find("img").replaceWith($("img#icon_collapse").clone().removeAttr('id'));
			content.show();
		}
	});
	
    var showChar = 350;
    var dots = "&hellip;";
    var moretext = "more";
    var lesstext = "less";
    $('.more').each(function() {
        var content = $(this).html();
 
        if(content.length > showChar) {
 
            var c = content.substr(0, showChar);
            var h = content.substr(showChar-1, content.length - showChar);
 
            var html = c + '<span class="moreellipses">' + dots + ' </span><span class="morecontent"><span>' + h + '</span> <a href="" class="morelink">' + moretext + '</a></span>';
            $(this).html(html);
        }
 
    });
 
    $(".morelink").click(function(){
        if($(this).hasClass("less")) {
            $(this).removeClass("less");
            $(this).html(moretext);
        } else {
            $(this).addClass("less");
            $(this).html(lesstext);
        }
        $(this).parent().prev().toggle();
        $(this).prev().toggle();
        return false;
    });
    
    $(".removeDocLink").click(function() {
    	var button = $(this);
		$.get($(button).attr("href"), {}, function(data) {
			var status=$("status", data).text();
			if (status=="error") {
				alert($("reason", data).text());
			} else {
				alert($("response", data).text());
				$(button).parentsUntil("li").parent().remove();
			}
		}, "xml");
		
		return false;
	});
    
	/*
	 * handler for presses enter button 
	 *
	$(".renameDocInput").keypress(function(event) {
		if(event.which == 13) {
			//renameSelected($(this));
			return false;
		}
	});
	*/
	$(".renameDocForm").submit(function(event) {
		event.preventDefault();
		renameSelected($(this));
		$(this).find('input').blur();
		return false;
	});
	
});

	
/**
 * renames a document
 */
function renameSelected(obj) {
	/*
	 * get the documents type to check wether
	 * the new ending is equals the old
	 */
	var type = obj.find(".showName").text().split(".");
	type = type[type.length-1];
	var renameForm = obj;
	
	var fileName = $.trim(obj.find('.renameDocInput').val()); //get value of the rename field
	
	var newFileType = fileName.split(".");
	var length = newFileType.length;
	newFileType = newFileType[newFileType.length-1];
	var fileExist = false;
	
	/*
	 * check wether the file-type is consistent
	 */
	if(!checkConsistency(type, newFileType, length)) {
		return false;
	}

	//check wether a file with this name already exists
	$(".renameDocInput").each(function() {
		var name = $.trim($(this).parent().find('.showName').val());
		if (name == fileName) {
			fileExist = true;
		}
	});
	
	//file already exists
	if (fileExist) {
		alert(getString("post.bibtex.fileExists"));
		//$(renameForm).remove();
		return false;
	}
	
	renameForm.val(decodeURI(renameForm.val()).replace(/&/g, "%26"));
	
	//do an ajaxsubmit of the renameForm
	$(renameForm).ajaxSubmit({
		dataType: "xml",
		success: renameRequestSuccess	
	});
}


function checkConsistency(oldSuffix, newSuffix) {
	/*
	 * check for each suffix-pair for which it is allowed to get 
	 * converted if it fit's to the new file's suffix
	 */
	if(oldSuffix.toLowerCase() != newSuffix.toLowerCase()) {
		var equalSuffixes = ["jpg-jpeg-png-tif-tiff", "pdf-ps", "djv-djvu", "txt-tex",
		                     "doc-docx-ppt-pptx-xls-xlsx", "ods-odt-odp", "htm-html"];
		var currentLeftSuffix, currentRightSuffix;
		var check = false;
		var testForRename = false;
				
		$.each(equalSuffixes, function(index, item) {
			var suffixes = item.split("-");
			var length = suffixes.length;
			for(var i = 0; i < length; i++) {
				for(var j = i; j < length; j++) {
					currentLeftSuffix = item.split("-")[i];
					currentRightSuffix = item.split("-")[j];
					if(oldSuffix.toLowerCase() ==  currentLeftSuffix && newSuffix.toLowerCase() == currentRightSuffix) {
						check = confirm(getString("post.bibtex.changeFiletype"));
						testForRename = true;
					} else if (oldSuffix.toLowerCase() ==  currentRightSuffix && newSuffix.toLowerCase() == currentLeftSuffix) {
						check = confirm(getString("post.bibtex.changeFiletype"));
						testForRename = true;
					}
				}
			}
		});
		
		/*
		 * user want's to save a new consistent suffix
		 */
		if(check) {
			return true;
		/*
		 * filetype is inconsitent, rename is not possible
		 */
		} else if(!check && !testForRename) {
			alert(getString("post.bibtex.inconsistentFiletype"));
			return false;
		}
	} else {
		return true;
	}
}


/**
 * handles the answer of the rename request
 * @param data
 */
function renameRequestSuccess(data) {
	var status = $("status", data).text();
	
	/*
	 * remove rename form
	 */
	$("#renameForm").remove();
	
	if (status == "error") {
			alert($("reason", data).text());
		return;
	}
	
	/*
	 * get response data
	 */
	var oldName = $("oldName", data).text();
	var newName = $("newName", data).text();
	var response = $("response", data).text();
	
	/*
	 * find and update all spans, containing old filenames
	 */
	var toRename = $('span.showName').filter(function(index) {
		return $(this).text() == oldName;
	});
	
	toRename.text(newName);
	
	/*
	 * change action uri of form
	 */
	var encodedOldName = encodeURIComponent(oldName).replace(/%20/g, '+');
	var encodedNewName = encodeURIComponent(newName).replace(/%20/g, '+');
	var newHref = toRename.parent().attr("action").replace(encodedOldName, encodedNewName);	
	var newDelHref = toRename.parent().next().attr('href').replace(encodedOldName, encodedNewName);	
	
	toRename.parent().attr("action", newHref);
	toRename.parent().next().attr('href', newDelHref);
	toRename.parent().next().attr('data-filename', newName);
	
	/*
	 * print status 
	 */
	alert(response);
	
	return;
}
