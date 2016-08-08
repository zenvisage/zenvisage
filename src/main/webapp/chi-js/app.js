
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

app.controller('options-controller', [
  '$scope', '$rootScope', '$http',
  function($scope, $rootScope, $http){
    $scope.similarity = 'Euclidean';
    $scope.representative = 'kmeans';
    $scope.aggregation = 'avg';
    $scope.numResults = 50;

    $scope.$watchGroup(['similarity', 'numResults'], function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
      }
    });

    $scope.$watch('representative', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callgetRepresentativeTrends();
      }
    });

    $scope.$watch('aggregation', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
        $scope.callgetRepresentativeTrends();
      }
    });

    $scope.callGetUserQueryResults = function() {
      $rootScope.$emit("callGetUserQueryResults", {});
    }

    $scope.callgetRepresentativeTrends = function() {
      $rootScope.$emit("callgetRepresentativeTrends", {});
    }

}]);



// populates and controls the dataset attributes on the left-bar
// does not dynamically adjust to change in dataset yet
app.controller('datasetController', [
  '$scope', '$rootScope', '$http', 'datasetInfo', 'plotResults',
  function($scope, $rootScope, $http, datasetInfo, plotResults){
    //goes to draw.js

    function initializeSketchpadOnDataAttributeChange( xdata, ydata, zdata )
    {
      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();
      initializeSketchpad(
        xdata["min"],xdata["max"],ydata["min"],ydata["max"],
        xdata["name"],ydata["name"],zdata["name"]
       );
    }

    // for all other normal queries
    $scope.getUserQueryResults = function getUserQueryResults()
    {
      var q = constructUserQuery(); //goes to query.js
      var data = q;

      $http.post('/zv/postSimilarity', data).
      success(function(response) {
        console.log("getUserQueryResults: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayUserQueryResults(response.outputCharts);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });

    }

    $scope.getRepresentativeTrendsWithoutCallback = function getRepresentativeTrendsWithoutCallback()
    {
      getRepresentativeTrends( getOutlierTrends );
    }

    // for representative trends
    function getRepresentativeTrends( outlierCallback )
    {
      var q = constructRepresentativeTrendQuery(); //goes to query.js
      var data = q;

      $http.post('/zv/postRepresentative', data).
      success(function(response) {
        console.log("getRepresentativeTrends: success");
        if (response.length == 0){console.log("empty response")}
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
      var data = q;

      $http.post('/zv/postOutlier', data).
      success(function(response) {
        console.log("getOutlierTrends: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayOutlierResults( response.outputCharts );
      }).
      error(function(response) {
        console.log("getOutlierTrends: fail");
      });
    }

    var q = constructDatasetChangeQuery(getSelectedDataset());
    //var q = constructDatasetChangeQuery("seed2");

    var params = {
      "query": q,
    };
    var config = {
      params: params,
    };

   $scope.onDatasetChange = function() {

      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();

      var q = constructDatasetChangeQuery(getSelectedDataset());

      var params = {
        "query": q,
      };
      var config = {
        params: params,
      };

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
          initializeSketchpadOnDataAttributeChange(
                response.xAxisColumns[$scope.xAxisItems[0]],
                response.yAxisColumns[$scope.yAxisItems[0]],
                response.zAxisColumns[$scope.categories[0]]
              );
          getRepresentativeTrends( getOutlierTrends );
        }).
        error(function(response) {
          alert('Request failed: /getformdata');
        });
    }

    // when the data selection is changed, the graphs needs to be re-initialized
    // and the rest of the graphs have to be fetched
    $scope.onDataAttributeChange = function() {
      var categoryData = datasetInfo.getCategoryData()[getSelectedCategory()]
      var xData = datasetInfo.getXAxisData()[getSelectedXAxis()]
      var yData = datasetInfo.getYAxisData()[getSelectedYAxis()]
      initializeSketchpadOnDataAttributeChange(xData, yData, categoryData); //only x and y values?
      getRepresentativeTrends(getOutlierTrends);
    };

    $rootScope.$on("callGetUserQueryResults", function(){
      $scope.getUserQueryResults();
    });

    $rootScope.$on("callgetRepresentativeTrends", function(){
      $scope.getRepresentativeTrendsWithoutCallback();
    });
  }]);
