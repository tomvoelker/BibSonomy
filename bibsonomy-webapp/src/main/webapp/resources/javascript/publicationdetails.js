function loadSelectedPage() {
	var dropDownExternalMenu = document.getElementById('dropDownExternal');
	var index = dropDownExternalMenu.selectedIndex;
	if (index != 0) {
		window.open(dropDownExternalMenu.options[index].value);
	}
}

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
	$('#citation').tabs().tabs('option', 'active', 1).tabs({
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
	
	// FIXME: this should not be done using javascript
	$(".tabs, .tabs .tab > li").removeClass("ui-corner-all ui-corner-top ui-corner-bottom");
	
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
            var h = content.substr(showChar, content.length - showChar);
 
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
    
    $(".removeDocBtn").click(function(event) {
    	
    	event.preventDefault();
    	
    	var button = $(this);
		
    	$.get($(this).attr("href"), {}, function(data) {
			var status=$("status", data).text();
			if(status=="error") {
				alert($("reason", data).text());
			} else {
				alert($("response", data).text());
				$(button).parentsUntil("li").parent().remove();
			}
		}, "xml");
		
		return false;
	});
    
	/*
	 * handler for press rename button 
	 */
	$(".renameBtn").click(function(event) {
		
		event.preventDefault();
		var field = $(this).prev();
		var okBtn = $(this).next();
		field.focus();
		field.select();
		$(this).hide();
		okBtn.show();
		
		return false;
	});
	
	/*
	$(".renameDocInput").focusout(function() {
		$(this).next().next().hide();
		$(this).next().show();
	});
	*/
	$(".renameDocInput").focusin(function() {
		
		$(this).next().hide();
		$(this).next().next().show();
		return false;
	});
	
	$(".renameDocForm .okBtn").click(function(event) {
		event.preventDefault();
		renameSelected($(this).parent());
		
		return false;
	});
	
	$(".renameDocForm").submit(function(event) {
		event.preventDefault();
		renameSelected($(this));
		
		return false;
	});
	
	$("li.document:gt(0)").removeClass('active');
	
	$("#previewSelectBullets a.bullet").click(function(event){
		
		event.preventDefault();
		
		var id = $(this).attr('rel');
		
		$(".bibtexpreviewimage").each(function(index) {
			if($(this).is(':visible')) {
				$(this).hide();
			}
		});
		
		$("#"+id).show();
		
		//remove class active 
		$("#previewSelectBullets a.bullet.active, li.document.active").removeClass('active');
		
		//add class active to new active elements
		$(this).addClass('active');
		$("li.document."+id).addClass('active');
		return false;
	});
	
	$('#showMoreUser').click(function(){
		
		if($('.moreUser').is(':visible')) {
			$(this).find('span').text('more');
		} else {
			$(this).find('span').text('less');
		}
		$('.moreUser').toggle();
	});
	
	$('input[type=file]').click();
	
});

function addDocument() {
	/*
	 * load jQuery form plugin which is needed for ajaxSubmit()
	 * 
	 * FIXME: ensure that script is loaded before user can do something
	 */
	$.getScript('/resources/jquery/plugins/form/jquery.form.js');
	
	/*
	 * when upload form already exists, remove it 
	 */
	if ($("#upForm").length) $("#upForm").remove();
	
	/*
	 * remove rename form if it already exists 
	 */
	if ($("#renameForm").length) $("#renameForm").remove();

	/*
	 * build upload form
	 */
	var upForm = "<form id='upForm' action='" + $(this).attr('href') + "' method='POST' enctype='multipart/form-data'>" + 
	"<input id='upFile' type='file' name='file'/></form>";

	/*
	 * append form
	 */
	var inputDiv = $("#inputDiv");
	if (inputDiv.length) {
		/*
		 * on /bibtex/... pages
		 */
		inputDiv.append($(upForm));
	} else {
		/*
		 * on other pages 
		 * 
		 * this = a
		 * parent = headline DIV
		 */
		$(this).parent().append($(upForm));
	}

	/*
	 * attach handler that is called when the user selected a file
	 */
	$("#upFile").change(fileSelected);
	$("#upFile").click();
	return false;
}


/**
 * handle answer of file upload ajax-request
 * 
 * @param data
 */
function uploadRequestSuccessful(data) {
	var status = $("status", data).text();

	/*
	 * remove progress icon and upload form
	 */
	var upParent = $("#upProgress").parent(); // used below to reach document icon
	$("#upProgress").remove();
	$("#upForm").remove();

	if (status == "error") {
		alert($("reason", data).text());
		return;
	}
	if (status == "ok") {
		var fileHash  = $("filehash", data).text();
		var intrahash = $("intrahash", data).text();
		var fileName  = $("filename", data).text();
		
		var href = "/bibtex/" + intrahash + "/" + encodeURIComponent(currUser);
		
		document.location.href=$(location).attr('href');
	}
}

/*
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
	obj.find('input').blur();
	obj.find('.okBtn').hide();
	obj.find('.renameBtn').show();
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
	var newDelHref = toRename.parent().find('.removeDocBtn').attr('href').replace(encodedOldName, encodedNewName);	
	
	toRename.parent().attr("action", newHref);
	toRename.parent().find('.removeDocLink').attr('href', newDelHref);
	toRename.parent().find('.removeDocLink').attr('data-filename', newName);
	
	/*
	 * print status 
	 */
	alert(response);
	
	return;
}
