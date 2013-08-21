/**
 * @author Bernd Terbrack
 */

/**
 * Method to change the layout. (via ajax)
 * @param {Object} name
 */
function changeCVLayout(name){
    $.ajax({
        type: "GET",
        url: "/ajax/cv",
        data: {
            layout: name,
            ckey: $('#ckey').val()
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
                    handleSuccessStatus("Changing Layout to " + name);
                }
                else {
                    wikiTextArea.val(wikiText);
                    handleErrorStatus("CV must not be empty");
                }
            }
            // status is not ok!
            else {
                handleError(data.globalErrors[0].message);
            }
        }
    });
    return false;
}

/**
 * In case an error happens, do this
 * @param {Object} e
 */
function handleError(e){
//    $('#statusText').text("Error: " + e);
    $('#statusField').removeClass('error success loading').addClass('error');
}

function handleLoadingStatus(e) {
//	$('#statusText').text("Loading... " + e);
    $('#statusField').removeClass('error success loading').addClass('loading');
}

function handleSuccessStatus(e) {
//	$('#statusText').text("Success: " + e);
    $('#statusField').removeClass('error success loading').addClass('success');
}

/**
 * Method to send a renderRequest to the server
 * @param {Object} renderOptions
 */
function submitWiki(renderOptions){
    $.ajax({
        type: "GET",
        url: "/ajax/cv",
        data: {
            ckey: $('#ckey').val(),
            wikiText: $('#wikiTextArea').val(),
            renderOptions: renderOptions
        },
        beforeSend: function(){
        },
        complete: function(){
        },
        success: function(data){
            var status = $("status", data).text();
            if ("ok" == status) {
                var wikiArea = $('#wikiArea');
                var renderedWikiText = $("renderedwikitext", data).text();
                wikiArea.empty();
                wikiArea.append(renderedWikiText);
                handleSuccessStatus(renderOptions);
            }
            else {
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
