//stores dygraphs
var userQueryDygraphs = {};
var representativeDygraphs = {};
var outlierDygraphs = {};

var userQueryDygraphsNew = {};
var representativeDygraphsNew = {};
var outlierDygraphsNew = {};

//displays user results

function displayUserQueryResultsHelperNew( userQueryResults )
{
  clearUserQueryResultsTable();
  var resultsDiv = $("#results-table");
  var current = 0;
  var connectSeparatedPoints = true;
  var pointSize = 1.0;
  var drawPoints = false;
  var strokeWidth = 1.0;
  if ( getScatterplotOption() )
  {
    connectSeparatedPoints = false;
    pointSize = 1;
    drawPoints = true;
    strokeWidth = 0;
  }
  for (var count = 0; count < userQueryResults.length; count++)
  {
    if (count % 2 == 0)
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

    var considerRange = userQueryResults[count]["considerRange"];
    //var data = combineTwoArrays(xData, yData, sketchpad.rawData_);

    var valueRange = [ymin, ymax];

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
    }

    var data2 = sketchpadData;
    userQueryDygraphsNew["result-" + count.toString()] = data;

    //top right bottom left
    var m = [0, 0, 20, 20]; // margins
    var width = 220//200// - m[1] - m[3]; // width
    var height = 105//85// - m[0] - m[2]; // height

    // X scale will fit all values from data[] within pixels 0-w
    var x = d3.scaleLinear().range([20, width-20]);
    var y = d3.scaleLinear().range([height-20, 20]);
    x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
    y.domain([0, d3.max(data, function(d) {return Math.max(d.yval); })]);

    var valueline = d3.line()
    .x(function(d) {
      return x(d.xval);
    })
    .y(function(d) {
      return y(d.yval);
    });

    // Add an SVG element with the desired dimensions and margin.
    var graph = d3.select("#result-" + count.toString())
          .append("svg")
          .attr("viewBox","0 0 "+width.toString()+" "+ (height+15).toString())
          .attr("width", width)// + m[1] + m[3])
          .attr("height", height)// + m[0] + m[2])

          //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");

    graph.append("rect")
        .attr("width", (xRange[0]-xmin)/(xmax-xmin)*(width-40) )
        .attr("height", height-40)
        .attr("transform", "translate(20,20)")
        .attr("fill", "grey");

    var newLoc = (xRange[1])/(xmax-xmin)*(width-40)+20
    graph.append("rect")
        .attr("width", (xmax-xRange[1])/(xmax-xmin)*(width-40))
        .attr("height", height-40)
        .attr("transform", "translate(" + newLoc.toString() + ",20)")
        .attr("fill", "grey");

    var trans = height-20
    // create yAxis
    graph.append("g")
      .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + trans + ")")
        .call(d3.axisBottom(x));

    graph.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (trans + m[0] + 30) + ")")
      .style("text-anchor", "middle")
      .text(xlabel);

    // Add the Y Axis
    graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y));

    // Add the line by appending an svg:path element with the data line we created above
    // do this AFTER the axes above so that the line is above the tick-lines
    graph.append("path").attr("d", valueline(data))
                        .attr("stroke", "black")
                        .attr("stroke-width", 1)
                        .attr("fill", "none");
    graph.append("path").attr("d", valueline(data2))
                        .attr("stroke", "teal")
                        .attr("stroke-width", 1)
                        .attr("fill", "none");

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

