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
