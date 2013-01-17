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
    $('.changeLayout').change(function(e){
    	
    	
        e.preventDefault();
        
        $.ajax({
            type: "GET",
            url: "/ajax/cv",
            data: {
                layout: $(this).find("option:selected").attr("data-layout"),
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
                    handleSuccessStatus("changing layout to " + $(this).find("option:selected").text());
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
    $('#loadingDiv').ajaxStart(function(){
        $('#statusField').show();
        $('#loadingDiv').show();
        handleLoadingStatus("");
//        $('#errorField').hide();
    }).ajaxSuccess(function(){
        $('#loadingDiv').hide();
        $('#statusField').hide();
    }).ajaxError(function() {
        handleErrorStatus("");
        $('#loadingDiv').hide();
    });
    
    /**
     * Handler to toggle the admin panel
     */
    $('#hideAdmin').click(function(){
        $('#fsform').hide('blind',function() {
			$('#showAdminField').show();
			$('#hideAdminField').hide();
		});
    });
    
    $('.toggleImage').click(function(e){
    	alert($(e).html);
    });
	
	/**
	 * Handler to toggle the admin panel
	 */
	$('#showAdmin').click(function() {
		$('#fsform').show('blind',function() {
			$('#showAdminField').hide();
			$('#hideAdminField').show();
		});
	});
	
	/**
	 * Handler for the layout form 
	 * @param {Object} e
	 */
	$('#layoutButton').click(function() {
		$('#layouts').toggle("blind");
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
