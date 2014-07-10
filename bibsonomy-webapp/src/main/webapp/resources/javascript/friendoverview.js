$(function() {
	$('.removeFriend').hover(removeFriendHover);
	$('.removeFriend').mouseleave(removeFriendLeave);
	$('.removeFriend').click(removeFriendClick);
	$('.addFriend').click(addFriendClick);
});

function addFriendClick() {
	$(this).hide();
	$(this).siblings('.removeFriend').show();
	
	var form = $(this).parent();
	form.find('input[name=action]').attr('value', 'addFriend');
	$.post(form.attr('action'), form.serialize());
	
	return false;
}

function removeFriendClick() {
	$(this).hide();
	$(this).siblings('.addFriend').show();
	
	var form = $(this).parent();
	form.find('input[name=action]').attr('value', 'removeFriend');
	$.post(form.attr('action'), form.serialize());
	return false;
}

function removeFriendHover() {
	$(this).removeClass('btn-success').addClass('btn-danger');
	$(this).children('.glyphicon').removeClass('glyphicon-ok').addClass('glyphicon-remove');
	$(this).children('.infotext').text(getString('friendoverview.friend.remove'));
}

function removeFriendLeave() {
	$(this).removeClass('btn-danger').addClass('btn-success');
	$(this).children('.glyphicon').removeClass('glyphicon-remove').addClass('glyphicon-ok');
	$(this).children('.infotext').text(getString('friendoverview.friend'));
}