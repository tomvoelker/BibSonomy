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
	
	// if no sidebar available, add sidebar into dom
	
	if ($("#sidebarroundcorner").attr("id") == null)
	{
		// add after id=outer
		$("#outer").after('<div id="sidebarroundcorner" style="display: none;"></div>');
	
		// adjust width of outer, threrefore remove attribute style from element, only if sidebar is visible
		$("#outer").attr("oldstyle", $("#outer").attr("style"));
	}
	
	
	// add help box as sidebox in front of #sidebarroundcorner's content
	$("#sidebarroundcorner").prepend('<div class="sidebarBoxOuter" id="helpbox"><div class="sidebarBoxInner"><div id="helpboxcontent" class="boxcontent"><div id="togglehelp" class="togglehelp closeX">x</div><div class="bc-head">Quicklinks</div><a href="/login">Loggen Sie sich jetzt mit Ihren Bibliotheksausweis ein.</a><br />CV anzeigen: Wählen Sie unter meinPUMA meinLebenslauf aus.<br /><div class="bc-head">Hilfe</div>Weitere Informationen erhalten sie auf den <a href="http://www.ub.uni-kassel.de/puma.html">Projektseiten</a>, in der Hilfe und den beiden Blogs <a href="http://puma-projekt.blogspot.com/">Puma-Projekt</a> und <a href="http://bibsonomy.blogspot.com/">Bibsonomy</a>.</div></div></div>');
	
	$(".sidebarBoxInner").corner("round 8px").parent().css('padding', '3px').corner("round 8px");
	
	$("#helpbox").toggle();

	// help side box toggle
	$("#nice_tnav").prepend('<div id="navitogglehelp" class="togglehelp">?</div>');
	$(".togglehelp").click(function () {

		if (($("#outer").attr("oldstyle") != null) || ($("#sidebarroundcorner").css("display")=="none")) {
			if (($("#outer").attr("style") != null) || ($("#sidebarroundcorner").css("display")=="none")){
				$("#outer").removeAttr("style");
				$("#sidebarroundcorner").css("display", "block");
				$("#helpbox").show();
			} else {
				$("#helpbox").hide();
				$("#sidebarroundcorner").css("display", "none");
				$("#outer").attr("style", $("#outer").attr("oldstyle"));
			}
		}
		else
		{
			$("#helpbox").toggle();
		}
	})

	
	
}
	
$(document).ready(pumainit);    


