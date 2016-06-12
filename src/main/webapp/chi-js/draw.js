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
  sketchObject = new Dygraph(document.getElementById("draw_div"), data,
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
          /*
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
          */
        }/*,
        strokeWidth: 1.5,
        gridLineColor: 'rgb(196, 196, 196)',
        drawYGrid: false,
        drawYAxis: false,
        drawXAxis: false*/
      });
      window.onmouseup = finishDraw;
  }
);