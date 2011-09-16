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
                }
                else {
                    wikiTextArea.val(wikiText);
                }
            }
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
    $('#errorText').text(e);
    $('#errorField').show();
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
    var tags = $(self).next().val();
    var reqUser = $('#reqUser').val();
    $(self).parent().parent().parent().next().empty();
    $.get("/layout/" + layout + "/" + type + "/" + reqUser + "/" + tags, function(data){
        $(self).parent().parent().parent().next().html(data);
    });
    return false;
}

/**
 * Method used on clear cv-textfield request
 */
function clearCVTextField(){
    var wikiTextArea = $('#wikiTextArea');
    wikiTextArea.val("");
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
