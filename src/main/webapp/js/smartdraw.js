//This file is used for modify
// would like to remove these if possible.. or make them more descriptive
var clickmodify = false;
var clickmodify1 = false; // clickmodify for second chart
var list = [[]]; //Javascript list of points for chart 0
var list1 = [[]]; //javasript list of points for draw chart 1

var Drag = false;
var i = 0;
var myPath = document.getElementById("myPath");
var sm_scale = false;
var myPath1 = document.getElementById("myPath1");

//var y = drawGraphHeight/2;
var dx = Math.round(drawGraphWidth/30);
var x = 0;

initialize_smartdraw();

$("#modify-1").click(function(){
	drawInitialPath(list, myPath);
	chart0Information['svgLayerObject'].css("display","block");
	chart0Information['tools_sketchObject'].css("display","none");
	chart0Information['dragAndDropObject'].css("display","none");
	chart0Information['visualisationObject'].css("display","none");
	clickmodify = true;
	histogram = false;
});

$("#modify-2").click(function(){
	clickmodify = true;
	histogram = false;
	chart1Information['svgLayerObject'].css("display","block");
	chart1Information['tools_sketchObject'].css("display","none");
	chart1Information['dragAndDropObject'].css("display","none");
	chart1Information['visualisationObject'].css("display","none");
	drawInitialPath(list1, myPath1);
});


//get mouse position relative to the target element
function cursorPoint(evt){
	var pt = document.getElementById("mySvg").createSVGPoint();
	pt.x = evt.offsetX;
	pt.y = evt.offsetY;
	return pt
}

function getClientxy(e){
	 var loc = cursorPoint(e);
	 if( sm_scale ){
		 i = Math.round(loc.x/scale); //what is scale doing??
	 }
	 else{
		 var ctrldist = canvas.width/30;
		 i = Math.round(loc.x/ctrldist);
	 }
	 var str = "point";
	var myPoint = str.concat(i.toString());
	if(Drag){
		var myCir = document.getElementById(myPoint);
		if(myCir != null ){
			myCir.setAttribute("cx", loc.x);
			myCir.setAttribute("cy", loc.y);
			myCir.setAttribute("r", 2);	//so we can see the point now!
			nx = loc.x;
			ny = loc.y;
			drawPath();
		}
	}
}

//For draw graph 1
function getClientxy1(e){
	 //Use offsetX and offsetY instead of cursor position. (Offset from start of current canvas, instead of from first canvas)
	 var loc = cursorPoint(e);
	 if( sm_scale ){
		 i = Math.round(loc.x/scale);
	 }
	 else{
		 var ctrldist = canvas.width/30;
		 i = Math.round(loc.x/ctrldist);
	 }
	 var str = "point" + i;
	str = str + "-1"; //suffix is -1 to denote the point is on draw graph 1
	//var myPointSec = str.concat((i+1).toString());
	if(Drag){
	 var myCir = document.getElementById(str);
	 //myCirSec = document.getElementById(myPointSec);
	 if(myCir != null ){
			myCir.setAttribute("cx", loc.x);
			myCir.setAttribute("cy", loc.y);
			myCir.setAttribute("r", 2); 	//so we can see the point now!
			nx1 = loc.x;
			ny1 = loc.y;
			drawPath1();
	 }
	}
}

function initialize_smartdraw(){
	var mySvg = document.getElementById("mySvg")
	mySvg.addEventListener("mousemove", getClientxy, false);
	mySvg.addEventListener("click", clickCheck, false);
	$('#mySvg').mouseleave(function(e){
		if(Drag)
		{
			onSubmit();
		}
		Drag = false;
	});

	var mySvg1 = document.getElementById("mySvg1");
	mySvg1.addEventListener("mousemove", getClientxy1, false);
	mySvg1.addEventListener("click", clickCheck, false);
	$('#mySvg1').mouseleave(function(e){
		if(Drag)
		{
			onSubmit();
		}
		Drag = false;
	});
}

