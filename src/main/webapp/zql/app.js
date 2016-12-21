/*global console */
/*global angular */
var app = angular.module('myApp', []);

app.controller('MyController', ['$scope', '$http', function ($scope, $http) {

	$scope.input = {};
	$scope.queries = {};
    $scope.queries['zqlRows'] = [];
    $scope.parsed = {};
    $scope.parsed['zqlRows'] = [];

	$scope.addRow = function () {
        //console.log(checkConstraints($scope.input.constraints));
        // Create a copy of parsed version of input for backend
        $scope.copy = angular.copy($scope.input);

        if (checkInput($scope.copy)) {
            console.log("request: ",$scope.copy);
            // Add to rows for front-end display
            $scope.queries['zqlRows'].push($scope.input);

            $scope.parsed['zqlRows'].push($scope.copy);
            $scope.input = {};
        }
	};

    $scope.submitZQL = function () {
    	$("#views_table").empty();
        $http.get('http://localhost:8080/zv/executeZQLComplete', {params: {'query': JSON.stringify($scope.parsed)}}
        ).then(
            function (response) {
                console.log("success: ", response);
                console.log(response);
                processBackEndData(response.data);
                $scope.queries = {};
                $scope.queries['zqlRows'] = [];
                $scope.parsed = {};
                $scope.parsed['zqlRows'] = [];
            },
            function (response) {
                console.log("failed: ", escape(response));
            }
        );
    };

}]);

// check for emput x, y and z and then check for syntax correctness
function checkInput(input) {
    var essentialColumns = input.name && input.x && input.y;
    if (essentialColumns === undefined) {
        console.error("X or Y or Z Column cannot be empty.");
        return false;
    }

    input.name = parseName(input.name);
    input.x = parseX(input.x);
    input.y = parseY(input.y);


    var constraints = null, viz = null, processe = null, z = null;
    if (input.z !== undefined) {
        input.z = parseZ(input.z);
    }
    if (input.constraints !== undefined) {
        input.constraints = parseConstraints(input.constraints);
    }
    if (input.viz !== undefined) {
        input.viz = parseViz(input.viz);
    }
    if (input.processe !== undefined) {
        input.processe = parseProcess(input.processe);
    }

    return (name && x && y && z && constraints && viz && processe) !== undefined;
}