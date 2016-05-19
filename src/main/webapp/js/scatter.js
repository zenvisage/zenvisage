 var w = 490,
          h = 210,
          margin = { top: 40, right: 20, bottom: 20, left: 40 },
          radius = 6;

      var svg = d3.select("#scatterplot").append("svg").attr({
    width: w,
    height: h
  });

  var dataset = [
  
  ];

  // We're passing in a function in d3.max to tell it what we're maxing (x value)
  var xScale = d3.scale.linear()
      .domain([0, 490])
      .range([margin.left, w - margin.right]);  // Set margins for x specific

  // We're passing in a function in d3.max to tell it what we're maxing (y value)
  var yScale = d3.scale.linear()
      .domain([0, 209])
      .range([h - margin.bottom,margin.top]);  // Set margins for y specific

  // Add a X and Y Axis (Note: orient means the direction that ticks go, not position)
  var xAxis = d3.svg.axis().scale(xScale).orient("bottom");
  var yAxis = d3.svg.axis().scale(yScale).orient("left");

  var circleAttrs = {
      cx: function(d) { return xScale(d.x); },
      cy: function(d) { return yScale(d.y); },
      r: radius
  };


  // Adds X-Axis as a 'g' element
  svg.append("g").attr({
"class": "axis",  // Give class so we can style it
transform: "translate(" + [0, h-margin.bottom] + ")"  // Translate just moves it down into position (or will be on top)
  }).call(xAxis);  // Call the xAxis function on the group

  // Adds Y-Axis as a 'g' element
  svg.append("g").attr({
"class": "axis",
transform: "translate(" + [margin.left, 0] + ")"
  }).call(yAxis);  // Call the yAxis function on the group

  svg.selectAll("circle")
  .data(dataset)
  .enter()
  .append("circle")
  .attr(circleAttrs)  // Get attributes from circleAttrs var
  .on("mouseover", handleMouseOver)
  .on("mouseout", handleMouseOut);

  // On Click, we want to add data to the array and chart
  svg.on("click", function() {
  var coords = d3.mouse(this);
  console.log("coords",coords);
  // Normally we go from data to pixels, but here we're doing pixels to data
  var newData= {
    x: Math.round( xScale.invert(coords[0])),  // Takes the pixel number to convert to number
    y: Math.round( yScale.invert(coords[1]))
  };

  dataset.push(newData);   // Push data to our array. Dataset has the data
  svg.selectAll("circle")  // For new circle, go through the update process
    .data(dataset)
    .enter()
    .append("circle")
    .attr(circleAttrs)  // Get attributes from circleAttrs var
    .on("mouseover", handleMouseOver)
    .on("mouseout", handleMouseOut);
    })

  // Create Event Handlers for mouse
  function handleMouseOver(d, i) {  // Add interactivity

    // Use D3 to select element, change color and size
    d3.select(this).attr({
      fill: "orange",
      r: radius * 2
    });

    // Specify where to put label of text
    svg.append("text").attr({
       id: "t" + d.x + "-" + d.y + "-" + i,  // Create an id for text so we can select it later for removing on mouseout
        x: function() { return xScale(d.x) - 30; },
        y: function() { return yScale(d.y) - 15; }
    })
    .text(function() {
      return [d.x, d.y];  // Value of the text
        });
      }

  function handleMouseOut(d, i) {
        // Use D3 to select element, change color back to normal
    d3.select(this).attr({
      fill: "black",
      r: radius
    });

    // Select text by id and then remove
    d3.select("#t" + d.x + "-" + d.y + "-" + i).remove();  // Remove text location
  }

