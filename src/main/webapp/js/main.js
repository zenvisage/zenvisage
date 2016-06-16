// contains the current form data, only global for now to save redundant calls to backend
var currentFormData;

var existingTrends = [];
var outputData;
var yMax;
var yMin;
var unit = 1;

var ExTrendindex = 0;

function transformX(xAxis) {
	if (xAxis == "Year") {
		for (i = 0; i < xValue.length; i++) {
			xValue[i] = xValue[i] - 2004;
		}
	}
	else if (xAxis == "Quarter") {
		for (i = 0; i < xOperator.length; i++) {
			xValue[i] = (xValue[i] - 2004)*4;
		}
	}
	else if (xAxis == "Month") {
		for (i = 0; i < xOperator.length; i++) {
			xValue[i] = (xValue[i] - 2004)*12;
		}
	}
}

function BaselineQuery(xAxis, yAxis, y2Axis, zAxis, aggrFunc, predicateOperator, predicateColumn, predicateValue, pageNum) {
	//transformX(xAxis);
	this.xAxis = xAxis;
	this.yAxis = [yAxis];
	if (y2Axis != "") this.yAxis.push(y2Axis);
	this.zAxis = zAxis;
	this.aggrFunc = aggrFunc;
	this.predicateOperator = predicateOperator;
	this.predicateColumn = predicateColumn;
	this.predicateValue = predicateValue;
	this.pageNum = pageNum;
	this.xOperator = xOperator.slice();
	this.xValue = xValue.slice();
	this.y1Operator = yOperator.slice();
	this.y1Value = yValue.slice();
	this.y2Operator = y2Operator.slice();
	this.y2Value = y2Value.slice();
}

function Query(method,Yaxis,Xaxis,groupBy,aggrFunc,aggrVar,outlierCount,dataX,dataY,predicateOperator,predicateColumn,predicateValue) {
  this.method = method;
  this.yAxis = Yaxis;
  this.xAxis = Xaxis;
  this.groupBy = groupBy;
  this.aggrFunc = aggrFunc;
  this.aggrVar = aggrVar;
  this.outlierCount = outlierCount;
  this.dataX = dataX;
  this.dataY = dataY;
  this.yMax = yMax * unit;
  this.yMin = yMin * unit;
  this.sketchPoints = null;
  this.distanceNormalized=false;
  this.outputNormalized = false;
  this.clustering="KMeans";
  this.distance_metric="Euclidean";
  this.predicateOperator = "=";
  this.predicateColumn = predicateColumn;
  this.predicateValue = predicateValue;
}

/*
function ScatterPlotQuery(x1, x2, y1,  y2,  xAxis,  yAxis,  zAxis,
			 numOfResults) {
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.xAxis = xAxis;
		this.yAxis = yAxis;
		this.zAxis = zAxis;
		this.numOfResults = numOfResults;
}
*/
function Rectangle(x1,x2,y1,y2){
	this.x1 = x1;
	this.x2 = x2;
	this.y1 = y1;
	this.y2 = y2;
}
function ScatterPlotQuery(rectangles, xAxis,yAxis, zAxis,numOfResults, method) {
	this.rectangles = rectangles;
	this.xAxis = xAxis;
	this.yAxis = yAxis;
	this.zAxis = zAxis;
	this.numOfResults = numOfResults;
	this.method = method;
}

function showCanvas(){
  document.getElementById("buttons").style.display = "block";
  document.getElementById("tools_sketch").style.display = "block";
}

function hideCanvas(){
  //document.getElementById("buttons").style.display = "none";
  //document.getElementById("tools_sketch").style.display = "none";
}

function updateTrends(){
	updateOneTrend("");
	updateOneTrend("1");
}

