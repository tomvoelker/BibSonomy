$(function() {
	$('.createcomment').submit(createComment);
	
	$('.updatecomment').hide().submit(function() {
		var form = $(this);
		var data = form.serialize();
		
		$.ajax({
			url: '/ajax/comments',
			method: 'POST',
			data: data,
			success: function(data) {
				var textfield = form.find('textarea[name=discussionItem\\.text]');
				var text = textfield.val();
				var commentUI = form.parent();
				commentUI.find('.text:first').text(text);
				var hash = data.hash;
				var infoUI = commentUI.find('div.info:first');
				// alert(hash);
				infoUI.data('discussion-item-hash', hash);
				var updateForm = commentUI.find('form.updatecomment:first');
				var updateHashInput = updateForm.find('input[name=discussionItem\\.hash]');
				updateHashInput.val(hash);
				var commentForm = commentUI.find('form.createcomment:first');
				var input = commentForm.find('input[name=discussionItem\\.parentHash]');
				input.val(hash);
				// alert(infoUI.data('discussion-item-hash'));
				form.hide();
			}
		});
		return false;
	});
});

function createComment() {
	var form = $(this);
	var data = form.serialize();
	
	$.ajax({
		url: '/ajax/comments',
		method: 'POST',
		data: data,
		success: function(data) {
			var textfield = form.find('textarea[name=discussionItem\\.text]');
			var text = textfield.val();
			
			var parentHash = form.find('input[name=discussionItem\\.parentHash]');
			
			var commentTemplate = $('#commentTemplate').clone();
			
			if (parentHash.length > 0) {
				form.parent().parent().append(commentTemplate);
			} else {
				form.parent().after(commentTemplate);
			}
			
			commentTemplate.show();
			setupActions(commentTemplate, text, data.hash);
			
			var textarea = commentTemplate.find('form.updatecomment').find('textarea');
			textarea.val(text);
			autosize(textarea);
			// reset form
			commentTemplate.effect("highlight", {}, 2500);
			textfield.val('');
		}
	});
	
	return false;
}