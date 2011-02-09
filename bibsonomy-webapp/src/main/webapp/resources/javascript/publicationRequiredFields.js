/**
 * create one-string representation of a list of strings
 * 
 * @param data
 *            array of strings
 * @param max_len
 *            return the representing string cut down to the size of
 *            max_len
 * @param delim
 * @return one string, containing concatenation of all strings,
 *         separated by either '\n' or the supplied delimeter
 */

function concatArray(data, max_len, delim) {
	var retVal = "";
	var entry;
	if(delim == null) {
		delim = "\n";
	}
	for(entry in data) {
		retVal += data[entry] + ((entry < data.length-1)?delim:"");
	}
	return ((max_len != null) && (retVal.length > max_len))?retVal.substr(0, max_len)+"...":retVal;
}

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

function createParameters(title) {
	if(title[title.length-1] == " ") {
		title = title.substr(0, title.length-1);
	}
	var partials = title.split(" ");
	title = "";

	for(i = 0; i < parseInt(partials.length); i++) {
		title += "sys:title:"+encodeURIComponent(partials[i])+((i+1 < parseInt(partials.length))?"+":"*"); 
	}

	return title;
}

function myownTagInit(chkbox, tagbox) {
	if(!(chkbox.length > 0 
			&& tagbox.length > 0)) 
		return;

	if(tagbox.val().search(/myown[ ]?/gi) != -1) {
		chkbox[0].checked = true;
	} 

	tagbox.keyup(function(){
		if(tagbox.val().search(/myown[ ]?/gi) != -1){
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
						&& tagbox.val().search(/myown[ ]?/gi) == -1){
					tagbox.val('myown '+tagbox.val());
				} else if(!this.checked) {
					tagbox.val(tagbox.val().replace(/myown[ ]?/gi, ''));
				}
			}).parent().removeClass('hiddenElement');
}

$(document).ready(
		function(){
			myownTagInit($('#myownChkBox'), $('#inpf'));
		}
);

function initSuggestionForPartTitles(el) {
	el.each(function(index){ $(this).autocomplete({
		source: function( request, response ) {

			$.ajax({
				url: "http://www.bibsonomy.org/json/tag/" + createParameters(request.term),
				data: {items: 10,resourcetype: 'publication', duplicates: 'no'},
				dataType: "jsonp",
				success: function( data ) {
					response( $.map( data.items, function( item ) {
						return {
							label: (highlightMatches(item.label, request.term)+' ('+item.year+')'),
							value: item.interHash,
							url: 
								'hash='+item.intraHash
								+'&user='+item.user+'&copytag='
								+item.tags,
								author: (concatArray(item.author, 40, ' '+getString('and')+' ')),
								user: item.user,
								tags: item.tags,
						}
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