function updateOneTrend(index){
	setPredicateValue();
	var query = new Query(
			'RepresentativeTrends',
		    getYAxis(),
		    getXAxis(),
		    getCategory(),
		    getAggregationMethod(),
		    getYAxis(),	//hey you called this again!
		    3,
		    [],
		    [],
  			getOperator(),
  			getPredicate(),
  			getPredicateValue()
      );
  query.sketchPoints = [new SketchPoints()];
  var xAxisType = getAxisType(xAxisSelect0.getValue()[0], "xAxisColumns");
  var yAxisType = getAxisType(yAxisSelect0.getValue()[0], "yAxisColumns");
  if(xAxisType == 'Q' && yAxisType == 'Q'){
  	setupScatterView();
  	  $("#views").empty(); //remove existing views
  	  $("#views_table").empty();
  	return;
  }
	getSuggestData(query);
	var numresults = $('#num-results input').val()
	var x = getXAxis();
	var y = getYAxis();
	var z = getCategory();

	document.getElementById(ExTrendindex).disabled = true;
	var num = 1-ExTrendindex;
	document.getElementById(num).disabled = false;
  listOfSketchPoints = []
  if(clickmodify){
		sketchPoints = new SketchPoints();
		for(var i =0; i<list.length; i++){
			//tempPoint = new Point(list[i][0],list[i][1]);
			var tempPoint = new Point(list[i][0], sketchPoints.maxY-list[i][1]);
			sketchPoints.points.push(tempPoint);
		}
    listOfSketchPoints.push(sketchPoints)

    if($("#blankChart1").is(':visible')){ //If we are using the second drawing graph
      var sketchPoints1 = new SketchPoints();
  		for(var i =0; i<list1.length; i++){
  			var tempPoint = new Point(list1[i][0], sketchPoints1.maxY-list1[i][1]);
  			sketchPoints1.points.push(tempPoint);
  		}
      listOfSketchPoints.push(sketchPoints1)
    }
	}
  query.sketchPoints = listOfSketchPoints
	onSubmit();
	if(typeof chartData != "undefined"){
		var aggrM = getAggregationMethod();
		var yaxis = getYAxis();
		var string = aggrM.concat('(');
		string = string.concat(yaxis);
		string = string.concat(')');
		console.log(string);
		chartData["yType"] = string;
        /*if($("#blankChart1").is(':visible')){ //If we are using the second drawing graph
            changeScaleBlankChart(min_X,max_X,min_Y,max_Y,chartData, "1");
        }*/
        changeScaleBlankChart(min_X,max_X,min_Y,max_Y,chartData, index);
	}
	else{
		setNoDataBlankChartAxes(getXAxis(),getYAxis(), index);
		// var aggrM = getAggregationMethod();
		// var yaxis = getYAxis();
		// var string = aggrM.concat('(');
		// string = string.concat(yaxis);
		// string = string.concat(')');
		// console.log("string", string);
		// /*
        // if($("#blankChart1").is(':visible')){ //If we are using the second drawing graph
        //     blankChart(string, "1")
        // }*/
        // blankChart(string, index);

	}
}

function setNoDataBlankChartAxes(xaxisVal, yaxisVal, index){
	var aggrM = getAggregationMethod();
	var yaxis = yaxisVal;
	var xaxis = xaxisVal;
	var string = aggrM.concat('(');
	string = string.concat(yaxis);
	string = string.concat(')');
	console.log("string", string);
	/*
	if($("#blankChart1").is(':visible')){ //If we are using the second drawing graph
		blankChart(string, "1")
	}*/
	blankChart(xaxis, string, index);
}



$(document).on( "click", "#predicate-column-header", function() {
    $("#predicate-column").children("li").slideToggle('fast');;
     $(this).find("span").toggleClass('glyphicon glyphicon-chevron-down').toggleClass('glyphicon glyphicon-chevron-up');
});
$(document).on( "click", "#category-list-header", function() {
    $("#category-list").children("li").slideToggle('fast');;
     $(this).find("span").toggleClass('glyphicon glyphicon-chevron-down').toggleClass('glyphicon glyphicon-chevron-up');
});
$(document).on( "click", "#x-axis-header", function() {
    $("#x-axis").children("li").slideToggle('fast');;
});
$(document).on( "click", "#y-axis-header", function() {
    $("#y-axis").children("li").slideToggle('fast');;
});
$(document).on( "click", "#dataset-header", function() {
    $("#dataset").children("li").slideToggle('fast');;
});

