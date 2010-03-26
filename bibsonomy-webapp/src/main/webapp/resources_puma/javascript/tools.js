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
	
	// add surrounding div .sidebarboxinner to #sidebarroundcorner's content
//	$("#sidebarroundcorner").wrapInner('<div class="sidebarBoxInner" />');

	// add surrounding div with class="sidebarBoxOuter" id="sidebox" to #sidebarroundcorner's content
//	$("#sidebarroundcorner").wrapInner('<div class="sidebarBoxOuter" id="sidebox" />');
	
	// add help box as sidebox in front of #sidebarroundcorner's content
	$("#sidebarroundcorner").prepend('<div class="sidebarBoxOuter" id="helpbox"><div class="sidebarBoxInner"><div id="helpboxcontent" class="boxcontent"><div id="togglehelp" class="togglehelp closeX">x</div><div class="bc-head">Quicklinks</div><a href="/login">Loggen Sie sich jetzt mit Ihren Bibliotheksausweis ein.</a><br />CV anzeigen: /cv/user/&lt;username&gt;" zur URL hinzufügen<br /><br/><div class="bc-head">Hilfe</div>Weitere Informationen erhalten sie auf den <a href="http://www.ub.uni-kassel.de/puma.html">Projektseiten</a>, in der Hilfe und den beiden Blogs <a href="http://puma-projekt.blogspot.com/">Puma-Projekt</a> und <a href="http://bibsonomy.blogspot.com/">Bibsonomy</a>.<br />Video-Tutorials<br /><div class="bc-highlight">Diese PUMA-Version ist noch in einem frühen Entwicklungsstadium. Daher funktionieren einige Funktionen noch nicht (korrekt).</div></div></div></div>');
	
	$(".sidebarBoxInner").corner("round 8px").parent().css('padding', '3px').corner("round 8px");
	
	// help side box toggle
	$("#nice_tnav").prepend('<div id="navitogglehelp" class="togglehelp">?</div>');
	$(".togglehelp").click(function () {
		$("#helpbox").toggle("slow");
	})


}
	
$(document).ready(pumainit);    


