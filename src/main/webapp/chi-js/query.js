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
  var points = []
  for(var i = 0; i < sketchpad.rawData_.length; i++){
    points.push(new Point( sketchpad.rawData_[i][0], sketchpad.rawData_[i][1] ));
  }
  this.sketchPoints = [new SketchPoints(this.xAxis, this.yAxis, points)];
  this.distanceNormalized = false; // fix to dynamically fetch
  this.outputNormalized = false; // fix to dynamically fetch
  this.clustering = "KMeans"; // fix to dynamically fetch
  this.distance_metric = getDistanceMethod(); // fix to dynamically fetch
  this.predicateOperator = "="; // fix to dynamically fetch
  this.predicateColumn = getSelectedCategory();
  this.predicateValue = ""; // fix to dynamically fetch
  this.xRange = getXRange();
  //this.segmentCount = getNumSegments();
}

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

function getXRange() //when zoomed in
{
  return sketchpad.xAxisRange()
}

function getAggregationMethod()
{
  return $('input[name = aggregation-method]:checked').val()
}

function getDistanceMethod()
{
  return $('input[name = distance-method]:checked').val()
}

function getNumResults()
{
  return $('#num-results input').val()
}

function getNumSegments()
{
  return $('#num-segments input').val()
}

function getSelectedDataset()
{
  return $("#dataset-form-control option:selected").val();
}

