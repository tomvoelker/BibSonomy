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
	var icon_copy = document.getElementById("icon_copy");
	var icon_copy_greyed = document.getElementById("icon_copy_greyed");
	for (var index=0; index<pubFields.length; index++) {
		//text input
		var post = $(pubFields[index] == "description"?"#post\\."+pubFields[index]:"#post\\.resource\\."+pubFields[index]).val().split(" ");
		var comparePost =  $(pubFields[index] == "description"?"#tmppost\\."+pubFields[index]:"#tmppost\\.resource\\."+pubFields[index]).val().split(" ");
			
		var changed = false;
		
		//result
		var comparePostValue = "";
		//number of words of each field
		var m = post.length;
		var n = comparePost.length;
		
		//opt is multidimensional array (opt[m+1][n+1])
		var opt = new Array(m+1);//(1+n);
		for(var k=0; k<m+1;k++){
			opt[k] = new Array(n+1);
		}
		
		for(var i=m-1; i>=0; i--) {
			for(var j=n-1; j>=0; j--){
				if(post[i] == comparePost[j]){
					opt[i][j] = (opt[i+1][j+1] +1);
				} else {
					opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
				}	
			}
		}
		
		var i=0;
		var j=0;
		while(i<m && j<n){
			if(post[i] == comparePost[j]){
				comparePostValue+=(comparePost[j]+" ");
				i++;
				j++;
			} else if (opt[i+1][j] >= opt[i][j+1]){
				comparePostValue+=('<span class="fsDiffMissingColor">' + post[i++] + '</span>'+ " ");
				changed = true;
			} else {
				comparePostValue+=('<span class="fsDiffAddColor">' + comparePost[j++] + '</span>'+ " ");
				changed = true;
			}
		}
		while(i<m) {
			comparePostValue+=('<span class="fsDiffMissingColor">' + post[i++] + '</span>'+ " ");
			changed = true;
		}while(j<n){
			comparePostValue+=('<span class="fsDiffAddColor">' + comparePost[j++] + '</span>'+ " ");
			changed = true;
		}document.getElementById(pubFields[index] == "description"?"comparePost."+pubFields[index]:"comparePost.resource."+pubFields[index]).innerHTML= comparePostValue;
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
	document.getElementById("comparePost"+tmppath).innerHTML= msg;//document.getElementById("tmppost"+tmppath).innerHTML;
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