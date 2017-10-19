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
              console.log(response);
              if(response){
                alert("Log in successfully");
                angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
              }else{
                alert("Fail to log in");
              }
           }
	});
	return false;
});


$("#register").on('click',function(e){
  // e.preventDefault();
  $('#loginModal').modal('hide');
  $.ajax({
           type: "POST",
           url: "/zv/register",
           data: $("#logInForm").serialize(), // serializes the form's elements.
           success: function(response)
           {
              console.log(response);
              if(response){
                alert("Register successfully");
                angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
              }else{
                alert("Fail to register");
              }
              
           }
  });
  return false;
});


// angular.module('zenvisage', ['ngCookies'])
// .controller('ExampleController', ['$cookieStore', function($cookieStore) {
//   // Put cookie
//   $cookieStore.put('myFavorite','oatmeal');
//   // Get cookie
//   var favoriteCookie = $cookieStore.get('myFavorite');
//   // Removing a cookie
//   $cookieStore.remove('myFavorite');
// }]);