function displayRepresentativeResultsHelperNew( representativePatternResults )
{
  clearRepresentativeTable();
  var resultsDiv = $("#representative-table");
  var varFinalArray = []
  var arrLength = representativePatternResults.length < 3 ? representativePatternResults.length : 3

  for(var count = 0; count < arrLength; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"representative-row-" + count.toString() + "\"></tr>")
    $("#representative-row-" + count.toString()).append("<td><div class=\"representative-results draggable-graph\" data-graph-type=\"representativeQuery\" id=\"representative-result-" + count.toString() + "\"></div></td>");
    varFinalArray.push( representativePatternResults[count] );
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

    var valueRange = [ymin, ymax];
    var xRange = [xmin, xmax];
    //representativeDygraphs["representative-result-" + count.toString()] = getRepresentativeAndOutlierDygraphObject( data, xRange, valueRange, xlabel, count, "representative-result-", representativeCount );


    // START HERE
    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
    }
    representativeDygraphsNew["representative-result-" + count.toString()] = data;

    //top right bottom left
    var m = [0, 0, 20, 20]; // margins
    var width = 220//200// - m[1] - m[3]; // width
    var height = 105//85// - m[0] - m[2]; // height

    // X scale will fit all values from data[] within pixels 0-w
    var x = d3.scaleLinear().range([20, width-20]);
    var y = d3.scaleLinear().range([height-20, 20]);
    x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
    y.domain([0, d3.max(data, function(d) {return Math.max(d.yval); })]);

    var valueline = d3.line()
    .x(function(d) {
      return x(d.xval);
    })
    .y(function(d) {
      return y(d.yval);
    });

    // Add an SVG element with the desired dimensions and margin.
    var graph = d3.select("#representative-result-" + count.toString())
          .append("svg")
          .attr("viewBox","0 0 "+width.toString()+" "+ (height+15).toString())
          .attr("width", width)// + m[1] + m[3])
          .attr("height", height)// + m[0] + m[2])
          //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");

    var trans = height-20
    // create yAxis
    graph.append("g")
      .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + trans + ")")
        .call(d3.axisBottom(x));


    graph.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (trans + m[0] + 30) + ")")
      .style("text-anchor", "middle")
      .text(xlabel);

    // Add the Y Axis
    graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y));

    // Add the line by appending an svg:path element with the data line we created above
    // do this AFTER the axes above so that the line is above the tick-lines
    graph.append("path").attr("d", valueline(data))
                        .attr("stroke", "black")
                        .attr("stroke-width", 1)
                        .attr("fill", "none");

  }
}

// function displayRepresentativeResultsHelper( representativePatternResults )
// {
//   clearRepresentativeTable();
//   var resultsDiv = $("#representative-table");
//   var varFinalArray = []
//   var arrLength = representativePatternResults.length < 4 ? representativePatternResults.length : 4

//   for(var count = 0; count < arrLength; count++) //need to fix count
//   {
//     var newRow = resultsDiv.append("<tr id=\"representative-row-" + count.toString() + "\"></tr>")
//     $("#representative-row-" + count.toString()).append("<td><div class=\"representative-results draggable-graph\" data-graph-type=\"representativeQuery\" id=\"representative-result-" + count.toString() + "\"></div></td>");
//     varFinalArray.push(representativePatternResults[count]);
//   }

//   for (var count = 0; count < varFinalArray.length; count++)
//   {
//     var xData = varFinalArray[count]["xData"];
//     var yData = varFinalArray[count]["yData"];

//     var xlabel = varFinalArray[count]["xType"];
//     var ylabel = varFinalArray[count]["yType"];

//     var xmin = Math.min.apply(Math, xData);
//     var xmax = Math.max.apply(Math, xData);
//     var ymin = Math.min.apply(Math, yData);
//     var ymax = Math.max.apply(Math, yData);
//     var representativeCount = " (" + varFinalArray[count]["count"] + ")";

//     var data = [];
//     var arrayLength = xData.length;
//     for (var i = 0; i < arrayLength; i++ ) {
//       data.push( [ Number(xData[i]), Number(yData[i]) ] );
//     }
//     var valueRange = [ymin, ymax];
//     var xRange = [xmin, xmax];
//     representativeDygraphs["representative-result-" + count.toString()] = getRepresentativeAndOutlierDygraphObject( data, xRange, valueRange, xlabel, count, "representative-result-", representativeCount );
//   }

//   $(".draggable-graph").draggable({
//     opacity: 0.5,
//     helper: function() {
//       return $(this).clone().css({
//         width: $(event.target).width(),
//         'border-style': "solid",
//         'border-width': 1
//       });
//     }
//   });
// }

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


