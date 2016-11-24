// imports and setups
_.mixin(_.str.exports());
$.ajaxSetup({
  timeout: 1000000,
});

function getBaselineData(query) {
	console.log(JSON.stringify(query));
	$.get('/zv/getbaseline','query='+ JSON.stringify(query), processBackEndData, 'json')
	.fail(function(){
		console.log("Failed")
		alert('Request failed: /getbaseline');
	});
}

function getSuggestData(query)
{
  $("#existing-trends").empty()
  $.get('/zv/getdata', 'query='+JSON.stringify(query), generateExistingTrends, 'json')
  .fail(function(){
    console.log("Failed")
    alert('Request failed: /getdata');
  });
}

//This method only GENERATES SCATTER Trends
function getScatterData(query){
	$("#existing-trends").empty()
	  $.get('/zv/getscatterplot','query='+ JSON.stringify(query), generateScatterTrends, 'json')
	  .fail(function(){
	    console.log("Failed")
	    alert('Request failed: /getscatterTrends');
	  });
}
//This method will get data for the OUTPUT charts
//When a person presses the "search" button, this will get called down the path
function getScatterPlot(query)
{
  console.log("sendquery", query)
  $.get('/zv/getscatterplot', 'query='+ JSON.stringify(query), returnResults, 'json')
      .fail(function(){
        console.log("Failed")
        alert('Request failed: /getscatterplot');
      });
}

function getSuggestTrends(query){
	  $("#existing-trends").empty()
	  $.get('/zv/getdata', JSON.stringify(query), generatingExistingTrending, 'json')
	  .fail(function(){
	    console.log("Failed")
	    alert('Request failed: /getdata');
	  });
}

function getZQLData(query) {
  $('div#container').empty();
  $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');

  console.log(JSON.stringify(query));
  $.get('/zv/executeZQL', 'query='+JSON.stringify(query), processBackEndData, 'json')
    .fail(function() {
      console.log("Failed")
      alert('Request failed: /executeZQL');
    });
}

function getData(query, xAxisType, yAxisType) {
  $('div#container').empty();
  $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');
  if (query.sketchPoints.length >0 && query.sketchPoints[0].points.length > 0 && query.method == "Outlier") {
	  query.method = "DissimilaritySearch";
	  console.log("Changed");
  }
  if (query.sketchPoints != []) {
	  for (var i = 0; i < 2; i++) {
		  var sketchPoints = query.sketchPoints[i];
		  if (sketchPoints == undefined) break;
		  for (var j = 0; j < sketchPoints.points.length; j++) {
			  if (sketchPoints.points[j].x > sketchPoints.maxX) {
				  alert("sketch points out of bounds");
				  return;
			  }
		  }
	  }
  }
  $.get('/zv/getdata', 'query='+JSON.stringify(query),
   function(data){
       /*function is the callback function. data is what server gives us.
         I do this so I can pass additional client info further along.
         This deals with the asyncronous nature of js
         what if users submitted again too fast before server finished first time?
         xAxisSelect0.getValue() will be updated, perhaps incorrectly!
         But the callback function relies on xAxisSelect0.getValue()! So it could use the wrong value!
       */
      processBackEndData(data, xAxisType, yAxisType);
      //update existing trends
  }, 'json')
    .fail(function() {
      console.log("Failed")
      alert('Request failed: /getdata');
    });
}

  function getInterfaceFormData(query){
	  var q = new formQuery(query);
	 //$.get('/getformdata',JSON.stringify(q),processFormData,'json')
    $.get('/zv/getformdata', 'query='+JSON.stringify(q) , processDatasetChange, 'json')
      .fail(function() {
        console.log("Failed")
        alert('Request failed: /getformdata');
      });
  }

  function getRepresentativeData(query){
	  //console.log(query);
	    $('div#container').empty();
	    $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');
	 //   console.log(JSON.stringify(query));
	    $.get('/zv/getdata','query='+JSON.stringify(query), processRepresentativeData, 'json')
	      .fail(function() {
	        console.log("Failed");
	        alert('Request failed: /getdata');
	      });
  }

  function get_sim(r,c,n) {
    if (r > c) {
      var tmp = r;
      r = c;
      c = tmp;
    }
    return r*((n-1-(r-1)) + (n-1))/2 + (c-r-1);
  }


