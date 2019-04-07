var isDrawing = false;
var lastDrawRow = null;
var lastDrawValue = null;
var usingPattern = false;

var sketchpadNew; // to store the data
var sketchpadData;

var xrangeNew;

function createSketchpad( data , flipY)
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

  var zoomwidth = 320 ;
  var zoomheight = 154 ;

  // set the ranges
  var x = d3.scaleLinear().range([0, width]);
  if(getflipY()){
      var y = d3.scaleLinear().range([0,height]);
  }
  else{
      var y = d3.scaleLinear().range([height, 0]);
  }
  log.info("initialize canvas axis",y.domain()[0],y.domain()[1])

/**************changed*******************/

  // range for the zoom
  var x2 = d3.scaleLinear().range([0, width]);
  if(getflipY()){
      var y2 = d3.scaleLinear().range([0,height2]);
  }
  else{
      var y2 = d3.scaleLinear().range([height2, 0]);
  }
  var miny = 0;
  var maxy = height;

  // Scale the range of the data
  xmin = d3.min(data, function(d) {return Math.min(d.xval); })
  xmax = d3.max(data, function(d) {return Math.max(d.xval); })
  ymin = d3.min(data, function(d) {return Math.min(d.yval); })
  ymax = d3.max(data, function(d) {return Math.max(d.yval); })

  x.domain([xmin, xmax]);
  y.domain([ymin, ymax]);
  xrangeNew = [xmin, xmax];

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
            "translate(" + margin.left + "," + margin.top + ")")

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
//zoom here
      // var zoomclip = svg.append("rect")
      //   .attr("width", zoomwidth)
      //   .attr("height", zoomheight)
      //   .attr("transform", "translate(" + margin.left + "," + margin.top + ")")
      //   .attr('fill', 'none')
      //   .attr('pointer-events', 'all');
      //   // .call(d3.zoom()      // zoom y axis behavior
      //   // .scaleExtent([1, Infinity])
      //   // .translateExtent([[0, 0], [width, height]]).extent([[0, 0], [width, height]])
      //   // .on("zoom", zoomed))
      //   // .on("mousedown.zoom", null)
      //   // .on("mousemove.zoom", null)
      //   // .on("mouseup.zoom", null)
      //   // .on("selectstart.zoom", null)
      //   // .on("click.zoom", null)
      //   // .on("dblclick.zoom", null)
      //   // .on("touchstart.zoom", null)
      //   // .on("touchmove.zoom", null)
      //   // .on("touchend.zoom", null)
      //   // .on("touchcancel.zoom", null);

  var brush = d3.brushX()
      //.scaleExtent([1, Infinity])
      .extent([[0, 0], [width, height2]])
      .on("end", brushed);

  focus.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline);

