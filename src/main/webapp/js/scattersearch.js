/**
 * Created by an5ra on 2/14/2016.
 */
var xStart;
        var yStart;
        var xEnd;
        var yEnd;
        var xlabel; var ylabel; var title;
        var rects = [];
        
function drawScatter(options) {
	console.log("drawscatter");
    var container, brushable, data,  brush;
    rects = [];
    //Find an element to render the chart
    if ($(options['renderTo']) != undefined) {
        container = $(options['renderTo']);
    }
    else {
        console.log("Tried to render chart to an unknown or missing element!");
        return;
    }
 
    if ('brush' in options) {
        brushable = options['brush'];
    }
    else
        brushable = false;
 
    if ('data' in options) {
        data = options['data'];
    }
    else
        data = {};
 
    title = options['title'];
    xlabel = options['x-label'];
    ylabel = options['y-label'];
 
    // finding actual dimensions of div
    var heightOfDiv = container.innerHeight();
    var widthOfDiv = container.innerWidth();
 
    // finding relative point radius
    var radius = 4;
 
    // Setting margins as percentages
    var topMargin = heightOfDiv * 0.14;
    var bottomMargin = heightOfDiv * 0.1;
    var rightMargin = widthOfDiv * 0.08;
    var leftMargin = widthOfDiv * 0.09;
 
    var margin = {
            top: Math.ceil(topMargin),
            right: Math.ceil(rightMargin),
            bottom: Math.ceil(bottomMargin),
            left: Math.ceil(leftMargin)
        },
        width = widthOfDiv - margin.left - margin.right,
        height = heightOfDiv - margin.top - margin.bottom;
 
 
    //function to get x Value from data
    var xVals = function (d) {
        return d['x'];
    }
 
    //function to get y Value from data
    var yVals = function (d) {
        return d['y'];
    }
    var domain = getXYmaxmin();
    //setting y-scale to fit in the svg window
    var yScale = d3.scale.linear()
    	.domain([0,domain[3]])
        //.domain([0, d3.max(data, yVals)])
        .range([height, 0]);
 
    //setting x-scale to fit in the svg window
    var xScale = d3.scale.linear()
    	.domain([0,domain[1]])
        //.domain([0, d3.max(data, xVals)])
        .range([0, width]);
 
    // AXES:
    // to change tick-sizes: .ticksize(inner, outer) where inner are the normal ticks and outer are the end ticks
    // here we are keeping the inner ticks to the default value of 6 and the outer to negative extremes to form a box
 
    var xAxis = d3.svg.axis()
        .scale(xScale)
        .orient("bottom");
    //.tickSize(6, -height);
 
 
    var yAxis = d3.svg.axis()
        .scale(yScale)
        .orient("left");
    //.tickSize(6, -width);
    
    var svg = d3.select(options['renderTo'])
    .attr("width", width + margin.left + margin.right)
    .attr("height", height + margin.top + margin.bottom)
    .append("g") //group that will house the plot
    .attr("transform", "translate(" + margin.left + "," + margin.top + ")"); //to center the g in the svg

    var i = 0;
    // declaring brush
    var brushes = [];
    var newBrush = function(){
    	console.log("in newBrush")
	    var brush = d3.svg.brush()
	        .x(d3.scale.identity().domain([0, width]))
	        .y(d3.scale.identity().domain([0, height]))
//	        .attr("id", "id"+i)
	        .on("brush", brushed)
	    	.on("brushend",brushend);
    	brushes.push(brush);
//    	brushes.push({id: brushes.length, brush: brush});
    	if (brushable){
             svg.append("g")
                .attr("class", "brush")
                .call(brush);
//            newBrush();
        }
    	i= i+1;
    }
    newBrush();
    
    function brushend(){
    	console.log("brushend")
    	console.log(brushes)
//    	var extent = brushes[brushes.length-1].extent();
    	var extent = brushes[brushes.length-1];
    	extent = extent.extent();
//        var extent = brush.extent();
        xStart = xScale.invert(extent[0][0]);
        yStart = yScale.invert(extent[0][1]);
        xEnd = xScale.invert(extent[1][0]);
        yEnd = yScale.invert(extent[1][1]);
        xStart = Math.round(xStart * 100) / 100;
        xEnd = Math.round(xEnd * 100) / 100;
        yStart = Math.round(yStart * 100) / 100;
        yEnd = Math.round(yEnd * 100) / 100;
        var rectangle = new Rectangle(xStart,xEnd,yEnd,yStart);
        rects.push(rectangle);
        console.log(rects);
    	newBrush();
    }
    
    
 // adding the brush
    
           // .call(brush.event);

    function brushed() {
//    	console.log("brushed");
    	
       //what is this for? $("#brush-region").text(("(" + xStart + ", " + yStart + ") to (" + xEnd + ", " + yEnd + ")"));
//       	brush();
    }
    
    /**
     * MAIN SCATTERPLOT
     */
    var scatterPlot = svg.append('g')
        .selectAll('circle').data(data)
        .enter().append('circle')
        .style('fill', "darkblue")
        .attr('r', 0)
        .attr('cx', function (d) {
            return xScale(d['x'])
        })
        .attr('cy', function (d) {
            return yScale(d['y'])
        })
        ;
 
    // load the points animatedly
    // reference url for ease: https://github.com/mbostock/d3/wiki/Transitions#d3_ease
    scatterPlot.transition()
        .attr('r', radius)
        .duration(1000)
        .ease('elastic');
 
 
    // adding the axes
    svg.append("g")
        .attr("class", "y axis")
        .call(yAxis);
 
    svg.append("g")
        .attr("class", "x axis")
        .attr("transform", "translate(0," + height + ")")
        .call(xAxis);
 
    // adding the axes labels
    svg.append("text")
        .attr("text-anchor", "middle")  // this makes it easy to centre the text as the transform is applied to the anchor
        .attr("transform", "translate(" + (-leftMargin / 2) + "," + (height / 2) + ")rotate(-90)")  // text is drawn off the screen top left, move down and out and rotate
        .text(ylabel);
 
    svg.append("text")
        .attr("text-anchor", "middle")  // this makes it easy to centre the text as the transform is applied to the anchor
        .attr("transform", "translate(" + (width / 2) + "," + (height + 6 + (bottomMargin / 2)) + ")")  // centre below axis
        .text(xlabel);
 
    // title
    svg.append("text")
        .attr("class", "chart-title")
        .attr("text-anchor", "middle")  // this makes it easy to centre the text as the transform is applied to the anchor
        .attr("transform", "translate(" + (width / 2) + "," + ( -20 + ")"))  // text is drawn off the screen top left, move down and out and rotate
        .text(title);
 
    
 
}

