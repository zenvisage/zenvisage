
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

$('#uploaderForm').on('submit', function(e) {
    e.preventDefault();
    $.ajax({
        url : $(this).attr('action'),
        type: $(this).attr('method'),
        data: new FormData(this),
        processData: false,
        contentType: false,
        success: function (data) {
            alert("success");
        },
        error: function (jXHR, textStatus, errorThrown) {
            alert(errorThrown);
        }
    });
});

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

      $http.get('/zv/getSimilarity', config).
      success(function(response) {
        console.log("getUserQueryResults: success");
        plotResults.displayUserQueryResults(response.outputCharts);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });
    }

    // for representative trends
    function getRepresentativeTrends( outlierCallback )
    {
      var q = constructRepresentativeTrendQuery(); //goes to query.js
      var params = {
        "query": q
      };
      var config = {
        params: params
      };
      $http.get('/zv/getRepresentative', config).
      success(function(response) {
        console.log("getRepresentativeTrends: success");
        plotResults.displayRepresentativeResults( response.outputCharts );
        outlierCallback();
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

      $http.get('/zv/getOutlier', config).
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
    //var q = constructDatasetChangeQuery("seed2");

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
      getRepresentativeTrends(getOutlierTrends);
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
        getRepresentativeTrends( getOutlierTrends );
      }).
      error(function(response) {
        alert('Request failed: /getformdata');
      });
}]);
