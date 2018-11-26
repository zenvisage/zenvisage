var app = angular.module('zenvisage', ['ngCookies']);
var globalDatasetInfo;
var allAxisColumns;
var login_ava;

app.controller('classCreationController', ['$scope', '$rootScope','$http', function ($scope, $rootScope, $http) {
  $scope.data = {
    option1: "default",
    option2: "default",
    option3: "default",
    option4: "default"
  };

  $scope.AxisInfo = [];
  $scope.$on("clearDynamicClassOptions", function() {
      $scope.data.option1 = '';
      $scope.data.option2 = '';
      $scope.data.option3 = '';
      $scope.data.option4 = '';
  });

  $scope.$on("loadAxisInfo", function() {
    $scope.AxisInfo = [];
    $scope.AxisInfo.push('');
    allAxisColumns = $.extend(true,globalDatasetInfo["xAxisColumns"],globalDatasetInfo["yAxisColumns"],globalDatasetInfo["zAxisColumns"]);
    for (var key in allAxisColumns) {
      $scope.AxisInfo.push(key);
    }
    $scope.classes = [];
  });

  // TODO(Renxuan): merge dynamic class creation and info 
  $scope.createOrModifyClasses = function() {
    var query = {};
    var classList = [];
    for (i = 1; i < 5; i++) {
      key = $("#dynamic-class-row-" + i + "\ > div").find(":selected").text();
      val = $("#dynamic-class-row-" + i + "\ > div > input")[0].value
      if (val && key)
      {
        var keyval = {};
        var min = allAxisColumns[key]["min"]
        var max = allAxisColumns[key]["max"]
        var replacedMin = val.replace("min", min);
        var replacedMinMax = replacedMin.replace("max", max);
        keyval["name"] = key
        keyval["values"] = JSON.parse("[" + replacedMinMax + "]");
        classList.push(keyval);
      }
    }
    query["dataset"] = getSelectedDataset();
    query["classes"] = classList;
    document.getElementById("loadingEclipse3").style.display = "inline";

    $http.post('/zv/createClasses', query
    ).then(
        function (response) {
          console.log("success: ", response);
          globalDatasetInfo["classes"] = JSON.parse(response.data);
          query = {};
          query["dataset"] = getSelectedDataset();
          $http.post('/zv/getClassInfo', query
          ).then(
              function (response) {
                console.log("success: ", response.data);
                globalDatasetInfo["classes"] = response.data
                var formattedRanges = formatRanges(response.data["classes"])
                for (var i = 0; i < response.data["classes"].length; i++){
                  response.data["classes"][i].formattedRanges = formattedRanges[i];
                  $scope.classes = response.data["classes"];
                }
                document.getElementById("load-dynamic-class-button").style.display = "inline";
              },
              function (response) {
                console.log("failed to get class info: ", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
              }
          );
          document.getElementById("loadingEclipse3").style.display = "none";
        },
        function (response) {
          console.log("failed to create classes", response.data);
          $("#errorModalText").html(response.data);
          $("#errorModal").modal();
          document.getElementById("loadingEclipse3").style.display = "none";
        }
    );
    log.info("Dynamic Class created",JSON.stringify(classList))
  }

  $scope.renameDynamicClass = function(classToRename) {
    $http.post('/zv/renameDynamicClass', "tableName: " + classToRename.name + ", classId: " + classToRename.class_id + ", tag: " + classToRename.tag
    ).then(
        function (response) {
          console.log("rename class success");
        },
        function (response) {
          console.log("failed to rename class: ", response.data);
          $("#errorModalText").html(response.data);
          $("#errorModal").modal();
        }
    );
  }

  $scope.deleteClass = function deleteClass(classes, index) {
    var classToDelete = classes[index]
    classes.splice(index, 1)

    $http.post('/zv/deleteClass', "tableName: " + classToDelete.name + ", classId: " + classToDelete.class_id + ", tag: " + classToDelete.tag
    ).then(
        function (response) {
          console.log("delete class success");
        },
        function (response) {
          console.log("failed to get delete class: ", response.data);
          $("#errorModalText").html(response.data);
          $("#errorModal").modal();
        }
    );
  }

  $scope.loadDynamicClass = function() {
    angular.element($("#sidebar")).scope().setDataAttributeToDynamicClass();
  }
}]);

app.controller('zqlScriptController', ['$scope', '$rootScope', '$http', 'plotResults', function($scope, $rootScope, $http, plotResults) {
    $scope.submitZQLScript = function () {
        // var test_script = "db = real_estate\n" +
  		// 		"ax x1 = [year]\n" +
  		// 		"ax y1 = [soldprice]\n" +
  		// 		"ax z1 = [state.*]\n" +
  		// 		"vc f1 = {x1, y1, z1}\n" +
  		// 		"ax y2 = [listingprice]\n" +
  		// 		"vc f2 = {x1, y1, z1}\n" +
  		// 		"ax v1 = process(argmin={z1},k=1,DEuclidean(f1,f2))\n" +
  		// 		"vc f3 = {x1, y1, v1}\n" +
  		// 		"display(f3)";
          var script = document.getElementById('zqlScriptCode').value;
          $http.get('/zv/executeZQLScript', {params: {'query': script}}
          ).then(
            function (response) {
                console.log("success: ", response);
                var userQueryResults = response.data.outputCharts;
                plotResults.displayUserQueryResults(userQueryResults, false);
            },
            function (response) {
                console.log("failed ZQL Query", escape(response.data));
                document.getElementById("loadingEclipse").style.display = "none";
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
          );
    };
}]);

app.controller('zqlTableController', ['$scope', '$rootScope', '$http', 'plotResults', '$compile', function ($scope, $rootScope, $http, plotResults, $compile) {
  $scope.input = {};
  $scope.queries = {};
  $scope.queries['zqlRows'] = [];

  $scope.removeRow = function ( index ) {
    $("#table-row-" + index).remove();
    console.log('remove regular row ',index,' added!');
  };

  $scope.addRow = function () {
    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;
    var rowNumber = (rowCount+1).toString();
    //$("#zql-table").append
   console.log('insert regular row ',rowNumber,' added!');
    $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
    //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
    $compile($el)($scope);
    //tree.addLeaf(count);
    //tree.addParent(1);
  };

  $scope.$on('insertRowhelper', function(event){
    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;
    var rowNumber = (rowCount+1).toString();
    //$("#zql-table").append
    $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
    //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
    $compile($el)($scope);
    //tree.addLeaf(count);
    //tree.addParent(1);
  });

  $scope.addProcessRow = function () {
    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;
    var rowNumber = (rowCount+1).toString();
    //$("#zql-table").append
              console.log('insert process row ',rowNumber,' added!');
    $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler processRow\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td colspan=\"5\"><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
    //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
    $compile($el)($scope);
    //tree.addParent(count);
  };

  $scope.$on('insertProcessRowhelper', function (event) {
    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;
    var rowNumber = (rowCount+1).toString();
    //$("#zql-table").append
  //        console.log('insert row ',i,' added!');
    $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler processRow\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td colspan=\"5\"><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
    //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
    $compile($el)($scope);
    //tree.addParent(count);
  });

  $scope.$on('removeAndInsertRowshelper', function( event, args ) {
    var table = $("#zql-table > tbody")[0];
    var rowCount = table.rows.length;

    for (i = rowCount; i > 0; i--) {
      // $("#table-row-" + i).remove();
    //  console.log('row ',i-1,' removed!');
      table.deleteRow(i-1)
    }

    for (i = 1; i <= args.n; i++) {
      var rowNumber = (i).toString();
//      console.log('row ',i,' added!');
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");

      $compile($el)($scope);
  }});

  // $scope.filterZQL = function () {
  //   console.log("Filter working!")
  //   // var xAxis = getSelectedXAxis();
  //   // var yAxis = getSelectedYAxis();
  //   // var zAxis = getSelectedCategory();
  //   // var constraint = $("#filter.form-control").val();
  //   // //Populate ZQL table
  //   // // $( ".tabler" ).each(function( index ) {
  //   // //   $(this).find(".name").val("*f1")
  //   // //   $(this).find(".x-val").val("x<-{'"+xAxis+"'}")
  //   // //   $(this).find(".y-val").val("y<-{'"+yAxis+"'}")
  //   // //   $(this).find(".z-val").val("z<-'"+zAxis+"'.*")
  //   // //   $(this).find(".constraints").val(constraint)
  //   // // })
  //   // // $scope.submitZQL();
  //   // $scope.getUserQueryResultsWithCallBack();
  //   // getRepresentativeTrendsWithoutCallback();
  //   // constructUserQuery();
  //   // constructRepresentativeTrendQuery();
  //   // constructOutlierTrendQuery();
  //   log.info("filter constraint: ",constraint)

  // }
  $scope.submitZQL = function () {
    $("#graph-div").empty();
    createZQLGraph( submitNodeZQL );
    clearUserQueryResultsTable();
    $scope.queries['zqlRows'] = [];
    var processRow = [];
    $( ".tabler" ).each(function( index ) {
      if ( $(this).hasClass("processRow") )
      {
        var processe = $(this).find(".process").val()
        if (processe !== undefined) {
          processe = parseProcess(processe);
        }
        processRow.push(processe);
      }
      else
      {
        var name = $(this).find(".name").val()
        var x = $(this).find(".x-val").val()
        var y = $(this).find(".y-val").val()
        var z = $(this).find(".z-val").val()
        var constraints = $(this).find(".constraints").val()
        // var viz = $(this).find(".viz").val()
        // var processe = $(this).find(".process").val()
        // "processe": processe
        var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};
        if (checkInput(input)) {
          if (input.name.sketch) {
            // if this row needs to grab data from the sketch
            var points = [];
            this.dataX = [];
            this.dataY = [];
            this.xAxis = getSelectedXAxis();
            this.yAxis = getSelectedYAxis();
            for(var i = 0; i < sketchpadData.length; i++){
              var xp = sketchpadData[i]["xval"];
              var yp = sketchpadData[i]["yval"];
              points.push(new Point( xp, yp ));
              this.dataX.push( xp );
              this.dataY.push( yp );
            }
            input["sketchPoints"] = new SketchPoints(this.xAxis, this.yAxis, points);
            input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x"+(index+1)};
            input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y"+(index+1)};
            input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z"+(index+1), expression: undefined};
            "z"+index + "<-'"+ getSelectedCategory() +"'.*";
          }
          $scope.queries['zqlRows'].push(input);
        }
      }
    });

    $.each( processRow, function( index, value ) {
      $scope.queries['zqlRows'][index]["processe"] = value;
    });

    $scope.queries['db'] = getSelectedDataset();
    console.log($scope.queries);

    $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify( $scope.queries )}}
    ).then(
        function (response) {
            console.log("success: ", response);
            var userQueryResults = response.data.outputCharts;
            plotResults.displayUserQueryResults(userQueryResults, false);
        },
        function (response) {
            console.log("failed ZQL Query", escape(response.data));
            document.getElementById("loadingEclipse").style.display = "none";
            $("#errorModalText").html(response.data);
            $("#errorModal").modal();
        }
    );
  };

  function submitNodeZQL( d )
  {
    $scope.queries['zqlRows'] = [];
    var input = { "name": "*f1", "x": d.xval, "y": d.yval, "z": d.zval, "constraints": d.constraint, "viz": ""};
    if (checkInput(input)) {
      if (input.name.sketch) {
        // if this row needs to grab data from the sketch
        var points = [];s
        this.dataX = [];
        this.dataY = [];
        this.xAxis = getSelectedXAxis();
        this.yAxis = getSelectedYAxis();
        for(var i = 0; i < sketchpadData.length; i++){
          var xp = sketchpadData[i]["xval"];
          var yp = sketchpadData[i]["yval"];
          points.push(new Point( xp, yp ));
          this.dataX.push( xp );
          this.dataY.push( yp );
        }
        input["sketchPoints"] = new SketchPoints(this.xAxis, this.yAxis, points);
        input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x"+(index+1)};
        input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y"+(index+1)};
        input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z"+(index+1), expression: undefined};
        "z"+index + "<-'"+ getSelectedCategory() +"'.*";
      }
      $scope.queries['zqlRows'].push(input);
    }
    $scope.queries['db'] = getSelectedDataset();
    $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify( $scope.queries )}}
    ).then(
        function (response) {
            console.log("success: ", response.data);
            var userQueryResults = response.data.outputCharts;
            plotResults.displayUserQueryResults(response.data.outputCharts,false);
        },
        function (response) {
            console.log("failed Node ZQL Query: ", escape(response.data));
            document.getElementById("loadingEclipse").style.display = "none";
            $("#errorModalText").html(response.data);
            $("#errorModal").modal();
        }
    );
  }

}]);

