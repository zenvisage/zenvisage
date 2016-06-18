// contains the current form data, only global for now to save redundant calls to backend
var currentFormData;

function transformX( xVal ) {
  var xAxis = $("#top-x").find("option:selected").text();
  if (xAxis == "Year") {
    return parseInt(xVal) - 2004;
  }
  else if (xAxis == "Quarter") {
    return (parseInt(xVal) - 2004)*4;
  }
  else if (xAxis == "Month") {
    return (parseInt(xVal) - 2004)*12;
  }
}

function previousPage(){
  var currentPage = $("#page-number").val()
  if (currentPage > 1)
  {
    document.getElementById("page-number").value = parseInt(currentPage) - 1;
    getBaseline();
  }
}

function nextPage(){
  var currentPage = $("#page-number").val()
  if (currentPage < $("#total-pages").text())
  {
    document.getElementById("page-number").value = parseInt(currentPage) + 1;
    getBaseline();
  }
}

function updateTrends(){
  //updateOneTrend("");
  //updateOneTrend("1");
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
  //changeScaleMainChart(xmin,xmax,ymin,ymax);
  $('#outlier_count_combobox').show();

  updateTrends();
}

function setXValues( xvalues ){
  //after setData and after clear() need to set our scriptChanged variables to be true
  $("#x-axis").empty()
  $.each( xvalues, function( index, value ) {
      $("#x-axis").append("<li class=\"x-axis\" value = " + value +" >" + value + "</li>")
    /*
    if (index == 0) {
      $(".x-axis:first").addClass("active")
    }
    */
  });

  $('[id^=xvals-]').empty()
  $.each( xvalues, function( index, value ) {
    if (value == "Year")
    {
      $("[id^=xvals-]").append("<option class=\"x-axis\" value = " + value +" >" + value + "</option>")
    }
  });

  $('#top-x').empty()
  $.each( xvalues, function( index, value ) {
    $("#top-x").append("<option class=\"x-axis\" value = " + value +" >" + value + "</option>")
  });

}

function setYValues( yvalues ){

  $("#y-axis").empty()
  $.each( yvalues, function( index, value ) {
    $("#y-axis").append("<li class=\"y-axis\" value = " + value +">" + value + "</li>")
    /*
    if (index == 0) {
      $(".y-axis:first").addClass("active")
    }
    */
  });

  $('[id^=yvals-]').empty()
  $.each( yvalues, function( index, value ) {
    $("[id^=yvals-]").append("<option class=\"y-axis\" value = " + value +" >" + value + "</option>")
  });

  $('#top-y1').empty()
  $.each( yvalues, function( index, value ) {
    $("#top-y1").append("<option class=\"y-axis\" value = " + value +" >" + value + "</option>")
  });

  $('#top-y2').empty()
  $("#top-y2").append("<option class=\"y-axis\" value =\"\"></option>")
  $.each( yvalues, function( index, value ) {
    $("#top-y2").append("<option class=\"y-axis\" value = " + value +" >" + value + "</option>")
  });

}

function setZValues( zvalues ){
  $("#category-list").empty()
  $.each( zvalues, function( index, value ) {
    $("#category-list").append("<li class=\"category-list\">" + value + "</li>");
    /*
    if (index == 0) {
      $(".category-list:first").addClass("active")
    }
    */
  });

  $('#top-z').empty()
  $.each( zvalues, function( index, value ) {
    $("#top-z").append("<option class=\"z-axis\" value = " + value +" >" + value + "</option>")
  });

  $.each( zvalues, function( index, value ) {
    $("#select-predicate").append("<option class=\"x-axis\" value = " + value +" >" + value + "</option>")
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


function getDataset()
{
  return $("#dataset .active").attr("id");
}

function getCategory()
{
  return $("#category-list .active").text()
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1);
}

function getY2Axis() {
  return $('#y2-axis input').val();
}

function getAggregationMethod()
{
  return $('input[name = aggregation-method]:checked').val()
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

function getBaseline() {
  var aggregationMethod = $('input[name = aggregation-method]:checked').val() //avg or sum
  var category = $("#top-z").find("option:selected").text();
  var xselected = $("#top-x").find("option:selected").text();
  var y1selected = $("#top-y1").find("option:selected").text();
  var y2selected = $("#top-y2").find("option:selected").text();
  var query = new BaselineQuery(
    xselected,
    y1selected,
    y2selected,
    category,
    aggregationMethod,
    getBaselineOperator(),
    getBaselinePredicate(),
    getBaselinePredicateValue(),
    getBaselinePageNumber()
  );
  getBaselineData(query);
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

  var constraintList = getConstraints();
  this.xOperator = constraintList[0]; //[];xOperator.slice();
  this.xValue = constraintList[1];//xValue.slice();
  this.y1Operator = constraintList[2];//yOperator.slice();
  this.y1Value = constraintList[3];//yValue.slice();
  this.y2Operator = constraintList[4];//y2Operator.slice();
  this.y2Value = constraintList[5];//y2Value.slice();
}

function getConstraints() {
  var xOperator = []
  var xValue = []
  var y1Operator = []
  var y1Value = []
  var y2Operator = []
  var y2Value = []
  var constraintList = []

  if ($('#row1-check').bootstrapSwitch('state'))
  {
    xOperator.push($("#row1-x-op").find("option:selected").text());
    y1Operator.push($("#row1-y1-op").find("option:selected").text());
    y2Operator.push($("#row1-y2-op").find("option:selected").text());

    xValue.push(transformX($("#row1-x-value").find("option:selected").text()));
    y1Value.push($("#row1-y1-value").val());
    y2Value.push($("#row1-y2-value").val());
  }
  if ($('#row2-check').bootstrapSwitch('state'))
  {
    xOperator.push($("#row2-x-op").find("option:selected").text());
    y1Operator.push($("#row2-y1-op").find("option:selected").text());
    y2Operator.push($("#row2-y2-op").find("option:selected").text());

    xValue.push(transformX($("#row2-x-value").find("option:selected").text()));
    y1Value.push($("#row2-y1-value").val());
    y2Value.push($("#row2-y2-value").val());
  }
  if ($('#row3-check').bootstrapSwitch('state'))
  {
    xOperator.push($("#row3-x-op").find("option:selected").text());
    y1Operator.push($("#row3-y1-op").find("option:selected").text());
    y2Operator.push($("#row3-y2-op").find("option:selected").text());

    xValue.push(transformX($("#row3-x-value").find("option:selected").text()));
    y1Value.push($("#row3-y1-value").val());
    y2Value.push($("#row3-y2-value").val());
  }
  constraintList.push(xOperator, xValue, y1Operator, y1Value, y2Operator, y2Value)
  return constraintList
}


function getBaselineOperator(){
  return $("#predicate-op").find("option:selected").text();
}

function getBaselinePredicate(){
  return  $("#select-predicate").find("option:selected").text();
  /*
  if (val == "X")
  {
    return $("#top-x").find("option:selected").text();
  }
  else // val == Z
  {
    return $("#top-z").find("option:selected").text();
  }
  */
}

function getBaselinePredicateValue(){
  return $("#predicate-value").val();
}
function getBaselinePageNumber(){
  return $("#page-number").val();
}