$(".front_dataset").click(function(){
	 $("#dataset li").removeClass("active");
	$(".nav_before").addClass("_after");
  var clicked = $(this);
  getInterfaceFormData(clicked.attr("id").toLowerCase());
  $("#front_dataset li").removeClass("active");
  clicked.addClass("active");
  var classname = document.getElementById(clicked.attr("id").toLowerCase());
  classname.className = classname.className+" active";
});

$(".graph").dblclick(function(){
// alert("hi")
//   var clicked = $(this);
//   getInterfaceFormData(clicked.attr("id"));
//   $("#dataset li").removeClass("active");
//   clicked.addClass("active");
});


$(".dataset").click(function(){

  var clicked = $(this);
  getInterfaceFormData(clicked.attr("id"));
  $("#dataset li").removeClass("active");
  clicked.addClass("active");
});

$('#category-list').on( 'click', '.category-list', function(){
	if(customQuery){
		return;
	}
  var clicked = $(this);
  $("#category-list li").removeClass("active");
  clicked.addClass("active");
  updateTrends();
});

$('#predicate-column').on( 'click', '.predicate-column', function(){
	  var clicked = $(this);
	  $("#predicate-column li").removeClass("active");
	  clicked.addClass("active");
	  updateTrends();
	});

$('#x-axis').on( 'click', '.x-axis', function(){
  if(customQuery){
	  return;
  }
  var clicked = $(this);
  $("#x-axis li").removeClass("active");
  clicked.addClass("active");

  var yselected = $("#y-axis .active").text()
  var xselected = $("#x-axis .active").text()

  //asdfasdf
  var ymetadata = currentFormData.yAxisColumns[yselected];
  var xmetadata = currentFormData.xAxisColumns[xselected];
  var xmin = xmetadata.min;
  var xmax = xmetadata.max;
  ymin = ymetadata.min;
  ymax = ymetadata.max;
  //changeScaleBlankChart( xmin , xmax , ymin , ymax );
  updateTrends();
});

$("#x-axis-select").on('change', function() {

  $("#x-axis li").removeClass("active");

  var yselected = $("#y-axis-select option:selected" ).text();
  var xselected = $("#x-axis-select option:selected" ).text();

  $('#x-axis li').filter(function() {
     return $(this).text() == xselected;
  }).addClass('active');

  var ymetadata = currentFormData.yAxisColumns[yselected];
  var xmetadata = currentFormData.xAxisColumns[xselected];
  var xmin = xmetadata.min;
  var xmax = xmetadata.max;
  ymin = ymetadata.min;
  ymax = ymetadata.max;

  updateTrends();
});

$('#y-axis').on( 'click', '.y-axis', function(){
	if(customQuery){
		return;
	}
  var clicked = $(this);
  $("#y-axis li").removeClass("active");
  clicked.addClass("active");

  var yselected = $("#y-axis .active").text();
  var xselected = $("#x-axis .active").text();
  var ymetadata = currentFormData.yAxisColumns[yselected];
  var xmetadata = currentFormData.xAxisColumns[xselected];
  var xmin = xmetadata.min;
  var xmax = xmetadata.max;
  ymin = ymetadata.min
  ymax = ymetadata.max;
  //changeYScale( min_X , max_X , min_Y , max_Y ,chartData, yselected);
  updateTrends();
  //changeYScale(yselected);

});

$("#y-axis-select").on('change', function() {

  $("#y-axis li").removeClass("active");

  var yselected = $("#y-axis-select option:selected" ).text();
  var xselected = $("#x-axis-select option:selected" ).text();

  $('#y-axis li').filter(function() {
     return $(this).text() == yselected;
  }).addClass('active');

  var ymetadata = currentFormData.yAxisColumns[yselected];
  var xmetadata = currentFormData.xAxisColumns[xselected];
  var xmin = xmetadata.min;
  var xmax = xmetadata.max;
  ymin = ymetadata.min;
  ymax = ymetadata.max;

  updateTrends();

});

