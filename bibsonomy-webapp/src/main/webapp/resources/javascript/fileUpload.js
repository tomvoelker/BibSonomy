// file upload on /bibtex/HASH/USER
$(function(){
	$(".deleteDocument").click(deleteLinkClicked);
});	

function deleteLinkClicked () {
	var button = $(this);
	$.get($(button).attr("href"), {}, function(data) {
		var status=$("status", data).text()
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
		var input="&lt;form class='upform' action='/ajax/documents?ckey=${ckey}&amp;temp=false&amp;intraHash=" + intraHash +"' method='POST' enctype='multipart/form-data'&gt;&lt;input class='fu' type='file' name='file'/&gt;&lt;/form&gt;";

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

		alert(LocalizedStrings["post.bibtex.fileExists"]);
		$(form).remove();
		return;
	}
	var options = {
			dataType: "xml",
			success: uploadRequestSuccessful		
	}
	$(form).ajaxSubmit(options);
	$(form).hide();
	var id = replaceInvalidChrs(fileName);
	var resdir = $(".resdir");
	var div = "&lt;div class='fsRow' id='" + id +"' &gt;" + fileName  + "&lt;img alt='uploading...' src='"+resdir.val()+"/image/ajax_loader.gif' /&gt; &lt;/div&gt;";
	$("#files").append($(div));
} 

function replaceInvalidChrs (input){
	var iChars = "!@#$%^&amp;*()+=-[]\\\';,./{}|\":&gt;&lt;?~_"; 
	var output = "";
	for (var i = 0; i &lt; input.length; i++) {
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
		var deleteMsg=LocalizedStrings["bibtex.actions.private_document.delete"];
		var div = "&lt;div class='fsRow'&gt;&lt;a class='documentFileName' href='/documents/${command.bibtex.list[0].resource.intraHash}/${mtl:encodeURI(command.bibtex.list[0].user.name)}/"+ 
		fileName + "'&gt;&lt;img alt='" + LocalizedStrings["bibtex.actions.private_document.download"] + "' src='"+ resdir + "/image/document-txt-blue.png' style='float: left;'/&gt;" +
		fileName + "&lt;/a&gt; (&lt;a class='deleteDocument' href='/ajax/documents?intraHash=${post.resource.intraHash}&amp;fileName="+ fileName + 
		"&amp;ckey=${ckey}&amp;temp=false&amp;action=delete'&gt;" + deleteMsg + "&lt;/a&gt;)&lt;/div&gt;";
		$("#files").append(div);
		$(".deleteDocument").click(deleteLinkClicked);
		var id = replaceInvalidChrs(fileName);
		$("#"+id).remove();	
		return;
	}
}