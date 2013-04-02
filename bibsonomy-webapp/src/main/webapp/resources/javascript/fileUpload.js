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
				currentName = item;
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
	var extractName = $(this).attr('href').split("&");
	var name;
	//get name of the document to rename out of the given href
	$.each(extractName, function(index, item) {
		if(item.indexOf("fileName") >= 0) {
			name = item.split("=")[1];
		}
	});
	
	/*
	 * the same rename button was clicked twice, so hide the rename form and do nothing 
	 */
	if(currentName && currentName.indexOf(name) >= 0) {
		return false;
	}
	
	/*
	 * on the edit publication page a label is already available,
	 *  on the default view-publication page it's not
	 */
	if($("#showName").length) {
		$("#showName").text(getString("post.bibtex.renameTitle") +" " +name);
		var renameForm = "<form id='renameForm' action='" + $(this).attr('href') + "' method='POST' enctype='multipart/form-data'>" + 
		 "<input id='renameFormTxt' type='text' name='newFileName'/>" +
				" <input id='renameBtn' type='button' value='"+ getString("post.bibtex.btnRename") +"' /></form> ";
	} else {
		var renameForm = "<form id='renameForm' action='" + $(this).attr('href') + "' method='POST' enctype='multipart/form-data'>" + 
		"<p id='showName'>" +getString("post.bibtex.renameTitle") +" " +name +":</p> <input id='renameFormTxt' type='text' name='newFileName'/>" +
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
	 */
	$("#renameBtn").click(renameSelected);

	return false;
}

/**
 * renames a document
 */
function renameSelected() {
	/*
	 * get the documents type to check wether
	 * already a document exists with the specified type
	 * and name or not
	 */
	var temp = $("#showName").text().split(" ");
	var type;
	$.each(temp, function(index, item) {
		if(item.indexOf(".") >= 0) {
			type = item.split(".")[1].replace(":","");
		}
	});
	
	var renameForm = $("#renameForm");
	var fileName = $("#renameFormTxt").val()+"."+type; //get value of the rename field
	var fileExist = false;
	
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

		var documentUri = "/documents/" + intrahash + "/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(fileName);
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
			var aDel       = "<a class='deleteDocument' href='/ajax/documents?intraHash=" + intrahash + "&fileName="+ fileName + "&ckey=" + ckey + "&temp=false&action=delete'></a>";
			var aRename = "<a class='renameDoc' href='/ajax/documents?ckey="+ckey+"&temp=false&intraHash=" + intrahash + "&fileName="+ fileName +"&action=rename'></a>";
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
			upA.children().first().replaceWith($(documentImg));
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
	var toRename = $('a:contains("' + oldName +'")');
	$('a[href*="' + oldName +'"]').each(function() {
		var newHref = $(this).prop("href").replace(oldName, newName);
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
	$('img[alt="'+oldName+'"]').each(function() {
		if($(this).attr("href")) {
			var newHref = $(this).prop("href").replace(oldName, newName);
			$(this).prop("href", newHref);
		}
		if($(this).attr("alt")) {
			$(this).prop("alt", newName);
		}
	});
	
	toRename.text(newName);
	
	/*
	 * print status 
	 */
	alert(response);
	
	return;
}