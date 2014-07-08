$(function() {
	$('.removeFriend').hover(removeFriendHover);
	
	$('.removeFriend').mouseleave(removeFriendLeave);
	
	$('.removeFriend').click(function() {
		var form = $(this).parent();
		$.post(form.attr('action'), form.serialize());
		
		form.parent().parent().parent().remove();
		return false;
	});
	
	$('.addFriend').click(function() {
		$(this).addClass('btn-success').empty();
		$(this).addClass('.removeFriend');
		$(this).append($('<span class="glyphicon glyphicon-ok"><span>'));
		
		var info = $('<span class="infotext"><span>');
		info.text(getString('friendoverview.friend'));
		$(this).append(' ');
		$(this).append(info);
		$(this).hover(removeFriendHover);
		$(this).mouseleave(removeFriendLeave);
		
		var form = $(this).parent();
		$.post(form.attr('action'), form.serialize());
		
		form.find('input[name=action]').attr('value', 'removeFriend');
		return false;
	});
});

function removeFriendHover() {
	$(this).removeClass('btn-success').addClass('btn-danger');
	$(this).children('.glyphicon').removeClass('glyphicon-ok').addClass('glyphicon-remove');
	$(this).children('.infotext').text(getString('friend.remove'));
}

function removeFriendLeave() {
	$(this).removeClass('btn-danger').addClass('btn-success');
	$(this).children('.glyphicon').removeClass('glyphicon-remove').addClass('glyphicon-ok');
	$(this).children('.infotext').text(getString('friendoverview.friend'));
}