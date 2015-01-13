
$(document).ready(function () {
	$('input[id=approveChkBox]').click(function(){
		alert('clicked');
		var checked = $(this).is(':checked');
		alert(checked);
		if(checked){
			$('input[name=approved]').val(1);
		}
		else{
			$('input[name=approved]').val(0);
		}
	});
	
});


maximizeById("general");