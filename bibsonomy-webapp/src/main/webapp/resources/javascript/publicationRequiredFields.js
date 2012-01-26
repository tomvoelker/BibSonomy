function highlightMatches(text, input) {
	var terms = input.split(" ");
	for (var i=0; i < terms.length; i++) {
		text = highlightMatch(text, terms[i]);
	} 
	return text;
}

function highlightMatch(text, term) {
	return text.replace( new RegExp("(?![^&;]+;)(?!<[^<>]*)(" +
			$.ui.autocomplete.escapeRegex(term) +
			")(?![^<>]*>)(?![^&;]+;)", "gi"
	), "<strong>$1</strong>"
	);
}

function myownTagInit(chkbox, tagbox) {
	var expr = /((^|[ ])myown($|[ ]))/gi;
	if(!(chkbox.length > 0 
			&& tagbox.length > 0)) 
		return;

	if(tagbox.val().search(expr) != -1) {
		chkbox[0].checked = true;
	} 

	tagbox.keyup(function(){
		if(tagbox.val().search(expr) != -1){
			chkbox[0].checked = true;
			return;
		}
		chkbox[0].checked = false;
	}
	);

	chkbox.click(
			function() {
				clear_tags ();
				if(this.checked 
						&& tagbox.val().search(expr) == -1){
					tagbox.removeClass('descriptiveLabel').val('myown '+tagbox.val());
				} else if(!this.checked) {
					tagbox.val(tagbox.val().replace(expr, ' ').replace(/^[ ]?/, ''));
				}
			}).parent().removeClass('hiddenElement');
}

$(function(){
		myownTagInit($('#myownChkBox'), $('#inpf'));
});

function initSuggestionForPartTitles(el) {
	el.each(function(index){ $(this).autocomplete({
		source: function( request, response ) {

			$.ajax({
				url: "/json/tag/" + createParameters(request.term),
				data: {items: 10,resourcetype: 'publication', duplicates: 'no'},
				dataType: "jsonp",
				success: function( data ) {
					response( $.map( data.items, function( item ) {
						return {
							label: (highlightMatches(item.label, request.term)+' ('+item.year+')'),
							value: item.interHash,
							url: 'hash='+item.intraHash+'&user='+item.user+'&copytag='+item.tags,
							author: (concatArray(item.author, 40, ' '+getString('and')+' ')),
							user: item.user,
							tags: item.tags
						};
					}));
				}
			});
		},
		minLength: 3,
		select: function( event, ui ) {
			window.location.href = '/editPublication?'+ui.item.url;
			return false;
		},
		focus: function( event, ui ) {
			return false;
		}
	})
	.data( 'autocomplete' )._renderItem = function( ul, item ) {
		return $('<li></li>')
		.data( 'item.autocomplete', item )
		.append(
				$('<a></a>')
				.html(	item.label+'<br><span class="ui-autocomplete-subtext">' 
						+item.author+' '+getString('by')+' '
						+item.user+'</span>'))
						.appendTo( ul );
	};
	});
}