$(".operator").click(function(){

	  var clicked = $(this);
	  $("#operator li").removeClass("active");
	  clicked.addClass("active");

	});
/**
 * Whenever a new dataset is selected. (Either from front page or list on main page)
 */
function processDatasetChange( data ){
  currentFormData = data;
  var schemax = []
  var schemay = []
  var schema = []
  var predicates = []

  for(xAxis in data.xAxisColumns){
    schemax.push(xAxis);
  }
  setXValues(schemax);

  for(yAxis in data.yAxisColumns){
    schemay.push(yAxis);
  }
  setYValues(schemay);

  for(zAxis in data.zAxisColumns){
    schema.push(zAxis);
  }
  setZValues(schema);

  for(predicate in data.predicateColumns){
    predicates.push(predicate);
  }

  var ymetadata = data.yAxisColumns[Object.keys(data.yAxisColumns)[0]]; //random elem
  var xmetadata = data.xAxisColumns[Object.keys(data.xAxisColumns)[0]]; //random elem
  var xmin = xmetadata.min;
  var xmax = xmetadata.max;
  ymin = ymetadata.min;
  ymax = ymetadata.max;
  //changeScaleBlankChart(xmin,xmax,ymin,ymax);
  $('#outlier_count_combobox').show();

//  var option = document.getElementById("sel").value;
//  if(option =="Scatter")
//	  setupScatterView();
//  else{
//  //get sidebar trends
//	  updateTrends();
//  }
  updateTrends();
}

function setXValues( xvalues ){
  //after setData and after clear() need to set our scriptChanged variables to be true
  if ( typeof xAxisSelect0 !== 'undefined' )
  {
    xAxisSelect0.setData(xvalues)
    scriptChangedX0 = true;
    xAxisSelect0.clear() //clears selection
    scriptChangedX0 = true;
    xAxisSelect0.setValue([xvalues[0]])

    xAxisSelect1.setData(xvalues)
    scriptChangedX1 = true;
    xAxisSelect1.clear() //clears selection
    scriptChangedX1 = true;
    xAxisSelect1.setValue([xvalues[0]])
  }

  $("#x-axis").empty()
  $.each( xvalues, function( index, value ) {
    $("#x-axis").append("<li class=\"x-axis\" value = " + value +" >" + value + "</li>")
    if (index == 0) {
      $(".x-axis:first").addClass("active")
    }
  });

  if ( typeof xAxisSelect0 !== 'undefined' )
  {
    addDropDownData();
  }
}

function setYValues( yvalues ){

  if ( typeof yAxisSelect0 !== 'undefined' )
  {
    yAxisSelect0.setData(yvalues)
    scriptChangedY0 = true
    yAxisSelect0.clear() //clears selection
    scriptChangedY0 = true
    yAxisSelect0.setValue([yvalues[0]])

    yAxisSelect1.setData(yvalues)
    scriptChangedY1 = true
    yAxisSelect1.clear() //clears selection
    scriptChangedY1 = true
    yAxisSelect1.setValue([yvalues[0]])
  }

  $("#y-axis").empty()
  $.each( yvalues, function( index, value ) {
    $("#y-axis").append("<li class=\"y-axis\" value = " + value +">" + value + "</li>")
    if (index == 0) {
      $(".y-axis:first").addClass("active")
    }
  });
}

function setZValues( zvalues ){
  $("#category-list").empty()
  $.each( zvalues, function( index, value ) {
    $("#category-list").append("<li class=\"category-list\">" + value + "</li>");
    if (index == 0) {
      $(".category-list:first").addClass("active")
    }
  });

}

function setPredicate( pValues ) {
	$("#predicate-column").empty()
	  $.each( zvalues, function( index, value ) {
	    $("#predicate-column").append("<li class=\"predicate-column\">" + value + "</li>");
	    if (index == 0) {
	      $(".predicate-column:first").addClass("active")
	    }
	  });
}

