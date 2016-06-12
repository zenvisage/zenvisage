function constructDatasetChangeQuery( datasetName )
{
  return new formQuery(datasetName);
}
function formQuery(databasename){
  this.databasename = databasename;
}

function constructDimensionChangeQuery(){
  return new Query();
}

function Query() {
  this.method = "SimilaritySearch"; // fix to dynamically fetch
  this.xAxis = getSelectedXAxis();
  this.yAxis = getSelectedYAxis();
  this.groupBy = getSelectedCategory();
  this.aggrFunc = "avg"; // fix to dynamically fetch
  this.aggrVar = getSelectedYAxis();
  this.outlierCount = 8; // fix to dynamically fetch
  this.dataX = []; // fix to dynamically fetch
  this.dataY = []; // fix to dynamically fetch
  this.yMax = null; // fix to dynamically fetch. is this field necessary?
  this.yMin = null; // fix to dynamically fetch. is this field necessary?
  this.sketchPoints = [new SketchPoints()];
  // need sketchpoints to fetch something.. should not be necessary

  this.distanceNormalized = false; // fix to dynamically fetch
  this.outputNormalized = false; // fix to dynamically fetch
  this.clustering="KMeans"; // fix to dynamically fetch
  this.distance_metric="Euclidean"; // fix to dynamically fetch
  this.predicateOperator = "="; // fix to dynamically fetch
  this.predicateColumn = getSelectedCategory();
  this.predicateValue = ""; // fix to dynamically fetch
}

// will become obsolete with new drawing
//xmin = sketchObject.xAxisRange()[0]
//xmax = sketchObject.xAxisRange()[1]
//ymin = sketchObject.yAxisRange()[0]
//ymax = sketchObject.yAxisRange()[1]
function SketchPoints(){
  this.points=[];
  this.minX=0;
  this.maxX=409;
  this.minY=0;
  this.maxY=210;
  this.yAxis = getSelectedYAxis();
  this.xAxis = getSelectedXAxis();
  this.groupBy = getSelectedCategory();
  this.aggrFunc = "avg";
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