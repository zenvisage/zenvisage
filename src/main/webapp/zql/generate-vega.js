

// input is data["outputCharts"][index];
function createJSON(data, width, height, xUnit, yUnit, existingTrend) {
  if (typeof existingTrend === 'undefined') { existingTrend = false; }

  // specific output for real_estate dataset

	if (getDataset() == "real_estate") {
		var json = {};
		var minmax = getMinMaxValues(data);
		// reduce k
		yUnit = "";
		unit = 1;
		if (minmax["ymax"] > 10000) {
			yData = data["yData"];
			for(i = 0; i < Object.keys(yData).length; i++) {
				yData[i] /= 1000;
			}
			yUnit = "K";
			minmax["ymin"] /= 1000;
			minmax['ymax'] /= 1000;
			unit = 1000;
		}

		json["width"] = width;
		json["height"] = height;
		json["data"] = createreal_estateDataJSON( data, existingTrend );
		json["scales"] = createreal_estateScaleJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
		json["axes"] = createreal_estateAxesJSON( data, xUnit, yUnit,existingTrend, json );
		json["marks"] = createMarksJSON();
		//json["padding"]= "auto";
		return json;
	}

	else {
		var json = {};
		var minmax = getMinMaxValues( data )
		json["width"] = width;
		json["height"] = height;
		json["data"] = createDataJSON( data );
		json["scales"] = createScaleJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
		json["axes"] = createAxesJSON( data, xUnit, yUnit, existingTrend);
		json["marks"] = createMarksJSON();
		//json["visualization"] = {"padding":0 15 0 0};
		return json;
	}
}

function createreal_estateAxesJSON( data, xUnit, yUnit,existingTrend, json )
{
  var xTitle = data["xType"]
  if (data["count"] != "0") {
	  xTitle += " (Count=" + data["count"] + ")"
  }
  //console.log(yUnit);
  if (yUnit != "K")
	  yUnit = "";
  var yTitle = data["yType"] + " " + yUnit;
  var  tickCount =10
  if (existingTrend)
  {
    tickCount = 5
  }
  var windowWidth = $(window).width()
  if(windowWidth <= 1240){
    tickCount = 6;
  }
  var axes =  [
    {"type": "x", "scale": "xValues", "title": xTitle, "titleOffset":45,
    "tickSizeEnd": 0,
    "properties" : {
      "title": {
         "fontSize": {
        	 "value": 11
         }
      },
      "labels": {
        "angle": {"value": 30},
        //,"text":{"value": " "},

      }

    }
    },
    {"type": "y", "scale": "y", "title": yTitle, "titleOffset":55, "ticks": tickCount, "tickSizeEnd": 0,"properties": {"title": {
        "fontSize": {"value": 11}}}}
  ];
  var Xparameter = getDistanceMethod();  // ignore x means DTW.
  if(Xparameter == "dtw"){
	  axes[0] = {"type": "x", "scale": "x", "title": xTitle, "titleOffset": 45, "values":[]}
  }
  return axes
}




function createreal_estateDataJSON(data, existingTrend) {
	var base = 2004; //Data assumes the base year of 2004
	var jsonData = [];
	var table_data_values = [];
	var xData = data["xData"];
	var yData = data["yData"];
  var xValues = []; //Keeps tracks of the values we want to display on the xAxis
  var xValueIndex = 0;
  var tickCount = 10;
  var windowWidth = $(window).width()
  if(windowWidth <= 1452){
    tickCount = 6;
  }  if (getXAxis() == "Year") {
    //console.log("Year", xData)
    if(existingTrend){
      tickCount = 6;
    }
    var interval = 0;
		for (i = 0; i < Object.keys(xData).length; i++) {
			var year = (base + parseInt(xData[i]) - 1).toString(); //Data is given as (1,2,3,4,5) years from the base
      if(interval ==0){
        xValues[xValueIndex++] = year;
        interval = Math.round(Object.keys(xData).length/tickCount);
      }
      interval --;
		}
	}
	else if (getXAxis() == "Quarter") {
    if(existingTrend){
      tickCount = 4;
    }
      //console.log("Quarter", xData)
      var interval = 0
      for(i = 0; i < Object.keys(xData).length; i++){
        year = (base + parseInt((xData[i]-1)/4)).toString(); //Data is given as quarters (1,2,3,4,5,...) so we need to convert that to years
        quarter = (parseInt(xData[i] % 4)).toString();
        if(quarter == 0){
          //x mod 4 = 0, should be fourth quarter
          quarter = 4;
        }
        if(interval ==0){
          xValues[xValueIndex++] = year+"/"+quarter;
          interval = Math.round(Object.keys(xData).length/tickCount);
        }
        interval--;
      }
	}
	else if (getXAxis() == "Month") {
    console.log("Month",xData)
    if(existingTrend){
      tickCount = 4;
    }
    var interval = 0;
		for (i = 0; i < Object.keys(xData).length; i++) {
			year = (base + parseInt((xData[i] - 1)/12)).toString(); //Data is given as months (1,2,3,4,5...) which we need to convert into years
			month = (parseInt(xData[i]) % 12).toString();
      if(month == 0){
        //aka 12 mod 12 or 24 mod 12, it's December
        month = 12;
      }
      if(interval ==0){
        xValues[xValueIndex++] = year+"/"+month;
        interval = Math.round(Object.keys(xData).length/tickCount);
      }
      interval --;

		}
	}
  //console.log("xValues = " + xValues);
	for(i = 0; i < Object.keys(xData).length; i++) {
		table_data_values.push({"x":xData[i], "y": yData[i]});
	}

  //Push into our JSON our data for the scales (plotting of the line)
	var table_data = { "name" : "table" };
	table_data["values"]= table_data_values;
	jsonData.push(table_data); //json["data"][0]["values"]

  var xValue_data = {"xValues": xValues}
  jsonData.push(xValue_data) //json["data"][1]["xValues"]

  //Push into our JSON the data for the ticks (values visualized)
  var table2_data = {"name": "table2"};
  var table2_data_values = []
  for(i = 0; i < xValues.length; i++){
    table2_data_values.push({"xValues": xValues[i]})
  }
  table2_data["values"] = table2_data_values;
  jsonData.push(table2_data)
  //console.log(JSON.stringify(jsonData));

	return jsonData;
}

