var sketchpad; //only global variable for sketchpad

// need to handle state somewhere else (move into controller)
var isDrawing = false;
var lastDrawRow = null;
var lastDrawValue = null;

function getSketchpadDygraphObject( data, valueRange, xRange = null )
{
  return new Dygraph(document.getElementById("draw-div"), data,
    {
      valueRange: valueRange,
      axisLabelFontSize: 9,
      xLabelHeight: 9,
      title: getSelectedCategory(),
      titleHeight: 9,
      dateWindow: xRange,
      xAxisRange: xRange,
      showRangeSelector: true,
      rangeSelectorHeight: 25,
      rangeSelectorPlotFillColor: "",
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
          setPoint(event, g, context, data, valueRange);
        },
        mousemove: function (event, g, context) {
          if (!isDrawing) return;
          setPoint(event, g, context, data, valueRange);
        },
        mouseup: function(event, g, context) {
          finishDraw(event, g, context);
        },
        mouseout: function(event, g, context) {
          if (isDrawing)
          {
            finishDraw(event, g, context);
          }
        },
        dblclick: function(event, g, context) {
          Dygraph.defaultInteractionModel.dblclick(event, g, context);
        }
      },
    });
}

function datetimeTesting()
{
  if (sketchpad != null) {
    sketchpad.destroy()
  }

  var data = [];
  for (var i = 0; i < temp3.length ; i++ ) {
    data.push([ new Date(temp3[i][0]), Number(temp3[i][1]) ]);
  }

  console.log(data)
  var valueRange = [28,91]
  sketchpad = new Dygraph(document.getElementById("draw-div"), data,
    {
      valueRange: valueRange,
      axisLabelFontSize: 9,
      xLabelHeight: 9,
      title: getSelectedCategory(),
      titleHeight: 9,
      //dateWindow: xRange,
      //xAxisRange: xRange,
      showRangeSelector: true,
      rangeSelectorHeight: 25,
      rangeSelectorPlotFillColor: "",
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
          setPoint(event, g, context, data, valueRange);
        },
        mousemove: function (event, g, context) {
          if (!isDrawing) return;
          setPoint(event, g, context, data, valueRange);
        },
        mouseup: function(event, g, context) {
          finishDraw(event, g, context);
        },
        mouseout: function(event, g, context) {
          if (isDrawing)
          {
            finishDraw(event, g, context);
          }
        },
        dblclick: function(event, g, context) {
          Dygraph.defaultInteractionModel.dblclick(event, g, context);
        }
      },
    });
}

// when drag dropped
function plotSketchpad( dygraphObject )
{
  var data = [];
  for (var i = 0; i < dygraphObject.rawData_.length; i++ ) {
    data.push([ Number(dygraphObject.rawData_[i][0]), Number(dygraphObject.rawData_[i][1]) ]);
  }
  var valueRange = dygraphObject.axes_[0]["valueRange"]
  var xRange = sketchpad.xAxisRange()

  if (sketchpad != null) {
    sketchpad.destroy()
  }
  sketchpad = getSketchpadDygraphObject( data, valueRange, xRange )
  angular.element($("#sidebar")).scope().getUserQueryResults();
  refreshZoomEventHandler();
}

function initializeSketchpad(xmin, xmax, ymin, ymax, xlabel, ylabel, category)
{
  if (sketchpad != null) {
    sketchpad.destroy()
  }
  var data = []
  for (var d = xmin; d < xmax + 1; d += 1 ) {
    data.push( [ d, (ymin+ymax)/2 ] );
  }
  var valueRange = [ymin, ymax];
  sketchpad = getSketchpadDygraphObject( data, valueRange );
  refreshZoomEventHandler();
}

function finishDraw(event, g, context) {
  isDrawing = false;
  lastDrawRow = null;
  lastDrawValue = null;
  angular.element($("#sidebar")).scope().getUserQueryResults();
}

function setPoint(event, g, context, data, valueRange) {
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

function Point(x, y){
  this.x=x;
  this.y=y;
}