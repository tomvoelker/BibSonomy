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
    $('.changeLayout').click(function(e){
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
     * Handler to toggle the admin panel
     */
    $('#hideAdmin').click(function(){
        $('#fsform').hide('blind',function() {
			$('#showAdminField').show('blind');
		});
    });
	
	/**
	 * Handler to toggle the admin panel
	 */
	$('#showAdmin').click(function() {
		$('#fsform').show('blind',function() {
			$('#showAdminField').hide('blind');
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
