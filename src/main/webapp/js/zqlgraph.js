
var simulation;

function createZQLGraph( callback ){

  var svg = d3.select("#graph-div").append("svg")
      .attr("width", "100%")
      .attr("height", "100%");

  svg.append("defs").selectAll("marker")
      .data(["end"])
    .enter().append("marker")
      .attr("id", function(d) { return d; })
      .attr("viewBox", "0 -5 10 10")
      .attr("refX", 25)
      .attr("refY", 0)
      .attr("markerWidth", 6)
      .attr("markerHeight", 6)
      .attr("orient", "auto")
    .append("path")
      .attr("d", "M0,-5L10,0L0,5")
      .style("stroke", "black")
      .style("opacity", "1");

  var link = svg.selectAll(".link"),
      node = svg.selectAll(".node");

  simulation = d3.forceSimulation()
    .force("link", d3.forceLink().id( function(d) { return d.index }))
    .force("charge", d3.forceManyBody().strength(-500))
    .force("center", d3.forceCenter( 500, 100));//width / 2, height / 2));

  var div = d3.select("body").append("div")
      .attr("class", "tooltip")
      .style("opacity", 0);

  d3.json("css/temp_data.json", function(error, graph) {
    if (error) throw error;

    var linkEnter = link.data(graph.links)
        .enter().append("line")
        .style("marker-end",  "url(#end)") // Modified line
        .attr("class", "link");

    link = linkEnter
          .merge(link);

    var nodes = node.data(graph.nodes)
        .enter().append("g")
        .attr("class", function (d) {
          if (d.type === "process") {
             return "process node";
          } else {
             return "zql node";
          }
        })
        .call(d3.drag()
                .on("start", dragstart)
                .on("drag", dragged)
                .on("end", dragended))
        .on("dblclick", function(d){
          if (d.type === "zql")
          {
            callback(d);
          }
        })
        .on("mouseover", function(d, i) {
          if (d.type === "process")
          {
            div.transition()
              .duration(200)
              .style("opacity", .9);
            div.html(d.process)
              .style("left", (d3.event.pageX + 15) + "px")
              .style("top", (d3.event.pageY - 15) + "px");
          }
          else
          {
            div.transition()
              .duration(200)
              .style("opacity", .9);
              if ( d.constraint ){
                div.html(
                  d.xval +
                  "<br/>"  + d.yval +
                  "<br/>"  + d.zval +
                  "<br/>"  + d.constraint
                )
              }
              else
              {
                div.html(
                  d.xval +
                  "<br/>"  + d.yval +
                  "<br/>"  + d.zval
                )
              }
            div.style("left", (d3.event.pageX + 15) + "px")
            .style("top", (d3.event.pageY - 15) + "px");
          }
        })
        .on("mouseout", function(d, i) {
            div.transition()
           .duration(1000)
           .style("opacity", 0);
         });


    circles = nodes.append("circle").attr("r", 12);
    texts = nodes.append("text")
      .attr("text-anchor", "middle")
      .text(function(d) {
        return d.name;
      });

    simulation
        .nodes(graph.nodes)
        .on("tick", tick);

    simulation
        .force("link")
        .links(graph.links);

  });
}

function tick() {
  d3.select("#graph-div").selectAll(".link").attr("x1", function(d) { return d.source.x; })
      .attr("y1", function(d) { return d.source.y; })
      .attr("x2", function(d) { return d.target.x; })
      .attr("y2", function(d) { return d.target.y; });

  circles.attr("cx", function(d) { return d.x; })
      .attr("cy", function(d) { return d.y; });

  texts.attr("dx", function(d) { return d.x + 1; })
      .attr("dy", function(d) { return d.y + 5; });
}

function dblclick(d) {
  console.log("double clicked")
  d3.select(this).classed("fixed", d.fixed = false);
}

function dragstart(d) {
    if (!d3.event.active) simulation.alphaTarget(0.3).restart();
    d.fx = d.x;
    d.fy = d.y;
}

function dragged(d) {
    d.fx = d3.event.x;
    d.fy = d3.event.y;
}

function dragended(d) {
    if (!d3.event.active) simulation.alphaTarget(0);
}


