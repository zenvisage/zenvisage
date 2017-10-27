// var app = angular.module('zenvisage', []);

  app.controller('loginModalController', [
      '$scope', '$rootScope', '$http', '$cookies','datasetInfo',
      function($scope, $rootScope, $http, $cookies, datasetInfo){

    $("#login_cancel").on('click',function(){
      $('#loginModal').modal('hide');
    });

    $("#loginmodaltrigger").on('click',function(e){
        if(!$cookies.getObject("userinfo")){
          $('#loginModal').modal('show');
        }else{
          alert("You have logged in")
        }
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
                    alert("You have logged in successfully");

                    var today = new Date();
                    var expiresValue = new Date(today);
                    //Set 'expires' option in 2 hours
                    expiresValue.setMinutes(today.getMinutes() + 120);
                    $cookies.putObject("userinfo",response,{'expires': expiresValue})
                    // angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
                    datasetInfo.storetablelist(response['tablelist']);
                    $scope.tablelist = datasetInfo.getTablelist();
                    location.reload();
                  }else{
                    alert("Failed to log in");
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
                    alert("You have registered successfully and are now logged in");

                    var today = new Date();
                    var expiresValue = new Date(today);
                    //Set 'expires' option in 2 hours
                    expiresValue.setMinutes(today.getMinutes() + 120);
                    $cookies.putObject("userinfo",response,{'expires': expiresValue})
                    // angular.element($('#sidebar')).scope().updatetablelist(response['tablelist']);
                    datasetInfo.storetablelist(response['tablelist']);
                    $scope.tablelist = datasetInfo.getTablelist();
                    location.reload();

                  }else{
                    alert("Username already exists, please login or use another username");
                  }
                  
               }
      });
      return false;
    });


    $("#signoutbutton").on('click',function(e){
        $cookies.remove("userinfo");
        alert("You have signed out!");
        location.reload();

    });
  }]);


