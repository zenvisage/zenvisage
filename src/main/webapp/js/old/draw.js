var isDrawing = false;
var lastDrawRow = null;
var lastDrawValue = null;

var sketchpadNew; // to store the data
var sketchpadData;

var xrangeNew;

function createSketchpadBarChart( data )
{
  // change these values somewhere. hard coded for now
  var topMargin = 0;
  var leftMargin = 30;
  var bottomMargin = 60;
  var rightMargin = 0;
  var margin = {top: topMargin, right: rightMargin, bottom: bottomMargin, left: leftMargin},
      width = 350 - margin.left - margin.right,
      height = 210 - margin.top - margin.bottom;

  var margin2 = {top: 180, right: 0, bottom: 0, left: 30};
  var height2 = height + margin.top + margin.bottom - margin2.top - margin2.bottom;

  // set the ranges
  var x = d3.scaleLinear().range([0, width]);
  var y = d3.scaleLinear().range([height, 0]);

  // range for the zoom
  var x2 = d3.scaleLinear().range([0, width]);
  var y2 = d3.scaleLinear().range([height2, 0]);
  var miny = 0;
  var maxy = height;

  // Scale the range of the data
  x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
  y.domain([0, 2*d3.max(data, function(d) {return Math.max(d.yval); })]);
  xrangeNew = [0, d3.max(data, function(d) {return Math.max(d.xval); })];

  // slider
  x2.domain(x.domain());
  y2.domain(y.domain());
  sketchpadNew = data;
  sketchpadData = data;

  // define the 1st line
  var valueline = d3.line()
      .x(function(d) {
        return x(d.xval);
      })
      .y(function(d) {
        return y(d.yval);
      });

  var valueline2 = d3.line()
      .x(function(d) { return x2(d.xval); })
      .y(function(d) { return y2(d.yval); });


  d3.select("#draw-div").selectAll("*").remove();//new
  var svg = d3.select("#draw-div").append("svg")
      .attr("viewBox", "-30 0 420 220") //new
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .on("mousedown", mousedownEvent )
      .on("mousemove", mousemoveEvent )
      .on("mouseup", mouseupEvent )
      .attr('fill', 'none')
      .attr('pointer-events', 'all')
      //.on("mouseout", mouseoutEvent )
      .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

  svg.append("defs").append("clipPath")
      .attr("id", "clip")
      .append("rect")
      .attr("width", width)
      .attr("height", height);

  var focus = svg.append("g")
      .attr("class", "focus")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  var context = svg.append("g")
      .attr("class", "context")
      .attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");

  var brush = d3.brushX()
      //.scaleExtent([1, Infinity])
      .extent([[0, 0], [width, height2]])
      .on("end", brushed);

  focus.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline);

  // Add the X Axis
  focus.append("g")
    .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  // Add the Y Axis
  focus.append("g")
      .attr("class", "axis axis--y")
      .call(d3.axisLeft(y));

  context.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline2);

  context.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height2 + ")")
      .call(d3.axisBottom(x2));

  context.append("g")
      .attr("class", "brush")
      .call(brush)
      .call(brush.move, x.range());

  function brushed() {
    if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
    var s = d3.event.selection || x2.range();
    x.domain(s.map(x2.invert, x2));
    focus.select(".line").attr("d", valueline);
    focus.select(".axis--x").call(d3.axisBottom(x));
    // svg.select(".zoom").call(zoom.transform, d3.zoomIdentity
    //     .scale(width / (s[1] - s[0]))
    //     .translate(-s[0], 0));

    var left = Number($(".selection")[0].getAttribute("x"))
    var right = Number($(".selection")[0].getAttribute("width"))
    xrangeNew = [x2.invert(left), x2.invert(left+right)];
    angular.element($("#sidebar")).scope().getUserQueryResults();
  }

  function setPointNew( ref )
  {
    var xclickedval = x.invert(d3.mouse(ref)[0]-leftMargin);
    var yclickedval = y.invert(d3.mouse(ref)[1]-topMargin);
    var closestRow = -1;

    var dataOjb = d3.select("path").data();
    var currentData = d3.select("path").data()[0];
    var numPoints = currentData.length;
    var diff = -1;
    var selectedPoint = -1;
    var smallestDiff = Number.POSITIVE_INFINITY;
    for (var point = 0; point < numPoints; point++) {
      var xval = currentData[point]["xval"];
      var diff = Math.abs(xval - xclickedval);

      if (diff < smallestDiff) {
        smallestDiff = diff;
        closestRow = xval;
        selectedPoint = point;
      }
    }

    if(selectedPoint != -1){
      var newPoint = yclickedval;
      if ( yclickedval > y.domain()[1] )
      {
        newPoint = y.domain()[1];
      }
      if ( yclickedval < y.domain()[0])
      {
        newPoint = y.domain()[0];
      }
      currentData[selectedPoint]["yval"] = newPoint;
    }

    sketchpadData = currentData;

    d3.select(".focus").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline);

    d3.select(".context").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline2);
  }

  function mousedownEvent () {
    // prevents mouse drags from selecting page text.
    if (event.preventDefault) {
      event.preventDefault();  // Firefox, Chrome, etc.
    } else {
      event.returnValue = false;  // IE
      event.cancelBubble = true;
    }
    isDrawing = true;
  }

  function mousemoveEvent (){
    if (!isDrawing) return;
    setPointNew( this );
  }

  function mouseupEvent (){
    finishDraw();
  }

  function mouseoutEvent (){
    if (isDrawing)
    {
      finishDraw();
    }
  }
}

