//stores dygraphs
var userQueryDygraphs = {};
var representativeDygraphs = {};
var outlierDygraphs = {};

//displays user results
function displayUserQueryResultsHelper( userQueryResults )
{
  clearUserQueryResultsTable();
  var resultsDiv = $("#results-table");
  var current = 0;
  for (var count = 0; count < userQueryResults.length; count++)
  {
    if (count%2 == 0)
    {
      var newRow = $("#results-table").append("<tr id=\"row-" + count.toString() + "\"></tr>")
      current = count;
    }
    $("#row-" + current.toString()).append("<td><div class=\"user-query-results draggable-graph\" data-graph-type=\"userQuery\" id=\"result-" + count.toString() + "\"></div></td>");
  }
  for (var count = 0; count < userQueryResults.length; count++)
  {
    var xData = userQueryResults[count]["xData"];
    var yData = userQueryResults[count]["yData"];

    var xlabel = userQueryResults[count]["xType"];
    var ylabel = userQueryResults[count]["yType"];
    var xRange = userQueryResults[count]["xRange"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    /*
    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ Number(xData[i]), Number(yData[i]), Number(sketchpad.rawData_[i][1]) ]);
    }
    */

    var data = combineTwoArrays(xData, yData, sketchpad.rawData_);

    var valueRange = [ymin, ymax];
    userQueryDygraphs["result-" + count.toString()] = new Dygraph(document.getElementById("result-" + count.toString()), data,
      {
        valueRange: valueRange,
        xlabel: xlabel,
        xLabelHeight: 11,
        axisLabelWidth: 11,
        axisLabelFontSize: 9,
        showLabelsOnHighlight: false,
        pixelsPerLabel: 20,
        highlightCircleSize: 0,
        interactionModel: {},
        drawGrid: false,
        colors: [ "0E3340", "#90C3D4" ],
        underlayCallback: function(canvas, area, g) {
            var first_left = g.toDomCoords(xmin, -20)[0];
            var first_right = g.toDomCoords(xRange[0], +20)[0];
            var second_left = g.toDomCoords(xRange[1], -20)[0];
            var second_right = g.toDomCoords(xmax, +20)[0];

            canvas.fillStyle = "rgba(70, 70, 70, 1.0)";
            canvas.fillRect(first_left, area.y, first_right - first_left, area.h);
            canvas.fillRect(second_left, area.y, second_right - second_left, area.h);
        },
      });
  }

  $(".draggable-graph").draggable({
    opacity: 0.5,
    helper: function() {
      return $(this).clone().css({
        width: $(event.target).width(),
        'border-style': "solid",
        'border-width': 1
      });
    }
  });

}

function displayRepresentativeResultsHelper( representativePatternResults )
{
  clearRepresentativeTable();
  var resultsDiv = $("#representative-table");
  var varFinalArray = []
  var arrLength = representativePatternResults.length < 4 ? representativePatternResults.length : 4
  for(var count = 0; count < arrLength; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"representative-row-" + count.toString() + "\"></tr>")
    $("#representative-row-" + count.toString()).append("<td><div class=\"representative-results draggable-graph\" data-graph-type=\"representativeQuery\" id=\"representative-result-" + count.toString() + "\"></div></td>");
    varFinalArray.push(representativePatternResults[count]);
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];

    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);
    var representativeCount = " (" + varFinalArray[count]["count"] + ")";

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ Number(xData[i]), Number(yData[i]) ] );
    }
    var valueRange = [ymin, ymax];
    var xRange = [xmin, xmax];
    representativeDygraphs["representative-result-" + count.toString()] = getRepresentativeAndOutlierDygraphObject( data, xRange, valueRange, xlabel, count, "representative-result-", representativeCount );
  }

  $(".draggable-graph").draggable({
    opacity: 0.5,
    helper: function() {
      return $(this).clone().css({
        width: $(event.target).width(),
        'border-style': "solid",
        'border-width': 1
      });
    }
  });
}

