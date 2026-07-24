'use strict';
$(function () {
	
	$('#reset_button').on('click', function () {
		$('#name').val('');
		console.log("test");
	})
	
	$('#reset_button_admisearch').on('click', function () {
		$('#name').val('');
		console.log("test");
	})
	
	$('#reset_button_user').on('click', function () {
		$('#inputName').val('');
		$('#inputEmail').val('');
		$('#zipcode').val('');
		$('#address').val('');
		$('#inputTel').val('');
		$('#inputPassword').val('');
		$('#inputConfirmationPassword').val('');
		console.log("test");
	})
	
	$('#reset_button_admi').on('click', function () {
		$('#inputName').val('');
		$('#inputEmail').val('');
		$('#inputPassword').val('');
		$('#inputConfirmationPassword').val('');
		console.log("test");
	})
	
	
});