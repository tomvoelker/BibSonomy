/**
 * 
 */

$(function () {
	addAutocomplete("#designatedAdmin", '2');
});

function addAutocomplete(id, type) {
	$(id).autocomplete({
		source: function (request, response) {
			return $.ajax({
				url: "../ajax/usersearch",
						data: {search:request.term, limit:10, showSpammers: false },
						async: false,
						success: function (data) {
						var names = new Array();
								$.each(data.items, function(index, item) {names.push(item.name); });
								response($.map(names, function(item) {
									return {
										label: item,
											value: item
									}
								}));
						}
			});
		}
	});
}