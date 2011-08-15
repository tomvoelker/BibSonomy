/**
 * @author Bernd
 */
function changeCVLayout(name){
    $.ajax({
        type: "GET",
        url: "/ajax/cv",
        data: {
            layout: name
        },
		 success: function(data){
			 alert("Yay, Callback!");
		}
        
    });
}