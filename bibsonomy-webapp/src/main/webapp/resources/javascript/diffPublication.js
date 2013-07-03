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
	for (var index=0; index<pubFields.length; index++) {
		//text input
		var post = $(pubFields[index] == "description"?"#post\\."+pubFields[index]:"#post\\.resource\\."+pubFields[index]).val().split(" ");
		var postDiff =  $(pubFields[index] == "description"?"#tmppost\\."+pubFields[index]:"#tmppost\\.resource\\."+pubFields[index]).val().split(" ");
			
		//result
		var postDiffValue = "";
		//number of words of each field
		var m = post.length;
		var n = postDiff.length;
		
		//opt is multidimensional array (opt[m+1][n+1])
		var opt = new Array(m+1);//(1+n);
		for(var k=0; k<m+1;k++){
			opt[k] = new Array(n+1);
		}
		
		for(var i=m-1; i>=0; i--) {
			for(var j=n-1; j>=0; j--){
				if(post[i] == postDiff[j]){
					opt[i][j] = (opt[i+1][j+1] +1);
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
		document.getElementById(pubFields[index] == "description"?"postDiff."+pubFields[index]:"postDiff.resource."+pubFields[index]).innerHTML= postDiffValue;
	}

}

function copyContent(path) {
	document.getElementById(path).value = document.getElementById("tmp"+path).innerHTML;
	var tmppath = path.slice(4,path.length)
	document.getElementById("postDiff"+tmppath).innerHTML= document.getElementById("tmppost"+tmppath).innerHTML;
} 
