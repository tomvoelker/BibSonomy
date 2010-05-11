/**
 * removes everything from tagField 
 */
function clearTagField() {
 	var sg = document.getElementById("tagField");
	while(sg.hasChildNodes()) 
		sg.removeChild(sg.firstChild);
}