function createSketchpad( data )
{
  // change these values somewhere. hard coded for now
  var topMargin = 0;
  var leftMargin = 30;
  var bottomMargin = 60;
  var rightMargin = 0;
  var margin = {top: topMargin, right: rightMargin, bottom: bottomMargin, left: leftMargin},
      width = 350 - margin.left - margin.right,
      height = 210 - margin.top - margin.bottom;

  var margin2 = {top: 180, right: 0, bottom: 0, left: 30};
  var height2 = height + margin.top + margin.bottom - margin2.top - margin2.bottom;

  // set the ranges
  var x = d3.scaleLinear().range([0, width]);
  var y = d3.scaleLinear().range([height, 0]);

  // range for the zoom
  var x2 = d3.scaleLinear().range([0, width]);
  var y2 = d3.scaleLinear().range([height2, 0]);
  var miny = 0;
  var maxy = height;

  // Scale the range of the data
  x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
  y.domain([0, 2*d3.max(data, function(d) {return Math.max(d.yval); })]);
  xrangeNew = [0, d3.max(data, function(d) {return Math.max(d.xval); })];

  // slider
  x2.domain(x.domain());
  y2.domain(y.domain());
  sketchpadNew = data;
  sketchpadData = data;

  // define the 1st line
  var valueline = d3.line()
      .x(function(d) {
        return x(d.xval);
      })
      .y(function(d) {
        return y(d.yval);
      });

  var valueline2 = d3.line()
      .x(function(d) { return x2(d.xval); })
      .y(function(d) { return y2(d.yval); });


  d3.select("#draw-div").selectAll("*").remove();//new
  var svg = d3.select("#draw-div").append("svg")
      .attr("viewBox", "-30 0 420 220") //new
      .attr("width", width + margin.left + margin.right)
      .attr("height", height + margin.top + margin.bottom)
      .on("mousedown", mousedownEvent )
      .on("mousemove", mousemoveEvent )
      .on("mouseup", mouseupEvent )
      .attr('fill', 'none')
      .attr('pointer-events', 'all')
      //.on("mouseout", mouseoutEvent )
      .attr("transform",
            "translate(" + margin.left + "," + margin.top + ")");

  svg.append("defs").append("clipPath")
      .attr("id", "clip")
      .append("rect")
      .attr("width", width)
      .attr("height", height);

  var focus = svg.append("g")
      .attr("class", "focus")
      .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

  var context = svg.append("g")
      .attr("class", "context")
      .attr("transform", "translate(" + margin2.left + "," + margin2.top + ")");

  var brush = d3.brushX()
      //.scaleExtent([1, Infinity])
      .extent([[0, 0], [width, height2]])
      .on("end", brushed);

  focus.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline);

  // Add the X Axis
  focus.append("g")
    .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x));

  // Add the Y Axis
  focus.append("g")
      .attr("class", "axis axis--y")
      .call(d3.axisLeft(y));

  context.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline2);

  context.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height2 + ")")
      .call(d3.axisBottom(x2));

  context.append("g")
      .attr("class", "brush")
      .call(brush)
      .call(brush.move, x.range());

  function brushed() {
    if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
    var s = d3.event.selection || x2.range();
    x.domain(s.map(x2.invert, x2));
    focus.select(".line").attr("d", valueline);
    focus.select(".axis--x").call(d3.axisBottom(x));
    // svg.select(".zoom").call(zoom.transform, d3.zoomIdentity
    //     .scale(width / (s[1] - s[0]))
    //     .translate(-s[0], 0));

    var left = Number($(".selection")[0].getAttribute("x"))
    var right = Number($(".selection")[0].getAttribute("width"))
    xrangeNew = [x2.invert(left), x2.invert(left+right)];
    angular.element($("#sidebar")).scope().getUserQueryResults();
  }

  function setPointNew( ref )
  {
    var xclickedval = x.invert(d3.mouse(ref)[0]-leftMargin);
    var yclickedval = y.invert(d3.mouse(ref)[1]-topMargin);
    var closestRow = -1;

    var dataOjb = d3.select("path").data();
    var currentData = d3.select("path").data()[0];
    var numPoints = currentData.length;
    var diff = -1;
    var selectedPoint = -1;
    var smallestDiff = Number.POSITIVE_INFINITY;
    for (var point = 0; point < numPoints; point++) {
      var xval = currentData[point]["xval"];
      var diff = Math.abs(xval - xclickedval);

      if (diff < smallestDiff) {
        smallestDiff = diff;
        closestRow = xval;
        selectedPoint = point;
      }
    }

    if(selectedPoint != -1){
      var newPoint = yclickedval;
      if ( yclickedval > y.domain()[1] )
      {
        newPoint = y.domain()[1];
      }
      if ( yclickedval < y.domain()[0])
      {
        newPoint = y.domain()[0];
      }
      currentData[selectedPoint]["yval"] = newPoint;
    }

    sketchpadData = currentData;

    d3.select(".focus").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline);

    d3.select(".context").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline2);
  }

  function mousedownEvent () {
    // prevents mouse drags from selecting page text.
    if (event.preventDefault) {
      event.preventDefault();  // Firefox, Chrome, etc.
    } else {
      event.returnValue = false;  // IE
      event.cancelBubble = true;
    }
    isDrawing = true;
  }

  function mousemoveEvent (){
    if (!isDrawing) return;
    setPointNew( this );
  }

  function mouseupEvent (){
    finishDraw();
  }

  function mouseoutEvent (){
    if (isDrawing)
    {
      finishDraw();
    }
  }
}

