/**
 *  @author jp
 */

var ellipsis = "...";
var lengthToBeTrimmed = 30;

function trimLength(text, maxLength)
{
    text = $.trim(text);

    if (text.length > maxLength)
    {
        text = text.substring(0, maxLength - ellipsis.length)
        return text.substring(0, text.lastIndexOf(" ")) + ellipsis;
    }
    else
        return text;
}


$(function() {
	var placeholders = [];
	$(".favouriteLayoutDisplayName").each(function() {
		var item = {source:$(this).attr("source").toUpperCase(), style:$(this).attr("style").toUpperCase()};
		placeholders.push(item);
	});
	
	processResult();
	
	function processResult(){
	//getting external JSON for CSL styles
	$.get("/csl-style", function (data) {
		//safety first, safety always
		data = $.trim(data);
		data = $.parseJSON(data);
		cslData = data;
		processResultCSL(data);
	});
	};
	
	//getting external JSON for JABREF styles
	$.get("/layoutinfo", function (data) {
		jabRefData = data;
		processResultJabref(data);
	});
	
	function processResultJabref(data) {
		for (var prop in data.layouts) {
			//replaces the shown displayName of each style with its correct "displayName"
			for (i = 0; i < placeholders.length; i++) {
				if(placeholders[i].source == "JABREF"){
					if(jabRefData.layouts[prop].name.toUpperCase() == placeholders[i].style){
						$(".favouriteLayoutDisplayName").each(function() {
							if($(this).attr("source").toUpperCase() == "JABREF"){
								if(jabRefData.layouts[prop].name.toUpperCase() == $(this).attr("style").toUpperCase()){
									$(this).text(trimLength(jabRefData.layouts[prop].displayName, lengthToBeTrimmed));
									$(this).removeClass("favouriteLayoutDisplayName");
								}
							}
						});
					}
				}
			};
		}
	};
	
	function processResultCSL(data) {
		for (var prop in data.layouts) {		
			//replaces the shown displayName of each style with its correct "displayName"
			for (i = 0; i < placeholders.length; i++) {
				if(placeholders[i].source == "CSL"){
					if(cslData.layouts[prop].name.toUpperCase() == placeholders[i].style){
						$(".favouriteLayoutDisplayName").each(function() {
							if($(this).attr("source").toUpperCase() == "CSL"){
								if(cslData.layouts[prop].name.toUpperCase() == $(this).attr("style").toUpperCase()){
									$(this).text(trimLength(cslData.layouts[prop].displayName, lengthToBeTrimmed));
									$(this).removeClass("favouriteLayoutDisplayName");
								}
							}
						});
					}
				}
			};
		}
	};
});