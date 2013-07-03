var bookFields = new Array("title", "url", "description");

$(document).ready(function()
{
	diff();
	
});

function diff() {	
	for (var index = 0; index < bookFields.length; index++) {
		//text input
		var post = $(bookFields[index] =="description"?"#post\\." + bookFields[index]:"#post\\.resource\\."+bookFields[index]).val().split(" ");
		var postDiff =  $(bookFields[index] =="description"?"#tmppost\\." + bookFields[index]:"#tmppost\\.resource\\."+bookFields[index]).val().split(" ");
		//result
		var postDiffValue = "";
		//number of words of each field
		var m = post.length;
		var n = postDiff.length;
		
		var opt = new Array(m+1);//(1+n);
		for(var k=0; k<m+1;k++){
			opt[k] = new Array(n+1);
		}
		
		for(var i=m-1; i>=0; i--) {
			for(var j=n-1; j>=0; j--){
				if(post[i] == postDiff[j]){
					opt[i][j] = opt[i+1][j+1] +1;
				} else {
					opt[i][j] = Math.max(opt[i+1][j], opt[i][j+1]);
				}	
			}
		}
		
		var i=0;
		var j=0;
		while(i<m && j<n){
			if(post[i] == postDiff[j]){
				postDiffValue+=(postDiff[j]+" ");
				i++;
				j++;
			} else if (opt[i+1][j] >= opt[i][j+1]){
				postDiffValue+=('<span class="fsDiffMissingColor">' + post[i++] + '</span>'+ " ");
			} else {
				postDiffValue+=('<span class="fsDiffAddColor">' + postDiff[j++] + '</span>'+ " ");
			}
		}
		while(i<m)
			postDiffValue+=('<span class="fsDiffMissingColor">' + post[i++] + '</span>'+ " ");
		while(j<n)
			postDiffValue+=('<span class="fsDiffAddColor">' + postDiff[j++] + '</span>'+ " ");
		document.getElementById(bookFields[index] == "description"?"postDiff."+bookFields[index]:"postDiff.resource."+bookFields[index]).innerHTML= postDiffValue;	
	}
}

function copyContent(path) {
	document.getElementById(path).value = document.getElementById("tmp"+path).innerHTML;
	var tmppath = path.slice(4,path.length)
	document.getElementById("postDiff"+tmppath).innerHTML= document.getElementById("tmppost"+tmppath).innerHTML;
}