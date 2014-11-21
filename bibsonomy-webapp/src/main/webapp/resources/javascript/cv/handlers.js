/**
 * @author Bernd Terbrack
 */
/*
 * Handlers
 */
$(function(){

	/*
     * Handler for the layout-links
     */
    $('#changeLayout').change(function(e){
        e.preventDefault();
        $.ajax({
            type: "POST",
            url: "/ajax/cv",
            data: {
                layout: $(this).find("option:selected").attr("data-layout"),
                ckey: ckey
            },
            success: function(data){
                var status = $("status", data).text();
                if ("ok" == status) {
                    var wikiText = $("wikitext", data).text();
                    var renderedWikiText = $("renderedwikitext", data).text();
                    var wikiTextArea = $('#wikiTextArea');
                    if ("" != renderedWikiText) {
                        var wikiArea = $('#wikiArea');
                        wikiTextArea.val(wikiText);
                        wikiArea.empty();
                        wikiArea.append(renderedWikiText);
                        handleSuccessStatus("changing layout to " + $(this).find("option:selected").text());
                    }
                }
                // status is not ok.
                else {
                    handleError(data.globalErrors[0].message);
                }
            }
        });
    });
    
    /**
     * Handler for the ajax loading gif's
     */
    $('#loadingDiv').ajaxStart(function(){
        $('#loadingDiv').show();
        $('#saveButton').toggleClass("disabled");
    }).ajaxSuccess(function(){
        $('#loadingDiv').hide();
        $('#saveButton').toggleClass("disabled");
    }).ajaxError(function() {
        $('#loadingDiv').hide();
        // this now enforces the preview buttons to be shown.
        $('#saveButton').toggleClass("disabled");
        // TODO: Handle Errors correctly.
//        handleError("Rendering error.");
    });
	
    
    /**
     * Handler to hide the cvedit panel
     */
    $('#hideAdmin').click(function(){
        $('#wikiEditArea').hide('blind',function() {
			$('#showAdmin').show();
			$('#hideAdmin').hide();
		});
    });
    
	/**
	 * Handler to show the cvedit panel
	 */
	$('#showAdmin').click(function() {
		$('#wikiEditArea').show('blind',function() {
			$('#showAdmin').hide();
			$('#hideAdmin').show();
		});
	});
});

/**
 * In case an error happens, do this
 * @param {Object} e
 */
function handleError(e){
    $('#statusText').text(e);
    $('#statusField').removeClass('error success').addClass('error');
}

function handleSuccessStatus(e) {
	$('#statusText').text("");
	$('#statusField').removeClass('error success').addClass('success');
}

/**
 * Method to send a renderRequest to the server
 * @param {Object} renderOptions
 */
function submitWiki(renderOptions){
    $.ajax({
        type: "POST",
        url: "/ajax/cv",
        data: {
            ckey: $('#ckey').val(),
            wikiText: $('#wikiTextArea').val(),
            renderOptions: renderOptions
        },
        success: function(data){
            var status = $("status", data).text();
            if ("ok" == status) {
                var wikiArea = $('#wikiArea');
                var renderedWikiText = $("renderedwikitext", data).text();
                wikiArea.empty();
                wikiArea.append(renderedWikiText);
                handleSuccessStatus(renderOptions);
            } else {
                handleError(data.globalErrors[0].message);
            }
        }
    });
    return false;
}

/**
 * Method which is called on an external publication layout change
 * @param {Object} self
 * @param {Object} type
 */
function formatPublications(self, type){
    var layout = $(self).val();
    var tags = $('#reqTags').val();
    var reqUser = $('#reqUser').val();
    $(self).parent().parent().parent().next().empty();
    $.get("/layout/" + layout + "/" + type + "/" + reqUser + "/" + tags + "?formatEmbedded=true", function(data){
        $(self).parent().parent().parent().next().html(data);
    });
    handleSuccessStatus("Loading " + layout + " style");
    return false;
}

/**
 * Method used on clear cv-textfield request
 */
function clearCVTextField(){
    var wikiTextArea = $('#wikiTextArea');
    wikiTextArea.val("");
    handleSuccessStatus("Clear");
    submitWiki("preview");
    return false;
}

/**
 * Method for the bookmark details (show/hide)
 * @param {Object} element
 */
function toggleDetails(self){
    var details = $(self).next();
    details.toggle();
    return false;
}
