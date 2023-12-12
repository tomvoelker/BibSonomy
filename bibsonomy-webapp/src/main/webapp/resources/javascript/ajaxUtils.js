var ERROR_CLASS = 'dissError';
var FIELD_DATA_KEY = 'field';

/**
 * handles all error from AJAX_ERRORS
 * 
 * TODO: improve documentation
 * 
 * @param form
 * @param errors
 */
function handleAjaxErrors(form, errors) {
	form.find('.spinner').hide();
	
	if (errors.globalErrors) {
		$.each(errors.globalErrors, function(index, globalError) {
			// TODO: display in inline html?
			alert(decodeHTML(globalError.message));
		});
	}
	
	if (errors.fieldErrors) {
		$.each(errors.fieldErrors, function(index, fieldError) {
			var message = fieldError.message;
			var field = fieldError.field;
			
			// replace all '.'; and build name selector
			var fieldSelector = '[name=' + escapeSelector(field) + ']:first';
			
			// find field with name selector
			var formElement = form.find(fieldSelector);
			
			if (formElement.length != 0) {
				// add a dissError div
				var div = $('<div></div>').addClass(ERROR_CLASS).css('display', 'block');
				div.html(message);
				div.data(FIELD_DATA_KEY, field);
				formElement.after(div);
			}
		});
		
		// prepare the error classes
		prepareAjaxErrorBoxes(ERROR_CLASS);
	}
}

// FIXME: Does jQuery provide a escape function?!
// FIXME: doesn't escape all special chars missing '[', ']', …
function escapeSelector(selector) {
	return selector.replace(/\./g,'\\.');
}

function decodeHTML(string) {
	return $('<div />').html(string).text()
}

/**
 * TODO: copy and paste; merge with prepareErrorBoxes in function.js
 * @param className
 */
function prepareAjaxErrorBoxes(className) {
	$('.' + className).each(function() {
		var errorField = $(this);
		if (parseInt(errorField.html().length) == 0) {
			return true;
		}
				  
		errorField.mouseover(function() {
		   $(this).fadeOut('slow');
		});
		
		// get the name of the input/textarea/…
		var field = errorField.data(FIELD_DATA_KEY);
		
		// bind "change" and "keyup" event to all matched input/… field
		errorField.siblings('[name=' + escapeSelector(field) + ']').bind("change keyup", function() {
			errorField.fadeOut('slow');
		});
	});
}

function showAjaxAlert(type, message) {
	var alert = $('<div></div>')
		.attr('class', 'alert alert-dismissible alert-' + type)
		.attr('role', 'alert');

	var closeBtn = $('<button></button>')
		.attr('class', 'close')
		.attr('data-dismiss', 'alert')
		.html('<span aria-hidden="true">&times;</span>');

	alert.append(closeBtn);
	if (type === 'danger') {
		alert.append($('<strong></strong>').html(getString("error") + ': '));
	}
	alert.append(message);

	$('#globalAjaxAlerts').append(alert);

	// Scroll to the global AJAX alerts section
	$('html, body').animate({
		scrollTop: $("#globalAjaxAlerts").offset().top
	}, 1000);
}