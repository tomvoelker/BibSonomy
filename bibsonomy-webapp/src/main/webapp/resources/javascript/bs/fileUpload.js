var documentUploadSuccessIcon = "/resources/image/document-txt-blue.png";
/*
 * file upload for publication references
 * 
 */


/*
 * add handler to delete, add and rename documents
 */
$(function() {
	var documentUploadTitle = getString("bibtex.actions.private_document.upload.title");
	$(".deleteDocument").click(deleteLinkClicked);
	$(".addDocument").click(addDocument).attr("title", documentUploadTitle);
	$(".renameDoc").click(renameClicked);
});

/**
 * rename has been clicked, show rename form and trigger rename
 */
function renameClicked() {
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
	
	var currentName;
	if ($("#renameForm").length) {
		var temp = $("#showName").text().split(" ");
		$.each(temp, function(index, item) {
			if(item.indexOf(".") >= 0) {
				currentName = item.substring(0, item.length-1);
			}
		});
	}
	
	/*
	 * remove rename form if it already exists 
	 */
	if ($("#renameForm").length) $("#renameForm").remove();
	/*
	 * remove text of renamelabel on editPublication-pages if it's still set
	 */
	if(!($("#showName").text() === "")) {
		$("#showName").text("");
	}

	/*
	 * build rename form
	 */
	var name = $(this).data('filename');
	
	/*
	 * the same rename button was clicked twice, so hide the rename form and do nothing 
	 */
	if (currentName && currentName == name) {
		return false;
	}
	
	/*
	 * on the edit publication page a label is already available,
	 *  on the default view-publication page it's not
	 */
	if($("#showName").length) {
		$("#showName").text(getString("post.bibtex.renameTitle") +" " +name);
		var renameForm = "<form id='renameForm' action='" + $(this).attr('href') + "' method='POST' enctype='multipart/form-data' autocomplete='off'>" + 
		 "<input id='renameFormTxt' type='text' name='newFileName' value='" +name.replace(/'/g, "&apos;") +"'/>" +
				" <input id='renameBtn' type='button' value='"+ getString("post.bibtex.btnRename") +"' /></form> ";
	} else {
		var renameForm = "<form id='renameForm' action='" + $(this).attr('href') + "' method='POST' enctype='multipart/form-data' autocomplete='off'>" + 
		"<p id='showName'>" +getString("post.bibtex.renameTitle") +" " +name +":</p> <input id='renameFormTxt' type='text' name='newFileName' value='" +name.replace(/'/g, "&apos;") +"' />" +
				" <input id='renameBtn' type='button' value='"+ getString("post.bibtex.btnRename") +"' /></form> ";
	}

	/*
	 * append form
	 */
	var inputDiv = $("#inputDiv");
	if (inputDiv.length) {
		/*
		 * on /bibtex/... pages
		 */
		inputDiv.append($(renameForm));
	} else {
		/*
		 * on other pages 
		 * 
		 * this = a
		 * parent = headline DIV
		 */ 
		$(this).parent().append($(renameForm));
	}

	/*
	 * attach handler that is called when the user selected a file
	 * Click handler
	 */
	$("#renameBtn").click(renameSelected);
	/*
	 * handler for presses enter button 
	 */
	$("#renameFormTxt").keypress(function(event) {
		if(event.which == 13) {
			renameSelected();
			return false;
		}
	});

	return false;
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
 * renames a document
 */
function renameSelected() {
	/*
	 * get the documents type to check wether
	 * the new ending is equals the old
	 */
	var temp = $("#showName").text().split(" ");
	var type;
	$.each(temp, function(index, item) {
		if(item.indexOf(".") >= 0) {
			type = item.split(".");
			type = type[type.length-1].replace(":","");
		}
	});
	
	var renameForm = $("#renameForm");
	var fileName = $("#renameFormTxt").val(); //get value of the rename field
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
	$(".documentFileName").each(function() {
		var name = $.trim($(this).text());
		if (name == fileName) {
			fileExist = true;
		}
	});
	
	//file already exists
	if (fileExist) {
		alert(getString("post.bibtex.fileExists"));
		$(renameForm).remove();
		return;
	}
	
	renameForm.val(decodeURI(renameForm.val()).replace(/&/g, "%26"));
	
	//do an ajaxsubmit of the renameForm
	$(renameForm).ajaxSubmit({
		dataType: "xml",
		success: renameRequestSuccessful
	});
}

/**
 * deletes a document
 * 
 * @return
 */
function deleteLinkClicked () {
	if (!confirmDeleteByUser("document")) return false;
	
	var button = $(this);
	$.get($(button).attr("href"), {}, function(data) {
		var status=$("status", data).text();
		if (status=="error") {
			alert($("reason", data).text());
		} else {
			var test = $(button).next(".documentFileName").text();
			alert($("response", data).text());
			$(button).parent("li").remove();
		}
	}, "xml");
	return false;
}

/**
 * @return
 */
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

	return false;
}

/**
 * is called when the user has selected a file
 * 
 * @param obj
 * @return
 */
function fileSelected(obj) {
	var upForm = $("#upForm");
	var fileName = $(obj.currentTarget).val().replace("C:\\fakepath\\", ""); // remove fakepath for Opera, Chrome, IE
	var fileExist = false;
	
	$(".documentFileName").each(function() {
		var name = $.trim($(this).text());
		if (name == fileName) {
			fileExist = true;
		}
	});

	if (fileExist) {
		alert(getString("post.bibtex.fileExists"));
		$(upForm).remove();
		return;
	}
	$(upForm).ajaxSubmit({
		dataType: "xml",
		success: uploadRequestSuccessful	
	});
	
	/*
	 * add progress icon and hide form (XXX: don't yet remove form, otherwise upload fails!)
	 */
	$(upForm).after($(
			"<div id='upProgress' >" + fileName  + 
			"<img alt='uploading...' src='/resources/image/ajax_loader.gif' /></div>"		
	)).hide();

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
		
		var encodedFileName = encodeURIComponent(fileName).replace(/%20/g, '+');

		var documentUri = "/documents/" + intrahash + "/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(encodedFileName);
		var documentHelp = getString("bibtex.actions.private_document.download");
		//var documentImg = "<img src='" + documentUploadSuccessIcon +"' style='float: left;'/>";
		
		var documentQRMessage = getString("qrcode.actions.download");
		var params = [projectName];
		var documentQRHelp = getString("qrcode.info.embedderInfoMessage", params);
		
		
		
		/*
		 * on /bibtex pages we have a list where we add links to the files
		 */
		var filesUl = $("#files");

		/*
		 * file extension for qr code embedding
		 */
		var suffix = ".pdf";
		
		if (filesUl.length) {
			
			var inner = "";
			
			var aQrCode    = "<a class='documentFileName preview' href='" + documentUri + "?qrcode=true'" + " title='" + documentHelp + "'>";
			var aNoQrCode  = "<a class='documentFileName preview' href='" + documentUri + "?qrcode=false'" + " title='" + documentHelp + "'>";
			var imgPreview = "<img style='display:none;' class='pre_pic' src='" + documentUri + "?preview=SMALL' alt='" + fileName + "' />";
			var aDel       = "<a class='deleteDocument' href='/ajax/documents?intraHash=" + intrahash + "&fileName="+ encodedFileName + "&ckey=" + ckey + "&temp=false&action=delete'></a>";
			var aRename = "<a class='renameDoc' href='/ajax/documents?ckey="+ckey+"&temp=false&intraHash=" + intrahash + "&fileName="+
			encodedFileName +"&action=rename' data-filename='" + encodedFileName +"'></a>";
			/*
			 * check if file ends with '.pdf'
			 */			
			if (fileName.toLowerCase().indexOf(suffix, fileName.length - suffix.length) != -1) {
				inner = aQrCode + imgPreview + fileName + "</a> ( " + aNoQrCode + imgPreview + documentQRMessage + "</a>" + 
				"<div class='help' style='float:none'> <b class='smalltext' style=''>?</b><div>" + documentQRHelp + "</div></div>" +
				") " + aDel +aRename;
			} else {
				inner = aNoQrCode + imgPreview + fileName + "</a> " + aDel +aRename;
			}
			
			filesUl.find("li:last-child").before("<li>" + inner + "</li>");
			$(".deleteDocument").click(deleteLinkClicked);
			/*
			 * find the new added document(it's always the element at index n-1 in filesUl)
			 * and add listener
			 */
			filesUl.find("li").eq(filesUl.find("li").length-2).find(".renameDoc").click(renameClicked);
		} else {
			/*
			 * change document link
			 */
			var upA = upParent.children(".addDocument").first();
			upA.attr("href", documentUri);
			upA.attr("class", "preview");
			upA.attr("title", documentHelp);
			upA.unbind('click');
			if(documentImg!==undefined) upA.children().first().replaceWith($(documentImg));
		}
		
		return;
	}
}


/**
 * handles the answer of the rename request
 * @param data
 */
function renameRequestSuccessful(data) {
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
	 * find and update all links, containing old filenames
	 */
	var toRename = $('a:contains("' + oldName +'")').filter(function(index) {
		return $(this).text() == oldName;
	});
	
	var children = toRename.children();
	
	// XXX: java is encoding whitespace as +, javascript as %20 :(
	var encodedOldName = encodeURIComponent(oldName).replace(/%20/g, '+');
	var encodedNewName = encodeURIComponent(newName).replace(/%20/g, '+');
	$('a[href*="' + encodedOldName +'"]').each(function() {
		var newHref = $(this).prop("href").replace(encodedOldName, encodedNewName);
		$(this).prop("href", newHref);
		if($(this).attr("title") && !$(this).hasClass('renameDoc')) {
			$(this).prop("title", newName);
		}
		if($(this).attr("alt")) {
			$(this).prop("alt", newName);
		}
	});
	
	/*
	 * find and update all preview pictures which contain outdated filenames
	 */
	$('img[alt="'+ oldName +'"]').each(function() {
		if($(this).attr("href")) {
			var newHref = $(this).prop("href").replace(oldName, newName);
			$(this).prop("href", newHref);
		}
		if($(this).attr("alt")) {
			$(this).prop("alt", newName);
		}
	});
	
	toRename.text(newName);
	toRename.append(children);
	toRename.siblings().filter('a:last').data('filename', newName);
	
	/*
	 * print status 
	 */
	alert(response);
	
	return;
}