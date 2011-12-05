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
function deleteUrl(self, url, hash, ckey){
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
    $(".postUrl").click(function(){
        var options = {
            success: function(data){;
                var object = $(this);
                var url = $("url", data).text();
				var status = $("status", data).text();
                var urlText = $("text", data).text();
                var ckey = $("ckey", data).text();
                var hash = $("hash", data).text();

                if("ok" == status) {
                	$('#urlList').prepend(function(){
                		var urlLnk = $('<a href="' + url + '">' + urlText + '</a>');
                		var element = $("<div></div>").append(urlLnk).append(' (').append($('<a href="">' + getString("post.bibtex.delete") + '</a>').click(function(){
                        deleteUrl(this, url, hash, ckey);
                        return false;
                    })).append(')');
                    $("#f_addURL").hide();
                    return element;
                })
				}else{
					alert(data.globalErrors[0].message);
                }
            },
		
        };
        
        $("#f_addURL").ajaxSubmit(options);
    });
});

/**
 * Function which sets the "f_addURL" - form to display = 'block'
 */
function addUrlForm(){
    document.getElementById('f_addURL').style.display = 'block';
}
