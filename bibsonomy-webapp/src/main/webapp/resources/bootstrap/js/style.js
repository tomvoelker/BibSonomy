/**
 * 
 */
function switchNavi(scope, element) {
	var element = $(element);
	/*
	 * XXX: a hack to "unhover" the list. the worst part of it: we have to wait
	 * some time until we make the list visible again (though it's then
	 * otherwise hidden by CSS).
	 */
	var ul = element.parents("ul");
	ul.css("visibility", "hidden");
	window.setTimeout(function() {
		ul.css("visibility", "visible");
	}, 10);

	/*
	 * remove old scope inputs
	 */
	$("input[name='scope']").remove();

	/*
	 * change form action to redirect with the given scope
	 */
	var form = $("#search form").attr("action", "/redirect").append(
			"<input type='hidden' name='scope' value='" + scope + "'/>");

	/*
	 * Exchange text before form input field to the selected text.
	 */
	var text = element.html();
	if (text.search(/- /) != -1) { // search in a group
		text = getString("navi.group") + ":" + text.substr(2);
	}
	$("#search a:first").html(text);
	/*
	 * remove all remaining list elements
	 */
	$("#search > ul > li").each(function() {
		if (!$(this).find("form, ul").length)
			$(this).remove();
	});

	/*
	 * heuristic to get the hint for the input field
	 */
	var hint = getString("navi." + scope.replace(/\/.*/, "") + ".hint");
	if (hint.search(/\?\?\?.*\?\?\?/) != -1) {
		hint = getString("navi.search.hint"); // fallback
	}

	/*
	 * prepare input field
	 */
	$("#inpf").attr("name", "search") // always do a search
	.val(hint) // set hint as value
	.addClass('descriptiveLabel') // add class
	.descrInputLabel({}); // make the label disappear on click/submit
	$("#inpf").parents("li").removeClass("hidden"); // show form

	/*
	 * Start autocompletion if 'tag' in Search-navibar is chosen, otherwise quit
	 * autocompletion
	 */

	$("#inpf").autocomplete('disable');

	if (scope == "tag") {
		startTagAutocompletion($("#inpf"), false, true, false, false);
	}

	// TODO: finish suggestion service
	/*
	 * if(scope == "search") { startPostAutocompletion($("#inpf")); }
	 */

	return false;
}