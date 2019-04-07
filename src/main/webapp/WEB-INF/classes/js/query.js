function constructDatasetChangeQuery( datasetName )
{
  return new formQuery( datasetName );
}
function formQuery( databasename ){
  this.databasename = databasename;
}

function constructUserQuery()
{
  return new Query( "SimilaritySearch" );
}

function constructRepresentativeTrendQuery()
{
  return new Query( "RepresentativeTrends" );
}

function constructOutlierTrendQuery()
{
  return new Query( "Outlier" );
}

function Query( searchMethod ) {
  this.method = searchMethod; // fix to dynamically fetch
  this.databasename = getSelectedDataset();
  this.xAxis = getSelectedXAxis();
  this.yAxis = getSelectedYAxis();
  this.groupBy = getSelectedCategory();
  this.aggrFunc = getAggregationMethod();
  this.aggrVar = getSelectedYAxis();
  this.outlierCount = getNumResults();
  this.dataX = []; // fix to dynamically fetch
  this.dataY = []; // fix to dynamically fetch
  this.yMax = null; // fix to dynamically fetch. is this field necessary?
  this.yMin = null; // fix to dynamically fetch. is this field necessary?
  this.error = getSelectedErrorAxis(); // error column use for errorbars
  //  this.error = 'listingpricepersqft'; // error column use for errorbars
  var points = []

  for(var i = 0; i < sketchpadData.length; i++){
    var xp = sketchpadData[i]["xval"];
    var yp = sketchpadData[i]["yval"];
    points.push(new Point( xp, yp ));
    this.dataX.push( xp );
    this.dataY.push( yp );
  }

  this.sketchPoints = [new SketchPoints(this.xAxis, this.yAxis, points)];
  this.distanceNormalized = "linear"; // fix to dynamically fetch
  this.outputNormalized = getOutputNormalized();
  this.clustering = "KMeans"; // fix to dynamically fetch
  this.kmeansClusterSize = getClusterSize();
  this.distance_metric = getDistanceMethod(); // fix to dynamically fetch
  this.predicateOperator = "";
  this.predicateColumn = "";
  this.predicateValue = "";
  this.filter = getFilter();
  this.xRange = getXRange();
  //this.segmentCount = getNumSegments();
  this.considerRange = getConsiderRange();
  this.smoothingType = getSmoothingType();
  this.smoothingcoefficient = getSmoothingCoefficient();
  this.download=false;
  this.includeQuery=false;
  this.yOnly=false;
  this.downloadAll=false;
  this.downloadThresh=getDownloadThresh();
  this.minDisplayThresh=getMinDisplayThresh();
}
function getDownloadThresh(){
  return $("#min-thresh-download").val();
}
function getFilter(){
  var filter = $("#filter.form-control").val();
  var eqIdx = filter.indexOf("=");
  var dquoteIdx = filter.indexOf("\"");
  if (eqIdx!=-1 && dquoteIdx!=-1){
    filter = filter.replace(/"/g, '\'') //replace all double with single quotes
  }
  return filter
}
// function getParsePredicate(){
//   var constraint = $("#filter.form-control").val();
//   var predicateOperator = "=";
//   var predicateColumn = getSelectedCategory();
//   var predicateValue = ""
//   if (constraint.includes(">")){
//     predicateOperator=">";
//   }
//   else if (constraint.includes("<")){
//     predicateOperator="<";
//   }
//   else if (constraint.includes("=")){
//     predicateOperator="=";
//   }
//   else{
//     //not a constraint statement
//     return [predicateOperator,predicateColumn,predicateValue]
//   }
//   predicateColumn= constraint.split(">")[0]
//   predicateValue= constraint.split(">")[1]
//   return [predicateOperator,predicateColumn,predicateValue]
// }
function SketchPoints(xAxisName, yAxisName, points){
  var xAxisData = globalDatasetInfo.xAxisColumns;
  var yAxisData = globalDatasetInfo.yAxisColumns;
  this.points = points;
  this.minX = xAxisData[xAxisName]["min"];
  this.maxX = xAxisData[xAxisName]["max"];
  this.minY = yAxisData[yAxisName]["min"];
  this.maxY = yAxisData[yAxisName]["max"];
  this.yAxis = getSelectedYAxis();
  this.xAxis = getSelectedXAxis();
  this.groupBy = getSelectedCategory();
  this.aggrFunc = getAggregationMethod();
  this.aggrVar = getSelectedYAxis();
}

function getSelectedXAxis()
{
  return angular.element($("#sidebar")).scope().selectedXAxis;
}

function getSelectedYAxis()
{
  return angular.element($("#sidebar")).scope().selectedYAxis;
}

function getSelectedCategory()
{
  return angular.element($("#sidebar")).scope().selectedCategory;
}

function getSelectedErrorAxis()
{
  var selectedError = angular.element($("#sidebar")).scope().selectedErrorAxis;
  if(selectedError == "none"){
    return null;
  }
  else{
    return selectedError;
  }
}

function resetSelectedErrorAxis()
{
  angular.element($("#sidebar")).scope().selectedErrorAxis = "none";
}

function getSmoothingCoefficient()
{
  return $( "#slider-range-max" ).slider( "value" );
}

function getSmoothingType()
{
  return angular.element($("#smoothing-form-control option:selected")).val()
}

function getXRange() //when zoomed in
{
  return xrangeNew;
}

function getAggregationMethod()
{
  return angular.element($("#table-div")).scope().aggregation;
}

function getDistanceMethod()
{
  return angular.element($("#table-div")).scope().similarity;
}

function getNumResults()
{
  return angular.element($("#table-div")).scope().numResults;
}
function getMinDisplayThresh()
{
  return angular.element($("#table-div")).scope().minDisplayThresh;
}

function getClusterSize()
{
  return angular.element($("#table-div")).scope().clusterSize;
}

function getConsiderRange()
{
  return angular.element($("#table-div")).scope().considerRange;
}

function getScatterplotOption()
{
  return angular.element($("#table-div")).scope().showScatterplot;
}
function getBarchartOption()
{
  return angular.element($("#table-div")).scope().showBar;
}
function getflipY()
{
  return angular.element($("#table-div")).scope().flipY;
}

function usingPattern()
{
  return angular.element($("#table-div")).scope().flipY;
}

function getShowOriginalSketch()
{
  return angular.element($("#table-div")).scope().showOriginalSketch;
}
function getOutputNormalized()
{
  return angular.element($("#table-div")).scope().outputNormalized;
}

function getCsvHeaders()
{
  return angular.element($("#define-attributes")).scope().csvHeaders;
}

function getNumSegments()
{
  return $('#num-segments input').val()
}

function getSelectedDataset()
{
  var ret= $("#dataset-form-control option:selected").val();
  return ret;
}

function mergejoin(outputcharts_orig,outputcharts_error)
{
 //console.log("original: ",outputcharts_orig);
 //console.log("error: ",outputcharts_error);
  var errochartsmap = {};
  outputcharts_error.forEach(function(outputcharts_error) {errochartsmap[outputcharts_error.title] = outputcharts_error.yData;});

  // now do the "join":
  outputcharts_orig.forEach(function(outputcharts_orig) {
      outputcharts_orig["error"] = errochartsmap[outputcharts_orig.title];
  });
  // console.log("final: ",outputcharts_orig);
  return outputcharts_orig;
}

function mergejoin_representative(outputcharts_orig,outputcharts_error)
{
 // console.log("original: ",outputcharts_orig);
 // console.log("error: ",outputcharts_error);
  var errochartsmap = {};
  outputcharts_error.forEach(function(outputcharts_error) {errochartsmap[outputcharts_error.title] = outputcharts_error.yData;});

  // now do the "join":
  outputcharts_orig.forEach(function(outputcharts_orig) {
      outputcharts_orig["error"] = errochartsmap[outputcharts_orig.xType];
  });
  //console.log("final: ",outputcharts_orig);
  return outputcharts_orig;
}

function removeZqlRow(rowNumber)
{
  return angular.element($("#zql-table")).scope().removeRow(rowNumber);
}