function createreal_estateScaleJSON( xmin, xmax, ymin, ymax ){
	  //end data-----
	  var scales = [
	    {
        //This is used for the x axis marks
	      "name": "x",
	      //"type": "ordinal",
	      "range": "width",
	      "domain": {"data": "table", "field": "x"},
	      "domainMin": xmin,
	      "domainMax": xmax,
	      "zero": false
	    },
	    {
	      "name": "y",
	      "range": "height",
	      //"nice": true,
	      "nice": false,
	      "domain": {"data": "table", "field": "y"},
	      "domainMin": ymin,
	      "domainMax": ymax,
	       "zero": false
	    },
	    {
	      "name": "color", "type": "ordinal", "range": "category10"
	    },
      {
      //This is used for the x axis tick labels
      "name": "xValues",
      "type": "ordinal",
      "range": "width",
      "domain": {"data": "table2", "field": "xValues"}
      }
    ];

	  return scales
	}

// input is data["outputCharts"][index];
function getMinMaxValues( data )
{
  //console.log("min_max", data)
  var minmax = {};
  minmax["xmin"] = Math.min.apply(null, data["xData"])
  minmax["xmax"] = Math.max.apply(null, data["xData"])
  minmax["ymin"] = Math.min.apply(null, data["yData"])
  minmax["ymax"] = Math.max.apply(null, data["yData"])
  return minmax;
}



function createMarksJSON()
{
  var marks =  [
      {
        "type": "line",
        "from": {"data": "table"},
        "properties": {
          "enter": {
            "x": {"scale": "x", "field": "x"},
            "y": {"scale": "y", "field": "y"},
            "stroke": {"scale": "color", "value": "steelblue"},
            "strokeWidth": {"value": 3},
          }
        }
      }
    ];
  return marks
}

function createAxesJSON( data, xUnit, yUnit, existingTrend )
{
  var xTitle = data["xType"] + "  (Count=" + data["count"] + ")"
  //var yTitle = data["yType"] + "  (" + yUnit + ")"
  var yTitle = data["yType"]
  var tickCount = 0
  if (existingTrend)
  {
    tickCount = 3
  }
  var axes =  [
    {"type": "x", "scale": "x", "title": xTitle, "titleOffset": 45, "ticks": tickCount,"properties": {"title": {
        "fontSize": {"value": 11}}}},
    {"type": "y", "scale": "y", "title": yTitle, "titleOffset": 55, "ticks": tickCount,"properties": {"title": {
        "fontSize": {"value": 11}}}}
  ];
  var Xparameter = getDistanceMethod();  // ignore x means DTW.
  if(Xparameter == "dtw"){
	  axes[0] = {"type": "x", "scale": "x", "title": xTitle, "titleOffset": 45, "values":[],"properties": {"title": {
	        "fontSize": {"value": 11}}}}
  }


  return axes
}


