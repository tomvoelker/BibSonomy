var documentUploadSuccessIcon = "/resources/image/document-txt-blue.png";
/*
 * file upload for publication references
 * 
 */


/*
 * add handler to delete, add and rename documents
 */
$(function() {
	// TODO: move somewhere else
	$.getScript('/resources/jquery/plugins/form/jquery.form.js');
	
	var documentUploadTitle = getString("bibtex.actions.private_document.upload.title");
	$(".deleteDocument").click(deleteLinkClicked);
	$(".addDocument").click(addDocument).attr("title", documentUploadTitle);
	$(".rename-btn").each(function(i, el) {
		
		var inputGroup = $(this).closest(".input-group");
		var inputGroupButtons = $(this).parent(".input-group-btn");
		var form = inputGroupButtons.closest("form");
		var renameInput = inputGroup.children(".renameDocInput");
		var renameButton = $(this);
		var deleteForm = inputGroupButtons.children(".remove-btn");
		
		var o = {
				renameButton:renameButton,
				form:form,
				deleteForm:deleteForm,
				renameInput:renameInput
		};
		
		renameButton.click(function(e) {
			renameFile(o);
		});
		
		form.submit(function(e) {
			e.preventDefault();
			renameFile(o);
			return false;
		});
	});
	
});

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
function renameSelected(button) {
	/*
	 * get the documents type to check wether
	 * the new ending is equals the old
	 */
	var entry = $(button).closest(".input-group").children(".renameDocInput").addClass("current-form");
	var temp = entry.val().split(" ");
	var type;
	$.each(temp, function(index, item) {
		if(item.indexOf(".") >= 0) {
			type = item.split(".");
			type = type[type.length-1].replace(":","");
		}
	});
	
	var fileName = entry.data("filename"); //get value of the rename field
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
		if($(this).hasClass("current-form")) {
			$(this).removeClass("current-form")
			return;
		}
		
		var name = $.trim($(this).text());
		if (name == fileName) {
			fileExist = true;
		}
	});
	
	//file already exists
	if (fileExist) {
		alert(getString("post.bibtex.fileExists"));
		return;
	}
	
	entry.val(decodeURI(entry.val()).replace(/&/g, "%26"));
	
	//do an ajaxsubmit of the renameForm
	$("#renameBtn").ajaxSubmit({
		dataType: "xml",
		success: function(data) {
			$(button).addClass("btn-success");
			renameRequestSuccessful(data);
		}
	
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
			$(button).closest("li").remove();
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
		success: function(data){
			uploadRequestSuccessful(data);
		}
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
		
		var copyData = $("#file-upload-placeholder").clone();
		copyData.attr("id", "");
		var encodedFileName = encodeURIComponent(fileName).replace(/%20/g, '+');
		var inputGroup = copyData.find(".input-group").filter(":first");
		var inputGroupButtons = copyData.find(".input-group-btn").filter(":first");

		var documentUri = "/documents/" + intrahash + "/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(encodedFileName);
		var documentHelp = getString("bibtex.actions.private_document.download");
		
		var documentQRMessage = getString("qrcode.actions.download");
		var params = [projectName];
		var documentQRHelp = getString("qrcode.info.embedderInfoMessage", params);
		var form = inputGroup.parent();
		var downloadForm = inputGroup.children(".download-btn").filter(":first").data("filename", encodedFileName).attr("href", documentUri + "?qrcode=false").attr("title", documentHelp);
		var qrForm = inputGroupButtons.children(".qrcode-btn").data("filename", encodedFileName).data("filename", encodedFileName).attr("href", documentUri + "?qrcode=true").attr("title", documentHelp);
		var renameInput = inputGroup.children(".renameDocInput").val(fileName).data("filename", fileName);
		var renameButton = inputGroupButtons.children(".rename-btn");
		var deleteForm = inputGroupButtons.children(".remove-btn").attr("href", "/ajax/documents?intraHash=" + intrahash + "&fileName="+ encodedFileName + "&ckey=" + ckey + "&temp=false&action=delete").attr("title", getString("delete")).data("filename", fileName);
		var imgPreview = inputGroup.children(".pre_pic").attr("src", documentUri + "?preview=SMALL").attr("alt", fileName);
		
		/*
		 * on /bibtex pages we have a list where we add links to the files
		 */
		var filesUl = $(".edit-document-forms").filter(":first");

		/*
		 * file extension for qr code embedding
		 */
		var suffix = ".pdf";
		var o = {
				renameButton:renameButton,
				form:form,
				deleteForm:deleteForm,
				renameInput:renameInput
		};
		form.attr("action", form.attr("action").replace("FILENAME_PLACEHOLDER", fileName));
		
		if (!filesUl.length) {
			//if(documentImg!==undefined) upA.children().first().replaceWith($(documentImg));
		}
			
			
			/*
			 * check if file ends with '.pdf'
			 */
		if (fileName.toLowerCase().indexOf(suffix, fileName.length - suffix.length) == -1) qrForm.hide();
			
			/*
			 * find the new added document(it's always the element at index n-1 in filesUl)
			 * and add listener
			 */
		filesUl.find(".bibtexpreviewimage").after(copyData);
		
		renameButton.click(function(e){
			renameFile(o);
		});
		
		form.submit(function(e) {
			e.preventDefault();
			renameFile(o);
			return false;
		});
		deleteForm.click(deleteLinkClicked);
		copyData.show();
		
		return;
	}
}

function renameFile(o) {
	var regExp = /\.[a-zA-Z0-9]*$/;
	var oldFilename = o.deleteForm.data("filename");
	var oldSuffix = o.deleteForm.data("filename").match(regExp)[0];
	var newSuffix = o.renameInput.val().match(regExp)[0];
	oldSuffix = (oldSuffix != null? oldSuffix.substr(1, oldSuffix.length-1):"");
	newSuffix = (newSuffix != null? newSuffix.substr(1, newSuffix.length-1):"");
	
	if(!checkConsistency(oldSuffix, newSuffix)) return;
	
	o.form.ajaxSubmit({
		dataType: "xml",
		success: function(data) {
			var inputGrp = o.renameInput.parent();
			
			o.renameButton.addClass("btn-success");
			//inputGrp.attr("title", $("response", data).text());
			o.form.attr("action", o.form.attr("action").replace(oldFilename, o.renameInput.val()));
			o.deleteForm.attr("href", o.deleteForm.data("filename", o.renameInput.val()).attr("href").replace(oldFilename, o.deleteForm.data("filename")));

			renameRequestSuccessful(data);
			//inputGrp.tooltip({trigger:"manual"}).tooltip("show");

			setTimeout(function() {
			  o.renameButton.removeClass("btn-success");
			  //inputGrp.tooltip("destroy");
			}, constants.RESPONSE_TIMEOUT);
			
		}
	
	});
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
	
	return;
}