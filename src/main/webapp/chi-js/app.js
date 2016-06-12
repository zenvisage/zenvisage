var app = angular.module('zenvisage', []);


// responsible for generating the query
app.controller('queryInputController', [
  '$scope', '$http',
  function($scope, $http){

    $scope.onDatasetChange = function () {
      updateEverything();
    };

    function updateEverything()
    {
      var q = constructDimensionChangeQuery();
      var params = {
        "query": q
      };
      var config = {
        params: params
      };

      console.log(config);

      $http.get('/zv/getdata', config).
      success(function(response) {
        console.log("success");
      }).
      error(function(response) {
        console.log("fail");
      });
    }
}]);


// populates and controls the dataset attributes on the left-bar
app.controller('datasetController', [
  '$scope', '$http',
  function($scope, $http){

    // TODO: params will need to be dynamic later
    var q = constructDatasetChangeQuery("real_estate");

    var params = {
      "query": q,
    };
    var config = {
      params: params,
    };

    $http.get('/zv/getformdata', config).
      success(function(response) {
        $scope.categories = [];
        $scope.xAxisItems = [];
        $scope.yAxisItems = [];
        $scope.selectedCategory;
        $scope.selectedXAxis;
        $scope.selectedYAxis;
        angular.forEach(response.zAxisColumns, function(value, key) {
         $scope.categories.push(key);
        });
        $scope.selectedCategory = $scope.categories[0];
        angular.forEach(response.xAxisColumns, function(value, key) {
         $scope.xAxisItems.push(key);
        });
        $scope.selectedXAxis = $scope.xAxisItems[0];
        angular.forEach(response.yAxisColumns, function(value, key) {
         $scope.yAxisItems.push(key);
        });
        $scope.selectedYAxis = $scope.yAxisItems[0];
      }).
      error(function(response) {
        alert('Request failed: /getformdata');
      });
}]);


// need to make angular
$("a.tooltip-question").tooltip();

$(function () {
  $('[data-toggle="popover"]').popover()
})


$(document).ready(function() {
  $( ".tabler" ).click(function() {
    $( ".tabler" ).removeClass("info");
    $(this).addClass("info");
  });

  $('#first-flash').click(function(){
    $(this).addClass("hide");
    $(this).after("<div class=\"result-x\">"+ $(this).attr("data") + "<a><span class=\"glyphicon glyphicon glyphicon-remove\"></span></a></div>");
    tree.initialize();
  });

  $('tbody').on('click', '.flash-button', function() {
    var resultNum = $(this).attr("data");
    $(this).addClass("hide");
    $(this).after("<div class=\"result-x\">"+ $(this).attr("data") + "<a><span class=\"glyphicon glyphicon glyphicon-remove\"></span></a></div>");
    var rowCount = $("#zql-table > tbody")[0].rows.length;
    var currentRow = resultNum.substring(1);
    //$("#process-" + currentRow).text()
    tree.addLeaf(rowCount-2);
  });

  $('#tree-option').click(function() {
    $(this).toggleClass("active");
    $("#tree-div").toggle("active");
  });

  $('#list-option').click(function() {
    $(this).toggleClass("active");
    $("#zql-table").toggle("active");
  });

  $('#add-row').click(function(){
    addRow();
  });
});


function executeRow(){
  $(this).hide();
}

function addRow() {
  var table = $("#zql-table > tbody")[0];
  var rowCount = table.rows.length;
  var rowNumber = (rowCount+1).toString();
  $("#zql-table").append("<tr id=\"row-" + rowNumber + "\"class=\"tabler\"><td><div><div class=\"icon\"><span class=\"glyphicon glyphicon-triangle-left\"></span></div><div class=\"number\">" + rowNumber + "</div></div></td><td><input class=\"form-control zql-table\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table\" type=\"text\" size=\"20\" value=\" \"></td><td><input class=\"form-control zql-table\" type=\"text\" id=\"process-" + rowNumber + "\"size=\"25\" value=\" \"></td><td><div><button type=\"button\" class=\"btn btn-default btn-xs flash-button\" data=\"R" + rowNumber + "\"><span class=\"glyphicon glyphicon glyphicon-flash\" ></span></button></div></td><td></td></tr>");
}

