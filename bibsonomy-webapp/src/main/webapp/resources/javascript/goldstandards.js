/**
 * for deleting and adding references
 */
var FADE_DURATION = 1000;
var GOLD_REFERENCE_URL = '/ajax/goldstandards/relation';

$(function() {
	$('div.related-publications-container').each(function(index, container) {
		if ($(container).find('ul>li').length == 0 && $(container).find('input').length == 0) {
			$(container).hide();
		}
	});
	
	// init title autocomplete
	var publicationSource = new Bloodhound({
		datumTokenizer: function (datum) {
			return Bloodhound.tokenizers.whitespace(datum.value);
		},
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		remote: {
			url: '/json/tag?tag=sys:title:%QUERY',
			prepare: function(query, settings) {
				var url = '/json/tag/';
				var tokens = query.split(' ');
				
				for (var i = 0; i < tokens.length; i++) {
					url += 'sys:title:' + encodeURIComponent(tokens[i]);
					url += '%20';
				}
				url += '?resourcetype=goldstandardPublication';
				settings.url = url;
				return settings;
			},
			filter: function (data) {
				return $.map(data.items, function (publication) {
					return {
						value: publication.label,
						interhash: publication.interHash,
						authors: publication.authors,
						user: publication.user,
						year: publication.year
					};
				});
			}
		}
	});
	publicationSource.initialize();
	
	var searchInput = $('.addRelation').typeahead({
		highlight: true,
		minLength: 1
	}, {
		displayKey: 'value',
		source: publicationSource.ttAdapter(),
		templates: {
			suggestion: Handlebars.compile("<p>{{value}} ({{year}})<br /><span class='author text-muted'>{{#each authors}}{{first}} {{last}}{{#unless @last}}, {{/unless}}{{/each}}</span></p>"),
		}
	});
	
	searchInput.on('typeahead:selected', function(evt, typeaheadData) {
		var relation = $(evt.target).data('relation');
		var dataToSend = 'ckey=' + ckey + '&hash=' + getGoldInterHash() + '&references=' + typeaheadData.interhash + '&relation=' + relation;
		
		$.ajax({
			url: GOLD_REFERENCE_URL,
			data: dataToSend,
			type: 'POST',
			success: function(data) {
				location.reload();
			}
		});
	});
	
	// delete relation links
	
	$('.deleteRelation').click(function() {
		var relation = $(this).data('relation');
		var referenceHash = $(this).data('interhash');
		if (confirm(getString('post.actions.edit.gold.references.delete.confirm'))) {
			$.ajax({
				url: GOLD_REFERENCE_URL + '?ckey=' + ckey + '&hash=' + getGoldInterHash() + '&references=' + referenceHash + '&relation=' + relation,
				type: 'DELETE',
				success: function(response) {
							location.reload();
						}
			});
		}
	});
});

/** 
 * @returns the hash of the publication
 */
function getGoldInterHash() {
	return $('#goldstandard').data('interhash');
}