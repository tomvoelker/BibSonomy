
function setupTitleAutocomplete(inputFieldSelector) {
	var titleData = [
"Dr.",
"Dr. agr.",
"Dr. biol. anim.",
"Dr. biol. hom",
"Dr. cult.",
"Dr. h.c",
"Dr. iur. ",
"Dr. iur. can.",
"Dr. iur. et rer. pol ",
"Dr. iur. utr.",
"Dr. math.",
"Dr. med.",
"Dr. med. dent.",
"Dr. med. vet. ",
"Dr. nat. med.",
"Dr. nat. techn",
"Dr. oec.",
"Dr. oec. publ.",
"Dr. oec. troph.",
"Dr. paed",
"Dr. PH ",
"Dr. pharm.",
"Dr. phil. ",
"Dr. phil. in art. ",
"Dr. phil. nat.",
"Dr. rer. agr.",
"Dr. rer. biol. hum.",
"Dr. rer. biol. vet. ",
"Dr. rer. cult.",
"Dr. rer. cur.",
"Dr. rer. forest.",
"Dr. rer. hort.",
"Dr. rer. med.",
"Dr. rer. medic.",
"Dr. rer. merc",
"Dr. rer. mont.",
"Dr. rer. nat.",
"Dr. rer. oec.",
"Dr. rer. physiol",
"Dr. rer. pol.",
"Dr. rer. publ.",
"Dr. rer. sec.",
"Dr. rer. silv.",
"Dr. rer. soc. ",
"Dr. rer. tech.",
"Dr. sc. agr. ",
"Dr. sc. hum.",
"Dr. sc. mus.",
"Dr. sc. oec.",
"Dr. sc. phil. ",
"Dr. sc. soc",
"Dr. sc. techn.",
"Dr. Sportwiss.",
"Dr. theol. ",
"Dr. troph.",
"Dr.-Ing. ",
"Dr.-Ing. (FH).",
"Prof. Dr.",
"Prof. Dr. agr.",
"Prof. Dr. biol. anim.",
"Prof. Dr. biol. hom",
"Prof. Dr. cult.",
"Prof. Dr. h.c",
"Prof. Dr. iur. ",
"Prof. Dr. iur. can.",
"Prof. Dr. iur. et rer. pol ",
"Prof. Dr. iur. utr.",
"Prof. Dr. math.",
"Prof. Dr. med.",
"Prof. Dr. med. dent.",
"Prof. Dr. med. vet. ",
"Prof. Dr. nat. med.",
"Prof. Dr. nat. techn",
"Prof. Dr. oec.",
"Prof. Dr. oec. publ.",
"Prof. Dr. oec. troph.",
"Prof. Dr. paed",
"Prof. Dr. PH ",
"Prof. Dr. pharm.",
"Prof. Dr. phil. ",
"Prof. Dr. phil. in art. ",
"Prof. Dr. phil. nat.",
"Prof. Dr. rer. agr.",
"Prof. Dr. rer. biol. hum.",
"Prof. Dr. rer. biol. vet. ",
"Prof. Dr. rer. cult.",
"Prof. Dr. rer. cur.",
"Prof. Dr. rer. forest.",
"Prof. Dr. rer. hort.",
"Prof. Dr. rer. med.",
"Prof. Dr. rer. medic.",
"Prof. Dr. rer. merc",
"Prof. Dr. rer. mont.",
"Prof. Dr. rer. nat.",
"Prof. Dr. rer. oec.",
"Prof. Dr. rer. physiol",
"Prof. Dr. rer. pol.",
"Prof. Dr. rer. publ.",
"Prof. Dr. rer. sec.",
"Prof. Dr. rer. silv.",
"Prof. Dr. rer. soc. ",
"Prof. Dr. rer. tech.",
"Prof. Dr. sc. agr. ",
"Prof. Dr. sc. hum.",
"Prof. Dr. sc. mus.",
"Prof. Dr. sc. oec.",
"Prof. Dr. sc. phil. ",
"Prof. Dr. sc. soc",
"Prof. Dr. sc. techn.",
"Prof. Dr. Sportwiss.",
"Prof. Dr. theol. ",
"Prof. Dr. troph.",
"Prof. Dr.-Ing. ",
"Prof. Dr.-Ing. (FH).",
"PD Dr.",
"PD Dr. agr.",
"PD Dr. biol. anim.",
"PD Dr. biol. hom",
"PD Dr. cult.",
"PD Dr. h.c",
"PD Dr. iur. ",
"PD Dr. iur. can.",
"PD Dr. iur. et rer. pol ",
"PD Dr. iur. utr.",
"PD Dr. math.",
"PD Dr. med.",
"PD Dr. med. dent.",
"PD Dr. med. vet. ",
"PD Dr. nat. med.",
"PD Dr. nat. techn",
"PD Dr. oec.",
"PD Dr. oec. publ.",
"PD Dr. oec. troph.",
"PD Dr. paed",
"PD Dr. PH ",
"PD Dr. pharm.",
"PD Dr. phil. ",
"PD Dr. phil. in art. ",
"PD Dr. phil. nat.",
"PD Dr. rer. agr.",
"PD Dr. rer. biol. hum.",
"PD Dr. rer. biol. vet. ",
"PD Dr. rer. cult.",
"PD Dr. rer. cur.",
"PD Dr. rer. forest.",
"PD Dr. rer. hort.",
"PD Dr. rer. med.",
"PD Dr. rer. medic.",
"PD Dr. rer. merc",
"PD Dr. rer. mont.",
"PD Dr. rer. nat.",
"PD Dr. rer. oec.",
"PD Dr. rer. physiol",
"PD Dr. rer. pol.",
"PD Dr. rer. publ.",
"PD Dr. rer. sec.",
"PD Dr. rer. silv.",
"PD Dr. rer. soc. ",
"PD Dr. rer. tech.",
"PD Dr. sc. agr. ",
"PD Dr. sc. hum.",
"PD Dr. sc. mus.",
"PD Dr. sc. oec.",
"PD Dr. sc. phil. ",
"PD Dr. sc. soc",
"PD Dr. sc. techn.",
"PD Dr. Sportwiss.",
"PD Dr. theol. ",
"PD Dr. troph.",
"PD Dr.-Ing. ",
"PD Dr.-Ing. (FH)."
];
	
	var substringMatcher = function(strs) {
		return function findMatches(q, cb) {
			var matches, substrRegex;
			 
			// an array that will be populated with substring matches
			matches = [];
			 
			// regex used to determine if a string contains the substring `q`
			substrRegex = new RegExp(q, 'i');
			 
			// iterate through the pool of strings and for any string that
			// contains the substring `q`, add it to the `matches` array
			$.each(strs, function(i, str) {
				if (substrRegex.test(str)) {
					// the typeahead jQuery plugin expects suggestions to a
					// JavaScript object, refer to typeahead docs for more info
					matches.push({ value: str });
				}
			});
			 
			cb(matches);
		};
	}; 	
	
	// constructs the suggestion engine
	/*
	var titles = new Bloodhound({
		datumTokenizer: Bloodhound.tokenizers.obj.whitespace('value'),
		queryTokenizer: Bloodhound.tokenizers.whitespace,
		prefetch: {
			'/resources/javascript/person/titles.json',
			filter: function(data){
		           // filter the returned data
		           return data.items;
		    }
		}
		limit: 10000
	});

	// kicks off the loading/processing of `local` and `prefetch`
	titles.initialize();
*/
	var personNameTypeahead = $(inputFieldSelector).typeahead({
		hint: true,
		highlight: true,
		minLength: 1
	},
	{
		name: 'titles',
		hint: false,
		displayKey: 'value',
		// `ttAdapter` wraps the suggestion engine in an adapter that
		// is compatible with the typeahead jQuery plugin
		//source: titles.ttAdapter(),
		source: substringMatcher(titleData),
		maxItem: 2000
	});
}

$(document).ready(function() {
	setupTitleAutocomplete('#formAcademicDegree');
});