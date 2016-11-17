$(function() {
	// buttons
	$('#publication-functions .btn-group .btn[data-target]').click(function() {
		var id = "#publication-" + $(this).data('target');
		if ($(this).hasClass('active')) {
			$(id).hide();
			$(this).removeClass('active');
		} else {
			$(this).siblings().removeClass('active');
			$(this).addClass('active');
			$('.publication-function').hide();
			$(id).show();
			$.scrollTo($('#publication-search'));
		}
	});
	
	// init title autocomplete
	var searchInput = $('#publication-search input');
	if (searchInput.length > 0) {
		var publicationSource = new Bloodhound({
			datumTokenizer: function (datum) {
				return Bloodhound.tokenizers.whitespace(datum.value);
			},
			queryTokenizer: Bloodhound.tokenizers.whitespace,
			remote: {
				url: '/json/ajax/autocomplete?search=%QUERY&resourceType=publication',
				filter: function (data) {
					return $.map(data.items, function (publication) {
						return {
							value: publication.label,
							intrahash: publication.intraHash,
							authors: publication.authors,
							user: publication.user,
							year: publication.year
						};
					});
				}
			}
		});
		
		publicationSource.initialize();
		
		var searchText = getString('post_publication.search.loading');
		var notFoundText = getString('post_publication.search.notfound');
		searchInput.typeahead({
			highlight: true,
			minLength: 1
		}, {
			displayKey: 'value',
			source: publicationSource.ttAdapter(),
			templates: {
				notFound: '<p class="tt-suggestion" id="tt-notfound">' + notFoundText + '</p>',
				pending : '<p class="tt-suggestion tt-loading">' + searchText + '</p>',
				suggestion: Handlebars.compile("<p>{{value}} ({{year}})<br /><span class='author text-muted'>{{#each authors}}{{first}} {{last}}{{#unless @last}}, {{/unless}}{{/each}}</span></p>"),
			}
		});
		
		// to e.g. keep isbns to pass them to the edit post controller
		var originalSearch;
		searchInput.bind('typeahead:select', function(evt, data) {
			var hash = data.intrahash;
			
			if (hash == "") {
				window.location.href = '/editPublication?selection=' + originalSearch;
				return;
			}
			
			window.location.href = '/editPublication?hash=' + data.intrahash + "&user=" + data.user;
		}).bind('typeahead:asyncreceive', function() {
			originalSearch = searchInput.val();
		}).bind('typeahead:render', function(evt, suggestion) {
			$('#tt-notfound').click(function() {
				var input = searchInput.val();
				showManualForm(input);
			});
		});
		
		// handle person add function from /persons page
		if ($('#personIndex').length > 0) {
			showManualForm('');
		} else {
			searchInput.focus();
		}
	}
	
	// whitespace substitute
	$('#whitespace').attr('disabled', 1);
	
	$('#delimiter').change(function(){
		$('#whitespace').attr('disabled', $(this).val() == " ");
	});
});

function showManualForm(titleText) {
	var manualButton = $('.btn[data-target=manual]');
	manualButton.click();
	$('#post\\.resource\\.title').focus().val(titleText);
}