function createAxesExistingTrendBar( data, xUnit, yUnit, existingTrend )
{
  var xTitle = data["xType"] + "  (Count=" + data["count"] + ")"
  //var yTitle = data["yType"] + "  (" + yUnit + ")"
  var yTitle = data["yType"]
  var tickCount = 3
  var axes =  [
    {"type": "y", "scale": "y", "title": yTitle, "titleOffset": 55, "ticks": tickCount,"properties": {"title": {
        "fontSize": {"value": 11}}}}
  ];
  if(!histogram){
	  var axes =[
	  	{"type": "x", "scale": "x", "title": xTitle, "titleOffset": 45, "ticks": tickCount,"properties": {"title": {
	      "fontSize": {"value": 11}}}},
	      {"type": "y", "scale": "y", "title": yTitle, "titleOffset": 55, "ticks": tickCount,"properties": {"title": {
	          "fontSize": {"value": 11}}}}
	    ];
	  }
  var Xparameter = getDistanceMethod();  // ignore x means DTW.
  if(Xparameter == "dtw"){
	  axes[0] = {"type": "x", "scale": "x", "title": xTitle, "titleOffset": 45, "values":[],"properties": {"title": {
	        "fontSize": {"value": 11}}}}
  }


  return axes
}

function createScaleBarChartJSON( xmin, xmax, ymin, ymax ){

	  //end data-----
	  var scales = [
	    {
	      "name": "x",
	      //"type": "ordinal",
	      "type": "ordinal",
	      "range": "width",
	      "domain": {"data": "table", "field": "x"},
	      "domainMin": xmin,
	      "domainMax": xmax,
	      "zero": false
	    },
	    {
	      "name": "y",
          "type":"linear",
	      "range": "height",
	      "nice": true,
	      "domain": {"data": "table", "field": "y"},
	      "domainMin": ymin,
	      "domainMax": ymax,
	       "zero": true
	    },
	    {
	      "name": "color", "type": "ordinal", "range": "category10"
	    }];
	  return scales
	}

function createScaleJSON( xmin, xmax, ymin, ymax ){
  //end data-----
  var scales = [
    {
      "name": "x",
      //"type": "ordinal",
      "range": "width",
      "domain": {"data": "table", "field": "x"},
      "domainMin": xmin,
      "domainMax": xmax,
      "zero": false
    },
    {
      "name": "y",
      "range": "height",
      "nice": false,
      "domain": {"data": "table", "field": "y"},
      "domainMin": ymin,
      "domainMax": ymax,
       "zero": false
    },
    {
      "name": "color", "type": "ordinal", "range": "category10"
    }];

  return scales
}

function createScaleScatterJSON( xmin, xmax, ymin, ymax ){
	console.log("createScaleScatter")
	  //end data-----
	  var domain = getXYmaxmin();
	  var scales = [
	    {
	      "name": "x",
	      //"type": "linear",
	      "range":"width",
	      "domain": {"data": "table", "field": "x"},
	      "domainMin": domain[0],
	      "domainMax": domain[1],
	      "zero": true,
	      "nice":true
	    },
	    {
	      "name": "y",
	      "type": "linear",
	      "range":"height",
	      "nice": true,
	      "domain": {"data": "table", "field": "y"},
	      "domainMin": domain[2],
	      "domainMax": domain[3]
	    },
	    {
	      "name": "color", "type": "ordinal", "range": "category10"
	    }];

	  return scales;
}

function createDataJSON( data ){
  //data----
  var jsonData = [];
  var table_data_values = [];
  var xData = data["xData"];
  var yData = data["yData"];

  for(i = 0; i < Object.keys(xData).length; i++) {
    table_data_values.push({"x":xData[i], "y": yData[i]});
  }

  var table_data = { "name" : "table" };
  table_data["values"]= table_data_values;
  jsonData.push(table_data);
  return jsonData;
}


// takes in full data
function generateExistingTrends( data ){
	console.log("data",data)
  var outputCharts = data["outputCharts"];
  var count = Object.keys(data).length
  var xUnit = data["xUnit"];
  var yUnit = data["yUnit"];
  for (var i = 0; i < 3; i++) {
    $("#existing-trends").append("<div class=\"graph\" ondragstart = \"drag(this,event)\" draggable = \"true\" id=\"existing-trend-" + i + "\"></div>")
    addExistingTrendGraph( outputCharts[i], i, xUnit, yUnit)
  }
  existingTrends[ExTrendindex] = data;
  //barConfig();
}

function addExistingTrendGraph( data, i, xUnit, yUnit ){
  var vegaInput = createJSON(data, 120, 60, xUnit, yUnit, true) //last one is for existingTrend
  if(histogram){
	  console.log("addexisting bar")
	 vegaInput = createExistingTrendsBarGraph(data, 120, 60, xUnit, yUnit)
  }
  if(scatter){
	  console.log("addexisting scatter")
	  vegaInput = createExistingTrendsScatterGraph(data, 120, 60, xUnit, yUnit)
  }
  vg.parse.spec(vegaInput, function(chart) {
    chart({
      el: "#existing-trend-"+i,
      hover: false
    }).update();
  });
}

