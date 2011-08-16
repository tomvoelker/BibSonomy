/**
 * @author Bernd
 */
function changeCVLayout(name,ckey){
    $.ajax({
        type: "GET",
        url: "/ajax/cv",
        data: {
            layout: name,
            ckey: ckey
        },
		 success: function(data){
			 var status = $("status", data).text();
	         if("ok" == status) {
	        	 var wikiText = $("wikitext", data).text();
	        	 var wikiTextArea = $('#wikiTextArea');
				 wikiTextArea.val(wikiText);
            } else {
        	   alert(data.globalErrors[0].message);
            }
		}
        
    });
}