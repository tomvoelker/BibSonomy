(function($) {
	var errorData = new errorBoxData("#upload");
	var resdir;
	var ckey;
	$.fn.documentUploader = function () {
		ckey=$(".ck").val();
		resdir = $(".resdir");
		$(this).change(function(e){
		var counterInput = $(".counter");	
		var counter=counterInput.val();
		var fileName=$(this).val();
		var duplicate=false;
		
		counter++;
		//for chrome, ie and opera
		fileName = fileName.replace("C:\\fakepath\\", "");
		
		
		// check already added files for duplicates
		$(".upform").each(function () {
			var oldFileName = $(this).find(".fu").val();
			if (fileName == oldFileName) {
				duplicate=true;
			}
		});
		$(".documentFileName").each(function (){
			var oldFileName = jQuery.trim($(this).text());
			if (fileName == oldFileName) {
				duplicate=true;
			}
		});
		
		var input="<input class='fu' type='file' name='file'/>;";
		if (duplicate) {
			errorData.msg = LocalizedStrings["post.bibtex.fileExists"];
			displayFileErrorBox(errorData);
			$(".fu").remove();
			var input="<input class='fu' type='file' name='file'/>;";
			$("#inputDiv").append($(input));
			$(".fu").documentUploader();
			return;
		}

		// create row with the added file
		var fileDiv = $("<li id='file_"+counter+"'>\n\t<span class='documentFileName'>"+fileName+"</span>\n"+
			"\n\t<img id='gif_"+counter+"' alt='uploading...' src='"+resdir.val()+"/image/ajax_loader.gif' />\n</li>");
		$("#upload").children('ul').append(fileDiv);
		
		
		// create new form to upload added file
		form = ("<form class='upform' id='uploadForm_"+counter+"' action='/ajax/documents?ckey="+ckey+"&amp;temp=true' method='POST' enctype='multipart/form-data'></form>");
		$("#hiddenUpload").append(form);
		form="uploadForm_"+counter;
		$(this).appendTo($("#"+form));
		$("<input class='fileID' type='hidden' name='fileID' value='"+counter+"'/>").appendTo($("#"+form));
		$("#upload").show();
		$(".counter").val(counter);
		var options = {
				dataType: "xml",
				success: onRequestComplete		
		}
		$("#"+form).ajaxSubmit(options);
		
		// createInputField($("#inputDiv"));
		var input="<input class='fu' type='file' name='file'/>;";
		$("#inputDiv").append($(input));
		$(".fu").documentUploader();

		}); 
		
	};
	
	function onRequestComplete(data) {
		if($("status", data).text() == "ok")
			return fileUploaded(data);
		// an error occured, 
		// display it to the user and hide the animation
		var file_id = prepareFileErrorBox(data);
		$("#gif_"+file_id).remove();
		$("#file_"+file_id).prepend($('<img src="'+resdir.val()+'/image/file_status_error.png"/>'));
	}

	function prepareFileErrorBox(data) {
		var fileID = "NaN";
		var reason = "Unknown Error!";
		if($("status", data).text() == "error") {
			fileID = $("fileid", data).text();
			reason = $("reason", data).text();
		}

		errorData.msg = reason;
		displayFileErrorBox(errorData);
		return fileID;
	}
		
	function fileUploaded(data) {
		var fileID=$("fileid", data).text();
		var fileHash=$("filehash", data).text();
		var fileName=$("filename", data).text();
		var hiddenFileInput = "<div id='file_"+fileID+"'>" +
				//"<input class='tempFileHash' name='fileHash' value='"+fileHash+"'/>"+
				"<input class='tempFileName' name='fileName' value='" + fileHash + fileName+"'/>"+
				"</div>";
		$(".addedFiles").append($(hiddenFileInput));
		$("#gif_"+fileID).hide();
		$("#file_"+fileID).
		append(" (").
		append($("<a class='deleteTempDocument' href='/ajax/documents?fileHash="+fileHash+"&amp;ckey="+ckey+"&amp;temp=true&amp;fileID="+fileID+"&amp;action=delete'>"+LocalizedStrings["post.bibtex.delete"]+"</a>")).
		append(")").
		prepend($('<img src="'+resdir.val()+'/image/file_status_okay.png"/>'));
		$(".deleteTempDocument").live("click", deleteFunction);
	}
	
	function deleteFunction(){
		var button = $(this);
		$.get($(button).attr("href"), {}, function(data) {
			if("ok"!=$("status", data).text())
				return prepareFileErrorBox(data);
				
			var fileID=$("fileid", data).text();
			button.parent("div").remove();
			$("#file_"+fileID).remove();
			$("#uploadForm_"+fileID).remove();
			
		}, "xml");
		return false;
	}
	
})(jQuery);