$("#loginDialog").modal('toggle');
$("#signIn").on('click', function() {
    if (($("#username").val() === secret["username"]) & ($("#password").val() === secret["password"])){
    	$("#loginDialog").modal('hide');
	}
});