function displayOutlierResultsHelperNew( outlierResults )
{
  clearOutlierTable();
  var resultsDiv = $("#outlier-table");
  var varFinalArray = [];
  for(var count = 0; count < 3; count++) //need to fix count
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
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
    }

    outlierDygraphsNew["outlier-result-" + count.toString()] = data;

    //top right bottom left
    var m = [0, 0, 20, 20]; // margins
    var width = 220//200// - m[1] - m[3]; // width
    var height = 105//85// - m[0] - m[2]; // height

    // X scale will fit all values from data[] within pixels 0-w
    var x = d3.scaleLinear().range([20, width-20]);
    var y = d3.scaleLinear().range([height-20, 20]);
    x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
    y.domain([0, d3.max(data, function(d) {return Math.max(d.yval); })]);

    var valueline = d3.line()
    .x(function(d) {
      return x(d.xval);
    })
    .y(function(d) {
      return y(d.yval);
    });

    // Add an SVG element with the desired dimensions and margin.
    var graph = d3.select("#outlier-result-" + count.toString())
          .append("svg")
          .attr("viewBox","0 0 "+width.toString()+" "+ (height+15).toString())
          .attr("width", width)// + m[1] + m[3])
          .attr("height", height)// + m[0] + m[2])
          //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");

    var trans = height-20
    // create yAxis
    graph.append("g")
      .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + trans + ")")
        .call(d3.axisBottom(x));

    // Add the Y Axis
    graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y));

    // Add the line by appending an svg:path element with the data line we created above
    // do this AFTER the axes above so that the line is above the tick-lines
    graph.append("path").attr("d", valueline(data))
                        .attr("stroke", "black")
                        .attr("stroke-width", 1)
                        .attr("fill", "none");
    graph.append("text")
        .attr("x", width / 2 )
        .attr("y",  height + 10)
        .style("text-anchor", "middle")
        .text("State");
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

function uploadToSketchpadNew( draggableId, graphType )
{
  var draggedGraph;
  switch( graphType ) {
    case "representativeQuery":
      draggedGraph = representativeDygraphsNew[draggableId];
      break;
    case "outlierQuery":
      draggedGraph = outlierDygraphsNew[draggableId];
      break;
    default: //userQuery
      draggedGraph = userQueryDygraphsNew[draggableId];
  }
  plotSketchpadNew( draggedGraph );
}

function addRow() {
  var table = $("#zql-table > tbody")[0];
  var rowCount = table.rows.length;
  var rowNumber = (rowCount+1).toString();
  $("#zql-table").append("<tr id=\"table-row-" + rowNumber + "\"class=\"tabler\"><td><input class=\"form-control zql-table number\" type=\"text\" size=\"5\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"20\" value=\" \"></td><td><input class=\"form-control zql-table process\" type=\"text\" id=\"process-" + rowNumber + "\"size=\"25\" value=\" \"></td><td></td></tr>");
}


$(document).ready(function(){

  // $('#add-row').click(function(){
  //   addRow();
  // });

  $("#draw-div").droppable({
    accept: ".draggable-graph",
    drop: function( event, ui )
    {
      uploadToSketchpadNew($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
      // uploadToSketchpad($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
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
    else if (arr1_xdata[i] < arr2[j][0])
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

function getEvaluatingRange( xmin, xmax, xrange )
{
  var first_left;
  var first_right;
  var second_left;
  var second_right;

  // if range does not cover anything... should not be displayed actually
  if (xrange[1] <= xmin || xrange[0] >= xmax )
  {
    first_left = xmin;
    first_right = xmax;
    second_left = xmax;
    second_right = xmax;
  }
  else if ( xrange[0] <= xmin && xrange[1] <= xmax && xrange[1] >= xmin)
  {
    first_left = xmin;
    first_right = xmin;
    second_left = xrange[1];
    second_right = xmax;
  }
  else if ( xrange[0] >= xmin && xrange[1] <= xmax )
  {
    first_left = xmin;
    first_right = xrange[0];
    second_left = xrange[1];
    second_right = xmax;
  }
  else if ( xrange[0] >= xmin && xrange[1] >= xmax && xrange[0] <= xmax )
  {
    first_left = xmin;
    first_right = xrange[0];
    second_left = xmax;
    second_right = xmax;
  }
  else
  {
    first_left = xmin;
    first_right = xmin;
    second_left = xmax;
    second_right = xmax;
  }
  return [first_left, first_right, second_left, second_right]
}