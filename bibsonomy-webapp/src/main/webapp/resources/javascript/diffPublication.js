var pubFields = new Array( "title","author","editor", "year",
		"booktitle","journal","volume","number","pages",
		"month","day","publisher","edition","chapter",
		"url","key","type","howpublished","institution","organization",
		"school","series","crossref","misc","abstract",
		"address","annote","note","privnote","description");

$(document).ready(function()
{
	diff();
});

function diff() {
	var dmp = new diff_match_patch();
	
	var icon_copy = document.getElementById("icon_copy");
	var icon_copy_greyed = document.getElementById("icon_copy_greyed");
	for (var index=0; index<pubFields.length; index++) {
		var changed = false;
		var comparePostValue = "";
		//text input
		var post = $(pubFields[index] == "description"?"#post\\."+pubFields[index]:"#post\\.resource\\."+pubFields[index]).val();
		var comparePost =  $(pubFields[index] == "description"?"#tmppost\\."+pubFields[index]:"#tmppost\\.resource\\."+pubFields[index]).val();
	//	if (post!=comparePost){
			comparePostValue = dmp.diff_main(comparePost,post);
			dmp.diff_cleanupSemantic(comparePostValue);
			comparePostValue = dmp.diff_prettyHtml(comparePostValue);
			changed = true;
	///	}
	//	else{
	//		comparePostValue = comparePost;
	//	}
		document.getElementById(pubFields[index] == "description"?"comparePost."+pubFields[index]:"comparePost.resource."+pubFields[index]).innerHTML= comparePostValue;
		if(changed){
			changeButtonImg(icon_copy, document.getElementById(pubFields[index] == "description"?"post."+pubFields[index]+"Img":"post.resource."+pubFields[index]+"Img"));
		}else {
			changeButtonImg(icon_copy_greyed, document.getElementById(pubFields[index] == "description"?"post."+pubFields[index]+"Img":"post.resource."+pubFields[index]+"Img"));
		}
	}

}

function copyContent(path) {
	var msg = document.getElementById("tmp"+path).value;
	document.getElementById(path).value = msg;//document.getElementById("tmp"+path).innerHTML;
	var tmppath = path.slice(4,path.length);
	document.getElementById("pathDiff"+tmppath).innerHTML= msg;//document.getElementById("tmppost"+tmppath).innerHTML;
}

function changeButtonImg(img, buttonImg){
	buttonImg.src = img.src;
}

$(function(){
  //bnd to all images with the copyButton class for click
  $('img.copyButton').click(function(e){
	  e.preventDefault();
	  //grab the image just clicked as a jquery object.
	  changeButtonImg(document.getElementById("icon_copy_greyed"), this);
  });
});

$(function(){
	  //bnd to images with the reloadButton class for click
	  $('img.reloadButton').click(function(e){
		  e.preventDefault();
		  //grab the image just clicked as a jquery object.
		  diff();
	  });
	});