/*
$(document).ready(function () {

  // need to initialize with real data
  var start_date = new Date("2002/12/29").getTime();
  var end_date = new Date().getTime();
  var data = [];
  for (var d = start_date; d < end_date; d += 604800 * 1000) {
    var millis = d + 2 * 3600 * 1000;
    data.push( [ new Date(Dygraph.dateString_(millis)), 50 ]);
  }

  var isDrawing = false;
  var lastDrawRow = null, lastDrawValue = null;
  // value range will be different
  var valueRange = [0, 100];

  function setPoint(event, g, context) {
    var graphPos = Dygraph.findPos(g.graphDiv);
    var canvasx = Dygraph.pageX(event) - graphPos.x;
    var canvasy = Dygraph.pageY(event) - graphPos.y;
    var xy = g.toDataCoords(canvasx, canvasy);
    var x = xy[0], value = xy[1];
    var rows = g.numRows();
    var closest_row = -1;
    var smallest_diff = -1;

    for (var row = 0; row < rows; row++) {
      var date = g.getValue(row, 0);  // millis
      var diff = Math.abs(date - x);
      if (smallest_diff < 0 || diff < smallest_diff) {
        smallest_diff = diff;
        closest_row = row;
      }
    }

    if (closest_row != -1) {
      if (lastDrawRow === null) {
        lastDrawRow = closest_row;
        lastDrawValue = value;
      }
      var coeff = (value - lastDrawValue) / (closest_row - lastDrawRow);
      if (closest_row == lastDrawRow) coeff = 0.0;
      var minRow = Math.min(lastDrawRow, closest_row);
      var maxRow = Math.max(lastDrawRow, closest_row);
      for (var row = minRow; row <= maxRow; row++) {
        var val = lastDrawValue + coeff * (row - lastDrawRow);
        val = Math.max(valueRange[0], Math.min(val, valueRange[1]));
        data[row][1] = val;
        if (val === null || value === undefined || isNaN(val)) {
          console.log(val);
        }
      }
      lastDrawRow = closest_row;
      lastDrawValue = value;
      g.updateOptions({ file: data });
      g.setSelection(closest_row);  // prevents the dot from being finnicky.
    }
  }

  function finishDraw(event, g, context) {
    isDrawing = false;
    lastDrawRow = null;
    lastDrawValue = null;
    //c.rawData_
    //angular.element($("#sidebar")).scope().updateEverything();
  }

  // sketchObject is global
  // x and y is not always date and value
  // TODO: need to initialize dynamically
  sketchpad = new Dygraph(document.getElementById("draw-div"), data,
      {
        valueRange: valueRange,
        labels: [ 'Date', 'Value' ],
        interactionModel: {
          mousedown: function (event, g, context) {
            // prevents mouse drags from selecting page text.
            if (event.preventDefault) {
              event.preventDefault();  // Firefox, Chrome, etc.
            } else {
              event.returnValue = false;  // IE
              event.cancelBubble = true;
            }
            isDrawing = true;
            setPoint(event, g, context);
          },
          mousemove: function (event, g, context) {
            if (!isDrawing) return;
            setPoint(event, g, context);
          },
          mouseup: function(event, g, context) {
            finishDraw(event, g, context);
          },
          //restore to original size
          dblclick: function(event, g, context) {
            Dygraph.defaultInteractionModel.dblclick(event, g, context);
          }

          mousewheel: function(event, g, context) {
            var normal = event.detail ? event.detail * -1 : event.wheelDelta / 40;
            var percentage = normal / 50;
            var axis = g.xAxisRange();
            var xOffset = g.toDomCoords(axis[0], null)[0];
            var x = event.offsetX - xOffset;
            var w = g.toDomCoords(axis[1], null)[0] - xOffset;
            var xPct = w === 0 ? 0 : (x / w);

            var delta = axis[1] - axis[0];
            var increment = delta * percentage;
            var foo = [increment * xPct, increment * (1 - xPct)];
            var dateWindow = [ axis[0] + foo[0], axis[1] - foo[1] ];

            g.updateOptions({
              dateWindow: dateWindow
            });
            Dygraph.cancelEvent(event);
          }

        },
        strokeWidth: 1.5,
        gridLineColor: 'rgb(196, 196, 196)',
        drawYGrid: false,
        drawYAxis: false,
        drawXAxis: false
      });
      window.onmouseup = finishDraw;
  }
);
*/