function generateScatterTrends(data){
	 var outputCharts = data["outputCharts"];
	  var count = Object.keys(data).length
	  var xUnit = data["xUnit"];
	  var yUnit = data["yUnit"];
	  for (var i = 0; i < 3; i++) {
	    $("#existing-trends").append("<div class=\"graph\" ondragstart = \"drag(this,event)\" draggable = \"true\" id=\"existing-trend-" + i + "\"></div>")
	    addExistingTrendGraph( outputCharts[i], i, xUnit, yUnit)
	  }
	  existingTrends[ExTrendindex] = data;
}

// full data (Callback function of getQuery)
//Function is called after server gets data
function processBackEndData(data, xAxisType, yAxisType) {
  outputData = data;
  //generateSuggestTrends(data)
  var xUnit = data["xUnit"];
  var yUnit = data["yUnit"];
  var outputCharts = data["outputCharts"];
  var charts_per_row = 2;
  var current_row;
  //alert(JSON.stringify(outputCharts))
  $("#views_table").empty();
  for(index = 0; index < Object.keys(outputCharts).length; index++){
    if(index % charts_per_row == 0){ //create a new table row element per every 2
      current_row = $('<tr>')
      $("#views_table").append(current_row)
    }
    //$("#views").append("<div ondragstart = \"drag(this,event)\" draggable = \"true\" class='mod' id='view-" + index + "'></div>")
    var current_td = $('<td>') //new column element
    //current_td.attr('id', "table_view"+index)
    current_row.append(current_td)
    current_td.append("<div class=\"graph\" ondragstart = \"drag(this,event)\" draggable = \"true\" class='mod' id='table_view" + index + "'></div>")
    if(index % charts_per_row == 1 && $("#blankChart1").is(':visible')){     //hightlight for second column
      current_td.addClass("secondDrawColumn")
    }
    addGraph(outputCharts[index], index, xUnit, yUnit, xAxisType, yAxisType);
  }
  itemid = "existing-trend-0" //allows drawTrend() or drawBarsAfterDragDrop() to draw on the main graph with the correct new existing trend
  var option = document.getElementById("sel").value;
//  alert(xAxisType);
  if(option == "Auto"){
      if(xAxisType == 'C' && yAxisType == 'Q'){
          setupBarView();
          drawBarsAfterDragDrop();
      }
      else if(xAxisType =='O' && yAxisType == 'Q'){
          setupLineView();
          drawTrend();
      }
      else if(xAxisType =='Q' && yAxisType == 'Q'){
          //should be createScatterPlot, not implemented hyet
    	
          setupScatterView();
          //drawScatterTrend()
      }
      else{ //unknown data types selected
          setupLineView();
          drawTrend();
      }
  }
  //Scroll to graphs automatically! (After stuff loads)
  $('html, body').animate({
    scrollTop: $("#views").offset().top
  }, 1600);
/*
  if(xAxisType=="Q" && yAxisType=="Q"){
      setupScatterView();
  }
  else if(xAxisType=="O" && yAxisType=="Q"){
      setupLineView();
  }
  else if(xAxisType=="C" && yAxisType =="Q"){
      setupBarView();
  }
  else{
      setupLineView();
  }
  */
}

//Adds a graph to the div specified by view count
function addGraph(data, index, xUnit, yUnit, xAxisType, yAxisType){
    var vegaInput;
    var windowWidth = $(window).width()
    var graphHeight = Math.round(windowWidth/10); //1450/10 = 145
    var graphWidth = graphHeight*2;
    if(windowWidth >1240 && windowWidth <= 1452){
    graphHeight = Math.round(windowWidth/12)
    graphWidth = graphHeight*2;
    }
    if(windowWidth <= 1240){
    graphHeight = Math.round(windowWidth/15)
    graphWidth = graphHeight*2;
    }

    var option = document.getElementById("sel").value;
    if(option == "Bar Chart"){
        vegaInput = createBarGraph(data, graphWidth, graphHeight, xUnit, yUnit);
	}
	else if(option == "Scatter"){
        //should be createScatterPlot, not implemented yet
        vegaInput = createScatterGraph(data, graphWidth, graphHeight, xUnit, yUnit);
	}
	else if(option == "Line Chart"){
        vegaInput = createJSON(data, graphWidth, graphHeight, xUnit, yUnit);
	}
    else if(option == "Auto"){ //Using our recommender system
    	if(xAxisType == 'C' && yAxisType == 'Q'){
    		vegaInput = createBarGraph(data, graphWidth, graphHeight, xUnit, yUnit);
    	}
    	else if(xAxisType =='O' && yAxisType == 'Q'){
    		vegaInput = createJSON(data, graphWidth, graphHeight, xUnit, yUnit);
    	}
        else if(xAxisType =='Q' && yAxisType == 'Q'){
            //should be createScatterPlot, not implemented yet
            vegaInput = createScatterGraph(data, graphWidth, graphHeight, xUnit, yUnit);
        }
        else{ //unknown data types selected
            vegaInput = createJSON(data, graphWidth, graphHeight, xUnit, yUnit);
        }
    }
  //spec1 = plotLineGraph(data);
  vg.parse.spec(vegaInput, function(chart) {
    chart({
      el: "#table_view"+index,
      hover: false
    }).update();
  });
}

