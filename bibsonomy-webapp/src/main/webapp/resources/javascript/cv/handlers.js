/**
 * @author Bernd Terbrack
 */
/*
 * Handlers
 */
$(function(){
    /**
     * Handler for the layout-links
     */
    $('a.changeLayout').click(function(e){
        e.preventDefault();
        $.ajax({
            type: "GET",
            url: "/ajax/cv",
            data: {
                layout: this.dataset.layout,
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
    });
    
    /**
     * Handler for the ajax loading gif's
     */
    $('#loadingDiv').hide().ajaxStart(function(){
        $(this).show();
        $('#errorField').hide();
    }).ajaxStop(function(){
        $(this).hide();
    });
    
    /**
     * Handler for the
     */
    $('#startEdit').click(function(){
        $(this).parent().hide('clip', function(){
            $(this).parent().next().next().show('clip', function(){
                $(this).prev().show('clip');
            });
        });
        return false;
    });
    
    /**
     * Handler for the textfield shortcuts
     * @param {Object} e
     */
    $('#wikiTextArea').keydown(function(e){
        if (e.ctrlKey) {
            if (e.keyCode == 13) { //ENTER
                e.preventDefault();
                submitWiki('preview');
            }
            else 
                if (e.keyCode == 80) { //"p"
                    e.preventDefault();
                    submitWiki('preview');
                }
                else 
                    if (e.keyCode == 83) { //"s"
                        e.preventDefault();
                        submitWiki('save');
                    }
                    else 
                        if (e.keyCode == 46) { //DELETE
                            e.preventDefault();
                            clearCVTextField();
                        }
        }
    });
});
