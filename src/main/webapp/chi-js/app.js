var app = angular.module('zenvisage', []);

// populates the dataset attributes on the left-bar
app.controller('datasetController', [
  '$scope', '$http',
  function($scope, $http){

    // TODO: params will need to be dynamic later
    var params = {
      "query": {"databasename": "real_estate"},
    };
    var config = {
      params: params,
    };
    $http.get('/zv/getformdata', config).
      success(function(response) {
        $scope.categories = [];
        $scope.xAxisItems = [];
        $scope.yAxisItems = [];
        angular.forEach(response.zAxisColumns, function(value, key) {
         $scope.categories.push(key);
        });
        angular.forEach(response.xAxisColumns, function(value, key) {
         $scope.xAxisItems.push(key);
        });
        angular.forEach(response.yAxisColumns, function(value, key) {
         $scope.yAxisItems.push(key);
        });
      }).
      error(function(response) {
        alert('Request failed: /getformdata');
      });
}]);

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

