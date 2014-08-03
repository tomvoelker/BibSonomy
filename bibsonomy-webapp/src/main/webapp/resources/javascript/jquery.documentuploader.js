function errorBoxData(parentId) {
	this.msg = null;
	this.parentId = parentId;
}

function displayFileErrorBox(data) {
	$(data.parentId).children('div:first').fadeOut('slow', function() {$(this).fadeIn('slow').children(':first').html(data.msg);});
}

var errorData = new errorBoxData("#upload");

(function($) {
	var ckey;
	$.fn.documentUploader = function () {
		ckey=$(".ck").val();
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
			$("#upload").first('.documents').append($("<li class='loading' id='file_"+counter+"'><span class='documentFileName'>"+fileName+"</span></li>"));

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
		if(data.getElementsByTagName("status")[0].innerHTML == "ok")
			return fileUploaded(data);
		var file_id = prepareFileErrorBox(data);
		$("#file_"+file_id).removeClass("loading").addClass("fileError");
	}

	function fileUploaded(data) {
		var fileID=data.getElementsByTagName("fileid")[0].innerHTML;
		var fileHash=data.getElementsByTagName("filehash")[0].innerHTML;
		var fileName=data.getElementsByTagName("filename")[0].innerHTML;
		var deleteLink = $("<a class='deleteDocument' href='/ajax/documents?fileHash="
				+fileHash
				+"&amp;ckey="
				+ckey
				+"&amp;temp=true&amp;fileID="
				+fileID+"&amp;action=delete'>"
				+"</a>")
				.click(function(){
					return deleteFunction(this);
				});
		$("#file_"+fileID).
		append("<input type='hidden' class='tempFileName' name='fileName' value='" + fileHash + fileName+"'/> ").
		append(deleteLink).
		removeClass("loading");
	}
})(jQuery);

function deleteFunction(button){
	$.get($(button).attr("href"), {}, function(data) {
		var fileID=data.getElementsByTagName("fileid")[0].innerHTML;
		if( "ok"==data.getElementsByTagName("status")[0].innerHTML || "deleted"==data.getElementsByTagName("status")[0].innerHTML) {
			errorData.msg = data.getElementsByTagName("response")[0].innerHTML;
			$(button).parent().remove();
			if(fileID != '') {
				$("#file_"+fileID).remove();
				$("#uploadForm_"+fileID).remove();			
			}
		}else {
			errorData.msg = data.getElementsByTagName("reason")[0].innerHTML;	
		}	
		displayFileErrorBox(data);
	}, "xml");
	return false;
}

function prepareFileErrorBox(data) {
	var fileID = "NaN";
	var reason = "Unknown Error!";
	
	if(data.getElementsByTagName("status")[0].innerHTML == "error") {
		fileID = data.getElementsByTagName("fileid")[0].innerHTML;
		reason = data.getElementsByTagName("reason")[0].innerHTML
	}

	errorData.msg = reason;
	displayFileErrorBox(errorData);
	return fileID;
}