$("input[name = search-method]:radio").change(function(){
  if( getSearchMethod() == 'SimilaritySearch' || getSearchMethod() == 'DissimilaritySearch' ){
    showCanvas();
    dragEnable = true;
  }
  else{
    hideCanvas();
  }
});

function onSubmit(){
  if(customQuery){
	  return;
  }
  $("#views").empty(); //remove existing views
  //$("#views_table").empty();

  var searchMethod = getSearchMethod();
  if(searchMethod == 'Outlier' || searchMethod == 'RepresentativeTrends'){
    trendAnalysis();
  }
  else if( searchMethod == "SimilaritySearch" || searchMethod == "DissimilaritySearch"){
    trendAnalysis();
  }
}

function getSimilarTrendData(){
	var operator = getOperator();
	var predicateColumn = getPredicate();
	var predicateValue = getSearchValue();
	if (predicateValue != "") {
		operator = "=";
	}
	else {
		predicateValue = getPredicateValue();
	}
	var query = new Query(
		getSearchMethod(),
	    getYAxis(),
	    getXAxis(),
	    getCategory(),
	    getAggregationMethod(),
	    getYAxis(),
	    parseInt(getNumResults()),
	    [],
	    [],
		operator,
		predicateColumn,
		predicateValue);
	  return setCommonQueryParams(query);
}


function getVegaOutlierData(){
  var aggregationMethod = $('input[name = aggregation-method]:checked').val() //avg or sum
  var method = $('input[name = search-method]:checked').val()
  var yselected = $("#y-axis .active").text()
  var xselected = $("#x-axis .active").text()
  var category = $("#category-list .active").text()
  var numResults = $('#num-results input').val()
  var query = new Query(
    method,
    yselected,
    xselected,
    category,
    aggregationMethod,
    yselected,
    parseInt(numResults),
    [],
    [],
	getOperator(),
	getPredicate(),
	getPredicateValue());
  getData(setCommonQueryParams(query));
}

function setCommonQueryParams(query){
  if( getOutputScaleOption() == 'ignore-scale' ){
    query.outputNormalized = true;
  }
  else {
    query.outputNormalized = false;
  }

  if( getScaleOption() == 'ignore-scale' ){
    query.distanceNormalized = true;
  }
  else {
    query.distanceNormalized = false;
  }

  if( getDistanceMethod() == 'dtw' ){
    query.distance_metric = "DTW";
  }
  else {
    query.distance_metric = "Euclidean";
  }
  return query;
}

function getDataset()
{
  return $("#dataset .active").attr("id");
}

function getXAxis()
{
	if(!customQuery){
	  return $("#x-axis .active").text()
   }
   else{
	   var row = magicSuggestRows[magicSuggestRows.length-1];
	   return row.x.getValue();
   }
 // if(ExTrendindex == 0){
 //   if (typeof xAxisSelect0 =='undefined' || xAxisSelect0 == null || xAxisSelect0.getValue() == null){
 //     return $("#x-axis .active").text()
 //   }
 //   return xAxisSelect0.getValue()[0]
 // }
 // else{
 //  if (typeof xAxisSelect1 =='undefined' || xAxisSelect1 == null || xAxisSelect1.getValue() == null){
 // 	    return $("#x-axis .active").text()
 //  }
 //  return xAxisSelect1.getValue()[0]
 // }

}

function getYAxis()
{
  //return yAxisSelect0.getValue()[0]
  if(!customQuery){
  	return $("#y-axis .active").text()
 }
 else{
	 var row = magicSuggestRows[magicSuggestRows.length-1];
	 return row.y.getValue();
 }
 //  pairwise implementation:
 //  if(ExTrendindex == 0){
 //   if (typeof yAxisSelect0 =='undefined' || yAxisSelect0 == null || yAxisSelect0.getValue() == null){
 //     return $("#y-axis .active").text()
 //   }
 //   return yAxisSelect0.getValue()[0]
 // }
 // else{
 //  if (typeof yAxisSelect1 =='undefined' || yAxisSelect1 == null || yAxisSelect1.getValue() == null){
 // 	    return $("#y-axis .active").text()
 //  }
 //  return yAxisSelect1.getValue()[0]
 // }

}

