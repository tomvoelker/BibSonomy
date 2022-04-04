$(function() {

    $('#selectCustomLayout').select2({
        // sorter: data => data.sort((a, b) => a.text.localeCompare(b.text)),
        sorter: function (data) {
            return data.sort(function (a, b) {
                return a.text.localeCompare(b.text);
            });
        },
        width: '100%'
    });

    // handle select change
    $('#selectCustomLayout').change(function() {
        var selected = $(this).find('option:selected');

        // get data values
        var dataSource = selected.data('source');
        var dataName = selected.data('name');
        var dataDisplayName = selected.data('displayname');

        // create new favorite layout html
        var source = dataSource.toUpperCase();
        var style = dataName.toUpperCase();
        var id = source + '/' + style;
        var favList = $('#favouriteLayoutsList');
        var items = favList.find('li[data-source="' + source + '"][data-style="' + style + '"]');
        var toHighlight;
        var deleteMsg = getString('delete');
        if (items.length == 0) {
            var toBeAppended = $('<li class="list-group-item favouriteLayoutsListItem clearfix" data-source="' + source + '" data-style="' + style + '"></li>');

            var input = $('<input type="hidden" name="user.settings.favouriteLayouts"  id="' + id + '" value="' + id + '"/>');
            var deleteButton = $('<span class="btn btn-danger btn-xs pull-right delete-Style">' + deleteMsg + '</span>');
            deleteButton.click(deleteStyle);

            toBeAppended.append(input);
            toBeAppended.append(deleteButton);
            toBeAppended.append(dataDisplayName);
            favList.append(toBeAppended);
            toHighlight = toBeAppended;
        } else {
            toHighlight = items;
        }

        // highlight new or already added export format
        toHighlight.effect("highlight", {}, 2500);
    });


	/* TODO: merge with friendsoverview logic */
	$('.groupUnshare').hover(function() {
		$(this).removeClass('btn-success').addClass('btn-danger');
		$(this).children('.fa').removeClass('fa-check').addClass('fa-times');
		$(this).children('.button-text').text(getString('groups.actions.unshareDocuments'));
	}).mouseleave(function() {
		$(this).removeClass('btn-danger').addClass('btn-success');
		$(this).children('.fa').removeClass('fa-times').addClass('fa-check');
		$(this).children('.button-text').text(getString('groups.documentsharing.shared'));
	});
	
	// getting the "Delete" batch to work
	$('.delete-Style').click(deleteStyle);
	
	function clearFavouriteLayoutsList() { // removing duplicates
		var seen = {};
		$('.favouriteLayoutsListItem').each(function() {
			var txt = $(this).data("source") + "/" + $(this).data("style");
			if (seen[txt]) {
				$(this).remove();
			} else {
				seen[txt] = true;
			}
		});
	}
	
	clearFavouriteLayoutsList();
});

function deleteStyle() {
	$(this).parent().slideUp(200, function() {
		$(this).remove();
	});
}
