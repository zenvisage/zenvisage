
function getSimilarityQuery(zqlRows){
    var name = $(this).find(".name").val()
    var x = $(this).find(".x-val").val();
    var y = $(this).find(".y-val").val();
    var z = $(this).find(".z-val").val();
    var constraints = $(this).find(".constraints").val();

    var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};

    var sketchpointWrapper = [];
    var sketchpoints = [];
    var dragAndDropPoints = getScatterPoints();
    for(var i = 0; i < dragAndDropPoints.length; i++){
        sketchpoints.push(new Point( dragAndDropPoints[i]["xval"],dragAndDropPoints[i]["yval"] ));
    }
    sketchpointWrapper.push({"points":sketchpoints})
    input["sketchPoints"] = new ScatterSketchPoints(getSelectedXAxis(), getSelectedYAxis(), sketchpointWrapper);
    input["name"] = {"output": false,"sketch": false,"name": "f1"};
    input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
    input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
    input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z1", "aggregate" : false};
    input["viz"] = {"map":{"type":"scatter"}};
    input["processe"] = {"variables":["v1"],"method":"Scatter","count":"50","metric":"argmin","arguments":["f1"],"axisList1":["z1"],"axisList2":[]};
    zqlRows.push(input);
    var input2 = { "name": name, "x": x, "y": y, "z": z};
    input2["name"] = {"output": true,"sketch": false,"name": "f2"};
    input2["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
    input2["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
    input2["z"] = { "values": [], "variable" : "v1", "aggregate" : false};
    input2["viz"] = {"map":{"type":"scatter"}};
    zqlRows.push(input2);

}

function getPolygonQueryHelper(zqlRows){

var name = $(this).find(".name").val()
var x = $(this).find(".x-val").val()
var y = $(this).find(".y-val").val()
var z = $(this).find(".z-val").val();
var constraints = $(this).find(".constraints").val();
var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};

var polygons = [];
var polygon = [];
var polypoints = getPolygons()
    polypoints = polypoints[polypoints.length-1]["points"]
    console.log("polygons test1",polypoints)
for(var i = 0; i < polypoints.length-1; i++){
    polygon.push(new Point( polypoints[i][0], polypoints[i][1] ));
}
polygons.push({"points": polygon})
input["sketchPoints"] = new ScatterSketchPoints(getSelectedXAxis(), getSelectedYAxis(), polygons);
console.log("polygons test2",polygons)

// console.log("get polygons:",ScatterService.getPolygons());
//
// console.log("polygon:",polygon);
// console.log("polygons:",polygons);
// var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};
//     input["sketchPoints"] = new ScatterSketchPoints(this.xAxis, this.yAxis, polygons);
//     input["name"] = {"output": true,"sketch": true,"name": "f1"};
//     input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
//     input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
//     input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z1", "aggregate" : false};
//     input["viz"] = {"map":{"type":"scatter"}};
//     input["processe"] = {"variables":["v2"],"method":"Rank","count":"50","metric":"argmin","arguments":["f1"],"axisList1":[],"axisList2":[]};
//     $scope.queries['zqlRows'].push(input);

input["name"] = {"output": false,"sketch": false,"name": "f1"};
input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z1", "aggregate" : false};
input["viz"] = {"map":{"type":"scatter"}};
input["processe"] = {"variables":["v1"],"method":"Rank","count":"50","metric":"argmin","arguments":["f1"],"axisList1":["z1"],"axisList2":[]};
zqlRows.push(input);
var input2 = { "name": name, "x": x, "y": y, "z": z};
input2["name"] = {"output": true,"sketch": false,"name": "f2"};
input2["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
input2["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
input2["z"] = { "values": [], "variable" : "v1", "aggregate" : false};
input2["viz"] = {"map":{"type":"scatter"}};
zqlRows.push(input2);

}

function scatterDatasetChangeQueryHelper(zqlRows){
    var name = $(this).find(".name").val()
    var x = $(this).find(".x-val").val()
    var y = $(this).find(".y-val").val()
    var z = $(this).find(".z-val").val()
    var constraints = "domestic=True"
    console.log(constraints)
    var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};
    input["name"] = {"output": true,"sketch": true,"name": "f1"};
    input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
    input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
    input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z1", "aggregate" : false};
    input["viz"] = {"map":{"type":"scatter"}};
    zqlRows.push(input);
}

function scatterSketchpadQueryHelper(zqlRows){
    var name = $(this).find(".name").val()
    var x = $(this).find(".x-val").val()
    var y = $(this).find(".y-val").val()
    var z = $(this).find(".z-val").val()
    var constraints = $(this).find(".constraints").val()
    var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};
    input["name"] = {"output": true,"sketch": true,"name": "f1"};
    input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x1"};
    input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y1"};
    input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z1", "aggregate" : true};
    input["viz"] = {"map":{"type":"scatter"}};
    zqlRows.push(input);
}
function ScatterSketchPoints(xAxisName, yAxisName, polygons){
    var xAxisData = globalDatasetInfo.xAxisColumns;
    var yAxisData = globalDatasetInfo.yAxisColumns;
    this.polygons = polygons;
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