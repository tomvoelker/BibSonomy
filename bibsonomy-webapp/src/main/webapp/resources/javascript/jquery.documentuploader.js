(function($) {
	var errorData = new errorBoxData("#upload");
	var ckey = $(".ck").val();
	$.fn.documentUploader = function () {
		$(this).change(function(e){
			var counter=$(".counter").val();
			var fileName=$(this).val();
			var duplicate=false;

			counter++;
			//for chrome, ie and opera
			fileName = fileName.replace("C:\\fakepath\\", "");


			// check already added files for duplicates
			$(".upform").each(function () {
				var oldFileName = $(this).find("#fu").val();
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

			if (duplicate) {
				errorData.msg = getString("post.bibtex.fileExists");
				displayFileErrorBox(errorData);
				$("#fu").replaceWith($("<input id='fu' type='file' name='file'/>"));
				$("#fu").documentUploader();
				return;
			}

			// create row with the added file
			$("#upload").children('ul').append($("<li class='loading' id='file_"+counter+"'><span class='documentFileName'>"+fileName+"</span></li>"));

			// create new form to upload added file
			var form = ("<form class='upform' id='uploadForm_"+counter+"' action='/ajax/documents?ckey="+ckey+"&amp;temp=true' method='POST' enctype='multipart/form-data'></form>");
			$("#hiddenUpload").append(form);
			form="uploadForm_"+counter;
			$(this).appendTo($("#"+form));
			$("<input class='fileID' type='hidden' name='fileID' value='"+counter+"'/>").appendTo($("#"+form));
			$("#upload").show();
			$(".counter").val(counter);
			var options = {
					dataType: "xml",
					success: onRequestComplete		
			};
			$("#"+form).ajaxSubmit(options);
			// FIXME: why is a new input field appended? Can't we just replace #fu?
			$("#inputDiv").append($("<input id='fu' type='file' name='file'/>"));
			$("#fu").documentUploader();

		}); 

	};

	function onRequestComplete(data) {
		if($("status", data).text() == "ok")
			return fileUploaded(data);
		var file_id = prepareFileErrorBox(data);
		$("#file_"+file_id).removeClass("loading").addClass("fileError");
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
		$("#file_"+fileID).
		append("<input type='hidden' class='tempFileName' name='fileName' value='" + fileHash + fileName+"'/>").
		append(" (").
		append($("<a class='deleteTempDocument' href='/ajax/documents?fileHash="+fileHash+"&amp;ckey="+ckey+"&amp;temp=true&amp;fileID="+fileID+"&amp;action=delete'>"+getString("post.bibtex.delete")+"</a>")).
		append(")").
		removeClass("loading");
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