function plotSketchpadNew( data )
{
  // NEED TO DEFINE WHAT DATA IS
  $("#draw-div").children().remove();
  //sketchpad = getSketchpadDygraphObject( data, valueRange, xRange )
  sketchpad = createSketchpad( data )

  angular.element($("#sidebar")).scope().getUserQueryResults();
  refreshZoomEventHandler();
}

function initializeSketchpadNew(xmin, xmax, ymin, ymax, xlabel, ylabel, category)
{
  // intialize to 100 points
  var data = [];
  for (var d = 0; d < 100; d += 1 ) {
    data.push( { "xval": xmin + (xmax-xmin)/100 * d, "yval": (ymin+ymax)/2 } );
  }
  // sketchpad = getSketchpadDygraphObject( data, valueRange );
  // getSketchpadDygraphObjectNew( data, valueRange );
  createSketchpad( data );
  refreshZoomEventHandler();
}

function finishDraw(event, g, context) {
  isDrawing = false;
  lastDrawRow = null;
  lastDrawValue = null;
  angular.element($("#sidebar")).scope().getUserQueryResults();
}

function setPoint(event, g, context) {
  var graphPos = Dygraph.findPos(g.graphDiv);
  var canvasx = Dygraph.pageX(event) - graphPos.x;
  var canvasy = Dygraph.pageY(event) - graphPos.y;
  var xy = g.toDataCoords(canvasx, canvasy);
  var x = xy[0], value = xy[1];
  var rows = g.numRows();
  var closest_row = -1;
  var smallest_diff = -1;
  var data = sketchpad.rawData_;
  var valueRange = sketchpad.axes_[0]["extremeRange"]

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