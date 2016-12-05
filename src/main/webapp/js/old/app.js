var app = angular.module('zenvisage', []);
var globalDatasetInfo;


app.controller('zqlTableController', ['$scope', '$http', 'plotResults', function ($scope, $http, plotResults) {
    $scope.input = {};
    $scope.queries = {};
    $scope.queries['zqlRows'] = [];
    $scope.parsed = {};
    $scope.parsed['zqlRows'] = [];

  $scope.removeRow = function ( index ) {
    $scope.queries['zqlRows'].splice( index , 1);
  };

  $scope.addRow = function () {
        // console.log(checkConstraints($scope.input.constraints));
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
        $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify($scope.parsed)}}
        ).then(
            function (response) {
                console.log("success: ", response);
                plotResults.displayUserQueryResults(response.data.outputCharts);
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
      displayUserQueryResultsHelperNew( userQueryResults );
      //displayUserQueryResultsHelper( userQueryResults );
    }

    plottingService.displayRepresentativeResults = function displayRepresentativeResults( representativePatternResults )
    {
      displayRepresentativeResultsHelperNew( representativePatternResults )
      //displayRepresentativeResultsHelper( representativePatternResults )
    }

    plottingService.displayOutlierResults = function displayOutlierResults( outlierResults )
    {
      displayOutlierResultsHelperNew( outlierResults )
      //displayOutlierResultsHelper( outlierResults )
    }

    return plottingService;
});

app.controller('options-controller', [
  '$scope', '$rootScope', '$http', 'ChartSettings',
  function($scope, $rootScope, $http, ChartSettings){
    $scope.similarity = 'Euclidean';
    $scope.representative = 'kmeans';
    $scope.aggregation = 'avg';
    $scope.numResults = 50;
    $scope.considerRange = true;
    $scope.equation =  '';
    $scope.zqltable = false;
    $scope.chartSettings = ChartSettings;
    $scope.chartSettings.chartOptions = ["Line", "Bar", "Scatter"];
    $scope.chartSettings.selectedChartOption = $scope.chartSettings.chartOptions[0];

    $scope.$watchGroup(['similarity', 'numResults'], function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
      }
    });

    $scope.$watchGroup( ['considerRange' ], function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
        $scope.callgetRepresentativeTrends();
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

    $scope.submitZQLTable = function() {
      //$(".tabler")
      console.log("test")
    }

    $scope.drawFunction = function() {
      var xval = [];
      var plotData = [];
      for (i = 0; i < sketchpad.rawData_.length; i++) {
        xval.push( sketchpad.rawData_[i][0] )
      }
      var scope = {
        x: xval,
      };
      var eq = $scope.equation.replace("^", ".^");
      var y = math.eval( eq, scope )
      if( eq.includes("x") )
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( [ xval[i], y[i] ] )
        }
      }
      else
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( [ xval[i], y ] )
        }
      }
      sketchpad.updateOptions(
        {
          'file': plotData,
           valueRange: [null, null],
        }
      );
      angular.element($("#sidebar")).scope().getUserQueryResults();
    }

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
  '$scope', '$rootScope', '$http', 'datasetInfo', 'plotResults', 'ScatterService', 'ChartSettings',
  function($scope, $rootScope, $http, datasetInfo, plotResults, scatterService, ChartSettings){

    $scope.chartSettings = ChartSettings;
    function initializeSketchpadOnDataAttributeChange( xdata, ydata, zdata )
    {
      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();

      switch( $scope.chartSettings.selectedChartOption ) {
          case 'Bar':
              break;
          case 'Scatter':
              scatterService.initializeScatterPlot(xdata["min"],xdata["max"],ydata["min"],ydata["max"]);
              break;
          default: // Line
              initializeSketchpadNew(
                xdata["min"],xdata["max"],ydata["min"],ydata["max"],
                xdata["name"],ydata["name"],zdata["name"]
               );
              break;
      }


    }

    // for all other normal queries
    $scope.getUserQueryResults = function getUserQueryResults()
    {
      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;

      console.log("calling getUserQueryResults");
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
      clearRepresentativeTable();

      var q = constructRepresentativeTrendQuery(); //goes to query.js
      var data = q;

      console.log("calling getRepresentativeTrends");
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
      clearOutlierTable();

      var q = constructOutlierTrendQuery(); //goes to query.js
      var data = q;

      console.log("calling getOutlierTrends");
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

app.service('ChartSettings', function () {
    return {};
})

