// var app = angular.module('zenvisage', []);

  app.controller('loginModalController', [
      '$scope', '$rootScope', '$cookies', 'datasetInfo',
      function($scope, $rootScope, $cookies, datasetInfo){

    $("#login_cancel").on('click',function(){
      $('#loginModal').modal('hide');
    });

    $("#loginmodaltrigger").on('click',function(e){
      // $(window).on('load', function() {
        if(!$cookies.getObject("userinfo")){
          $('#loginModal').modal('show');
        }else{
          alert("You have signed in")
        }
      // })
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

                    var today = new Date();
                    var expiresValue = new Date(today);
                    //Set 'expires' option in 2 hours
                    expiresValue.setMinutes(today.getMinutes() + 120);
                    $cookies.putObject("userinfo",response,{'expires': expiresValue})
                    angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
                    // datasetInfo.storetablelist(response['tablelist']);
                    // $scope.tablelist = datasetInfo.getTablelist();
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
                    // datasetInfo.storetablelist(response['tablelist']);
                    // $scope.tablelist = datasetInfo.getTablelist();
                  }else{
                    alert("Username already exists, please login or choose another one");
                  }
                  
               }
      });
      return false;
    });

  }]);


