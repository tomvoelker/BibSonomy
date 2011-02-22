(function($) {
	
	$.fn.documentUploader = function () {
		
		$(this).change(function(e){
		var counterInput = $(".counter");	
		var counter=counterInput.val();
		counter++;
		var ckey=$(".ck");
		var resdir=$(".resdir");
		var fileName=$(this).val();
		
		//for chrome, ie and opera
		fileName = fileName.replace("C:\\fakepath\\", "");
		
		var duplicate=false;
		
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
			alert(LocalizedStrings["post.bibtex.fileExists"]);
			$(".fu").remove();
			var input="<input class='fu' type='file' name='file'/>;";
			$("#inputDiv").append($(input));
			$(".fu").documentUploader();
			return;
		}
		
		// create row with the added file
		var fileDiv = "<div class='fsRow' id='file_"+counter+"'>\n\t<span class='documentFileName'>"+fileName+"</span>\n"+
			"<span id='gif_"+counter+"'>\n\t<img alt='uploading...' src='"+resdir.val()+"/image/ajax_loader.gif' />\n</span>\n</div>";
		$("#upload").append(fileDiv);
		
		// create new form to upload added file
		form = ("<form class='upform' id='uploadForm_"+counter+"' action='/ajax/documents?ckey="+ckey.val()+"&amp;temp=true' method='POST' enctype='multipart/form-data'></form>");
		$("#hiddenUpload").append(form);
		form="uploadForm_"+counter;
		$(this).appendTo($("#"+form));
		$("<input class='fileID' type='hidden' name='fileID' value='"+counter+"'/>").appendTo($("#"+form));
		$("#upload").show();
		$(".counter").val(counter);
		var options = {
				dataType: "xml",
				success: uploadRequestSuccessful		
		}
		$("#"+form).ajaxSubmit(options);
		
		// createInputField($("#inputDiv"));
		var input="<input class='fu' type='file' name='file'/>;";
		$("#inputDiv").append($(input));
		$(".fu").documentUploader();

		}); 
		
	};
	
	function uploadRequestSuccessful(data) {
		var status=$("status", data).text();
		switch (status) {
			case "error":
				fileUploadError(data);
				break;
			case "ok":
				fileUploaded(data);
				break;
			default:
				alert("Unknown error!");
			break;
		}
	}
	
	function fileUploadError(data) {
		var fileID=$("fileid", data).text();
		var reason=$("reason", data).text();
		$("#gif_"+fileID).hide();
		$("#file_"+fileID).append(reason);
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
		var ckey=$(".ck");
		$("#file_"+fileID).append(" (");
		$("#file_"+fileID).append($("<a class='deleteTempDocument' href='/ajax/documents?fileHash="+fileHash+"&amp;ckey="+
				ckey.val()+"&amp;temp=true&amp;fileID="+fileID+"&amp;action=delete'> "+LocalizedStrings["post.bibtex.delete"]+"</a>"));
		$("#file_"+fileID).append(")");
		$(".deleteTempDocument").live("click", deleteFunction);
	}
	
	function deleteFunction(){
		var button = $(this);
		$.get($(button).attr("href"), {}, function(data) {
			var status=$("status", data).text();
			var fileID=$("fileid", data).text();
			if(status=="error"){
				alert(("reason", data).text);
			} else {
				button.parent("div").remove();
				$("#file_"+fileID).remove();
				$("#uploadForm_"+fileID).remove();
			}
		}, "xml");
		return false;
	}
	
})(jQuery);