$(function() {
	$("#gold_exports").tabs();
});

function editReferences() {
	if ($('.reference_menu').length > 0) {
		// remove edit menu
		removeReferenceMenu();
		// TODO replace menu text
	} else {
		// TODO replace menu text
		addReferenceMenu();
	}
}

function addReferenceMenu() {
	$("li.reference").each(function() {
		var deleteLink = $('<span class="reference_menu"> <a href="#">delete reference</a></span>'); // TODO: i18n
		deleteLink.hide();
		$(this).append(deleteLink);
		deleteLink.fadeIn(500);
		
		deleteLink.click(function() {
			var listElement = $(this).parent('li')
			var hash = $('#gold_title').attr("data-interhash");
			var reference = listElement.attr("data-interhash");
			var ckey = $('#gold_menu').attr("data-ckey");
			var deleteUrl = '/ajax/goldstandards/references?ckey=' + ckey + '&hash=' + hash + "&references="+ reference;
			if (confirm("delete ?")) {
				alert(deleteUrl);
				$.ajax({
					type: 'DELETE',
					url: deleteUrl,
					success: function(transport) {
								listElement.fadeOut(500, function() {
									listElement.remove();
								});
							}
				});
			}
			
			return false;
		});
	});
	
	var addForm = $('<form class="reference_menu"></form>');
	var input = $('<input type="text" />');
	addForm.append($('<label>Add Reference: </label>')); // TODO: i18n
	addForm.append(input);
	input.autocomplete({
		source: function(request, response) {
			$.ajax({
				url: "/json/search/" + request.term,
				data: {items: 10, resourcetype: 'goldStandardPublication', duplicates: 'no'},
				dataType: "jsonp",
				success: function( data ) {
					response($.map(data.items, function(item) {
						return {
							label: item.label + ' (' + item.year + ')',
							value: item.interHash,
							author: (concatArray(item.author, 40, ' ' + getString('and') + ' '))
						}
					}));
				}
			});
		},
		minLength: 3,
		select: function(event, ui) {
			var reference = ui.item.value;
			var hash = getHash();
			alert(reference);
			return false;
		},
		focus: function(event, ui) {
			return false;
		}
	})
	.data('autocomplete')._renderItem = function(ul, item) {
		if (item.value == getHash()) {
			return ul;
		}
		return $('<li></li>').data('item.autocomplete', item).append($('<a></a>').html(item.label + '<br><span class="ui-autocomplete-subtext">' + item.author + '</span>')).appendTo(ul);
	};
	
	
	addForm.hide();
	$("#gold_references").append(addForm);
	addForm.fadeIn(500);
}

function getHash() {
	return $('#gold_title').attr("data-interhash");
}

function removeReferenceMenu() {
	$('.reference_menu').fadeOut(500, function() {
		$(this).remove();
	});
}