//file upload on /bibtex/HASH/USER
$(function(){
	$(".deleteDocument").click(deleteLinkClicked);
});	

function deleteLinkClicked () {
	var button = $(this);
	$.get($(button).attr("href"), {}, function(data) {
		var status=$("status", data).text();
		if(status=="error"){
			alert($("reason", data).text());
		} else {
			var test = $(button).next(".documentFileName").text();
			alert($("response", data).text());
			$(button).parent(".fsRow").remove();
		}
	}, "xml");
	return false;
}

$(function(){
	$(".addDocument").click(function(){
		var emptyInput = false;
		$(".fu").each(function(){
			if($(this).val()=="") {
				emptyInput = true;
			}
		});
		if(emptyInput){
			return false;
		}
		var intraHash=$(".intraHash").val();
		var input="<form class='upform' action='/ajax/documents?ckey=" + ckey +
		"&temp=false&intraHash=" + intraHash + 
		"' method='POST' enctype='multipart/form-data'>" + 
		"<input class='fu' type='file' name='file'/></form>";

		$("#inputDiv").append($(input));
		$(".fu").change(fileSelected);

		return false;
	});
});

function fileSelected(obj){
	var fileName = $(obj.currentTarget).val();
	var form = $(obj.currentTarget).parent(".upform");
	fileName = fileName.replace("C:\\fakepath\\", ""); //remove fakepath for Opera, Chrome, IE
	var fileExist = false;
	$(".documentFileName").each(function(){
		var name = jQuery.trim($(this).text());
		if (name == fileName) {
			fileExist = true;
		}
	});

	if(fileExist){
		alert(getString("post.bibtex.fileExists"));
		$(form).remove();
		return;
	}
	$(form).ajaxSubmit({
		dataType: "xml",
		success: uploadRequestSuccessful		
	});
	$(form).hide();
	var id = replaceInvalidChrs(fileName);
	var resdir = $(".resdir");
	var div = "<div class='fsRow' id='" + id +"' >" + fileName  + 
	"<img alt='uploading...' src='"+resdir.val()+"/image/ajax_loader.gif' /></div>";
	$("#files").append($(div));
} 

function replaceInvalidChrs (input){
	var iChars = "!@#$%^&*()+=-[]\\\';,./{}|\":><?~_"; 
	var output = "";
	for (var i = 0; i < input.length; i++) {
		if (iChars.indexOf(input.charAt(i)) != -1) {
			output += input.charCodeAt(i);
		} else {
			output += input.charAt(i);
		}
	}
	return output;
}

function uploadRequestSuccessful(data) {
	var status=$("status", data).text();
	if (status=="error"){

		alert($("reason", data).text());
		var fileName=$("filename", data).text();
		var id = replaceInvalidChrs(fileName);
		$("#"+id).remove();

		return;
	}
	if (status=="ok") {
		var fileID=$("fileid", data).text();
		var fileHash=$("filehash", data).text();
		var fileName=$("filename", data).text();

		var resdir = $(".resdir").val();
		var deleteMsg=getString("bibtex.actions.private_document.delete");
		var div = "<div class='fsRow'><a class='documentFileName' href='/documents/" + fuIntraHash + "/" + encodeURIComponent(currUser) + "/" + encodeURIComponent(fileName) + "'><img alt='" + getString("bibtex.actions.private_document.download") + "' src='"+ resdir + "/image/document-txt-blue.png' style='float: left;'/>" +
		fileName + "</a> (<a class='deleteDocument' href='/ajax/documents?intraHash=" + fuIntraHash + "&fileName="+ fileName + 	"&ckey=" + ckey + "&temp=false&action=delete'>" + deleteMsg + "</a>)</div>";
		$("#files").append(div);
		$(".deleteDocument").click(deleteLinkClicked);
		var id = replaceInvalidChrs(fileName);
		$("#"+id).remove();	
		return;
	}
}