/*
	replaces content of first text child node of given element 
 */
function replaceElementsTextnode(elname, t, color) {
	
	// get Element
	e = document.getElementById(elname);
	
	if (e) {
		// replace nodevalue of first child
		e.firstChild.nodeValue = t;
		if (color && (color.length==7 || color.length==4)) {
			e.setAttribute("style", "color:"+color+";");
		}
		else
		{
			e.removeAttribute("style");
		}
	}
	
}


function pumainit(){
	
}
	
$(pumainit);    


