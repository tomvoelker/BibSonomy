/*
	replaces content of first text child node of given element 
 */
function replaceElementsTextnode(elname, t) {
	
	// get Element
	e = document.getElementById(elname);
	
	if (e) {
		// replace nodevalue of first child
		e.firstChild.nodeValue = t;
	}
	
}


$(document).ready(function(){
  
	$("#nice_tnav").prepend('<div id="navitogglehelp" class="togglehelp">?</div>');
	
	$(".togglehelp").click(function () {
    $("#helpbox").toggle("slow");
  });    

})


