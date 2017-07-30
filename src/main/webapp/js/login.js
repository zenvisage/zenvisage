$("#loginDialog").modal('toggle');
$("#signIn").on('click', function() {
	for (var i=0 ; i<secret.length ; i++) { 
	  var kv = secret[i]
	  if (($("#username").val() === kv["username"]) & ($("#password").val() === kv["password"])){
    	$("#loginDialog").modal('hide');
    	$("#loginModal").remove()
	  }
	}
});

// $("#loginDialog").modal('toggle');
// app.controller('passwordController', [
// 	'$scope', '$rootScope', '$http',
// 	function($scope, $rootScope, $http){
// 		$("#signIn").on('click', function() {
// 		 // if (($("#username").val() === secret["username"]) & ($("#password").val() === secret["password"])){
// 		 //    	$("#loginDialog").modal('hide');
// 		 //    	$("#loginModal").remove()
// 		 // }
// 	 	console.log("submit")
// 	 	var query = {};
// 	 	query["username"] = $("#username").val();
// 	    query["password"] = $("#password").val();

// 	 	$http.get('/zv/verifyPassword', query).then(
// 	        function (response) {
// 	          console.log("success: ", response);
// 	          // globalDatasetInfo["classes"] = JSON.parse(response.data)
// 	        },
// 	        function (response) {
// 	          console.log("failed: ", response);
// 	        }
// 	    )
// 	});
// }]);
// app.controller('passwordController', ['$scope' ,'$http', 'plotResults', '$compile', function ($scope, $http, plotResults, $compile) {
//  	$http.get('/zv/verifyPassword', query).then(
//         function (response) {
//           console.log("success: ", response);
//           // globalDatasetInfo["classes"] = JSON.parse(response.data)
//           // $('#class-creation-close-button')[0].click();
//         },
//         function (response) {
//           console.log("failed: ", response);
//         }
//     )
// });