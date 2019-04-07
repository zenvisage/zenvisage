var svg = d3.select("#colorbar").append("svg").attr("transform",  "translate(30)")
    .attr("width", 30)
    .attr("height", 180);
var gradient = svg.append("defs")
  .append("linearGradient")
    .attr("id", "gradient")
    .attr("x1", "0%")
    .attr("y1", "0%")
    .attr("x2", "0%")
    .attr("y2", "100%")
    .attr("spreadMethod", "pad");
gradient.append("stop")
    .attr("offset", "0%")
    .attr("stop-color", "darkblue")
    .attr("stop-opacity", 1);
gradient.append("stop")
    .attr("offset", "100%")
    .attr("stop-color", "lightblue")
    .attr("stop-opacity", 1);
svg.append("rect")
    .attr("width", 10)
    .attr("height", 160)
    .style("fill", "url(#gradient)").attr("transform","translate(20,10)");

var yScale = d3.scaleLinear()
    .range([0, 158])
    .domain([3,0]);

/*var yAxis = d3.svg.axis()
    	.orient("left")
    	.ticks(5)  //Set rough # of ticks
    	  //.tickFormat(formatPercent)
    	.scale(xScale);*/

svg.append("g")
    .attr("class", "axis") //Assign "axis" class
    .attr("transform","translate(20,10)")
    .call(d3.axisLeft(yScale).ticks(5).tickSize(0));
