

var app = angular.module('zenvisage', []);


var globalDatasetInfo; //remove after fixing sketchpad controller
/*
sketchpad should be a controller
app.factory('sketchpadState', function() {

  var isDrawing = false;
  var lastDrawRow = null;
  var lastDrawValue = null;
  var sketchpadService = {};

  sketchpadService.setLastDrawRow = function( item ) {
      items.push(item);
  };
  sketchpadService.setLastDrawRow = function( item ) {
      items.push(item);
  };

  return sketchpadService;
});
*/

app.factory('datasetInfo', function() {
  var categoryData;
  var xAxisData;
  var yAxisData;
  var datasetService = {};

  datasetService.store = function( response ) {
    categoryData = response.zAxisColumns;
    xAxisData = response.xAxisColumns;
    yAxisData = response.yAxisColumns;
  };
  datasetService.getCategoryData = function()
  {
    return categoryData;
  }
  datasetService.getXAxisData = function()
  {
    return xAxisData;
  }
  datasetService.getYAxisData = function()
  {
    return yAxisData;
  }
  return datasetService;
});

app.factory('plotResults', function() {

    var plottingService = {};
    plottingService.displayUserQueryResults = function displayUserQueryResults( userQueryResults )
    {
      displayUserQueryResultsHelper( userQueryResults );

    }

    plottingService.displayRepresentativeResults = function displayRepresentativeResults( representativePatternResults )
    {
      displayRepresentativeResultsHelper( representativePatternResults )
    }

    plottingService.displayOutlierResults = function displayOutlierResults( outlierResults )
    {
      displayOutlierResultsHelper( outlierResults )
    }

    return plottingService;
});


// populates and controls the dataset attributes on the left-bar
// does not dynamically adjust to change in dataset yet
app.controller('datasetController', [
  '$scope', '$http', 'datasetInfo', 'plotResults',
  function($scope, $http, datasetInfo, plotResults){
    //goes to draw.js

    function initializeSketchpadOnDatasetChange( xdata, ydata, zdata )
    {
      initializeSketchpad(
        xdata["min"],xdata["max"],ydata["min"],ydata["max"],
        xdata["name"],ydata["name"],zdata["name"]
       );
    }

    // for all other normal queries
    $scope.getUserQueryResults = function getUserQueryResults()
    {
      var q = constructUserQuery(); //goes to query.js
      var params = {
        "query": q
      };
      var config = {
        params: params
      };

      $http.get('/zv/getdata', config).
      success(function(response) {
        console.log("getUserQueryResults: success");
        plotResults.displayUserQueryResults(response.outputCharts);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });
    }

    // for representative trends
    function getRepresentativeTrends()
    {
      var q = constructRepresentativeTrendQuery(); //goes to query.js
      var params = {
        "query": q
      };
      var config = {
        params: params
      };

      $http.get('/zv/getdata', config).
      success(function(response) {
        console.log("getRepresentativeTrends: success");
        plotResults.displayRepresentativeResults( response.outputCharts );
      }).
      error(function(response) {
        console.log("getRepresentativeTrends: fail");
      });
    }

    function getOutlierTrends()
    {
      var q = constructOutlierTrendQuery(); //goes to query.js
      var params = {
        "query": q
      };
      var config = {
        params: params
      };

      $http.get('/zv/getdata', config).
      success(function(response) {
        console.log("getOutlierTrends: success");
        plotResults.displayOutlierResults( response.outputCharts );
      }).
      error(function(response) {
        console.log("getOutlierTrends: fail");
      });
    }

    // TODO: params will need to be dynamic later
    var q = constructDatasetChangeQuery("real_estate");

    var params = {
      "query": q,
    };
    var config = {
      params: params,
    };

    // when the data selection is changed, the graphs needs to be re-initialized
    // and the rest of the graphs have to be fetched
    $scope.onDataAttributeChange = function() {
      var categoryData = datasetInfo.getCategoryData()[getSelectedCategory()]
      var xData = datasetInfo.getXAxisData()[getSelectedXAxis()]
      var yData = datasetInfo.getYAxisData()[getSelectedYAxis()]
      initializeSketchpadOnDatasetChange(xData, yData, categoryData); //only x and y values?
      getRepresentativeTrends();
      getOutlierTrends();
    };

    // when the page first loads, initialize and then set default values
    $http.get('/zv/getformdata', config).
      success(function(response) {
        globalDatasetInfo = response;
        datasetInfo.store(response); //saves form data to datasetInfo
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
        //send in first item info
        initializeSketchpadOnDatasetChange(
              response.xAxisColumns[$scope.xAxisItems[0]],
              response.yAxisColumns[$scope.yAxisItems[0]],
              response.zAxisColumns[$scope.categories[0]]
            );
        getRepresentativeTrends();
        getOutlierTrends();
      }).
      error(function(response) {
        alert('Request failed: /getformdata');
      });
}]);




/* -- old stuff -- */

// need to make angular
$("a.tooltip-question").tooltip();

$(function () {
  $('[data-toggle="popover"]').popover()
})



// OLD CODE
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

