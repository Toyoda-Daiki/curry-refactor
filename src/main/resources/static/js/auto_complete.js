'use strict'
$(function() {
    $.getJSON("http://localhost:8080/searchItem",
	    function(data) {
	        $('#name').autocomplete({
	            source : data,
	            autoFocus: true,
	            minLength : 1
	        });
	    });
});