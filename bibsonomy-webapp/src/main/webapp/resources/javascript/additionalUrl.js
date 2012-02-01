/**
 * @author bte
 */
/**
 * Function to delete the given url
 * 
 * @param {Object}
 *            url
 * @param {Object}
 *            hash
 * @param {Object}
 *            ckey
 */
function deleteUrl(self, url, hash, ckey) {
    $.ajax({
        type: "GET",
        url: "/ajax/additionalURLs",
        data: {
            action: 'deleteUrl',
            url: url,
            hash: hash,
            ckey: ckey
        },
		 success: function(data){
			var status = $("status", data).text();
            if("ok" == status) {
					$(self).parent().remove();
            } else {
        	   alert(data.globalErrors[0].message);
            }
		}
        
    });
}

/**
 * Function to post the given url which is defined in form: f_addURL
 * (bibtexdetails.tagx)
 */
$(function(){
    $(".postUrl").click(function() {
    	/*
    	 * load the jQuery script for sending the form
    	 */
    	$.getScript("/resources/jquery/plugins/form/jquery.form.js", function() {
    	
	    	
	        $("#f_addURL").ajaxSubmit({
	            success: function(data) {;
	            	var url = $("url", data).text();
	            	var status = $("status", data).text();
	            	var urlText = $("text", data).text();
	            	var ckey = $("ckey", data).text();
	            	var hash = $("hash", data).text();
	
	            	if ("ok" == status) {
	            		$('#urlList').prepend(function() {
	            			$("#f_addURL").hide();
	            			return $("<div><a href='" + url + "'>" + urlText + "</a> (</div>")
	            				.append($("<a href=''>" + getString("post.bibtex.delete") + "</a>").click(function() {
	            					deleteUrl(this, url, hash, ckey);
	            					return false;
	            				})).append(')');
	            		});
	            	} else {
	            		alert(data.globalErrors[0].message);
	            	}
	        	}
	        });
    	});
    });
});

/**
 * Function which sets the "f_addURL" - form to display = 'block'
 */
function addUrlForm() {
    $('#f_addURL').css("display", "block");
}
