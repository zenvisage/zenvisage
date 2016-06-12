function Point(x, y){
    this.x=x;
    this.y=y;
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

//function Rectangles(){
//	this.rectangleList=[];
//	this.minX=-1;
//	this.maxX=408;
//	this.minY=0;
//	this.maxY=209;
//}

function singleRec( u, i , o, p){
	this.startX = u;
	this.startY = i;
	this.width = o;
	this.hight = p;
}

function Circles(){
	this.circleList = [];
	this.minX= -1;
	this.maxX = 408;
	this.minY = 0;
	this.maxY = 209;
}

function oneCir(u, i, o){
	this.center_x = u;
	this.center_y = i
	this.radius = o;

}

function drawAll(){
	ctx.clearRect(0, 0, canvas.width, canvas.height);
	for(var z =0; z< circles_rem.length; z++){
		//console.log("drawall")
		var myCircle = circles_rem[z];
		ctx.beginPath();
		ctx.globalAlpha = 1;
		ctx.arc(myCircle[0], myCircle[1], myCircle[2], 0, 2*Math.PI,false);
		ctx.fillStyle = "#FFD34E";
		ctx.fill();
	}

}

function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

var isDrawing, myPoints = [ ], radius = 15, drawCircle = false;
var circle_x, circle_y, r, singleC, cancel = false, last_r=0, max_w= 0, max_h = 0;

var canvas = document.getElementById("tools_sketch"),
	ctx = canvas.getContext('2d'),
	rect = {},
	drawline = true,
	lastX, lastY,
	Dragy = false;

var canvas1 = document.getElementById("tools_sketch1"),
  ctx1 = canvas1.getContext('2d'),
  rect1 = {},
  drawline1 = true,
  lastX1, lastY1,
  Dragy1 = false;

function init(){
	canvas.addEventListener("mousedown", mouseDown, false);
	canvas.addEventListener("mouseup", mouseUp, false);
	canvas.addEventListener("mousemove", mouseMove, false);
	$('#tools_sketch').mouseleave(function(e){
		if(Dragy){
			onSubmit();
		}
		Dragy = false;
		isDrawing = false;
	});

  canvas1.addEventListener("mousedown", mouseDown1, false);
  canvas1.addEventListener("mouseup", mouseUp1, false);
  canvas1.addEventListener("mousemove", mouseMove1, false);
  $('#tools_sketch1').mouseleave(function(e){
    if(Dragy1){
      //if leave canvas without mouse up
      onSubmit();
    }
    Dragy1 = false;
    isDrawing = false;
  });
}

function mouseDown(e){
    var canvasOffset = $('#tools_sketch').offset();
	if(drawCircle){
		 isDrawing = true;
		  myPoints.push({
		    x:  e.pageX - this.offsetLeft,
		    y: e.pageY - this.offsetTop,
		    radius: getRandomInt(5, 20),
		    opacity: Math.random()*0.010
		  });
	}
	else if(singleC){
		Dragy = true;
		circle_x = e.pageX - this.offsetLeft;
		circle_y = e.pageY - this.offsetTop;
		hit = -1;

		for(var z=0; z<circles_rem.length; z++){
			var myCircle = circles_rem[z];
			var dx = circle_x - myCircle[0];

			var dy = circle_y - myCircle[1];
			//console.log(dx)
			//console.log(dy)
			//console.log(dx*dx +dy*dy);
			//console.log(myCircle[2] * myCircle[2]);
			if(dx*dx +dy*dy < myCircle[2] * myCircle[2]){
				hit = z;
			}
		}
		//console.log(hit)

	}
	else{
		rect.startX = e.pageX - this.offsetLeft;
		//console.log(rect.startX);
		rect.startY = e.pageY - this.offsetTop;
		//console.log(rect.startY);
		//console.log(this.offset);
		//Draw(e.pageX-this.offsetLeft, e.pageY-this.offsetTop, true);
    //alert(e.pageX+ " " + e.pageY)
    //alert(e.clientX + " " + e.clientY)
    //alert(e.offsetX + " " + e.offsetY)
    //alert((e.pageX-osetx) + " " +(e.pageY-osety))
		//Draw(e.pageX- osetx, e.pageY-osety, false);
        //Draw(e.pageX-canvasOffset.left, e.pageY - canvasOffset.top, false, ctx, 0);
    Draw(e.offsetX, e.offsetY, false, ctx, 0);
        //alert("cursor: "  + e.offsetX + " " + e.offsetY)
		Dragy = true;
		console.log("dr", Dragy)
	}

}


function mouseDown1(e){
        //var canvasOffset = $('#tools_sketch1').offset();
		//Draw(e.pageX-canvasOffset.left, e.pageY - canvasOffset.top, false, ctx1, 1);
        Draw(e.offsetX, e.offsetY, false, ctx1, 1);
		Dragy1 = true;
}



function mouseMove(e) {
    var canvasOffset = $('#tools_sketch').offset();
	if(drawCircle){
		 if (isDrawing){

			  myPoints.push({
			    x: e.pageX - this.offsetLeft,
			    y: e.pageY - this.offsetTop,
			    radius: getRandomInt(5, 20),
			    opacity: Math.random()*0.010
			  });
			  for (var i = 0; i < myPoints.length; i++) {
				    ctx.beginPath();
				    ctx.globalAlpha = myPoints[i].opacity;
				    ctx.lineJoin = ctx.lineCap = 'round';
					 ctx.fillStyle = '#EB7F00';
				    ctx.arc(
				      myPoints[i].x, myPoints[i].y, myPoints[i].radius,
				      false, Math.PI * 2, false);
				    ctx.fill();
				    //console.log("circle")
			}
		 }

	}

	else if(singleC && Dragy){

			var center = new Point(circle_x, circle_y);

			var temp = new Point(e.pageX - this.offsetLeft,e.pageY - this.offsetTop);
			r = lineDistance(center, temp);
			//console.log("r");
			//if(r< last_r){
			//cir_Draw_white();

			//}
			//else
		if(hit< 0){
			drawAll();
			cir_Draw();
		}
		else{
			circles_rem[hit][0] = e.pageX - this.offsetLeft;
			circles_rem[hit][1] = e.pageY - this.offsetTop;

			drawAll();
		}
			//last_r = r;

	}
	else{
		if (Dragy && !drawline) {
			//console.log(Math.abs(-3));
			rect.w = (e.pageX - this.offsetLeft) - rect.startX;
			rect.h = (e.pageY - this.offsetTop) - rect.startY ;
			if(Math.abs(rect.w) > Math.abs(max_w))  max_w = rect.w;
			//console.log(max_w)
			if(Math.abs(rect.h) > Math.abs(max_h))  max_h = rect.h;
		    //ctx.clearRect(0,0,canvas.width,canvas.height);
			draw();

		}
		if(Dragy && drawline){
		//Draw(e.pageX-this.offsetLeft, e.pageY-this.offsetTop, true);
		//Draw(e.pageX-osetx, e.pageY-osety, true);
    Draw(e.offsetX, e.offsetY, true, ctx, 0);
    //console.log("cursor: "  + e.offsetX + " " + e.offsetY)

    //Draw(e.pageX-canvasOffset.left, e.pageY - canvasOffset.top, true, ctx, 0);
		//console.log(drag);
		//console.log(e.pageX+","+this.offsetLeft);
		}
	}

}

function mouseMove1(e) {
    var canvasOffset = $('#tools_sketch1').offset();
		if(Dragy1 && drawline1){
		//Draw(e.pageX-this.offsetLeft, e.pageY-this.offsetTop, true);
		Draw(e.offsetX, e.offsetY, true, ctx1, 1);
        //Draw(e.pageX-canvasOffset.left, e.pageY - canvasOffset.top, true, ctx, 1);

		//console.log(drag);
		//console.log(e.pageX+","+this.offsetLeft);
		}
}

function mouseUp(e) {
	//console.log("mouseup")
	last_r = 0;

	if(drawCircle){
		isDrawing = false;
		myPoints.length = 0;
		}
	else{
		Dragy = false;
		onSubmit();
		if(!singleC && !drawline){
		//console.log(rect.startX);
		//console.log((rectangles.maxY-rect.startY));
		//console.log(rect.w);
		//console.log(rect.h);
		var rectangle = new singleRec(rect.startX,(rectangles.maxY-rect.startY), rect.w, rect.h );
		rectangles.rectangleList.push(rectangle);
		}
		else if(singleC){
		//console.log(circle_x);
		//console.log(circles.maxY-circle_y);
		//console.log(r);
		var circle = new oneCir(circle_x, (circles.maxY-circle_y), r);
		circles.circleList.push(circle);

		if(hit < 0){
			circles_rem.push([
			circle_x,
			circle_y,
			r
			]);
		}
		//console.log(circles_rem[1][1])

		}
	}
}

function mouseUp1(e) {
	//console.log("mouseup")
	last_r = 0;
	Dragy1 = false;
  onSubmit();

}




function cir_Draw(){
	//console.log("draw cir");
	ctx.beginPath();
	ctx.globalAlpha = 1;
	ctx.arc(circle_x, circle_y, r, 0, 2*Math.PI,false);
	ctx.fillStyle = "#FFD34E";
	ctx.fill();
}

/*
function cir_Draw_white(){
	//console.log("draw white cir");
	ctx.beginPath();
	ctx.globalAlpha = 1;
	ctx.arc(circle_x, circle_y, last_r, 0, 2*Math.PI,false);

	ctx.lineWidth = 9;
	ctx.strokeStyle = "#ffffff";
	ctx.stroke();
}
*/

function draw() { //draw rectangle

		ctx.globalAlpha = 1;
		ctx.fillStyle= "#ACF0F2";
		ctx.fillRect(rect.startX, rect.startY, rect.w, rect.h);
	//ctx.stroke();
}


function Draw(x, y, isDown, ctxA, drawGraphIndex){ //add ctx parameter (for the correct context depending on canvas (since we can have multiple))
	//console.log("Draw");
        if(drawGraphIndex == 0){
            canvas = document.getElementById("tools_sketch")
            ctx = canvas.getContext("2d");
            ctxA = ctx
        }
        if(drawGraphIndex == 1){
            canvas1 = document.getElementById("tools_sketch1")
            ctx1 = canvas1.getContext("2d");
            ctxA = ctx1
        }
		if(isDown){

			ctxA.beginPath();
			ctxA.globalAlpha = 1;
			ctxA.strokeStyle = "#1695A3";
			ctxA.lineWidth = 2.5;
			ctxA.lineCap = "round";
			ctxA.lineJoin = "round";
			ctxA.moveTo(lastX, lastY);
			ctxA.lineTo(x,y);
			//console.log(x, y)
			//console.log(y)
      if(sketchPointsList.length == 0){
        sketchPointsList = [new SketchPoints(), new SketchPoints()];
      }
      sketchPointsList[drawGraphIndex].points.push(new Point(x, sketchPointsList[drawGraphIndex].maxY-y));
			//ctx.closePath();
			ctxA.stroke();
			//console.log("path");
		}
		lastX = x; lastY = y;

}



init();

function clearBlankChart(suffix) {
	//histogram=false;
	clickmodify = false;
  sketchPointsList = [new SketchPoints(), new SketchPoints()];
	 	ctx.clearRect(0, 0, canvas.width, canvas.height);
	 	document.getElementById("svgLayer"+suffix).style.display = "none";
	 	if(!histogram){
			document.getElementById("tools_sketch"+suffix).style.display = "block";
			document.getElementById("visualisation"+suffix).style.display = "none";
	 	}
	 	else{
	 		document.getElementById("tools_sketch"+suffix).style.display = "none";
			document.getElementById("visualisation"+suffix).style.display = "block";
			barConfig();
	 	}
		clickmodify = false;
		circles_rem = [];
		hit = -1;

}

document.getElementById("clear-1").onclick = function () {
	//histogram=false;
	//remove retangles of scatterplot
	d3.selectAll("g").remove();
	drawRandomChart();
	clickmodify = false;
 //   sketchPointsList = [new SketchPoints(), new SketchPoints()];
    	sketchPointsList = [];
	 	ctx.clearRect(0, 0, canvas.width, canvas.height);
	 	document.getElementById("svgLayer").style.display = "none";
	 	if(!histogram){
			document.getElementById("tools_sketch").style.display = "block";
			document.getElementById("visualisation").style.display = "none";

	 	}
	 	else{
	 		document.getElementById("tools_sketch").style.display = "none";
			document.getElementById("visualisation").style.display = "block";
			barConfig();
	 	}
		clickmodify = false;
		circles_rem = [];
		hit = -1;

};

document.getElementById("line-1").onclick = function(){
	clickmodify = false;
	histogram =false;
	   drawline = true;
	   drawCircle = false;
	   singleC = false;
	  // console.log("stop drawing circle")
	   ctx.clearRect(0, 0, canvas.width, canvas.height);
	   document.getElementById("svgLayer").style.display = "none";
	   document.getElementById("tools_sketch").style.display = "block";
	   document.getElementById("visualisation").style.display = "none";
	   //disableDragAndDrop();
	   clickmodify = false;
}

document.getElementById("line-2").onclick = function(){
	clickmodify1 = false;
	histogram =false;
	   drawline = true;
	   drawCircle = false;
	   singleC = false;
	   clickmodify1 = false;

     ctx1.clearRect(0,0, canvas1.width, canvas1.height);
     document.getElementById("svgLayer1").style.display = "none";
     document.getElementById("tools_sketch1").style.display = "block";
     document.getElementById("visualisation1").style.display = "none";

}

document.getElementById("circle").onclick = function(){
	   drawCircle = true;
	   drawline =false;
	   singleC = false;
	   ctx.clearRect(0, 0, canvas.width, canvas.height);
	   document.getElementById("svgLayer").style.display = "none";
	   document.getElementById("tools_sketch").style.display = "block";
	   //disableDragAndDrop();
	   clickmodify = false;

	   ctx1.clearRect(0, 0, canvas1.width, canvas1.height);
	   document.getElementById("svgLayer1").style.display = "none";
	   document.getElementById("tools_sketch1").style.display = "block";
}

/*
document.getElementById("singlecircle").onclick = function(){
	   drawCircle = false;
	   drawline =false;
	   singleC = true;
	  //console.log(singleC);
	   ctx.clearRect(0, 0, canvas.width, canvas.height);
	   document.getElementById("svgLayer").style.display = "none";
	   document.getElementById("tools_sketch").style.display = "block";
	   //disableDragAndDrop();
	   clickmodify = false;
}
*/

/*
document.getElementById("eraseCir").onclick = function(){
		if(hit > -1){
	 	circles_rem.splice(hit, 1);
		}
		drawAll();
	   document.getElementById("svgLayer").style.display = "none";
	   document.getElementById("tools_sketch").style.display = "block";
	   //disableDragAndDrop();
	   clickmodify = false;

}
*/

/*
document.getElementById("cancel").onclick = function(){
	if(!drawline && !drawCircle &&! singleC)
	   cancel = true;
	//console.log("canceling")
	 // console.log(max_w)
			ctx.clearRect(rect.startX, rect.startY, max_w, max_h);
			max_w = 0;
			max_h = 0;
			var tem = rectangles.rectangleList.pop();
			console.log(tem);

}

*/

function trendAnalysis(){	//use list and list1 to update
  //So our drawn trend is stored in sketchPoints.
  //We need new sketchPoints if we are using modify button
	var query = getSimilarTrendData();
	//console.log("query",query)
  var listOfSketchPoints = [] //[SketchPoints(), SketchPoints()]
	if(clickmodify){ //if modifying line, not drawing line
		sketchPoints = new SketchPoints();
        sketchPoints.maxX = drawGraphWidth;
        sketchPoints.maxY = drawGraphHeight;
		for(var i =0; i<list.length; i++){
			//tempPoint = new Point(list[i][0],list[i][1]);
			var tempPoint = new Point(list[i][0], sketchPoints.maxY-list[i][1]);
			sketchPoints.points.push(tempPoint);
		}
    listOfSketchPoints.push(sketchPoints)

    if($("#blankChart1").is(':visible')){ //If we are using the second drawing graph
      var sketchPoints1 = new SketchPoints();
      sketchPoints1.maxX = drawGraphWidth;
      sketchPoints1.maxY = drawGraphHeight;
  		for(var i =0; i<list1.length; i++){
  			var tempPoint = new Point(list1[i][0], sketchPoints1.maxY-list1[i][1]);
  			sketchPoints1.points.push(tempPoint);
  		}
      listOfSketchPoints.push(sketchPoints1)
    }
    //alert(list)
    //alert(list1)
		//console.log("using svg new list");
		//console.log(list);
	}
	if(histogram){

		sketchPoints = new SketchPoints();
        sketchPoints.maxX = drawGraphWidth;
        sketchPoints.maxY = drawGraphHeight;
		for(var i =0; i<numOfBar; i++){
			var tempPoint = new Point(listOfBarPoints[i][0], sketchPoints.maxY-listOfBarPoints[i][1]);
			sketchPoints.points.push(tempPoint);

		}
		//console.log(sketchPoints.points);
    listOfSketchPoints.push(sketchPoints)
    //todo: histogram for second chart?
	}
  if(listOfSketchPoints.length == 0){
    listOfSketchPoints = sketchPointsList; //use the drawing sketchPoints
    //console.log(sketchPointsList.length)
  }
  if($("#blankChart1").is(':hidden')){ //If we are NOT using the second drawing graph
      if(listOfSketchPoints.length>0)
        listOfSketchPoints = [listOfSketchPoints[0]]
    //console.log(listOfSketchPoints.length)

  }

  if(listOfSketchPoints.length >0){
    //get value returns an array of the selected JSON VALUES
    //alert(listOfSketchPoints[0])
    listOfSketchPoints[0].xAxis = getXAxis(); //from combobox
    listOfSketchPoints[0].yAxis = getYAxis(); //temporary
    listOfSketchPoints[0].aggrVar = getYAxis();
    listOfSketchPoints[0].maxX = drawGraphWidth; //set for draw case
    listOfSketchPoints[0].maxY = drawGraphHeight;
  }
  if(listOfSketchPoints.length > 1){
    listOfSketchPoints[1].xAxis = getXAxis();
    listOfSketchPoints[1].yAxis = getYAxis(); //temporary
    listOfSketchPoints[1].aggrVar = getYAxis();
    listOfSketchPoints[1].maxX = drawGraphWidth; //set for draw case
    listOfSketchPoints[1].maxY = drawGraphHeight;
    if(listOfSketchPoints[1].points.length == 0)
      listOfSketchPoints = [listOfSketchPoints[0]]; //if second sketch is empty, consider the (1) chart case
  }

	query.sketchPoints=listOfSketchPoints;
    var xAxisType = getAxisType(xAxisSelect0.getValue()[0], "xAxisColumns");
    var yAxisType = getAxisType(yAxisSelect0.getValue()[0], "yAxisColumns");
    //repeat for pairwise
    //console.log("xtype", xAxisType);
    //console.log("ytype", yAxisType);
	//myQuery.rectangles=rectangles;
	//myQuery.circles=circles;
    if(xAxisType == 'Q' && yAxisType == 'Q'){
    	getScatterData(query);
    }
    else{
    	getData(query, xAxisType, yAxisType); //back end call    	
    }
	
	/*
  if(listOfSketchPoints.length > 0){
	  console.log(listOfSketchPoints[0].points);
    if(listOfSketchPoints.length > 1)
      console.log(listOfSketchPoints[1].points);
  }
  */
}









	  //ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

function lineDistance( point1, point2 )
{
  var xs = 0;
  var ys = 0;

  xs = point2.x - point1.x;
  xs = xs * xs;

  ys = point2.y - point1.y;
  ys = ys * ys;

  return Math.sqrt( xs + ys );
}