function getCategory()
{
  return $("#category-list .active").text()
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function getPredicate()
{
	return $("#category-list .active").text()
}

function getOperator()
{
	return $("#operator .active").attr("id")
}

function getXOperator(index) {
	str = '#x-op' + JSON.stringify(index)+' input';
	return $(str).val()
}

function getXValue(index) {
	str = '#x-value'+JSON.stringify(index)+' input';
	console.log(str)
	return $(str).val()
}

function getY1Operator(index) {
	str = '#y1-op'+JSON.stringify(index)+' input'
	return $(str).val();
}

function getY1Value(index) {
	str = '#y1-value'+JSON.stringify(index)+' input'
	return $(str).val();
}

function getY2Operator(index) {
	str = '#y2-op'+JSON.stringify(index)+' input'
	return $(str).val();
}

function getY2Value(index) {
	str = '#y2-value'+JSON.stringify(index)+' input'
	return $(str).val();
}

function getY2Axis() {
	return $('#y2-axis input').val();
}

function getAggregationMethod()
{
  return $('input[name = aggregation-method]:checked').val()
}

function getScaleOption()
{
  if($("#ignoreY").is(':checked')){
	  //changeScaleBlankChart(min_X,max_X,min_Y,max_Y, chartData);
	  //var yselected = $("#y-axis .active").text();
	  //changeYScale(yselected);
	 return "ignore-scale";
  }

  return "consider-scale";
  //return $('input[name = scale-option]:checked').val()
}

function getOutputScaleOption()
{
	if($("#ignoreY").is(':checked')){
		//alert("ignore-scale");
		 return "ignore-scale";
	  }
	return "consider-scale";
  //return $('input[name = output-scale-option]:checked').val()
}

function getDistanceMethod()
{
	if($("#ignoreX").is(':checked')){
		//alert("DTW");
		return "dtw";
	}
	return "euclidean";
  //return $('input[name = distance-method]:checked').val()
}

function getNumResults()
{
  return $('#num-results input').val()
}

function getPageNumber()
{
  return $('#page-number input').val()
}

function getPredicateValue()
{
  //console.log(capitalizeFirstLetter($('#predicate-value input').val().toLowerCase()));
  return $('#predicate-value input').val()
}

function setPredicateValue()
{
  $('#predicate-value input').empty();
}

function getSearchValue()
{
  return $('#search-value input').val()
}

function setSearchValue()
{
  $('#search-value input').empty();
}

function getSearchMethod()
{
  return $('input[name = search-method]:checked').val()
}

function fillPredicateDropdown(){
    var xaxis = getXAxis();
    var yaxis = getYAxis();
    var category = getCategory();
    document.getElementById("category").innerHTML = category;
    document.getElementById("xaxis").innerHTML = xaxis;
    document.getElementById("yaxis").innerHTML = yaxis;
    spaceBetweenTables(); //testing function

}

$(".dropdown-menu li").click(function(){

	  var clicked = $(this);
	  document.getElementById("menu1").innerHTML = clicked.text();
	});

function changeExistingTrends(index){
	if (index == 1 && !isGraphVisible("1")) return;
	ExTrendindex = index;
	document.getElementById(ExTrendindex).disabled = true;
	var num = 1- ExTrendindex;
	document.getElementById(num).disabled = false;
	generateExistingTrends(existingTrends[ExTrendindex]);
}

// handle different chart types and different layers
var select = document.getElementById("sel");
select.onchange = function() {
	var option = document.getElementById("sel").value;
	if(option == "Bar Chart"){
        setupBarView();
        drawBarsAfterDragDrop();
        onSubmit();
	}
	else if(option =="Scatter"){
        setupScatterView();
//        onSubmit();
	}
	else{
        setupLineView();
        drawTrend();
        onSubmit();
	}
}

function setupLineView(){
    histogram = false;
    scatter = false;
    clickmodify = false;

    $("#svgLayer").css("display","none");
    $("#tools_sketch").css("display","block");
    $("#draganddrop").css("display","none");
    $("#visualisation").css("display","none");
    $("#blankChart").css("display", "block");
    $("#scatterplot").css("display","none");

    generateExistingTrends(existingTrends[ExTrendindex]);
    //updateMainGraph();
}
function setupBarView(){
    histogram = true;
    scatter = false;
    clickmodify = false;

    $("#scatterplot").css("display","none");
    $("#svgLayer").css("display","none");
    $("#tools_sketch").css("display","none");
    $("#draganddrop").css("display","none");
    $("#visualisation").css("display","block");
    $("#blankChart").css("display", "block");

    barConfig();
    generateExistingTrends(existingTrends[ExTrendindex]);
    //updateMainGraph();

}
//function testing(a){
//	console.log("AAAA",a)
//}
function setupScatterView(){
    console.log("setupScatterView")
    //d3.selectAll("g .brush").remove()
	d3.selectAll("g").remove()
    drawRandomChart();

    histogram = false;
    scatter = true;
    clickmodify = false;

    $("#scatterplot").css("display","block");
    $("#blankChart").css("display", "none");
    $("#svgLayer").css("display","none");
    $("#tools_sketch").css("display","none");
    $("#draganddrop").css("display","none");
    $("#visualisation").css("display","none");

    var numresults = $('#num-results input').val()
	var x = getXAxis();
	var y = getYAxis();
	var z = getCategory();
    var scatterquery =new ScatterPlotQuery([],x, y, z, numresults, "ScatterRep");
	getScatterData(scatterquery);
    generateScatterTrends(existingTrends[ExTrendindex]);
    //updateMainGraph
}
/*
  getAxisType("Year", "xAxisColumns") -> "O"
  getAxisType("Major", "xAxisColumns") -> "C"
*/
function getAxisType(axisNameToFind, axisColumns){
    console.log("form", currentFormData)
    console.log("form", currentFormData[axisColumns])
    console.log(axisColumns)
    for( axisName in currentFormData[axisColumns]){
        if(axisName == axisNameToFind){
            return currentFormData[axisColumns][axisName]["columnType"];
        }
    }
    return "Q"; //default to quantitative
}

function getBaseline() {
	  var aggregationMethod = $('input[name = aggregation-method]:checked').val() //avg or sum
	  var method = $('input[name = search-method]:checked').val()
	  var yselected = $("#y-axis .active").text()
	  var xselected = $("#x-axis .active").text()
	  var category = $("#category-list .active").text()
	  var numResults = $('#num-results input').val()
	  var y2Axis = $('#y2-axis input').val()
	  var query = new BaselineQuery(
	    xselected,
	    yselected,
	    y2Axis,
	    category,
	    aggregationMethod,
		getOperator(),
		getPredicate(),
		getPredicateValue(),
		getPageNumber());
	  getBaselineData(query);
}

function addConstraints(index) {
	console.log(index);
	if (getXValue(index) == "" || getY1Value(index) == "") {
		alert("missing value");
		return;
	}
	xOperator.push(getXOperator(index));
	xValue.push(parseInt(getXValue(index)));
	yOperator.push(getY1Operator(index));
	yValue.push(getY1Value(index));
	y2Operator.push(getY2Operator(index));
	y2Value.push(getY2Value(index));
}

function clearConstraints() {
	  xOperator.length = 0;
	  xValue.length = 0;
	  yOperator.length = 0;
	  yValue.length = 0;
	  y2Operator.length = 0;
	  y2Value.length = 0;
}

function SketchPoints(){
  this.points=[];
  this.minX=0;
  this.maxX=409; //use global variable
  this.minY=0;
  this.maxY=210;  //use global variable
  this.yAxis = getYAxis();
  this.xAxis = getXAxis();
  this.groupBy = getCategory();
  this.aggrFunc = getAggregationMethod();
  this.aggrVar = getYAxis();
}
