var documentUploadSuccessIcon = "/resources/image/document-txt-blue.png";
/*
 * file upload for publication references
 * 
 */


/*
 * add handler to delete documents
 */
$(function() {
	var documentUploadTitle = getString("bibtex.actions.private_document.upload.title");
	$(".deleteDocument").click(deleteLinkClicked);
	$(".addDocument").click(addDocument).attr("title", documentUploadTitle);
});


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
			$(button).parent(".fsRow").remove();
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
		var documentImg = "<img src='" + documentUploadSuccessIcon +"' style='float: left;'/>";
		
		/*
		 * on /bibtex pages we have a DIV where we add links to the files
		 */
		var filesDiv = $("#files");

		if (filesDiv.length) {
			var div = "<div class='fsRow'>" + 
			"<a class='documentFileName preview' href='" + documentUri + " title='" + documentHelp + "''>" + documentImg + fileName + "</a> " + 
			"(<a class='deleteDocument' href='/ajax/documents?intraHash=" + intrahash + "&fileName="+ fileName + "&ckey=" + ckey + "&temp=false&action=delete'>" + getString("bibtex.actions.private_document.delete") + "</a>)</div>";
			filesDiv.append(div);
			$(".deleteDocument").click(deleteLinkClicked);
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