// check for emput x, y and z and then check for syntax correctness
function checkInput(input) {
  for (var property in input) {
      if (input.hasOwnProperty(property)) {

        input[property] = input[property].trim();
          // do stuff
      }
  }
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
        if (0 === input.constraints.length)
        {
          input.constraints = undefined;
        }
        //input.constraints = input.constraints //parseConstraints(input.constraints);
    }
    if (input.viz !== undefined) {
        input.viz = parseViz(input.viz);
    }
    return (name && x && y && z && constraints && viz) !== undefined;
}

// checks the input process variable and converts it to proper object format to sent to backend
function checkProcessInput(input)
{
    if (input.processe !== undefined) {
      input.processe = parseProcess(input.processe);
    }
    return processe !== undefined;
}

app.factory('datasetInfo', function() {
  var tablelist;
  var categoryData;
  var xAxisData;
  var yAxisData;
  var datasetService = {};

  datasetService.store = function( response ) {
    categoryData = response.zAxisColumns;
    xAxisData = response.xAxisColumns;
    yAxisData = response.yAxisColumns;
  };

  datasetService.storetablelist = function( response ) {
    tablelist = response;
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
  datasetService.getTablelist = function()
  {
    return tablelist;
  }
  return datasetService;
});

app.factory('plotResults', function() {

    var plottingService = {};
    plottingService.displayUserQueryResults = function displayUserQueryResults( userQueryResults,  includeSketch = true )
    {
      displayUserQueryResultsHelper( userQueryResults, includeSketch );
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
    '$scope', '$rootScope', '$http', '$cookies','datasetInfo', 'plotResults', 'ScatterService', 'ChartSettings',
    function($scope, $rootScope, $http, $cookies, datasetInfo, plotResults, scatterService, ChartSettings){
    $scope.similarity = 'Euclidean';
    $scope.representative = 'kmeans';
    $scope.aggregation = 'avg';
    $scope.numResults = 50;
    $scope.clusterSize = 3;
    $scope.minDisplayThresh =0.0;
    $scope.considerRange = true;
    $scope.showOriginalSketch = true;
    $scope.showBar = false;
    $scope.showScatterplot = false;
    $scope.flipY = false;
    $scope.outputNormalized = true;
    $scope.equation =  '';
    $scope.selectedSmoothing = "none";
    $scope.zqltable = false;
    $scope.chartSettings = ChartSettings;
    $scope.chartSettings.chartOptions = ["Line", "Bar", "Scatter"];
    $scope.chartSettings.selectedChartOption = $scope.chartSettings.chartOptions[0];
    // $scope.filter= '';
    
    var var_map = {"similarity": $scope.similarity, "aggregation": $scope.aggregation, "numResults": $scope.numResults,
    		       "clusterSize": $scope.clusterSize, "minDisplayThresh": $scope.minDisplayThresh, "considerRange": $scope.considerRange, 
    		       "showOriginalSketch": $scope.showOriginalSketch, "showScatterplot": $scope.showScatterplot, "showBar": $scope.showBar, 
    		       "flipY": $scope.flipY, "selectedSmoothing": $scope.selectedSmoothing
    		       }

    var toggleWatch = function(watchExpr, fn) {
  	  var watchFn;
  	  return function() {
  	    if (watchFn) {
  	      watchFn();
  	      watchFn = undefined;
  	      console.log("Disabled " + watchExpr);
  	    } else {
  	      watchFn = $scope.$watch(watchExpr, fn);
  	      console.log("Enabled " + watchExpr);
  	    }
  	  };
    }; 
    
    var watchFunc = function(varName, funcIndex){
    	return function( newValue, oldValue ) {
	        if (newValue !== oldValue){
	          log.info(varName, var_map[varName]);
	          if(funcIndex == 1){
	        	  document.getElementById("loadingEclipse").style.display = "inline";
	        	  $scope.callGetUserQueryResults(); 
	          }else if(funcIndex == 2){
	        	  document.getElementById("loadingEclipse").style.display = "inline";
	        	  $scope.callGetUserQueryResultsWithCallBack();//dont call representative trends
	          }else if(funcIndex == 3){
	        	  $scope.callgetRepresentativeTrends();
	          }
	        }
        }
    };

    $scope.considerRangeToggle = toggleWatch("considerRange", watchFunc("considerRange", 1));
    $scope.considerRangeToggle();
    $scope.showOriginalSketchToggle = toggleWatch("showOriginalSketch", watchFunc("showOriginalSketch", 1));
    $scope.showOriginalSketchToggle();
    $scope.showScatterplotToggle = toggleWatch("showScatterplot", watchFunc("showScatterplot", 2));
    $scope.showScatterplotToggle();
    $scope.showBarToggle = toggleWatch("showBar", watchFunc("showBar", 1));
    $scope.showBarToggle();
    $scope.flipYToggle = toggleWatch("flipY", watchFunc("flipY", 2));
    $scope.flipYToggle();
    $scope.similarityToggle = toggleWatch("similarity", watchFunc("similarity", 1));
    $scope.similarityToggle();
    $scope.aggregationToggle = toggleWatch("aggregation", watchFunc("aggregation", 2));
    $scope.aggregationToggle();
    $scope.clusterSizeToggle = toggleWatch("clusterSize", watchFunc("clusterSize", 2));
    $scope.clusterSizeToggle();
    $scope.numResultsToggle = toggleWatch("numResults", watchFunc("numResults", 1));
    $scope.numResultsToggle();
    $scope.minDisplayThreshToggle = toggleWatch("minDisplayThresh", watchFunc("minDisplayThresh", 2));
    $scope.minDisplayThreshToggle();
    $scope.outputNormalizedToggle = toggleWatch("outputNormalized", watchFunc("outputNormalized", 2));
    $scope.outputNormalizedToggle();
    $scope.selectedSmoothingToggle = toggleWatch("selectedSmoothing", watchFunc("selectedSmoothing", 2));
    $scope.selectedSmoothingToggle();
    
    $( "#slider-range-max" ).slider({
      range: "max",
      min: 0,
      max: 1,
      step:0.05,
      value: 0.5,
      slide: function( event, ui ) {
        $( "#amount" ).val( ui.value );
        log.info("smoothingcoefficient", ui.value)
        if(getSmoothingType() != "none"){
          $scope.getUserQueryResults();
          $scope.callgetRepresentativeTrends();
        }
      }
    } );

    $scope.onFilterChange = function() {
        log.info("filter change", $("#filter.form-control").val());
        $scope.callGetUserQueryResultsWithCallBack();
    };

    $scope.onflipYChange = function() {
        document.getElementById("loadingEclipse").style.display = "inline";
        if(usingPattern == true){
          patternLoad();
        }
        else{
          createSketchpad(sketchpadData);
      };
    }
    
    $scope.drawFunction = function() {
        log.info('input equation',$scope.equation)
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

        angular.element('#class-creation').triggerHandler('click');

        plotSketchpadNew( plotData )
        //angular.element($("#sidebar")).scope().getUserQueryResults();
    }
    
    // $scope.$watch('representative', function( newValue, oldValue ) {
    //   if (newValue !== oldValue)
    //   {
    //     $scope.callgetRepresentativeTrends();
    //   }
    // });


    $scope.changeZQLTableState = function() {
        // activate zqltable, and deactivate zqlscript
        if (!$scope.zqltable) {
            $scope.zqltable = true;
            $scope.zqlscript = false;
        } else {
            // deactive zqltable
            $scope.zqltable = false;
        }
    }

    $scope.changeZQLScriptState = function() {
        // activate zqlscript, deactivate zqltable
        if (!$scope.zqlscript) {
            $scope.zqlscript = true;
            $scope.zqltable = false;
        } else {
            $scope.zqlscript = false;
        }
    }
    
    
    $scope.removeAndInsertRows = function( n ){
      $scope.$broadcast('removeAndInsertRowshelper', {n} );
    }

    $scope.insertRow = function(){
      $scope.$broadcast('insertRowhelper');
    }

    $scope.insertProcessRow = function(){
      $scope.$broadcast('insertProcessRowhelper');
    }

    // TOP K
    $scope.getTopK = function()
    {
      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;

      console.log("calling getTopK");
      $http.post('/zv/findbestclass', data).
      success(function(response) {
        console.log("getTopK: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayUserQueryResults(response.outputCharts,true);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
        document.getElementById("loadingEclipse").style.display = "none";
        $("#errorModalText").html(response);
        $("#errorModal").modal();
      });
    }

    

  $scope.clearQuery = function() {
      $scope.removeAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("")
      $($( ".tabler" )[0]).find(".x-val").val("")
      $($( ".tabler" )[0]).find(".y-val").val("")
      $($( ".tabler" )[0]).find(".z-val").val("")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")
  }

  $scope.populateWeatherQuery1 = function() {
      $("#dataset-form-control").val('weather');
      angular.element($("#sidebar")).scope().onDatasetChange('weather');
      $scope.removeAndInsertRows( 1 );

      // $scope.insertRow()
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("location='Melbourne'")
      // $($( ".tabler" )[0]).find(".process").val("")
      $scope.insertRow()
      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val(" z2<-'location'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[2]).find(".process").val("v2<-argmin_{z2}[k=5]DEuclidean(f1,f2)")

      $scope.insertRow()
      $($( ".tabler" )[3]).find(".name").val("*f3")
      $($( ".tabler" )[3]).find(".x-val").val("x1")
      $($( ".tabler" )[3]).find(".y-val").val("y1")
      $($( ".tabler" )[3]).find(".z-val").val("v2")
      $($( ".tabler" )[3]).find(".constraints").val("")

          $scope.insertRow()

      // $($( ".tabler" )[2]).find(".process").val("")
  //      removeZqlRow(6);  // hacky, remove extra rows
        removeZqlRow(5);
 //      removeZqlRow(4);
 //     removeZqlRow(3);
       removeZqlRow(7);


  }

    $scope.populateWeatherQuery2 = function() {
      $("#dataset-form-control").val('weather');
      angular.element($("#sidebar")).scope().onDatasetChange('weather');
      $scope.removeAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("location='Melbourne'")
      // $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val(" z2<-'location'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[2]).find(".process").val("v2<-argmax_{z2}[k=5]DEuclidean(f1,f2)")

      $scope.insertRow()
      $($( ".tabler" )[3]).find(".name").val("*f3")
      $($( ".tabler" )[3]).find(".x-val").val("x1")
      $($( ".tabler" )[3]).find(".y-val").val("y1")
      $($( ".tabler" )[3]).find(".z-val").val("v2")
      $($( ".tabler" )[3]).find(".constraints").val("")
      // $($( ".tabler" )[3]).find(".process").val("")

      $scope.insertRow()
      removeZqlRow(6);
      removeZqlRow(5);
 //     removeZqlRow(4);
    }

    $scope.populateWeatherQuery3 = function() {
      $("#dataset-form-control").val('weather');
      angular.element($("#sidebar")).scope().onDatasetChange('weather');
      $scope.removeAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[1]).find(".process").val("v1<-argmax_{z1}[k=5]T(f1)")

      $scope.insertRow()
      $($( ".tabler" )[2]).find(".name").val("*f2")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v1")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
      $scope.insertRow()
      removeZqlRow(6);  // hacky, remove extra rows
      removeZqlRow(5);
      removeZqlRow(4);
  //  removeZqlRow(3);

    }

    // $scope.populateQuery1 = function() {
    //   $scope.removeAndInsertRows( 1 );
    //   $($( ".tabler" )[0]).find(".name").val("*f1")
    //   $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
    //   $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
    //   $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
    //   $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
    //   // $($( ".tabler" )[0]).find(".process").val("")
    // }

    // $scope.populateQuery2 = function() {

    //   $scope.removeAndInsertRows( 3 );
    //   $($( ".tabler" )[0]).find(".name").val("f1")
    //   $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
    //   $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
    //   $($( ".tabler" )[0]).find(".z-val").val(" z1<-'state'.*")
    //   $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
    //   $($( ".tabler" )[0]).find(".process").val("")

    //   $($( ".tabler" )[1]).find(".name").val("f2")
    //   $($( ".tabler" )[1]).find(".x-val").val("x1")
    //   $($( ".tabler" )[1]).find(".y-val").val("y1")
    //   $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.*")
    //   $($( ".tabler" )[1]).find(".constraints").val("")
    //   $($( ".tabler" )[1]).find(".process").val("v2<-argmin_{z2}[k=7]DEuclidean(f1,f2)")

    //   $($( ".tabler" )[2]).find(".name").val("*f3")
    //   $($( ".tabler" )[2]).find(".x-val").val("x1")
    //   $($( ".tabler" )[2]).find(".y-val").val("y1")
    //   $($( ".tabler" )[2]).find(".z-val").val("v2")
    //   $($( ".tabler" )[2]).find(".constraints").val("")
    //   $($( ".tabler" )[2]).find(".process").val("")
    // }

    $scope.populateQuery3 = function() {
      $("#dataset-form-control").val('real_estate');
      angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
      $scope.removeAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year','month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice','listingprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.'CA'")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.'NY'")
      $($( ".tabler" )[1]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[2]).find(".process").val("x2,y2<-argmin_{x1,y1}[k=1]DEuclidean(f1,f2)")

      $scope.insertRow()
      $($( ".tabler" )[3]).find(".name").val("*f3")
      $($( ".tabler" )[3]).find(".x-val").val("x2")
      $($( ".tabler" )[3]).find(".y-val").val("y2")
      $($( ".tabler" )[3]).find(".z-val").val("'state'.{'CA','NY'}")
      $($( ".tabler" )[3]).find(".constraints").val("")
      $($( ".tabler" )[3]).find(".process").val("")
      removeZqlRow(6);
      removeZqlRow(5);
 //     removeZqlRow(4);
    }

    $scope.populateQuery4 = function() {
      $("#dataset-form-control").val('real_estate');
      angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
      $scope.removeAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("state='NY'")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'city'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[2]).find(".process").val("v2<-argmax_{z2}[k=3]DEuclidean(f1,f2)")

      $scope.insertRow()
      $($( ".tabler" )[3]).find(".name").val("*f3")
      $($( ".tabler" )[3]).find(".x-val").val("x1")
      $($( ".tabler" )[3]).find(".y-val").val("y1")
      $($( ".tabler" )[3]).find(".z-val").val("v2")
      $($( ".tabler" )[3]).find(".constraints").val("")
      $($( ".tabler" )[3]).find(".process").val("")

//      removeZqlRow(4);
    }

    $scope.populateQuery5 = function() {
      $("#dataset-form-control").val('real_estate');
      angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
      //Pairwise example
      $scope.removeAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y2<-{'listingprice'}")
      $($( ".tabler" )[1]).find(".z-val").val("z1")
      $($( ".tabler" )[1]).find(".constraints").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[2]).find(".process").val("v1<-argmin_{z1}[k=1]DEuclidean(f1,f2)")

      $scope.insertRow()
      $($( ".tabler" )[3]).find(".name").val("*f3")
      $($( ".tabler" )[3]).find(".x-val").val("x1")
      $($( ".tabler" )[3]).find(".y-val").val("y3<-{'soldprice','listingprice'}")
      $($( ".tabler" )[3]).find(".z-val").val("v1")
      $($( ".tabler" )[3]).find(".constraints").val("")
      $($( ".tabler" )[3]).find(".process").val("")
      removeZqlRow(6);
      removeZqlRow(5);
  //    removeZqlRow(4);
    }
    $scope.populateQuery7 = function() {
      $("#dataset-form-control").val('real_estate');
      angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
      //Increasing example
      $scope.removeAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $scope.insertProcessRow()
      $($( ".tabler" )[1]).find(".process").val("v1<-argmax_{z1}[k=5]Tincreasing(f1)")

      $scope.insertRow()
      $($( ".tabler" )[2]).find(".name").val("*f2")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v1")
      $($( ".tabler" )[2]).find(".constraints").val("")

      removeZqlRow(6);
      removeZqlRow(5);
      removeZqlRow(4);
    }

    $scope.callGetUserQueryResults = function() {
      //$rootScope.$broadcast("callGetUserQueryResults", {});
      $scope.getUserQueryResults();
    }

    $scope.callgetRepresentativeTrends = function() {
      //$rootScope.$broadcast("callgetRepresentativeTrends", {});
      $scope.getRepresentativeTrendsWithoutCallback()
    }

    $scope.callGetUserQueryResultsWithCallBack = function() {
      //$rootScope.$broadcast("callGetUserQueryResultsWithCallBack", {});
      $scope.getUserQueryResultsWithCallBack();
    }

// merged options and dataset controllers

$scope.inittablelist = function () {
  $http.get('/zv/gettablelist'
  ).then(
    function (response) {
      console.log("success: ", response);
      console.log("cookies: ", userinfo);
      var userinfo = $cookies.getObject('userinfo');

      $http.get('zv/loginAvailable')
      .then(
        function (response_ava){
          login_ava = response_ava.data;
          if(login_ava){
            if(userinfo){
              // $scope.updatetablelist(userinfo['tablelist']);
              document.getElementById("loginmodaltrigger").style.display = "none";
              document.getElementById("signoutbutton").style.display = "block";
              datasetInfo.storetablelist(userinfo['tablelist'])
            }else{
              document.getElementById("signoutbutton").style.display = "none";
              document.getElementById("loginmodaltrigger").style.display = "block";
              datasetInfo.storetablelist(response.data);

            }
          }
          else{
            document.getElementById("signoutbutton").style.display = "none";
            document.getElementById("loginmodaltrigger").style.display = "none";
            datasetInfo.storetablelist(response.data);
          }
          $scope.tablelist = Array.from(new Set(datasetInfo.getTablelist().reverse()));
        }
      )


    },
    function (response) {
      console.log("failed to get table list: ", response);
      $("#errorModalText").html(response);
      $("#errorModal").modal();
    }

  );
};

    $scope.updatetablelist = function updatetablelist(args){
      // datasetInfo.storetablelist(args);
      $scope.tablelist = args;
    }

    //$scope.chartSettings = ChartSettings;
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
                xdata["name"],ydata["name"], (zdata==null) ? null : zdata["name"]
               );
              break;
      }
    }

    $scope.callLoadAxisInfo = function() {
      $rootScope.$broadcast("loadAxisInfo");
    }
    $scope.callClearDynamicClassOptions = function() {
      clearDynamicClassModal($('#dynamic-class'));
      $rootScope.$broadcast("clearDynamicClassOptions");
    }

    $scope.callGetClassInfo = function() {
      log.info("show dynamic class info")
      $rootScope.$broadcast("callGetClassInfo", {});
    }

    $scope.getUserQueryResultsWithCallBack = function getUserQueryResultsWithCallBack()
    {

      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;
      console.log("calling getUserQueryResults");
      console.log(data);
      $http.post('/zv/postSimilarity', data)
      .success(function(response) {
        console.log("getUserQueryResults: success");
        if (response.length == 0){console.log("empty response")}
        if(data.error != null)
        {
          console.log("calling getErrorResults");
          $http.post('/zv/postSimilarity_error', data).
          success(function(response_error) {
            console.log("getErrorResults: success");
            if (response_error.length == 0){console.log("empty response")}
            console.log("merged result: ", mergejoin(response.outputCharts,response_error.outputCharts));
            plotResults.displayUserQueryResults(response.outputCharts,true);
            $scope.getRepresentativeTrendsWithoutCallback();
          }).
          error(function(response_error) {
            console.log("getUserQueryResults: fail");
            document.getElementById("loadingEclipse").style.display = "none";
            $("#errorModalText").html(response);
            $("#errorModal").modal();
          });

        }
        else{
          plotResults.displayUserQueryResults(response.outputCharts,true);
          $scope.getRepresentativeTrendsWithoutCallback();}
        })
      .error(function(response) {
          console.log("getUserQueryResults: fail");
          document.getElementById("loadingEclipse").style.display = "none";
          document.getElementById("loadingEclipse2").style.display = "none";
          $("#errorModalText").html(response);
          $("#errorModal").modal();
       });
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
        if(data.error != null)
        {
          console.log("calling getErrorResults");
          $http.post('/zv/postSimilarity_error', data).
          success(function(response_error) {
            console.log("getErrorResults: success");
            if (response_error.length == 0){console.log("empty response")}
            console.log("merged result: ", mergejoin(response.outputCharts,response_error.outputCharts));
            plotResults.displayUserQueryResults(response.outputCharts,true);
         //$scope.getRepresentativeTrendsWithoutCallback(); dont recompute representative
          }).
          error(function(response_error) {
            console.log("getUserQueryResults: fail");
            document.getElementById("loadingEclipse").style.display = "none";
            document.getElementById("loadingEclipse2").style.display = "none";
            $("#errorModalText").html(response_error);
            $("#errorModal").modal();
          });

        }

        else{
          plotResults.displayUserQueryResults(response.outputCharts,true);
          //$scope.getRepresentativeTrendsWithoutCallback(); dont recompute representative
        }

      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
        document.getElementById("loadingEclipse").style.display = "none";
        document.getElementById("loadingEclipse2").style.display = "none";
        $("#errorModalText").html(response);
        $("#errorModal").modal();
      });


       // Update globalDatasetInfo['classes']
      var query = {};
      query["dataset"] = getSelectedDataset();
      $http.post('/zv/getClassInfo', query
      ).then(
          function (response) {
            console.log("success: ", response);
            globalDatasetInfo["classes"] = response.data
            var formattedRanges = formatRanges(response.data["classes"])
            for (var i = 0; i < response.data["classes"].length; i++){
              response.data["classes"][i].formattedRanges = formattedRanges[i]
              $scope.classes = response.data["classes"]
            }
          },
          function (response) {
            console.log("failed: ", response);
          }
      );

    }
    $scope.downloadResults =function downloadResults(args){
      console.log("downloading results")
      var q = constructUserQuery(); //goes to query.js
      var data = q;
      q.download = true;
      console.log("calling downloadSimilarity");
      q.includeQuery = getIncludeQuery();
      q.yOnly = getyOnlyCheck();

      var address = '/zv/downloadSimilarity';
      if (args=='query'){
        q.downloadThresh = getMinThresh();
        q.outlierCount = $("#num-results-download").val();
        q.downloadAll = getDownloadAll();
        address = '/zv/downloadSimilarity';
        log.info("query result download",q.outlierCount,q.yOnly,q.includeQuery,q.downloadAll,q.downloadThresh);
      }else if (args == 'representative'){
        q.kmeansClusterSize = $("#num-clusters-download").val();
        q.downloadAll = getDownloadAllRepresentative();
        address = '/zv/postRepresentative';
        log.info("representative result download",q.kmeansClusterSize,q.yOnly,q.downloadAll);
      }else if (args == 'outlier'){
        address = '/zv/downloadOutlier';
        q.downloadThresh =  getMinOutlierThresh();
        q.kmeansClusterSize = $("#num-outlier-download").val();
        log.info("outlier result download",q.kmeansClusterSize,q.yOnly);
      }
      console.log("before http post")
      $http.post(address, data).
      success(function(response) {
        // console.log("Response:",response);
        for (var key in response) {
          if (response.hasOwnProperty(key)) {
            //console.log(key + " -> " + response[key]);
            //var file = new File(["Hello, world!"], "hello world.txt", {type: "text/plain;charset=utf-8"});
            var file = new File([response[key].join("\n")], key, {type: "text/plain;charset=utf-8"});
            saveAs(file);
          }
        }
        console.log("download : success");
        // alert("Sucessfully saved to zenvisage/target")
      }).
      error(function(response) {
        console.log("download : fail");
        $("#errorModalText").html(response);
        $("#errorModal").modal();
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
        if(data.error != null)
        { console.log("original representative: ",response.outputCharts);
          console.log("calling getErrorResults");
          $http.post('/zv/postSimilarity_error', data).
          success(function(response_error) {
            console.log("getErrorResults: success");
            if (response_error.length == 0){console.log("empty response")}
            console.log("merged result in representative: ", mergejoin_representative(response.outputCharts,response_error.outputCharts));
            plotResults.displayRepresentativeResults(response.outputCharts,true);
            outlierCallback();
          }).
          error(function(response_error) {
            console.log("getRepresentativeTrends: fail");
            document.getElementById("loadingEclipse2").style.display = "none";
            $("#errorModalText").html(response_error);
            $("#errorModal").modal();
          });

        }
        else{plotResults.displayRepresentativeResults( response.outputCharts );
        outlierCallback();}
      }).
      error(function(response) {
        console.log("getRepresentativeTrends: fail");
        document.getElementById("loadingEclipse2").style.display = "none";
        $("#errorModalText").html(response);
        $("#errorModal").modal();
      });
    }
    function getyOnlyCheck(){
      return $("#yOnly").is(':checked');
    }

    function getIncludeQuery(){
      return $("#includeQuery").is(':checked');
    }

    function getDownloadAll(){
      return $("#downloadAll").is(':checked');
    }
    function getDownloadAllRepresentative(){
      return $("#downloadAllRepresentative").is(':checked');
    }
    function getMinThresh(){
        return $("#min-thresh-download").val();;
    }
    function getMinOutlierThresh(){
      return $("#min-thresh-download-outlier").val();;
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
        if(data.error != null)
        {
          console.log("calling getErrorResults");
          $http.post('/zv/postSimilarity_error', data).
          success(function(response_error) {
            console.log("getErrorResults: success");
            if (response_error.length == 0){console.log("empty response")}
            console.log("merged result: ", mergejoin(response.outputCharts,response_error.outputCharts));
            plotResults.displayOutlierResults(response.outputCharts,true);
          }).
          error(function(response_error) {
            console.log("getUserQueryResults: fail");
            document.getElementById("loadingEclipse2").style.display = "none";
            $("#errorModalText").html(response_error);
            $("#errorModal").modal();
          });

        }
        else{plotResults.displayOutlierResults( response.outputCharts )};
      }).
      error(function(response) {
        console.log("Don't worry, this outlier fail is normal. Ignoring failed outlier trends because sometimes because there are no outliers if representative takes that one up.")
        // console.log("getOutlierTrends: fail");
        // document.getElementById("loadingEclipse2").style.display = "none";
        // $("#errorModalText").html(response);
        // $("#errorModal").modal();
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

    function clearDynamicClassModal(form) {
      $(':input', form).each(function () {
          var type = this.type;
          if (type == 'text') {
            this.value = "";
          }
      });
      document.getElementById("load-dynamic-class-button").style.display = "none";
    };

   $scope.onDatasetChange = function(input) {
      console.log("on change,",getSelectedDataset());
      document.getElementById("loadingEclipse").style.display = "inline";
      document.getElementById("loadingEclipse2").style.display = "inline";
      log.info("dataset selected",$('#dataset-form-control').val());
      initSettingPanel();
      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();
      console.log('selected dataset',getSelectedDataset());
      if(input == 'initialize'){var q = constructDatasetChangeQuery('real_estate_tutorial');} //just for tutorial purposes
      else if(input == 'weather'){var q = constructDatasetChangeQuery('weather');}
      else if(input == 'real_estate'){var q = constructDatasetChangeQuery('real_estate');}
      else{
            var q = constructDatasetChangeQuery(getSelectedDataset());
          }

      var params = {
        "query": q,
      };
      var config = {
        params: params,
      };

      var datasetname = $('#dataset-form-control').val();

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
          angular.forEach(response.xAxisColumns, function(value, key) {
           $scope.xAxisItems.push(key);
          });
          angular.forEach(response.yAxisColumns, function(value, key) {
           $scope.yAxisItems.push(key);
          });
          // hard coding default x, y axis for preloaded dataset
          if (datasetname == "breast_cancer_cells"){
            $scope.selectedCategory = "gene";
            $scope.selectedXAxis = "timepoint";
            $scope.selectedYAxis = "expression";
          }else if(datasetname == "cmu"){
            $scope.selectedCategory = "class";
            $scope.selectedXAxis = "o2";
            $scope.selectedYAxis = "ea";
          }else if(datasetname == "des"){
            $scope.selectedCategory = "objid_cycle_band";
            $scope.selectedXAxis = "mjd57000";
            $scope.selectedYAxis = "psf_flux";
            $scope.selectedErrorAxis = "psf_flux_err"
          }else if(datasetname == "weather"){
          $scope.selectedCategory = "location";
          $scope.selectedXAxis = "month";
          $scope.selectedYAxis = "temperature";
        }
          else{
            $scope.selectedCategory = $scope.categories[0];
            $scope.selectedXAxis = $scope.xAxisItems[0];
            $scope.selectedYAxis = $scope.yAxisItems[0];
          }

          log.info("initialized data attribute",getSelectedCategory(),getSelectedXAxis(),getSelectedYAxis())
          //send in first item info

          // $.when(initializeSketchpadOnDataAttributeChange(
          //       response.xAxisColumns[$scope.xAxisItems[0]],
          //       response.yAxisColumns[$scope.yAxisItems[0]],
          //       response.zAxisColumns[$scope.categories[0]]
          //     )).done(function(){
          //       getRepresentativeTrends( getOutlierTrends );
          //     });


          initializeSketchpadOnDataAttributeChange(
                response.xAxisColumns[$scope.selectedXAxis],
                response.yAxisColumns[$scope.selectedYAxis],
                response.zAxisColumns[$scope.selectedCategory]
              );
          $scope.getUserQueryResultsWithCallBack();

          $scope.callLoadAxisInfo();
          $scope.callClearDynamicClassOptions();
        }).

        error(function(response) {
          alert('Request failed: /getformdata');
          document.getElementById("loadingEclipse").style.display = "none";
          document.getElementById("loadingEclipse2").style.display = "none";
        });
        resetSelectedErrorAxis();
    }

    // when the data selection is changed, the graphs needs to be re-initialized
    // and the rest of the graphs have to be fetched
    $scope.setDataAttributeToDynamicClass = function() {
      if($scope.categories && !$scope.categories.includes("dynamic_class")) {
        $scope.categories.push("dynamic_class");
      }
      $scope.selectedCategory = "dynamic_class";
      $scope.onDataAttributeChange();
    }

    $scope.onDataAttributeChange = function() {
      document.getElementById("loadingEclipse").style.display = "inline";
      document.getElementById("loadingEclipse2").style.display = "inline";

      var categoryData = datasetInfo.getCategoryData()[getSelectedCategory()]
      var xData = datasetInfo.getXAxisData()[getSelectedXAxis()]
      var yData = datasetInfo.getYAxisData()[getSelectedYAxis()]
      log.info("data attribute changed",getSelectedCategory(), getSelectedXAxis(),getSelectedYAxis())
      // $.when(initializeSketchpadOnDataAttributeChange(xData, yData, categoryData))
      // .done(function(){
      //   getRepresentativeTrends( getOutlierTrends );
      // });
      initializeSketchpadOnDataAttributeChange(xData, yData, categoryData);
      $scope.getUserQueryResultsWithCallBack();
    };

    $scope.onErrorAttributeChange = function() {
      document.getElementById("loadingEclipse").style.display = "inline";
      document.getElementById("loadingEclipse2").style.display = "inline";
      var categoryData = datasetInfo.getCategoryData()[getSelectedCategory()]
      var xData = datasetInfo.getXAxisData()[getSelectedXAxis()]
      var yData = datasetInfo.getYAxisData()[getSelectedYAxis()]
      log.info("error attribute changed",getSelectedCategory(), getSelectedXAxis(),getSelectedYAxis())
      // $.when(initializeSketchpadOnDataAttributeChange(xData, yData, categoryData))
      // .done(function(){
      //   getRepresentativeTrends( getOutlierTrends );
      // });
      initializeSketchpadOnDataAttributeChange(xData, yData, categoryData);
      $scope.getUserQueryResultsWithCallBack();
    };



    $scope.$on("callGetUserQueryResultsWithCallBack", function(){
      $scope.getUserQueryResultsWithCallBack();
    });

    $scope.$on("callGetUserQueryResults", function(){
      $scope.getUserQueryResults();
    });

    $scope.$on("callgetRepresentativeTrends", function(){
      $scope.getRepresentativeTrendsWithoutCallback();
    });


    // this init is just for tutorial purpose
    var init = function () {
       $scope.onDatasetChange('initialize');
    };
    // init();
    // and fire it after definition
    $scope.$on("updateAxes", function(event, xAxis, yAxis, category) {
        $scope.selectedXAxis = xAxis;
        $scope.selectedYAxis = yAxis;
        $scope.selectedCategory = category;
    });
}]);

app.service('ChartSettings', function () {
    return {};
})

  // $('#tree-option').click(function() {
  //   $(this).toggleClass("active");
  //   $("#tree-div").toggle("active");
  // });