function displayOutlierResultsHelper( outlierResults )
{
  clearOutlierTable();
  var resultsDiv = $("#outlier-table");
  var varFinalArray = [];
  for(var count = 0; count < 4; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"outlier-row-" + count.toString() + "\"></tr>")
    $("#outlier-row-" + count.toString()).append("<td><div class=\"outlier-results draggable-graph\" data-graph-type=\"outlierQuery\" id=\"outlier-result-" + count.toString() + "\"></div></td>");
    varFinalArray.push(outlierResults[count]);
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];

    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ Number(xData[i]), Number(yData[i]) ] );
    }
    var valueRange = [ymin, ymax];
    var xRange = [xmin, xmax];
    outlierDygraphs["outlier-result-" + count.toString()] = getRepresentativeAndOutlierDygraphObject( data, xRange, valueRange, xlabel, count, "outlier-result-" )
  }

  $(".draggable-graph").draggable({
    opacity: 0.5,
    helper: function() {
      return $(this).clone().css({
        width: $(event.target).width(),
        'border-style': "solid",
        'border-width': 1
      });
    }
  });
}

function getRepresentativeAndOutlierDygraphObject( data, xRange, valueRange, xLabel, count, id, representativeCount = "" )
{
  return new Dygraph(document.getElementById(id + count.toString()), data,
    {
      valueRange: valueRange,
      axisLabelFontSize: 9,
      pixelsPerLabel: 20,
      title: getSelectedCategory(),
      titleHeight: 9,
      axisLabelWidth: 11,
      dateWindow: xRange,
      xAxisRange: xRange,
      showLabelsOnHighlight: false,
      interactionModel: {},
      colors: [ "0E3340" ],
      xlabel: xLabel + representativeCount.toString(),
      xLabelHeight: 11,
      drawGrid: false,
    });
}

function uploadToSketchpad( draggableId, graphType )
{
  var draggedGraph;
  switch( graphType ) {
    case "representativeQuery":
      draggedGraph = representativeDygraphs[draggableId];
      break;
    case "outlierQuery":
      draggedGraph = outlierDygraphs[draggableId];
      break;
    default: //userQuery
      draggedGraph = userQueryDygraphs[draggableId];
  }
  plotSketchpad( draggedGraph );
}


$(document).ready(function(){
  $("#draw-div").droppable({
    accept: ".draggable-graph",
    drop: function( event, ui )
    {
      uploadToSketchpad($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
    }
  });
});

function clearRepresentativeTable()
{
  $("#representative-table").find('tr').not('.middle-right-headers').remove();
}

function clearOutlierTable()
{
  $("#outlier-table").find('tr').not('.middle-right-headers').remove();
}

function clearUserQueryResultsTable()
{
  $("#results-table").empty();
}


// custom event handler which triggers when zoom range is adjusted
var global_xrange;
function refreshZoomEventHandler() {
  $("#draw-div").off();
  $(".dygraph-rangesel-fgcanvas").off();
  $(".dygraph-rangesel-zoomhandle").off();
  $("#draw-div").on('mousedown', '.dygraph-rangesel-fgcanvas, .dygraph-rangesel-zoomhandle', function(){
    global_xrange = sketchpad.xAxisRange();
  });
  $("#draw-div").on('mouseup', '.dygraph-rangesel-fgcanvas, .dygraph-rangesel-zoomhandle', function() {
    var xr = sketchpad.xAxisRange();
    if (global_xrange[0] !== xr[0] || global_xrange[1] !== xr[1])
    {
      angular.element($("#sidebar")).scope().getUserQueryResults();
    }
  });
}

function combineTwoArrays( arr1_xdata, arr1_ydata, arr2 )
{
  data = [];
  i = 0;
  j = 0;
  while (arr1_xdata.length > i && arr2.length > j)
  {
    if ( Number(arr1_xdata[i]) == arr2[j][0] )
    {
      data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), arr2[j][1]] );
      i += 1;
      j += 1;
    }
    else if (arr1_xdata[i][0] < arr2[j][0])
    {
       data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), null] );
       i += 1;
    }
    else //(arr1_xdata[i] > arr2[j])
    {
      var vals = arr2[j];
      data.push( [vals[0], null, vals[1]] );
      j += 1;
    }
  }
  while(arr1_xdata.length > i)
  {
    data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), null] );
    i += 1;
  }
  while(arr2.length > j)
  {
    var vals = arr2[j];
    data.push( [vals[0], null, vals[1]] );
    j += 1;
  }
  return data
}

function separateTwoArrays( data )
{
  arr1 = [];
  arr2 = [];
  i = 0
  while ( data.length > i)
  {
    var item = data[i];
    if (item[1] && item[2])
    {
      arr1.push([item[0], item[1]]);
      arr2.push([item[0], item[2]]);
    }
    else if (item[1]) //item[2] is null
    {
      arr1.push([item[0], item[1]]);
    }
    else
    {
      arr2.push([item[0], item[2]]);
    }
    i += 1;
  }
  return [arr1, arr2]
}