//zoom here
      function zoomed() {
      var t = d3.event.transform;
      y.domain(t.rescaleY(y2).domain());
      focus.select(".axis--y").call(d3.axisLeft(y).ticks(8, ".2"));
      focus.select(".line").attr("d",valueline);
    //  x.domain(t.rescaleX(x2).domain());
    //  focus.select(".axis--x").call(d3.axisBottom(x).ticks(8, ".2"));
    }

    function reset() {
     d3.select("#draw-div").transition().duration(750).call(zoom.transform, d3.zoomIdentity);
      }
     d3.select(".reset").on("click", reset);
  //Add the X Axis
  // focus.append("g")
  //     .attr("class", "axis x")
  //     .attr("transform", "translate(0," + height + ")")
  //     .call( d3.axisBottom(x).ticks(0) );


  // Add the Y Axis
  if ((Math.log10(ymax)<=0)&(Math.log10(ymax)>=-2)){
    focus.append("g")
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y).ticks(8, ".2"));
    }else{
    focus.append("g")
        .attr("class", "axis axis--y")
        .call(d3.axisLeft(y).ticks(8, "s"));
    }


    // Add the second x axis
    if ((Math.log10(xmax)<=3)&(Math.log10(xmax)>=-3)){
    focus.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x).ticks(8, "s"));
    }

    else if(getSelectedXAxis()==="year"){
        focus.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + height + ")")
          .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d")));
        } // for formatting all year x axis ticks except hardcoded real estate dataset
  else{
    focus.append("g")
      .attr("class", "axis axis--x")
      .attr("transform", "translate(0," + height + ")")
      .call(d3.axisBottom(x).ticks(6, "s"));
    }

  context.append("path")
      .data([data])
      .attr("class", "line")
      .attr("d", valueline2);

  if ((Math.log10(xmax)<=3)&(Math.log10(xmax)>=-3)){
  context.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height2 + ")")
    .call(d3.axisBottom(x).ticks(8, "s"));
  }

  else if(getSelectedXAxis()==="year"){
      context.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + height2 + ")")
        .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d")));
      } // for formatting all year x axis ticks except hardcoded real estate dataset

  else{
  context.append("g")
    .attr("class", "axis axis--x")
    .attr("transform", "translate(0," + height2 + ")")
    .call(d3.axisBottom(x).ticks(5, "s"));
  }

  context.append("g")
      .attr("class", "brush")
      .call(brush)
      .call(brush.move, x.range());

  function brushed() {
    if (d3.event.sourceEvent && d3.event.sourceEvent.type === "zoom") return; // ignore brush-by-zoom
    if (d3.event.sourceEvent === null) return; // ignore when not brushed
    var s = d3.event.selection || x2.range();
    x.domain(s.map(x2.invert, x2));
    focus.select(".line").attr("d", valueline);
    if ((Math.log10(xmax)<=3)&(Math.log10(xmax)>=-3)){
    focus.select(".axis--x").call(d3.axisBottom(x).ticks(8, "s"));
    }
    else if(getSelectedXAxis()==="year"){
        focus.select(".axis--x").call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d"))); // for formatting all year x axis ticks except hardcoded real estate dataset
      }
    else{
      focus.select(".axis--x").call(d3.axisBottom(x).ticks(6, "s"));
    }
    // svg.select(".zoom").call(zoom.transform, d3.zoomIdentity
    //     .scale(width / (s[1] - s[0]))
    //     .translate(-s[0], 0));

    var left = Number($(".selection")[0].getAttribute("x"))
    var right = Number($(".selection")[0].getAttribute("width"))
    xrangeNew = [x2.invert(left), x2.invert(left+right)];
    angular.element($("#sidebar")).scope().getUserQueryResults();
    log.info("brushing x range",xrangeNew[0],xrangeNew[1])
  }

  function setPointNew( ref )
  {
    var xclickedval = x.invert(d3.mouse(ref)[0]-leftMargin);
    var yclickedval = y.invert(d3.mouse(ref)[1]-topMargin);

    valueRange = [y.domain()[0], y.domain()[1]]

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
        selectedPoint = point;
      }
    }

    if(selectedPoint != -1){
      if (lastDrawRow === null) {
        lastDrawRow = selectedPoint;
        lastDrawValue = yclickedval;
      }
      var coeff = (yclickedval - lastDrawValue) / (selectedPoint - lastDrawRow);
      if (selectedPoint == lastDrawRow) coeff = 0.0;
      var minRow = Math.min(lastDrawRow, selectedPoint);
      var maxRow = Math.max(lastDrawRow, selectedPoint)

      for (var row = minRow; row <= maxRow; row++) {
        var val = lastDrawValue + coeff * (row - lastDrawRow);
        val = Math.max(valueRange[0], Math.min(val, valueRange[1]));
        currentData[row]["yval"] = val;
        if (val === null || yclickedval === undefined || isNaN(val)) {
        }
      }

      lastDrawRow = selectedPoint;
      lastDrawValue = yclickedval;

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

    d3.select("#draw-div .focus").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline);

    d3.select("#draw-div .context").selectAll("path")
          .data([currentData])
          .attr("class", "line")
          .attr("d", valueline2);
  }

  function mousedownEvent () {
    // prevents mouse drags from selecting page text.
    if (d3.event.preventDefault) {
      d3.event.preventDefault();  // Firefox, Chrome, etc.
    } else {
      d3.event.returnValue = false;  // IE
      d3.event.cancelBubble = true;
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

function plotSketchpadNew( data )//, xType, yType, zType)
{
    document.getElementById("loadingEclipse").style.display = "inline";
  $("#draw-div").children().remove();
  sketchpad = createSketchpad( data )

  // angular.element($("#sidebar")).scope().selectedCategory = zType;
  // angular.element($("#sidebar")).scope().selectedXAxis = xType;
  // angular.element($("#sidebar")).scope().selectedYAxis = yType;

  angular.element($("#sidebar")).scope().getUserQueryResults();
  refreshZoomEventHandler();
}

// initialize scatter?

function initializeSketchpadNew(xmin, xmax, ymin, ymax, xlabel, ylabel, category , flipY)
{
  // intialize to 100 points
  var data = [];
  for (var d = 0; d < 100; d += 1 ) {
    // data.push( { "xval": xmin + (xmax-xmin)/100 * d, "yval": (ymin+ymax)*d/100 } );
    data.push( { "xval": xmin + (xmax-xmin)*d/100, "yval": ymin + (ymax-ymin)*d/100 } );
  }
  // sketchpad = getSketchpadDygraphObject( data, valueRange );
  // getSketchpadDygraphObjectNew( data, valueRange );
  createSketchpad( data , flipY);
  refreshZoomEventHandler();
}

function finishDraw(event, g, context) {
  isDrawing = false;
  lastDrawRow = null;
  lastDrawValue = null;
  angular.element($("#sidebar")).scope().getUserQueryResults();
  log.info("sketched query",JSON.stringify(sketchpadData))
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
      }
    }
    lastDrawRow = closest_row;
    lastDrawValue = value;
    g.updateOptions({ file: data });
    g.setSelection(closest_row);  // prevents the dot from being finnicky.
  }
}

function patternLoad(){
  var delimiter=" "
  if ($("#x-pattern").val().indexOf(",")>-1){
    delimiter=","
  }
  var xvals = $("#x-pattern").val().split(delimiter);
  var yvals = $("#y-pattern").val().split(delimiter);
  if (xvals.length != yvals.length){
    alert("Error: The lengths of x and y values must match!");
  }
  data =[];
  for(var i = 0; i< xvals.length; i++){
     data.push({"xval":xvals[i],"yval":yvals[i]})
  }
  // data = JSON.parse($("#pattern-upload-textarea")[0].value);
  usingPattern = true;
  log.info("patternLoad : ",xvals,yvals)
  createSketchpad( data );
  refreshZoomEventHandler();
  $("#pattern-upload").modal('toggle'); // close the pattern-upload modal
  finishDraw();
}

function Point(x, y){
  this.xval=x;
  this.yval=y;
}
