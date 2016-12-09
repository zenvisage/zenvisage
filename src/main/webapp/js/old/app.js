var app = angular.module('zenvisage', []);
var globalDatasetInfo;


app.controller('zqlTableController', ['$scope', '$http', 'plotResults', '$compile', function ($scope, $http, plotResults, $compile) {
    $scope.input = {};
    $scope.queries = {};
    $scope.queries['zqlRows'] = [];
    // $scope.parsed = {};
    // $scope.parsed['zqlRows'] = [];

  $scope.removeRow = function ( index ) {
    $("#table-row-" + index).remove();
  };

  $scope.addRow = function () {

    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;
    var rowNumber = (rowCount+1).toString();
    //$("#zql-table").append
    $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"5\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"20\" value=\" \"></td><td><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
    $compile($el)($scope);
    //<td><input class=\"form-control zql-table viz\" type=\"text\" size=\"1\" value=\" \"></td>

        // console.log(checkConstraints($scope.input.constraints));
        // //Create a copy of parsed version of input for backend
        // $scope.copy = angular.copy($scope.input);

        // if (checkInput($scope.copy)) {
        //     console.log("request: ",$scope.copy);
        //     // Add to rows for front-end display
        //     $scope.queries['zqlRows'].push($scope.input);
        //     $scope.parsed['zqlRows'].push($scope.copy);
        //     $scope.input = {};
        // }
  };

    $scope.submitZQL = function () {
        $("#views_table").empty();
        $scope.queries['zqlRows'] = [];

        $( ".tabler" ).each(function( index ) {
          var name = $(this).find(".name").val()
          var x = $(this).find(".x-val").val()
          var y = $(this).find(".y-val").val()
          var z = $(this).find(".z-val").val()
          var constraints = $(this).find(".constraints").val()
          // var viz = $(this).find(".viz").val()
          var processe = $(this).find(".process").val()
          var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": "", "processe": processe };
          if (checkInput(input)) {
            $scope.queries['zqlRows'].push(input);
          }
        });

        $scope.queries['db'] = getSelectedDataset();

        console.log($scope.queries);

        $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify( $scope.queries )}}
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
  '$scope', '$rootScope', '$http', 'ChartSettings', '$compile',
  function($scope, $rootScope, $http, ChartSettings, $compile){
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

    $scope.$watch('showScatterplot', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
        $scope.callgetRepresentativeTrends();
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

    $scope.removerAndInsertRows = function( n )
    {
      var table = $("#zql-table > tbody")[0];
      var rowCount = table.rows.length;
      for (i = 1; i < rowCount; i++) {
        $("#table-row-" + i).remove();
      }

      for (i = 1; i < n; i++) {
        var rowNumber = (i).toString();
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"5\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"15\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"20\" value=\" \"></td><td><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
        //<td><input class=\"form-control zql-table viz\" type=\"text\" size=\"1\" value=\" \"></td>
        $compile($el)($scope);
      }
    }

    $scope.clearQuery = function() {
      $scope.removerAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("")
      $($( ".tabler" )[0]).find(".x-val").val("")
      $($( ".tabler" )[0]).find(".y-val").val("")
      $($( ".tabler" )[0]).find(".z-val").val("")
      $($( ".tabler" )[0]).find(".constraints").val("")
      //$($( ".tabler" )[0]).find(".viz").val("")
      $($( ".tabler" )[0]).find(".process").val("")
    }

    $scope.populateQuery1 = function() {

      $scope.removerAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      //$($( ".tabler" )[0]).find(".viz").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("*f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z1")
      $($( ".tabler" )[1]).find(".constraints").val("state='CA'")
      //$($( ".tabler" )[1]).find(".viz").val("")
      $($( ".tabler" )[1]).find(".process").val("")
    }

    $scope.populateQuery2 = function() {

      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
      //$($( ".tabler" )[0]).find(".viz").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")
      //$($( ".tabler" )[1]).find(".viz").val("")
      $($( ".tabler" )[1]).find(".process").val("v1,v2<-argmin_{z1}x{z2}[k=7]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v2")
      $($( ".tabler" )[2]).find(".constraints").val("")
      //$($( ".tabler" )[2]).find(".viz").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateQuery3 = function() {

      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      //$($( ".tabler" )[0]).find(".viz").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y2<-{'listingprice'}")
      $($( ".tabler" )[1]).find(".z-val").val("z1")
      $($( ".tabler" )[1]).find(".constraints").val("")
      //$($( ".tabler" )[1]).find(".viz").val("")
      $($( ".tabler" )[1]).find(".process").val("v1<-argmin_{y1,y2}[k=7]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y3<-{'soldprice','listingprice'}")
      $($( ".tabler" )[2]).find(".z-val").val("v1")
      $($( ".tabler" )[2]).find(".constraints").val("")
      //$($( ".tabler" )[2]).find(".viz").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.drawFunction = function() {
      var xval = [];
      var plotData = [];

      for(var i = 0; i < sketchpadData.length; i++){
        var xp = sketchpadData[i]["xval"];
        //var yp = sketchpadData[i]["yval"];
        xval.push( xp )
      }

      var scope = {
        x: xval,
      };

      var eq = $scope.equation.replace("^", ".^");
      var y = math.eval( eq, scope )
      if( eq.includes("x") )
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( { "xval": xval[i], "yval":y[i] } )
        }
      }
      else
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( { "xval": xval[i], "yval": y } )
        }
      }
      plotSketchpadNew( plotData )
      //angular.element($("#sidebar")).scope().getUserQueryResults();
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

