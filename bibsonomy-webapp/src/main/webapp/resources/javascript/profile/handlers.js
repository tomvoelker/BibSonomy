/**
 * Handlers for the user profile update site
 * @author cut
 */

$(function(){
	
	$('#useInt').click(function(){
		$('#pictUseInt').show();
		$('#pictUseExt').hide();
	});
	
	$('#useExt').click(function(){
		$('#pictUseInt').hide();
		$('#pictUseExt').show();
	});
	
	$('#useIntPath').change(function(){
		if ( $('#useIntPath').val() != '' )
			$('#useIntDelete').prop('disabled', true);
		else
			$('#useIntDelete').prop('disabled', false);
	});
	
	$('#useIntDelete').change(function(){
		if ( $('#useIntDelete').is(':checked') )
			$('#useIntPath').prop('disabled', true);
		else
			$('#useIntPath').prop('disabled', false);
	});

});