function clickCheck(){
	if( Drag ){
		Drag = false;
		onSubmit();
	}
	else
		Drag = true;
}

function updateDefaultModify(){
	y = drawGraphHeight/2;
	dx = Math.floor(drawGraphWidth/30)
	list = []
	x = 0;
	for(var i = 0; i <30; i++){
		list.push([x, y])
		x += dx;
	}
	x = 0;
	list1 = []
	for(var i = 0; i <30; i++){
		list1.push([x, y])
		x += dx;
	}
}
var nx =list[0][0], ny=list[0][1];
var nx1 = list1[0][0], ny1=list1[0][1];

function drawPath(){
	var front,sub='',laststr='', flag= true, flag2=true;
	if(i< list.length){
	list[i][0]= nx;
	list[i][1]= ny;
	}
	for(z=0; z<list.length; z++){
		if(list[z].length!= 0){
			if(flag){
				var listOfString = list[z].map(String);
				front = listOfString.join();
				front = front.replace(",", " ");
				strm = "M ";
				front = strm.concat(front);
				front = front.concat(" L");
				flag = false;
				flag2 =false;
			}
			else{
				var listOfString = list[z].map(String);
				sub = listOfString.join();
				sub = sub.replace(",", " ");
				empty = " ";
				sub = empty.concat(sub);
				sub = laststr.concat(sub);
				laststr = sub;
				flag2 = true;
			}
		}
	}
	var whole = front.concat(sub);
	if( flag2 )
	{
		myPath.setAttribute('d', whole);
	}
}

function drawPath1(){
	var front,sub='',laststr='',flag= true,flag2=true;
	if(i< list1.length){
	list1[i][0]= nx1;
	list1[i][1]= ny1;
	}
	for(z=0; z<list1.length; z++){
		if(list1[z].length!= 0){
			if(flag){
				var listOfString = list1[z].map(String);
				front = listOfString.join();
				front = front.replace(",", " ");
				strm = "M ";
				front = strm.concat(front);
				front = front.concat(" L");
				flag = false;
				flag2 =false;
			}
			else{
				var listOfString = list1[z].map(String);
				sub = listOfString.join();
				sub = sub.replace(",", " ");
				empty = " ";
				sub = empty.concat(sub);
				sub = laststr.concat(sub);
				laststr = sub;
				flag2 = true;
			}
		}
	}
	var whole = front.concat(sub);
	if(flag2)
		myPath1.setAttribute('d', whole);
}

function drawInitialPath(list, myPath){
	var front,sub='',laststr='', flag= true, flag2=true;
	for(z=0; z<list.length; z++){
		if(list[z].length!= 0){
			if(flag){
				var listOfString = list[z].map(String);
				front = listOfString.join();
				front = front.replace(",", " ");
				strm = "M ";
				front = strm.concat(front);
				front = front.concat(" L");
				flag = false;
				flag2 =false;
			}
			else{
				var listOfString = list[z].map(String);
				sub = listOfString.join();
				sub = sub.replace(",", " ");
				empty = " ";
				sub = empty.concat(sub);
				sub = laststr.concat(sub);
				laststr = sub;
				flag2 = true;
			}
		}
	}
	var whole = front.concat(sub);
	if(flag2)
		myPath.setAttribute('d', whole);
}

/* commented out 1/23/16
if(list.length ==0){
	for(var i = 0; i <30; i++){
		list.push([x, y])
		x += dx;
	}
}
x = 0;

if(list1.length ==0){
	for(var i = 0; i <30; i++){
		list1.push([x, y])
		x += dx;
	}	//list1 = [[9,100],[27, 100],[51, 100],[71, 100],[92, 100],[112, 100],[119, 100],[152, 100],[172, 100],[194, 100],[214, 100],[234, 100],[253, 100],[276, 100],[296, 100],[317, 100],[337, 100],[357, 100],[379, 100],[390, 100],[395,100]];
}
x = 0;
*/
