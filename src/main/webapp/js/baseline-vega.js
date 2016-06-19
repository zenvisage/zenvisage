
function getXAxis()
{
  return $("#top-x").find("option:selected").text();
}

function getYAxis()
{
  return $("#top-y").find("option:selected").text();
}

function getDataset()
{
  return "real_estate"
}

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
	      "domain": {"data": "table", "field": "data.x"},
	      "domainMin": xmin,
	      "domainMax": xmax,
	      "zero": false
	    },
	    {
	      "name": "y",
	      "range": "height",
	      //"nice": true,
	      "nice": false,
	      "domain": {"data": "table", "field": "data.y"},
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
      "domain": {"data": "table2", "field": "data.xValues"}
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
            "x": {"scale": "x", "field": "data.x"},
            "y": {"scale": "y", "field": "data.y"},
            "stroke": {"scale": "color"},
            "strokeWidth": {"value": 3}
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

function createScaleBarChartJSON( xmin, xmax, ymin, ymax ){
	  //end data-----
	  var scales = [
	    {
	      "name": "x",
	      "type": "ordinal",
	      "range": "width",
	      "domain": {"data": "table", "field": "data.x"},
	      "domainMin": xmin,
	      "domainMax": xmax,
	      "zero": false
	    },
	    {
	      "name": "y",
	      "range": "height",
	      "nice": true,
	      "domain": {"data": "table", "field": "data.y"},
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
      "domain": {"data": "table", "field": "data.x"},
      "domainMin": xmin,
      "domainMax": xmax,
      "zero": false
    },
    {
      "name": "y",
      "range": "height",
      "nice": false,
      "domain": {"data": "table", "field": "data.y"},
      "domainMin": ymin,
      "domainMax": ymax,
       "zero": false
    },
    {
      "name": "color", "type": "ordinal", "range": "category10"
    }];

  return scales
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
  var outputCharts = data["outputCharts"];
  var count = Object.keys(data).length
  var xUnit = data["xUnit"];
  var yUnit = data["yUnit"];
  for (var i = 0; i < 3; i++) {
    $("#existing-trends").append("<div class=\"graph\" ondragstart = \"drag(this,event)\" draggable = \"true\" id=\"existing-trend-" + i + "\"></div>")
    addExistingTrendGraph( outputCharts[i], i, xUnit, yUnit)
  }
  existingTrends[ExTrendindex] = data;
  barConfig();
}

function addExistingTrendGraph( data, i, xUnit, yUnit ){
  var vegaInput = createJSON(data, 120, 60, xUnit, yUnit, true) //last one is for existingTrend
  vg.parse.spec(vegaInput, function(chart) {
    chart({
      el: "#existing-trend-"+i,
      hover: false
    }).update();
  });
}

function setTotalPages(totalPages){
  $("#total-pages").empty()
  var realPages = totalPages + 1
  $("#total-pages").append("<text><h5>" + realPages.toString() + "</h5></text>")
}

// full data
function processBackEndData(data) {
  setTotalPages(data.totalPage)
  $("#secondary-table").css("visibility","visible")
  outputData = data;
  //generateSuggestTrends(data)
  var xUnit = data["xUnit"];
  var yUnit = data["yUnit"];
  var outputCharts = data["outputCharts"];
  var charts_per_row = 2;
  var current_row;
  $("#views_table").empty()
  for(index = 0; index < Object.keys(outputCharts).length; index++){
    if(index % charts_per_row == 0){ //create a new table row element per every 2
      current_row = $('<tr>')
      $("#views_table").append(current_row)
    }
    var current_td = $('<td>') //new column element
    current_row.append(current_td)
    current_td.append("<div class=\"graph\" class='mod' id='table_view" + index + "'></div>")
    if(index % charts_per_row == 1 && $("#mainChart1").is(':visible')){     //hightlight for second column
      current_td.addClass("secondDrawColumn")
    }
    addGraph(outputCharts[index], index, xUnit, yUnit);
  }
  var pages = $('<tr align="right">')
  pages.append("<div class=\"btn-group\" role=\"group\" aria-label=\"First group\"><button type=\"button\" class=\"btn btn-default\" onclick=\"previousPage()\">Prev</button><button type=\"button\" class=\"btn btn-default\" onclick=\"nextPage()\">Next</button></div>")
  $("#views_table").append(pages)

  //Scroll to graphs automatically! (After stuff loads)
  $('html, body').animate({
    scrollTop: $("#views").offset().top
  }, 1600);
}

//Adds a graph to the div specified by view count
function addGraph(data, index, xUnit, yUnit){
	var vegaInput;
  var windowWidth = $(window).width()

  var graphHeight = Math.round(windowWidth/6); //1450/10 = 145
  var graphWidth = graphHeight*2;

  if(windowWidth >1240 && windowWidth <= 1452){
    graphHeight = Math.round(windowWidth/8)
    graphWidth = graphHeight*2;
  }
  if(windowWidth <= 1240){
    graphHeight = Math.round(windowWidth/10)
    graphWidth = graphHeight*2;
  }
	vegaInput = createJSON(data, graphWidth, graphHeight, xUnit, yUnit);

  //spec1 = plotLineGraph(data);
  vg.parse.spec(vegaInput, function(chart) {
    chart({
      el: "#table_view"+index,
      hover: false
    }).update();
  });
}

function createBlankNoScale(xtitle, ytitle){
  var json = {};
  //console.log(xmin);
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
          "x": {"scale": "x", "field": ""},
          "width": {"scale": "x", "offset": -1},
          "y": {"scale": "y", "field": ""},
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

function createBlank(xmin,xmax,ymin,ymax,chartData){
  if (getDataset() == "real_estate") {
    var json = {};
    var minmax = getMinMaxValues(chartData);
    json["width"] = drawGraphWidth;
    json["height"] = drawGraphHeight;
    json["data"] = createreal_estateDataJSON( chartData, existingTrends[ExTrendindex] );
    json["scales"] = createreal_estateScaleJSON( minmax["xmin"], minmax["xmax"], minmax["ymin"], minmax["ymax"] );
    json["axes"] = createreal_estateAxesJSON( chartData, drawGraphWidth, drawGraphHeight,existingTrends[ExTrendindex], json );
    //json["marks"] = createMarksJSON();
    //json["padding"]= "auto";
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

    json["scales"] = [
      {
        "name": "x",
        "nice": true,
        "range": "width",
        "domainMin": xmin,
        "domainMax": xmax,
        "zero": false
      },
      {
        "name": "y",
        "range": "height",
        "nice": true,
        "domainMin": ymin,
        "domainMax": ymax,
        "zero": false
      }
    ];

    json["axes"] =
     createAxesJSON(chartData,existingTrends[ExTrendindex]["xUnit"], existingTrends[ExTrendindex]["yUnit"]);
    json["marks"] =  [
      {
        "type": "rect",
        "from": {"data": "table"},
        "properties": {
          "enter": {
            "x": {"scale": "x", "field": ""},
            "width": {"scale": "x", "offset": -1},
            "y": {"scale": "y", "field": ""},
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
}
