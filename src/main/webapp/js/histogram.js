var dots = visualisation.createSVGPoint();
var chart = document.getElementById("visualisation");
var listOfBarPoints =[];
var histogram = false;
var histogram_drag = false;


function barConfig(){
	while (chart.firstChild) {
		chart.removeChild(chart.firstChild);
	}
	numOfBar = existingTrends[ExTrendindex]["outputCharts"][0].xData.length; //numOfBar is a global
	listOfBarPoints =[];
	for(var i=0; i< numOfBar; i++){
		listOfBarPoints.push([i, 210]);
	}

	for(var i = 0; i< numOfBar;i++){
		var name = "svg";
		name = name.concat(i.toString());

	var obj= document.createElementNS("http://www.w3.org/2000/svg", "rect");
	obj.id = name;
	obj.setAttribute("style", "stroke-width:0.5;stroke:rgb(0,0,0);fill:rgb(22,149,163)");
	chart.appendChild(obj);

	}
}

function getxy(e){
	 loc = cursorPoint(e);
	 if(histogram_drag){
		 var offset = $('#visualisation').offset();
		 var ypos = e.offsetY;
		 var xpos =  e.offsetX;
		 var divide = drawGraphWidth/numOfBar;
		 for(var i= 0; i< numOfBar; i++){
			 if(((xpos)>=divide*i) && ((xpos)< divide*(i+1))){
				 var name = "svg";
				name = name.concat(i.toString());
				name = document.getElementById(name);
				listOfBarPoints[i][1] = ypos;
				 name.setAttribute("y", ypos);
				 name.setAttribute("height",drawGraphHeight -ypos);
				 name.setAttribute("x",divide*i);
				 name.setAttribute("width",divide);
			 }
		}
	 }
}

function initChart(){
	chart.addEventListener("mousemove", getxy, false);
	//chart.addEventListener("click", clickCheck, false); //what is this doing?
	chart.addEventListener("mousedown", histrogram_mousedown, false);
	$('#visualisation').mouseleave(function(e){
		if(histogram_drag){
			histogram_drag = false;
			isDrawing = false;
			onSubmit();
		}
	});
}

function histrogram_mousedown(e){
	if(histogram_drag){
		histogram_drag = false;
		onSubmit();
	}
	else{
		histogram_drag = true;
	}
}
initChart();

//show bars after drag and drop
function drawBarsAfterDragDrop(){
	while (chart.firstChild) {
		chart.removeChild(chart.firstChild);
	}
	numOfBar = existingTrends[ExTrendindex]["outputCharts"][0].xData.length; //numOfBar is a global
	updateChart(chart0Information)
	var chartdata = chart0Information['chartData']["yData"]
	console.log("YDATA",chartdata)
	console.log("chart0", chart0Information)
	//alert(JSON.stringify(chart0Information))
	changeScaleMainChart(chart0Information, "histogram");
//	for(var i=0; i< numOfBar; i++){
//		listOfBarPoints.push([i, 210]);
//	}
//
	for(var i = 0; i< numOfBar;i++){
		var name = "svg";
		name = name.concat(i.toString());

	var obj= document.createElementNS("http://www.w3.org/2000/svg", "rect");
	obj.id = name;
	obj.setAttribute("style", "stroke-width:0.5;stroke:rgb(0,0,0);fill:rgb(22,149,163)");
	var height = (chartdata[i]/chart0Information['max_Y'])*drawGraphHeight
	obj.setAttribute("y", drawGraphHeight - height);
	obj.setAttribute("height", height);
	var divide = drawGraphWidth/numOfBar;
	obj.setAttribute("x",divide*i);
	obj.setAttribute("width",divide);
	chart.appendChild(obj);

	}
}
