angular.module('zenvisageControllers')

    .controller('InputController', ['$scope','$state', function ($scope, $state) {
        $scope.stuff = "This is an input element!";
        $scope.state = $state;
    }]);