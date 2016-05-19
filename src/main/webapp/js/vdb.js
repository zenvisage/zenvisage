//No Angular required currently.
angular.module('vdb', [])
  .controller('formController', ['$scope','$rootScope', function($scope, $rootScope) {
    $rootScope.cols = [];

    $scope.enterForm = function () {
      $rootScope.cols = [
        //{text:'Columns'}
        ]; //reset cols everytime form is entered


      for(i=0; i < columnNum; i++){
        var dropdown = $("#col"+i);
        //alert ($(dropdown).val());
        $rootScope.cols.push({text:$(dropdown).val()});
      }

    };
    //not related:
    $scope.remaining = function() {
      var count = 0;
      angular.forEach($scope.todos, function(todo) {
        count += todo.done ? 0 : 1;
      });
      return count;
    };
    //not related
    $scope.archive = function() {
      var oldTodos = $scope.todos;
      $scope.todos = [];
      angular.forEach(oldTodos, function(todo) {
        if (!todo.done) $scope.todos.push(todo);
      });
    };
  }]);

  //Simulates getting backend data
  function getvdbData(column){
    index = 0;
    if(column.indexOf("A")>-1){
      index = 1;
    }
    if(column.indexOf("B")>-1){
      index = 2;
    }
    if(column.indexOf("C")>-1){
      index = 3;
    }
    if(column.indexOf("D")>-1){
      index = 4;
    }
    if(column.indexOf("E")>-1){
      index = 5;
    }
    if(column.indexOf("1")> -1){
      return temp_data_db1[index];
    }
    if(column.indexOf("2")>-1){
      return temp_data_db2[index];
    }
    if(column.indexOf("3")>-1){
      return temp_data_db3[index];
    }

  }