$("#search").click(function(){
	var numresults = $('#num-results input').val()
	var x = getXAxis();
	var y = getYAxis();
	var z = getCategory();
	createQuery(rects,x, y, z, numresults);

});

function createQuery(rectangles ,xlabel, ylabel, title, num){
	

	var query = new ScatterPlotQuery(rectangles,xlabel,ylabel,title,num,"");
//	var query = new ScatterPlotQuery(xStart, xEnd, yStart,yEnd,"GPA","FamilyIncome","Major",num);

	getScatterPlot(query);
}

/*
function initialquerycall(query)
{
  var query = new ScatterPlotQuery(0,0,0,0, "","",50);
  console.log("sendquery", query)
  $.get('/getscatterplot', JSON.stringify(query), showMainChart, 'json')
  .fail(function(){
    console.log("Failed")
    alert('Request failed: /getscatterplot');
  });
}

function showMainChart(data){
	drawRandomChart(data);
}
*/
function returnResults(data){
	console.log("return result",data);
	 var charts_per_row = 2;
	  var current_row;
	  $("#views_table").empty
	  for(index = 0; index < Object.keys(data["outputCharts"]).length; index++){
	    if(index % charts_per_row == 0){ //create a new table row element per every 2
	      current_row = $('<tr>')
	      $("#views_table").append(current_row)
	    }
	    //$("#views").append("<div ondragstart = \"drag(this,event)\" draggable = \"true\" class='mod' id='view-" + index + "'></div>")
	    var current_td = $('<td>') //new column element
	    //current_td.attr('id', "table_view"+index)
	    current_row.append(current_td)
	    current_td.append("<div class=\"graph\" ondragstart = \"drag(this,event)\" draggable = \"true\" class='mod' id='table_view" + index + "'></div>")
	    if(index % charts_per_row == 1 && $("#blankChart1").is(':visible')){     //hightlight for second column
	      current_td.addClass("secondDrawColumn")
	    }
	    addScatterGraph(data, index, "", "")
	  }

	  //Scroll to graphs automatically! (After stuff loads)
	  $('html, body').animate({
	    scrollTop: $("#views").offset().top
	  }, 1600);
	
	console.log("nice!")
	console.log(data)
}

function addScatterGraph(data, index, xUnit, yUnit){
  var windowWidth = $(window).width()
  var graphHeight = Math.round(windowWidth/10); //1450/10 = 145
  var graphWidth = graphHeight*2;
  if(windowWidth >1240 && windowWidth <= 1452){
    graphHeight = Math.round(windowWidth/12)
    graphWidth = graphHeight*2;
  }
  if(windowWidth <= 1240){
    graphHeight = Math.round(windowWidth/15)
    graphWidth = graphHeight*2;
  }
  var vegaInput = createScatterGraph(data["outputCharts"][index], graphWidth, graphHeight, "", "");
  vg.parse.spec(vegaInput, function(chart) {
    chart({
      el: "#table_view"+index,
      hover: false
    }).update();
  });
}

function getXYmaxmin(){
	console.log(currentFormData)
	var columnx = getXAxis();
	var columny = getYAxis();
	var xmax = currentFormData["xAxisColumns"][columnx]["max"];
	var ymax = currentFormData["yAxisColumns"][columny]["max"];
	var xmin = currentFormData["xAxisColumns"][columnx]["min"];
	var ymin = currentFormData["xAxisColumns"][columnx]["min"];
	return [xmin,xmax, ymin,ymax]
}

function drawRandomChart() {
	//getXYminmax();
    var options = {
        'renderTo': "#scatterplot",
        //'data': [{'x': 4, 'y': 5}, {'x': 3, 'y': 5}, {'x': 4, 'y': 2}, {'x': 4, 'y': 7},{'x': 0, 'y': 8},{'x': 2, 'y': 2},{'x': 1, 'y': 4},{'x': 6, 'y': 2},{'x': 2, 'y': 1},{'x': 9, 'y': 3}],
        'data': [],
        //'x-label': "x-axis",
        //'y-label': "y-axis",
        'brush': true
        //'title': ""
    };
    drawScatter(options);
 
}