function createBarGraph(backend_data, width, height, xUnit, yUnit){

	var json = {};
	  var minmax = getMinMaxValues( backend_data )
	  //json["padding"]= {"top": 10, "left": 40, "bottom": 20, "right": 10};
	  json["width"] = width;
	  json["height"] = height;
	  json["data"] = createDataJSON( backend_data );
	  json["scales"] = createScaleBarChartJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
	  json["axes"]=createAxesExistingTrendBar(backend_data,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"], false);
	  //json["axes"] = createAxesJSON( backend_data, xUnit, yUnit );
	  //json["marks"] = createBarChartMarksJSON(minmax["xmin"]);
	  json["marks"] = createBarChartMarksJSON();
      json["signals"] = [
        {
          "name": "tooltip",
          "init": {},
          "streams": [
            {"type": "rect:mouseover", "expr": "datum"},
            {"type": "rect:mouseout", "expr": "{}"}
          ]
        }
      ]
	  return json;
}

function createExistingTrendsScatterGraph(backend_data, width, height, xUnit, yUnit){
	var json = {};
	  var minmax = getMinMaxValues( backend_data )
	  //json["padding"]= {"top": 10, "left": 40, "bottom": 20, "right": 10};
	  json["width"] = width;
	  json["height"] = height;
	  json["data"] = createDataJSON( backend_data );
	  json["scales"] = createScaleScatterJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
	  json["axes"]=createAxesExistingTrendBar(backend_data,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"], false);
	  json["marks"] = createScatterChartMarksJSON();
   
	  return json;
}


function createExistingTrendsBarGraph(backend_data, width, height, xUnit, yUnit){

	var json = {};
	  var minmax = getMinMaxValues( backend_data )
	  //json["padding"]= {"top": 10, "left": 40, "bottom": 20, "right": 10};
	  json["width"] = width;
	  json["height"] = height;
	  json["data"] = createDataJSON( backend_data );
	  json["scales"] = createScaleBarChartJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
      //json["scales"] = []
	  json["axes"]=createAxesExistingTrendBar(backend_data,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"], true);
	  //json["axes"] = createAxesJSON( backend_data, xUnit, yUnit );
	  //json["marks"] = createBarChartMarksJSON(minmax["xmin"]);
	  json["marks"] = createBarChartMarksJSON();
      //https://vega.github.io/vega-editor/?mode=vega&spec=tooltip  basis for tooltips
      json["signals"] = [
        {
          "name": "tooltip",
          "init": {},
          "streams": [
            {"type": "rect:mouseover", "expr": "datum"},
            {"type": "rect:mouseout", "expr": "{}"}
          ]
        }
      ]
	  return json;
}

function createScatterGraph(backend_data, width, height, xUnit, yUnit){
	var json = {};
	  var minmax = getMinMaxValues( backend_data )
	  json["width"] = width;
	  json["height"] = height;
	  json["data"] = createDataJSON( backend_data );
	  json["scales"] = createScaleScatterJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
	  json["axes"]=createAxesJSON(backend_data,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"]);
	  json["marks"] = createScatterChartMarksJSON();
	  return json;
}

function createScatterChartMarksJSON()
{
  var marks =  [
      {
        "type": "symbol",
        "from": {"data": "table"},
        "properties": {
          "enter": {
            "x": {"scale": "x", "field": "x"},
            "width": {"scale": "x", "offset": -1},
            "y": {"scale": "y", "field": "y"},
            "y2":{"scale":"y","value":0},
            "stroke":{"value":"pink"},
            "fillOpacity":{"value":0.5}
          },
          "update": {
        	  "fill":{"value":"steelblue"},
        	  "size":{"value":50}
    	  },
          "hover":{"fill":{"value":"steelblue"},"size":{"value":60}}
        }
      }
    ];
  return marks
}


function createBarChartMarksJSON()
{
  var marks =  [
    {
        "type": "rect",
        "from": {"data": "table"},
        "properties": {
          "enter": {
            "x": {"scale": "x", "field": "x"},
            "width": {"scale": "x", "band": true, "offset": -1},
            "y": {"scale": "y", "field": "y"},
            "y2":{"scale":"y","value":0}
          },
          "update": {
            "fill": [
             { "test": "datum._id == tooltip._id",
               "value": "red"
             },
             {"value": "steelblue"}
            ]
          }
        }
    },
    {
      "type": "text",
      "properties": {
        "enter": {
          "align": {"value": "center"},
          "fill": {"value": "#333"}
        },
        "update": {
            //x, y and dx help align the text right above the bar hovered over
          "x": {"scale": "x", "signal": "tooltip.x"},
          "dx": {"scale": "x", "band": true, "mult": 0.5},
          "y": {"scale": "y", "signal": "tooltip.y", "offset": -5},
          "text": {"signal": "tooltip.x"},
          "fillOpacity": [
            { "test": "!tooltip._id",
              "value": 0
            },
            {"value": 1}
          ]
        }
      }
    }
    ];
  return marks
}



function createBlankNoScale(xtitle, ytitle){
  var json = {};
  json["width"] = drawGraphWidth;
  json["height"] = drawGraphHeight;

  var data = [{
      "name": "table",
      "values": [
        {
          "x": 3,
          "y": 4
        },
        {
          "x":1,
          "y":0
        }
      ]
    }];
  json["data"] = data;

  json["scales"] = [
    {
      "name": "x",
      "nice": true,
      "range": "width",
      "domainMin": 0.0,
      "domainMax": 0.0,
      "zero": false
    },
    {
      "name": "y",
      "range": "height",
      "nice": true,
      "domainMin": 0.0,
      "domainMax": 0.0,
      "zero": false
    }
  ];

  json["axes"] =  [
    {"type": "x", "scale": "x", "title": xtitle, "titleOffset": 45,"properties": {"title": {
        "fontSize": {"value": 11}}}},
    {"type": "y", "scale": "y", "title": ytitle, "titleOffset": 55,"properties": {"title": {
        "fontSize": {"value": 11}}}}
  ];

  json["marks"] =  [
    {
      "type": "rect",
      "from": {"data": "table"},
      "properties": {
        "enter": {
          "x": {"scale": "x", "field": "x"},
          "width": {"scale": "x", "offset": -1},
          "y": {"scale": "y", "field": "y"},
          "y2": {"scale": "y", "value": 0}
        },
        "update": {
          "fill": {"value": "steelblue"}
        },
        "hover": {
          "fill": {"value": "red"}
        }
      }
    }
  ];
  return json;
}

//chartType is optional param
function createBlank(xmin,xmax,ymin,ymax,chartData, chartType){
    //scales for real estate
    var scales = [
      {
      //This is used for the x axis marks
        "name": "x",
        //"type": "ordinal",
        "range": "width",
        "domain": {"data": "table", "field": "x"},
        "domainMin": {"signal": "xMin"},
        "domainMax": {"signal": "xMax"},
        "zero": false
      },
      {
        "name": "y",
        "range": "height",
        //"nice": true,
        "nice": false,
        "domain": {"data": "table", "field": "y"},
        "domainMin": {"signal": "yMin"},
        "domainMax": {"signal": "yMax"},
         "zero": false
      },
      {
        "name": "color", "type": "ordinal", "range": "category10"
      },
    {
    //This is used for the x axis tick labels
    "name": "xValues",
    "type": "ordinal",
    "range": "width",
    "domain": {"data": "table2", "field": "xValues"},
    "domainMin": {"signal": "xMin"},
    "domainMax": {"signal": "xMax"} //doesnt work yet
    }
  ];

  if(chartType == "histogram"){
      scales[0]["type"] = "ordinal";
  }
  if (getDataset() == "real_estate") {
    var json = {};
    var minmax = getMinMaxValues(chartData);
    json["width"] = drawGraphWidth;
    json["height"] = drawGraphHeight;
    json["data"] = createreal_estateDataJSON( chartData, existingTrends[ExTrendindex] );
    //json["scales"] = createreal_estateScaleJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );

  json["scales"] = scales;
    json["axes"] = createreal_estateAxesJSON( chartData, drawGraphWidth, drawGraphHeight,existingTrends[ExTrendindex], json );
    //json["marks"] = createMarksJSON();
    //json["padding"]= "auto";
    json["signals"] =  [
    {
      "name": "point",
      "init": 0,
      "streams": [{
        "type": "mousedown",
        "expr": "{x: eventX(), y: eventY()}"
      }]
    },
    {
      "name": "delta",
      "init": 0,
      "streams": [{
        "type": "[mousedown, window:mouseup] > window:mousemove",
        "expr": "{x: point.x - eventX(), y: eventY() - point.y}"
      }]
    },
    {
      "name": "xAnchor",
      "init": 0,
      "streams": [{
        "type": "mousemove",
        "expr": "eventX()",
        "scale": {"name":"x", "invert":true}
      }]
    },
    {
      "name": "yAnchor",
      "init": 0,
      "streams": [{
        "type": "mousemove",
        "expr": "eventY()",
        "scale": {"name":"y", "invert":true}
      }]
    },
    {
      "name": "zoom",
      "init": 1.0,
      "verbose": true,
      "streams": [
        {"type": "wheel", "expr": "pow(1.001, event.deltaY*pow(16, event.deltaMode))"}
      ]
    },
    {
      "name": "xs",
      "streams": [{
        "type": "mousedown, mouseup, wheel",
        "expr": "{min: xMin, max: xMax}"}
      ]
    },
    {
      "name": "ys",
      "streams": [{
        "type": "mousedown, mouseup, wheel",
        "expr": "{min: yMin, max: yMax}"
      }]
    },
    {
      "name": "xMin",
      "init": xmin,
      "streams": [
        {"type": "delta", "expr": "xs.min + (xs.max-xs.min)*delta.x/width"},
        {"type": "zoom", "expr": "(xs.min-xAnchor)*zoom + xAnchor"}
      ]
    },
    {
      "name": "xMax",
      "init": xmax,
      "streams": [
        {"type": "delta", "expr": "xs.max + (xs.max-xs.min)*delta.x/width"},
        {"type": "zoom", "expr": "(xs.max-xAnchor)*zoom + xAnchor"}
      ]
    },
    {
      "name": "yMin",
      "init": ymin,
      "streams": [
        {"type": "delta", "expr": "ys.min + (ys.max-ys.min)*delta.y/height"},
        {"type": "zoom", "expr": "(ys.min-yAnchor)*zoom + yAnchor"}
      ]
    },
    {
      "name": "yMax",
      "init": ymax,
      "streams": [
        {"type": "delta", "expr": "ys.max + (ys.max-ys.min)*delta.y/height"},
        {"type": "zoom", "expr": "(ys.max-yAnchor)*zoom + yAnchor"}
      ]
    },
    {
      "name": "pointSize",
      "init": 20,
      "streams": [{
        "type": "xMin",
        "expr": "clamp(60/(xMax-xMin), 1, 100)"
      }]
    }
];
    return json;
  }
  else{
    var json = {};
    json["width"] = drawGraphWidth;
    json["height"] = drawGraphHeight;
    var data = [    {
        "name": "table",
        "values":  [
                             {
                                 "x": 3,
                                 "y": 4
                               },
                               {
                                "x":1,
                                "y":0
                               }
                             ]
      }];
    json["data"] = data;
    if(chartType == "histogram"){
        json["data"] = createDataJSON(chartData);
    }
    var scales =  [
      {
        "name": "x",
        "nice": true,
        "range": "width",
        "domainMin": {"signal": "xMin"},
        "domainMax": {"signal": "xMax"},
        "zero": false
      },
      {
        "name": "y",
        "range": "height",
        "nice": true,
        "domainMin": {"signal": "yMin"},
        "domainMax": {"signal": "yMax"},
        "zero": false
      }
    ];

    if(chartType == "histogram"){
        //scales = createScaleBarChartJSON(xmin,xmax,ymin,ymax);
        //This below emulates what createScaleBarChartJSON does, while keeping signal compatibility
        scales[0]["type"] = "ordinal";
        scales[0]["domain"] = {"data": "table", "field": "x"};
        scales[1]["domain"] ={"data": "table", "field": "y"};
        //alert(JSON.stringify(scales))
    }

    json["scales"] = scales;

    //json["axes"] =
     var axes = createAxesJSON(chartData,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"]);
     if(chartType == "histogram"){
         axes[0]["properties"]["labels"] = {
            "fill": {"value": "steelblue"},
           "angle": {"value": 30},
           "dy": {"value": 5}
           //,"text":{"value": " "},

       };
     }
     //alert(JSON.stringify(axes));
     json["axes"] = axes;
    /*
  [
      {"type": "x", "scale": "x"},
      {"type": "y", "scale": "y"}
    ];
  */

/*
    Unneeded for now. Causes issue with making canvas.marks size too big
    json["marks"] =  [
      {
        "type": "rect",
        "from": {"data": "table"},
        "properties": {
          "enter": {
            "x": {"scale": "x", "field": "x"},
            "width": {"scale": "x", "offset": -1},
            "y": {"scale": "y", "field": "y"},
            "y2": {"scale": "y", "value": 0}
          },
          "update": {
            "fill": {"value": "steelblue"}
          },
          "hover": {
            "fill": {"value": "red"}
          }
        }
      }
    ];
*/
    json["signals"] =  [
    {
      "name": "point",
      "init": 0,
      "streams": [{
        "type": "mousedown",
        "expr": "{x: eventX(), y: eventY()}"
      }]
    },
    {
      "name": "delta",
      "init": 0,
      "streams": [{
        "type": "[mousedown, window:mouseup] > window:mousemove",
        "expr": "{x: point.x - eventX(), y: eventY() - point.y}"
      }]
    },
    {
      "name": "xAnchor",
      "init": 0,
      "streams": [{
        "type": "mousemove",
        "expr": "eventX()",
        "scale": {"name":"x", "invert":true}
      }]
    },
    {
      "name": "yAnchor",
      "init": 0,
      "streams": [{
        "type": "mousemove",
        "expr": "eventY()",
        "scale": {"name":"y", "invert":true}
      }]
    },
    {
      "name": "zoom",
      "init": 1.0,
      "verbose" : true,
      "streams": [
        {"type": "wheel", "expr": "pow(1.001, event.deltaY*pow(16, event.deltaMode))"}
      ]
    },
    {
      "name": "xs",
      "streams": [{
        "type": "mousedown, mouseup, wheel",
        "expr": "{min: xMin, max: xMax}"}
      ]
    },
    {
      "name": "ys",
      "streams": [{
        "type": "mousedown, mouseup, wheel",
        "expr": "{min: yMin, max: yMax}"
      }]
    },
    {
      "name": "xMin",
      "init": xmin,
      "streams": [
        {"type": "delta", "expr": "xs.min + (xs.max-xs.min)*delta.x/width"},
        {"type": "zoom", "expr": "(xs.min-xAnchor)*zoom + xAnchor"}
      ]
    },
    {
      "name": "xMax",
      "init": xmax,
      "streams": [
        {"type": "delta", "expr": "xs.max + (xs.max-xs.min)*delta.x/width"},
        {"type": "zoom", "expr": "(xs.max-xAnchor)*zoom + xAnchor"}
      ]
    },
    {
      "name": "yMin",
      "init": ymin,
      "streams": [
        {"type": "delta", "expr": "ys.min + (ys.max-ys.min)*delta.y/height"},
        {"type": "zoom", "expr": "(ys.min-yAnchor)*zoom + yAnchor"}
      ]
    },
    {
      "name": "yMax",
      "init": ymax,
      "streams": [
        {"type": "delta", "expr": "ys.max + (ys.max-ys.min)*delta.y/height"},
        {"type": "zoom", "expr": "(ys.max-yAnchor)*zoom + yAnchor"}
      ]
    },
    {
      "name": "pointSize",
      "init": 20,
      "streams": [{
        "type": "xMin",
        "expr": "clamp(60/(xMax-xMin), 1, 100)"
      }]
    }
];
    return json;
  }
}



//Creates the Blank Chart displayed center of front end, to help users visualize
//what they are inputting
function blankChart(xtitle, ytitle, suffix){
  var spec1 = createBlankNoScale(xtitle, ytitle);
  vg.parse.spec(spec1, function(chart) {
  self.view1 = chart({
    el: "#blankChart"+suffix,
    hover: false
  }).update();
});
}

/*A function that returns DONE only when update() is complete
 note returning DONE is different than returning (function still returns as usual).
 (but the blankChartAsync.done() depends on whether this returned DONE or not)
  */
function blankChartAsync(ytitle, suffix){
    var dfrd1 = $.Deferred();
    var spec1 = createBlankNoScale(" ", ytitle);
    //This function below is ran asyncronous. This function will wait until this is done to return.
    vg.parse.spec(spec1, function(chart) {
    self.view1 = chart({
      el: "#blankChart"+suffix,
      hover: false
    }).update();
    //console.log("Updated blank chart")
    dfrd1.resolve();

  });
  return $.when(dfrd1).done(function(){
     // console.log("Return blank chart")
   }).promise();
}

//optional chartType param
function changeScaleBlankChart(xmin,xmax,ymin,ymax,chartData, suffix, chartType){
    var spec1 = createBlank(xmin,xmax,ymin,ymax,chartData, chartType);
    yMin = ymin;
    yMax = ymax;
    /*if(!clickmodify){
	    var x_title = chartData["xType"].split(": ")[1];
	    spec1["axes"][0]["title"] = x_title;
    }
    else
    	spec1["axes"][0]["title"] = "";
    */
    spec1["axes"][0]["title"] = "";
    vg.parse.spec(spec1, function(chart) {
    self.view1 = chart({
      el: "#blankChart"+suffix,
      hover: false
    }).update();
  });
}
