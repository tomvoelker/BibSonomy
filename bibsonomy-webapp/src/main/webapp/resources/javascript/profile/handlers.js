// TODO: move to settings.js file
/**
 * Handlers for the user profile update site
 * @author cut
 */

/**
 * Shows form fields for external picture source; 
 * hides those for internal source. 
 */
function showExternal ()
{
	$('#pictUseInt').hide();
	$('#pictUseExt').show();
}

/**
 * Shows form fields for internal picture source; 
 * hides those for external source. 
 */
function showInternal ()
{
	$('#pictUseInt').show();
	$('#pictUseExt').hide();
}

$(function(){
	/*
	 * If picture source changed:
	 * 		external chosen -> show external, hide internal
	 * 		internal chosen -> vice versa.
	 * This now work using IE, hopefully using Chrome, too.
	 */
	$('#useExtPicSelect').change(function(){
		if ( $('#useExtPicSelect').val() == $('#useExt').val() )
			showExternal();
		else
			showInternal();
	});
	
//	This doesn't seems to work using IE, Chrome, ... :-(	
//	$('#useInt').click(function(){
//		showInternal();
//	});
//	$('#useExt').click(function(){
//		showExternal();
//	});
	
	/*
	 * If internal picture path changed:
	 * 		path filled in	-> disable delete button
	 * 		path empty		-> enable delete button
	 */
	$('#useIntPath').change(function(){
		if ( $('#useIntPath').val() != '' )
			$('#useIntDelete').prop('disabled', true);
		else
			$('#useIntDelete').prop('disabled', false);
	});
	
	/*
	 * If delete internal picture changed:
	 * 		checked		-> disable path field
	 * 		unchecked	-> enable path field
	 */
	$('#useIntDelete').change(function(){
		if ( $('#useIntDelete').is(':checked') )
			$('#useIntPath').prop('disabled', true);
		else
			$('#useIntPath').prop('disabled', false);
	});

});