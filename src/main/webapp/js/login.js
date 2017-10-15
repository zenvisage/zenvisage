
// $('#signIn')['on']('click', function() {
//     for (var _0xd97cx1 = 0; _0xd97cx1 < secret['length']; _0xd97cx1++) {
//         var _0xd97cx2 = secret[_0xd97cx1];
//         if (($('#username')['val']() === _0xd97cx2['username']) & ($('#password')['val']() === _0xd97cx2['password'])) {
//             $('#loginDialog')['modal']('hide');
//             $('#loginModal')['remove']()
//         }
//     }
// })

// $(document).ready(function(){

$("#login_cancel").on('click',function(){
	$('#loginModal').modal('hide');
});

$(window).on('load', function() {
	$('#loginModal').modal('show');
})

$("#signIn").on('click',function(e){
	// e.preventDefault();
	$('#loginModal').modal('hide');
	$.ajax({
           type: "POST",
           url: "/zv/login",
           data: $("#logInForm").serialize(), // serializes the form's elements.
           success: function(response)
           {
           		angular.element($('#sidebar')).scope().updatetablelist(response);
           }
	});

	return false;
});
