//stores dygraphs
var userQueryDygraphs = {};
var representativeDygraphs = {};
var outlierDygraphs = {};

var userQueryDygraphsNew = {};
var representativeDygraphsNew = {};
var outlierDygraphsNew = {};
var globCount = 0;

function formatRanges( classData ){
  var formattedRanges = []
  for (var i = 0; i < classData.length; i++){
    var formattedRange = []
    var attributes = classData[i].attributes.replace('[', '').replace(']', '').split(",");
    var ranges = classData[i].ranges.split(",")
    for (var j = 0; j < attributes.length; j++){
      var vals = ranges[j].replace('[', '').replace(']', '').split(" ")
      formattedRange.push(vals[0].trim() + " < " + attributes[j].trim() + " <= " + vals[1].trim())
    }
    formattedRanges.push(formattedRange)
  }
  return formattedRanges
}


// function formatRangeForTooltip( classData ){

//     var attributes = classData[i].attributes.replace('[', '').replace(']', '').split(",");
//     var ranges = classData[i].ranges.split(",")
//     for (var j = 0; j < attributes.length; j++){
//       var vals = ranges[j].replace('[', '').replace(']', '').split(" ")
//       formattedRange.push(vals[0].trim() + " < " + attributes[j].trim() + " <= " + vals[1].trim())
//     }
//     formattedRanges.push(formattedRange)
//   }
//   return formattedRanges
// }


//displays user results

function displayUserQueryResultsHelper( userQueryResults, flipY, includeSketch = true )
{
  clearUserQueryResultsTable();
  var resultsDiv = $("#results-table");
  var current = 0;
  var connectSeparatedPoints = true;
  var pointSize = 1.0;
  var drawPoints = false;
  var strokeWidth = 1.0;
  if ( getScatterplotOption() )
  {
    connectSeparatedPoints = false;
    pointSize = 1;
    drawPoints = true;
    strokeWidth = 0;
  }
  for (var count = 0; count < userQueryResults.length; count++)
  {
    if (count % 2 == 0)
    {
      var newRow = $("#results-table").append("<tr id=\"row-" + count.toString() + "\"></tr>")
      current = count;
    }
    $("#row-" + current.toString()).append("<td><div class=\"undraggable-user-query-results undraggable-graph\" data-graph-type=\"userQuery\" id=\"undraggable-result-" + count.toString() + "\"><div class=\"user-query-results draggable-graph\" data-graph-type=\"userQuery\" id=\"result-" + count.toString() + "\"></div></div></td>");
  }
  var skipped=0;
  for (var count = 0; count < userQueryResults.length; count++)
  {
    var xData = userQueryResults[count]["xData"];
    var yData = userQueryResults[count]["yData"];
    var errorData = userQueryResults[count]["error"];
    var xlabel = replaceAll(userQueryResults[count]["xType"], "'", "");
    var ylabel = replaceAll(userQueryResults[count]["yType"], "'", "");
    var zAttribute = replaceAll(userQueryResults[count]["zType"], "'", ""); // city
    var zValueFull = userQueryResults[count]["title"]
    var zValue = replaceAll(zValueFull.substring(0, 25), "'", ""); // the actual city value, like NY

    if (zAttribute=="dynamic_class" && zValue[0]=="-"){
      skipped+=1;

    }
    else{

      var newCount=count-skipped;
      var xRange = userQueryResults[count]["xRange"];
      //var similarityDistance = userQueryResults[count]["distance"];
      var similarityDistance = userQueryResults[count]["normalizedDistance"];
      if (count <userQueryResults.length-1){
        var deltaSimilarityDistance = Math.abs(userQueryResults[count+1]["normalizedDistance"]-similarityDistance);
      }else{
        var deltaSimilarityDistance = 0;
      }
      var xmin = Math.min.apply(Math, xData);
      var xmax = Math.max.apply(Math, xData);
      var ymin = Math.min.apply(Math, yData);
      var ymax = Math.max.apply(Math, yData);

      if (xRange == null)
      {
        xRange = [xmin,xmax]
      }

      var considerRange = userQueryResults[count]["considerRange"];
      //var data = combineTwoArrays(xData, yData, sketchpad.rawData_);

      var valueRange = [ymin, ymax];

      var data = [];
      var arrayLength = xData.length;

      if(errorData != null){
        for (var i = 0; i < arrayLength; i++ ) {
          data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]),"errorval": Number(errorData[i]) } );
        }
      }
      else{
        for (var i = 0; i < arrayLength; i++ ) {
          data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
        }
      }
      var data2 = sketchpadData;
      userQueryDygraphsNew["result-" + newCount.toString()] = {"data": data, "xType": xlabel, "yType": ylabel, "zType": zAttribute}

      //top right bottom left
      var m = [0, 0, 20, 20]; // margins
      var width = 250//200// - m[1] - m[3]; // width
      var height = 105//85// - m[0] - m[2]; // height

      // X scale will fit all values from data[] within pixels 0-w
      var x = d3.scaleLinear().range([20, width-20]);

      if(getflipY()){
          var y = d3.scaleLinear().range([20, height-20]);
      }
      else{
          var y = d3.scaleLinear().range([height-20, 20]);
      }

      x.domain([xmin, xmax]);
      y.domain([ymin, ymax]);
      // x.domain([0, d3.max(data, function(d) {return Math.max(d.xval); })]);
      // y.domain([0, d3.max(data, function(d) {return Math.max(d.yval); })]);

      var valueline = d3.line()
      .x(function(d) {
        return x(d.xval);
      })
      .y(function(d) {
        return y(d.yval);
      });

      // Add an SVG element with the desired dimensions and margin.
      var graph = d3.select("#result-" + newCount.toString())
            .append("svg")
            .attr("viewBox","0 0 " + width.toString()+" "+ (height+15).toString())
            .attr("width", width)// + m[1] + m[3])
            .attr("height", height)// + m[0] + m[2])
            .attr("id","resultsvg-" + newCount.toString())
            .attr("xmlns","http://www.w3.org/2000/svg")
            .attr("version","1.1")
            //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");


      graph.append("defs").append("clipPath")
          .attr("id", "clip-" + newCount.toString())
          .append("rect")
          .attr("width", 220)
          .attr("height", 65)
          .attr("transform", "translate(20,20)");


      //xmin xmax ymin ymax
      var newRanges = getEvaluatingRange( xmin, xmax, xRange )
      //return [first_left, first_right, second_left, second_right]

      graph.append("rect")
          //.attr("width", Math.abs( (xRange[0]-xmin)/(xmax-xmin)*(width-40) ) )
          .attr("width", Math.abs( (newRanges[1] - newRanges[0])/(xmax-xmin)*(width-40) ) )
          .attr("height", height-40)
          .attr("transform", "translate(20,20)")
          .attr("fill", "grey");

      // if negative, newLoc no longer works
      var newLoc = Math.abs((newRanges[2]-xmin)/(xmax-xmin)*(width-40))+20
      graph.append("rect")
          //.attr("width", Math.abs( (xmax-xRange[1])/(xmax-xmin)*(width-40) ) )
          .attr("width", Math.abs( (newRanges[3] - newRanges[2])/(xmax-xmin)*(width-40) ) )
          .attr("height", height-40)
          .attr("transform", "translate(" + newLoc.toString() + ",20)")
          .attr("fill", "grey");

      var trans = height-20


      if (getSelectedDataset()==="real_estate")
      {
        if(getSelectedXAxis()==="month")
        {
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call( d3.axisBottom(x).ticks(4).tickFormat(function (d) {
                var mapper = {
                  "50": "02/2008",
                  "100": "04/2012",
                }
                return mapper[ d.toString() ]
              }));
        }
        else if(getSelectedXAxis()==="quarter")
        {
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
                var mapper = {
                  "10": "Q2/2006",
                  "20": "Q4/2008",
                  "30": "Q2/2011",
                  "40": "Q4/2013",
                }
                return mapper[ d.toString() ]
              }));
        }
        else if(getSelectedXAxis()==="year")
        {
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
                var mapper = {
                  "1": "2004",
                  "2": "2005",
                  "3": "2006",
                  "4": "2007",
                  "5": "2008",
                  "6": "2009",
                  "7": "2010",
                  "8": "2011",
                  "9": "2012",
                  "10": "2013",
                  "11": "2014",
                  "12": "2015",
                }
                return mapper[ d.toString() ]
              }));
        }
        else{
          graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call(d3.axisBottom(x).ticks(5, "s"));

        }
      }
      else{
        if(getSelectedXAxis()==="timestep")
        {
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
                var mapper = {
                  "0": '0hr',
                  "1": '6hr',
                  "2": '12hr',
                  "3": '18hr',
                  "4": '24hr',
                  "5": '36hr',
                  "6": '48hr',
                  "7": '4d',
                  "8": '7d',
                  "9": '9d',
                  "10":'14d'
                }
                return mapper[ d.toString() ]
              }));
        }
        else if(getSelectedXAxis()==="year" || getSelectedXAxis()==="date" || getSelectedXAxis()==="day"){
            graph.append("g")
              .attr("class", "axis axis--x")
              .attr("transform", "translate(0," + trans + ")")
              .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d")));
            }  // for formatting all year x axis ticks except hardcoded real estate dataset
            else{
              graph.append("g")
              .attr("class", "axis axis--x")
              .attr("transform", "translate(0," + trans + ")")
              .call(d3.axisBottom(x).ticks(5, "s"));

            }
      }

      if (deltaSimilarityDistance!=0){
        fmtSimScore = d3.format("."+(Math.abs(Math.round(Math.log10(deltaSimilarityDistance)))+1)+"f")
      }else{
        fmtSimScore=d3.format(".3");
      }

      // if ((Math.log10(deltaSimilarityDistance)<=0)&(Math.log10(deltaSimilarityDistance)>=-2)){
      //     fmtSimScore=d3.format(".2");
      // }else{
      //     fmtSimScore=d3.format("s");
      // }
      // if (Math.log10(similarityDistance)==0){
      //   fmtSimScore=d3.format(".3");
      // }
      if  (!isNaN(similarityDistance)){

        // $("#undraggable-result-"+count.toString()).text(zAttribute + ": " + zValue + " (" + similarityDistance.toFixed(2) + ")" );
        d3.select("#undraggable-result-"+newCount.toString()).append("g")

        d3.select("#undraggable-result-"+newCount.toString()).append("text")
          .attr("transform",
                "translate(" + (width/2) + " ," +
               (trans + m[0] + 30) + ")")
          .style("text-anchor", "middle")
          .attr("count", newCount.toString())
          .attr("id",'ztitle')
          .attr("type",'queryResult')
          .attr('label',zValue)
          // .text(zAttribute + ": " + zValue + " (" + similarityDistance + ")" );
          .text(zAttribute + ": " + zValue + " (" + fmtSimScore(similarityDistance) + ")" );
          //<text data-placement="right" title="This is a<br />test...<br />or not">Hover over me</text>
      }
      graph.append("text")
        .attr("transform",
              "translate(" + (width/2) + " ," +
             15 + ")")
        .attr("font-size", 9)
        .style("text-anchor", "middle")
        .text(ylabel + " by " + xlabel);

      // Add the Y Axis
      if ((Math.log10(ymax)<=0)&(Math.log10(ymax)>=-2)){
        graph.append("g")
          .attr("class", "axis axis--y")
          .attr("transform", "translate(20,0)")
          .call(d3.axisLeft(y).ticks(4, ".2"));
      }else{
        graph.append("g")
          .attr("class", "axis axis--y")
          .attr("transform", "translate(20,0)")
          .call(d3.axisLeft(y).ticks(4, "s"));
      }
      // Add the line by appending an svg:path element with the data line we created above
      // do this AFTER the axes above so that the line is above the tick-lines

      if (getScatterplotOption()) {
        graph.selectAll("dot")
            .data(data)
            .enter().append("circle")
            .attr("r", 1)
            .attr("cx", function(d) { return x(d.xval); })
            .attr("cy", function(d) { return y(d.yval); })
            .style("fill", "black");
      } else if (getBarchartOption()) {
          graph.selectAll(".bar")
                .data(data)
                .enter().append("rect")
                .attr("class", "bar")
                .style("fill","steelblue")
                .attr("x", function(d) { return x(d.xval); })
                .attr("width", width/data.length)
                .attr("y", function(d) { return y(d.yval); })
                .attr("height", function(d) {return height-y(d.yval);});

      } else {
          graph.append("path").attr("d", valueline(data))
              .attr("stroke", "black")
              .attr("stroke-width", 1)
              .attr("fill", "none");
        if(errorData != null){
          graph.selectAll("dot")
            .data(data)
            .enter().append("line")
            .attr("r", 1)
            .attr("x1", function(d) {
              return x(d.xval);
            })
            .attr("y1", function(d) {
              return y(d.yval + (d.errorval / 2));
            })
            .attr("x2", function(d) {
              return x(d.xval);
            })
            .attr("y2", function(d) {
              return y(d.yval - (d.errorval / 2));
            })
            .style("stroke", "blue");

          graph.selectAll("dot")
            .data(data)
            .enter().append("line")
            .attr("r", 1)
            .attr("x1", function(d) {
              return x(d.xval)-2;
            })
            .attr("y1", function(d) {
              return y(d.yval + (d.errorval / 2));
            })
            .attr("x2", function(d) {
              return x(d.xval)+2;
            })
            .attr("y2", function(d) {
              return y(d.yval + (d.errorval / 2));
            })
            .style("stroke", "blue");

            graph.selectAll("dot")
              .data(data)
              .enter().append("line")
              .attr("r", 1)
              .attr("x1", function(d) {
                return x(d.xval)-2;
              })
              .attr("y1", function(d) {
                return y(d.yval - (d.errorval / 2));
              })
              .attr("x2", function(d) {
                return x(d.xval)+2;
              })
              .attr("y2", function(d) {
                return y(d.yval - (d.errorval / 2));
              })
              .style("stroke", "blue");

                }
        }

      if (data2 != null && data2 != undefined && includeSketch && getShowOriginalSketch())
      {
        graph.append("g").attr("clip-path", "url(#clip-" + newCount.toString() + ")")
                          .append("path").attr("d", valueline(data2))
                          .attr("stroke", "teal")
                          .attr("stroke-wid", 1)
                          .attr("fill", "none");
      }

      if (getSelectedCategory() == "dynamic_class" && globalDatasetInfo["classes"])
      {

        var tooltip = graph.append("g")
          .attr("class", "custom-tooltip")
          .attr("id", "custom-tooltip" + newCount.toString())
          .style("display", "none");

        var tooltipLength = 0;
        var tooltipTexts = [];
        for (var i = 0; i < zValue.split(".").length; i++) {
          var tooltipText = ""
          for (var j = 0; j < globalDatasetInfo["classes"]["classes"].length; j++)
          {
            if (globalDatasetInfo["classes"]["classes"][j].tag === zValue)
            {
              var tooltipText = globalDatasetInfo["classes"]["classes"][j].formattedRanges[i]
              tooltipTexts.push(tooltipText)
              if (tooltipText.length > tooltipLength)
              {
                tooltipLength = tooltipText.length
              }
            }
          }
        }

        tooltip.append("rect")
          .attr("width", tooltipLength * 7)
          .attr("height", 18*zValue.split(".").length)
          .attr("fill", "black")
          .style("opacity", 0.65);

        for (var i = 0; i < tooltipTexts.length; i++) {
          tooltip.append("text")
            .each(function (d) {
               d3.select(this).append("tspan")
                   .text(tooltipTexts[i])
                   .attr("dy", i ? (1.3*i).toString() + "em" : 0)
                   .attr("x", 3)
                   .attr("y", 15)
                   .attr("fill", "white")
                   .attr("text-anchor", "left")
                   .attr("class", "tspan" + i)
                   .attr("font-size", "13px");
            });
        }

        graph.on("mouseover", function() { $($(this).find(".custom-tooltip")[0]).show(); })
        .on("mouseout", function() { $($(this).find(".custom-tooltip")[0]).hide(); })
        .on("mousemove", function(d) {
          var xPosition = d3.mouse(this)[0] - 100;
          var yPosition = d3.mouse(this)[1] - 47;
          $($(this).find(".custom-tooltip")[0]).attr("transform", "translate(" + xPosition + "," + yPosition + ")");
          var ttt = $($($(this).find(".custom-tooltip")[0]).children()[1]).attr("text")
          $($($(this).find(".custom-tooltip")[0]).children()[1]).text(ttt);
        });
      }
    }
  }
  d3.select('#resultsvg-0')
  .attr("data-intro","Similarity search results are shown for the submitted user defined pattern. The query pattern is overlaid in green for comparison.")
  .attr("data-step","6")
  .attr("data-position","right");

  $(".draggable-graph").draggable({
    opacity: 0.5,
    appendTo: 'body',
    start : function(){
      try{
        if (typeof($(this)[0].parentElement.querySelector("#ztitle").getAttribute("label"))=='string'){
          var textObj = $(this)[0].parentElement.querySelector("#ztitle")
          log.info("dragging",textObj.getAttribute('type'), textObj.getAttribute('label'))
        }
      }catch(err){;}
    },
    helper: function(event) {
      return $(this).clone().css({
        width: $(event.target).width(),
        'border-style': "solid",
        'border-width': 1
      });
    }
  });

// Set double click handlers for exporting results graphs
for(let i = 0; i < getNumResults(); i++) {
  $('#resultsvg-' + i).dblclick(function() {
    createcanvas("#resultsvg-",i);
  });
}

  document.getElementById("loadingEclipse").style.display = "none";
}

var createcanvas = function(id,number) {
  // the canvg call that takes the svg xml and converts it to a canvas
  canvg('canvas', $(id+number.toString())[0].outerHTML);
  // the canvas calls to output a png
  var canvas = document.getElementById("canvas");
  canvas.toBlob(function(blob) {
      saveAs(blob, "output_viz.png");
  });
  canvas.style.display="none";

}

function replaceAll(str, find, replace) {
  return str.replace(new RegExp(find, 'g'), replace);
}

function displayRepresentativeResultsHelper( representativePatternResults , flipY )
{
  clearRepresentativeTable();
  var resultsDiv = $("#representative-table");
  var varFinalArray = []
  var arrLength = getClusterSize()


  for(var count = 0; count < arrLength; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"representative-row-" + count.toString() + "\"></tr>")
    $("#representative-row-" + count.toString()).append("<td><div class=\"undraggable-representative-results undraggable-graph\" data-graph-type=\"userQuery\" id=\"undraggable-representative-result-" + count.toString() + "\"><div class=\"representative-results draggable-graph\" data-graph-type=\"representativeQuery\" id=\"representative-result-" + count.toString() + "\"></div></div></td>");
    varFinalArray.push( representativePatternResults[count] );
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];
    var errorData = representativePatternResults[count]["error"];
    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];
    var zlabel = varFinalArray[count]["zType"];
    var title = varFinalArray[count]["title"].substring(0,25);

    var clusterCount = varFinalArray[count]["count"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);
    var representativeCount = " (" + varFinalArray[count]["count"] + ")";

    var valueRange = [ymin, ymax];
    var xRange = [xmin, xmax];

    // START HERE
    var data = [];
    var arrayLength = xData.length;

    if(errorData != null){
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]),"errorval": Number(errorData[i]) } );
    }
  }

  else{
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
    }
  }
    representativeDygraphsNew["representative-result-" + count.toString()] = {"data": data, "xType": xlabel, "yType": ylabel, "zType": zlabel}
    //top right bottom left
    var m = [0, 0, 20, 20]; // margins
    var width = 250//200// - m[1] - m[3]; // width
    var height = 105//85// - m[0] - m[2]; // height

    // X scale will fit all values from data[] within pixels 0-w
    var x = d3.scaleLinear().range([20, width-20]);
    if(getflipY()){
        var y = d3.scaleLinear().range([20, height-20]);
    }
    else{
        var y = d3.scaleLinear().range([height-20, 20]);
    }

    x.domain([xmin, xmax]);
    y.domain([ymin, ymax]);

    var valueline = d3.line()
    .x(function(d) {
      return x(d.xval);
    })
    .y(function(d) {
      return y(d.yval);
    });

    // Add an SVG element with the desired dimensions and margin.
    var graph = d3.select("#representative-result-" + count.toString())
          .append("svg")
          .attr("viewBox","0 0 "+width.toString()+" "+ (height+15).toString())
          .attr("width", width)// + m[1] + m[3])
          .attr("height", height)// + m[0] + m[2])
          .attr("id","representativesvg-"+ count.toString())
          //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");

    var trans = height-20


    if (getSelectedDataset()==="real_estate")
    {
      if(getSelectedXAxis()==="month")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(4).tickFormat(function (d) {
              var mapper = {
                "50": "02/2008",
                "100": "04/2012",
              }
              return mapper[ d.toString() ]
            }));
      }
      else if(getSelectedXAxis()==="quarter")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "10": "Q2/2006",
                "20": "Q4/2008",
                "30": "Q2/2011",
                "40": "Q4/2013",
              }
              return mapper[ d.toString() ]
            }));
      }
      else if(getSelectedXAxis()==="year")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "1": "2004",
                "2": "2005",
                "3": "2006",
                "4": "2007",
                "5": "2008",
                "6": "2009",
                "7": "2010",
                "8": "2011",
                "9": "2012",
                "10": "2013",
                "11": "2014",
                "12": "2015",
              }
              return mapper[ d.toString() ]
            }));
      }
      else{
        graph.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + trans + ")")
        .call(d3.axisBottom(x).ticks(5, "s"));
      }
    }
    else{
      if(getSelectedXAxis()==="timestep")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "0": '0hr',
                "1": '6hr',
                "2": '12hr',
                "3": '18hr',
                "4": '24hr',
                "5": '36hr',
                "6": '48hr',
                "7": '4d',
                "8": '7d',
                "9": '9d',
                "10":'14d'
              }
              return mapper[ d.toString() ]
            }));
      }

      else if(getSelectedXAxis()==="year" || getSelectedXAxis()==="date" || getSelectedXAxis()==="day"){
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d")));
          } // for formatting all year x axis ticks except hardcoded real estate dataset
          else{
            graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call(d3.axisBottom(x).ticks(5, "s"));

          }
    }


    d3.select("#undraggable-representative-result-"+count.toString()).append("g")

    d3.select("#undraggable-representative-result-"+count.toString()).append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (trans + m[0] + 30) + ")")
      .attr("id",'ztitle')
      .attr("type",'representativeResult')
      .attr('label',title)
      .style("text-anchor", "middle")
      .text(title + " (" + clusterCount + " more like this)");

    graph.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
           15 + ")")
      .attr("font-size", 9)
      .style("text-anchor", "middle")
      .text(getSelectedYAxis() + " by " + getSelectedXAxis());

    // Add the Y Axis
    if ((Math.log10(ymax)<=0)&(Math.log10(ymax)>=-2)){
      graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y).ticks(4, ".2"));
    }else{
      graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y).ticks(4, "s"));
    }


    // Add the line by appending an svg:path element with the data line we created above
    // do this AFTER the axes above so that the line is above the tick-lines
    if (getScatterplotOption())
    {
      graph.selectAll("dot")
          .data(data)
          .enter().append("circle")
          .attr("r", 1)
          .attr("cx", function(d) { return x(d.xval); })
          .attr("cy", function(d) { return y(d.yval); })
          .style("fill", "black");
    }else if (getBarchartOption()) {
        graph.selectAll(".bar")
              .data(data)
              .enter().append("rect")
              .attr("class", "bar")
              .style("fill","steelblue")
              .attr("x", function(d) { return x(d.xval); })
              .attr("width", width/data.length)
              .attr("y", function(d) { return y(d.yval); })
              .attr("height", function(d) {return height-y(d.yval);});

    }
    else
    {
      graph.append("path").attr("d", valueline(data))
          .attr("stroke", "black")
          .attr("stroke-width", 1)
          .attr("fill", "none");
          if(errorData != null){
            graph.selectAll("dot")
              .data(data)
              .enter().append("line")
              .attr("r", 1)
              .attr("x1", function(d) {
                return x(d.xval);
              })
              .attr("y1", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .attr("x2", function(d) {
                return x(d.xval);
              })
              .attr("y2", function(d) {
                return y(d.yval - (d.errorval / 2));
              })
              .style("stroke", "blue");

            graph.selectAll("dot")
              .data(data)
              .enter().append("line")
              .attr("r", 1)
              .attr("x1", function(d) {
                return x(d.xval)-2;
              })
              .attr("y1", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .attr("x2", function(d) {
                return x(d.xval)+2;
              })
              .attr("y2", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .style("stroke", "blue");

              graph.selectAll("dot")
                .data(data)
                .enter().append("line")
                .attr("r", 1)
                .attr("x1", function(d) {
                  return x(d.xval)-2;
                })
                .attr("y1", function(d) {
                  return y(d.yval - (d.errorval / 2));
                })
                .attr("x2", function(d) {
                  return x(d.xval)+2;
                })
                .attr("y2", function(d) {
                  return y(d.yval - (d.errorval / 2));
                })
                .style("stroke", "blue");

                  }
    }
  }
  d3.select('#representativesvg-0')
  .attr("data-intro","Representative patterns show KMeans clustering results, sorted from largest to smallest clusters. A representative visualization from each of the cluster is shown and labelled by the the visualization identifier with the number of visualizations in that cluster in brackets.")
  .attr("data-step","11")
  .attr("data-position","left");

  for(let i = 0; i < getClusterSize(); i++) {
    $('#representativesvg-' + i).dblclick(function() {
      createcanvas('#representativesvg-',i);
    });
  }
    document.getElementById("loadingEclipse2").style.display = "none";
}

function displayOutlierResultsHelper( outlierResults )
{
  clearOutlierTable();
  var resultsDiv = $("#outlier-table");
  var varFinalArray = [];
  var arrLength = getClusterSize();
  for(var count = 0; count < arrLength; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"outlier-row-" + count.toString() + "\"></tr>")
    $("#outlier-row-" + count.toString()).append("<td><div class=\"undraggable-outlier-results undraggable-graph\" data-graph-type=\"userQuery\" id=\"undraggable-outlier-result-" + count.toString() + "\"><div class=\"outlier-results draggable-graph\" data-graph-type=\"outlierQuery\" id=\"outlier-result-" + count.toString() + "\"></div></div></td>");
    varFinalArray.push(outlierResults[count]);
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];
    var errorData = outlierResults[count]["error"];
    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];
    var zlabel = varFinalArray[count]["zType"];
    var title = varFinalArray[count]["title"].substring(0,25);

    var clusterCount = varFinalArray[count]["count"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    var data = [];
    var arrayLength = xData.length;

    if(errorData != null){
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]),"errorval": Number(errorData[i]) } );
    }
  }
    else{
      for (var i = 0; i < arrayLength; i++ ) {
      data.push( { "xval": Number(xData[i]), "yval": Number(yData[i]) } );
    }
  }

    outlierDygraphsNew["outlier-result-" + count.toString()] = {"data": data, "xType": xlabel, "yType": ylabel, "zType": zlabel}

    //top right bottom left
    var m = [0, 0, 20, 20]; // margins
    var width = 250//200// - m[1] - m[3]; // width
    var height = 105//85// - m[0] - m[2]; // height

    // X scale will fit all values from data[] within pixels 0-w
    var x = d3.scaleLinear().range([20, width-20]);
    if(getflipY()){
        var y = d3.scaleLinear().range([20, height-20]);
    }
    else{
        var y = d3.scaleLinear().range([height-20, 20]);
    }

    x.domain([xmin, xmax]);
    y.domain([ymin, ymax]);

    var valueline = d3.line()
    .x(function(d) {
      return x(d.xval);
    })
    .y(function(d) {
      return y(d.yval);
    });

    // Add an SVG element with the desired dimensions and margin.
    var graph = d3.select("#outlier-result-" + count.toString())
          .append("svg")
          .attr("viewBox","0 0 "+width.toString()+" "+ (height+15).toString())
          .attr("width", width)// + m[1] + m[3])
          .attr("height", height)// + m[0] + m[2])
          .attr("id","outliersvg-" + count.toString());

          //.attr("transform", "translate(" + m[3] + "," + m[0] + ")");

    var trans = height-20
    // create xAxis


    if (getSelectedDataset()==="real_estate")
    {
      if(getSelectedXAxis()==="month")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(4).tickFormat(function (d) {
              var mapper = {
                "50": "02/2008",
                "100": "04/2012",
              }
              return mapper[ d.toString() ]
            }));
      }
      else if(getSelectedXAxis()==="quarter")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "10": "Q2/2006",
                "20": "Q4/2008",
                "30": "Q2/2011",
                "40": "Q4/2013",
              }
              return mapper[ d.toString() ]
            }));
      }
      else if(getSelectedXAxis()==="year")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "1": "2004",
                "2": "2005",
                "3": "2006",
                "4": "2007",
                "5": "2008",
                "6": "2009",
                "7": "2010",
                "8": "2011",
                "9": "2012",
                "10": "2013",
                "11": "2014",
                "12": "2015",
              }
              return mapper[ d.toString() ]
            }));
      }
      else{
        graph.append("g")
        .attr("class", "axis axis--x")
        .attr("transform", "translate(0," + trans + ")")
        .call(d3.axisBottom(x).ticks(5, "s"));

      }
    }
    else{
      if(getSelectedXAxis()==="timestep")
      {
        graph.append("g")
          .attr("class", "axis axis--x")
          .attr("transform", "translate(0," + trans + ")")
          .call( d3.axisBottom(x).ticks(5).tickFormat(function (d) {
              var mapper = {
                "0": '0hr',
                "1": '6hr',
                "2": '12hr',
                "3": '18hr',
                "4": '24hr',
                "5": '36hr',
                "6": '48hr',
                "7": '4d',
                "8": '7d',
                "9": '9d',
                "10":'14d'
              }
              return mapper[ d.toString() ]
            }));
      }
      else if(getSelectedXAxis()==="year" || getSelectedXAxis()==="date" || getSelectedXAxis()==="day"){
          graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call(d3.axisBottom(x).ticks(5).tickFormat(d3.format("d")));
          } // for formatting all year x axis ticks except hardcoded real estate dataset
          else{
            graph.append("g")
            .attr("class", "axis axis--x")
            .attr("transform", "translate(0," + trans + ")")
            .call(d3.axisBottom(x).ticks(5, "s"));

          }
    }

    //}


    // graph.append("g")
    //   .attr("class", "axis axis--x")
    //     .attr("transform", "translate(0," + trans + ")")
    //     .call(d3.axisBottom(x).ticks(5, "s"));

    graph.append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
           15 + ")")
      .attr("font-size", 9)
      .style("text-anchor", "middle")
      .text(getSelectedYAxis() + " by " + getSelectedXAxis());

    // Add the Y Axis
    if ((Math.log10(ymax)<=0)&(Math.log10(ymax)>=-2)){
      graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y).ticks(4, ".2"));
    }else{
      graph.append("g")
        .attr("class", "axis axis--y")
        .attr("transform", "translate(20,0)")
        .call(d3.axisLeft(y).ticks(4, "s"));
    }


    // Add the line by appending an svg:path element with the data line we created above
    // do this AFTER the axes above so that the line is above the tick-lines
    if (getScatterplotOption())
    {
      graph.selectAll("dot")
          .data(data)
          .enter().append("circle")
          .attr("r", 1)
          .attr("cx", function(d) { return x(d.xval); })
          .attr("cy", function(d) { return y(d.yval); })
          .style("fill", "black");
    }else if (getBarchartOption()) {
        graph.selectAll(".bar")
              .data(data)
              .enter().append("rect")
              .attr("class", "bar")
              .style("fill","steelblue")
              .attr("x", function(d) { return x(d.xval); })
              .attr("width", width/data.length)
              .attr("y", function(d) { return y(d.yval); })
              .attr("height", function(d) {return height-y(d.yval);});

    }
    else
    {
      graph.append("path").attr("d", valueline(data))
          .attr("stroke", "black")
          .attr("stroke-width", 1)
          .attr("fill", "none");

          if(errorData != null){
            graph.selectAll("dot")
              .data(data)
              .enter().append("line")
              .attr("r", 1)
              .attr("x1", function(d) {
                return x(d.xval);
              })
              .attr("y1", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .attr("x2", function(d) {
                return x(d.xval);
              })
              .attr("y2", function(d) {
                return y(d.yval - (d.errorval / 2));
              })
              .style("stroke", "blue");

            graph.selectAll("dot")
              .data(data)
              .enter().append("line")
              .attr("r", 1)
              .attr("x1", function(d) {
                return x(d.xval)-2;
              })
              .attr("y1", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .attr("x2", function(d) {
                return x(d.xval)+2;
              })
              .attr("y2", function(d) {
                return y(d.yval + (d.errorval / 2));
              })
              .style("stroke", "blue");

              graph.selectAll("dot")
                .data(data)
                .enter().append("line")
                .attr("r", 1)
                .attr("x1", function(d) {
                  return x(d.xval)-2;
                })
                .attr("y1", function(d) {
                  return y(d.yval - (d.errorval / 2));
                })
                .attr("x2", function(d) {
                  return x(d.xval)+2;
                })
                .attr("y2", function(d) {
                  return y(d.yval - (d.errorval / 2));
                })
                .style("stroke", "blue");

                  }
    }

    d3.select("#undraggable-outlier-result-"+count.toString()).append("g");

    d3.select("#undraggable-outlier-result-"+count.toString()).append("text")
      .append("text")
      .attr("transform",
            "translate(" + (width/2) + " ," +
                           (trans + m[0] + 30) + ")")
      .style("text-anchor", "middle")
      .attr("id","ztitle")
      .attr("type","outlierResult")
      .attr("label",title)
      .text(title);
      //.text(xlabel + " (" + clusterCount + ")");
  }
  d3.select('#outliersvg-0')
  .attr("data-intro","Outlier results highlight anomalies that look different from most visualizations in the dataset.")
  .attr("data-step","13")
  .attr("data-position","left");



  var id = "#outliersvg-"

  for(let i = 0; i < getClusterSize(); i++) {
    $("#outliersvg-" + i).dblclick(function() {
      createcanvas("#outliersvg-",i);
    });
  }

  $(".draggable-graph").draggable({
    opacity: 0.5,
    start : function(){
      try{
        if (typeof($(this)[0].parentElement.querySelector("#ztitle").getAttribute("label"))=='string'){
          var textObj = $(this)[0].parentElement.querySelector("#ztitle")
          log.info("dragging",textObj.getAttribute('type'), textObj.getAttribute('label'))
        }
      }catch(err){;}
    },
    helper: function(event) {
      return $(this).clone().css({
        width: $(event.target).width(),
        'border-style': "solid",
        'border-width': 1
      });
    },
    appendTo: 'body'
  });
}

function uploadToSketchpadNew( draggableId, graphType )
{
  var draggedGraph;
  var xType, yType, category;
  switch( graphType ) {
    case "representativeQuery":
      draggedGraph = representativeDygraphsNew[draggableId]["data"];
      xType = representativeDygraphsNew[draggableId]["xType"];
      yType = representativeDygraphsNew[draggableId]["yType"];
      category = representativeDygraphsNew[draggableId]["zType"];
      break;
    case "outlierQuery":
      draggedGraph = outlierDygraphsNew[draggableId]["data"];
      xType = outlierDygraphsNew[draggableId]["xType"];
      yType = outlierDygraphsNew[draggableId]["yType"];
      category = outlierDygraphsNew[draggableId]["zType"];
      break;
    default: //userQuery
      draggedGraph = userQueryDygraphsNew[draggableId]["data"];
      xType = userQueryDygraphsNew[draggableId]["xType"];
      yType = userQueryDygraphsNew[draggableId]["yType"];
      category = userQueryDygraphsNew[draggableId]["zType"];
  }

  angular.element($("#sidebar")).scope().selectedCategory = category;
  angular.element($("#sidebar")).scope().selectedXAxis = xType;
  angular.element($("#sidebar")).scope().selectedYAxis = yType;
  plotSketchpadNew( draggedGraph )//, xType, yType, zType);
  return draggedGraph
}

// function addRow() {
//   var table = $("#zql-table > tbody")[0];
//   var rowCount = table.rows.length;
//   var rowNumber = (rowCount+1).toString();
//   $("#zql-table").append("<tr id=\"table-row-" + rowNumber + "\"class=\"tabler\"><td><input class=\"form-control zql-table number\" type=\"text\" size=\"3\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table process\" type=\"text\" id=\"process-" + rowNumber + "\"size=\"25\" value=\" \"></td><td></td></tr>");
// }

$(document).ready(function(){
  // $('#add-row').click(function(){
  //   addRow();
  // });

  $("#draw-div").droppable({
    accept: ".draggable-graph",
    drop: function( event, ui )
    {
      log.info("dropped successfully to canvas")
      console.log("drop",$(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'))
      uploadToSketchpadNew($(ui.draggable).attr('id'), $(ui.draggable).data('graph-type'));
    }
  });
});

function clearRepresentativeTable()
{
  $("#representative-table").find('tr').not('.middle-right-headers').remove();
}

function clearOutlierTable()
{
  $("#outlier-table").find('tr').not('.middle-right-headers').remove();
}

function clearUserQueryResultsTable()
{
  $("#results-table").empty();
}


// custom event handler which triggers when zoom range is adjusted
var global_xrange;
function refreshZoomEventHandler() {
  $("#draw-div").off();
  $(".dygraph-rangesel-fgcanvas").off();
  $(".dygraph-rangesel-zoomhandle").off();
  $("#draw-div").on('mousedown', '.dygraph-rangesel-fgcanvas, .dygraph-rangesel-zoomhandle', function(){
    global_xrange = sketchpad.xAxisRange();
  });
  $("#draw-div").on('mouseup', '.dygraph-rangesel-fgcanvas, .dygraph-rangesel-zoomhandle', function() {
    var xr = sketchpad.xAxisRange();
    if (global_xrange[0] !== xr[0] || global_xrange[1] !== xr[1])
    {
      angular.element($("#sidebar")).scope().getUserQueryResults();
    }
  });
}

function combineTwoArrays( arr1_xdata, arr1_ydata, arr2 )
{
  data = [];
  i = 0;
  j = 0;
  while (arr1_xdata.length > i && arr2.length > j)
  {
    if ( Number(arr1_xdata[i]) == arr2[j][0] )
    {
      data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), arr2[j][1]] );
      i += 1;
      j += 1;
    }
    else if (arr1_xdata[i] < arr2[j][0])
    {
       data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), null] );
       i += 1;
    }
    else //(arr1_xdata[i] > arr2[j])
    {
      var vals = arr2[j];
      data.push( [vals[0], null, vals[1]] );
      j += 1;
    }
  }
  while(arr1_xdata.length > i)
  {
    data.push( [Number( arr1_xdata[i] ), Number( arr1_ydata[i] ), null] );
    i += 1;
  }
  while(arr2.length > j)
  {
    var vals = arr2[j];
    data.push( [vals[0], null, vals[1]] );
    j += 1;
  }
  return data
}

function separateTwoArrays( data )
{
  arr1 = [];
  arr2 = [];
  i = 0
  while ( data.length > i)
  {
    var item = data[i];
    if (item[1] && item[2])
    {
      arr1.push([item[0], item[1]]);
      arr2.push([item[0], item[2]]);
    }
    else if (item[1]) //item[2] is null
    {
      arr1.push([item[0], item[1]]);
    }
    else
    {
      arr2.push([item[0], item[2]]);
    }
    i += 1;
  }
  return [arr1, arr2]
}

function getEvaluatingRange( xmin, xmax, xrange )
{
  var first_left;
  var first_right;
  var second_left;
  var second_right;

  // if range does not cover anything... should not be displayed actually
  if (xrange[1] <= xmin || xrange[0] >= xmax )
  {
    first_left = xmin;
    first_right = xmax;
    second_left = xmax;
    second_right = xmax;
  }
  else if ( xrange[0] <= xmin && xrange[1] <= xmax && xrange[1] >= xmin)
  {
    first_left = xmin;
    first_right = xmin;
    second_left = xrange[1];
    second_right = xmax;
  }
  else if ( xrange[0] >= xmin && xrange[1] <= xmax )
  {
    first_left = xmin;
    first_right = xrange[0];
    second_left = xrange[1];
    second_right = xmax;
  }
  else if ( xrange[0] >= xmin && xrange[1] >= xmax && xrange[0] <= xmax )
  {
    first_left = xmin;
    first_right = xrange[0];
    second_left = xmax;
    second_right = xmax;
  }
  else
  {
    first_left = xmin;
    first_right = xmin;
    second_left = xmax;
    second_right = xmax;
  }
  return [first_left, first_right, second_left, second_right]
}

$("#resultsvg-0").dblclick(function() {

// the canvg call that takes the svg xml and converts it to a canvas
canvg('canvas', $("#resultsvg-0")[0].outerHTML);

// the canvas calls to output a png
var canvas = document.getElementById("canvas");
canvas.toBlob(function(blob) {
    saveAs(blob,  "output_viz.png");
});
canvas.style.display="none";
//var img = canvas.toDataURL("image/png");


});

function checkAll(source,type) {
  if(type == "x"){
      checkboxes = document.getElementsByName('x-checkbox');
  }
  if(type == "y"){
      checkboxes = document.getElementsByName('y-checkbox');
  }
  if(type == "z"){
      checkboxes = document.getElementsByName('z-checkbox');
  }

  for(var i=0, n=checkboxes.length;i<n;i++) {
    checkboxes[i].checked = source.checked;
  }
}

function autoSelect(source,type) {
  var i = 0;
  if(type == "x"){
      checkboxes = document.getElementsByName('x-checkbox');
      $(".types").each(function(i){
        var selectedOption = $(this).children("option").filter(":selected").text()
          if( selectedOption == "int" || selectedOption == "float" ){
                  checkboxes[i].checked = source.checked;
          }
          i++;
      });
  }
  if(type == "y"){
      checkboxes = document.getElementsByName('y-checkbox');
      $(".types").each(function(){
        var selectedOption = $(this).children("option").filter(":selected").text()
          if( selectedOption == "int" || selectedOption == "float" ){
                  checkboxes[i].checked = source.checked;
          }
          i++;
      });
  }
  if(type == "z"){
      checkboxes = document.getElementsByName('z-checkbox');
      $(".types").each(function(){
        var selectedOption = $(this).children("option").filter(":selected").text()
          if( selectedOption == "string"){
                  checkboxes[i].checked = source.checked;
          }
          i++;
      });
  }
}

function parseCSV(data) {
 Papa.parse(data.get("csv"), {
 // preview: 7000,
 complete: function(results){
   var textAttributeName = "<tr> <td> (De)select All </td> </tr> <tr> <td> Auto Select </td> </tr><tr> <td>&nbsp</td> </tr>";
   var textAttributeSelection = "<tr> <td>" + "<input type='checkbox' onClick=\"checkAll(this,'x')\" style = 'margin-left: 12px; margin-right: 12px;' ><input type='checkbox' onClick=\"checkAll(this,'y')\" style = 'margin-right: 12px;'><input type='checkbox' onClick=\"checkAll(this,'z')\" style = 'margin-right: 12px;'>"+"</td></tr>"
   +"<tr> <td>" + "<input id = 'x-autoselect' type='checkbox' onClick=\"autoSelect(this,'x')\" style = 'margin-left: 12px; margin-right: 12px;'><input id = 'y-autoselect' type='checkbox' onClick=\"autoSelect(this,'y')\" style = 'margin-right: 12px;'><input id = 'z-autoselect' type='checkbox' onClick=\"autoSelect(this,'z')\" style = 'margin-right: 12px;'>"+"</td></tr> <tr> <td>&nbsp</td> </tr>";
   var textDataType  = "<tr> <td>&nbsp</td> </tr><tr> <td>&nbsp</td> </tr><tr> <td>&nbsp</td> </tr>";
   for (i = 0; i < results["data"][0].length; i++) {
   previewRow = results["data"].map(function(value,index) { return value[i]; })
   type = getType(previewRow)
   textAttributeName += "<tr> <td><div style='margin-bottom: 1px;'>"  + results["data"][0][i] +
   "</div></td> </tr>";
   textAttributeSelection += "<tr> <td>" + "<input type='checkbox' value = '" + results["data"][0][i] + "' name ='x-checkbox' style = 'margin-left: 12px; margin-right: 12px;margin-bottom: 4px;'><input type='checkbox' value = '" + results["data"][0][i] + "' name ='y-checkbox' style = 'margin-right: 12px;'><input type='checkbox' value = '" + results["data"][0][i] + "' name ='z-checkbox' style = 'margin-right: 12px;'>"
   +"</select> </td></tr>";
   textDataType += "<tr> <td>" + "<select class='types' style = 'float:right;'>"
   +"<option value=" + results["data"][0][i] + " selected='selected'>"+type+"</option>"
   if(type === "float"){
        textDataType += "<option value=" + results["data"][0][i] + " string'>string</option>"
   }

   else if(type === "int"){
     textDataType += "<option value=" + results["data"][0][i] + " string'>string</option>"
     textDataType += "<option value=" + results["data"][0][i] + " float'>float</option>"
   }
   textDataType += "</select> </td></tr>";
 }

   $('.x-attributes').html(textAttributeName);
   $('.y-attributes').html(textAttributeSelection);
   $('.z-attributes').html(textDataType);
   $('#uploaderModal').modal('toggle');
   $('#define-attributes').modal('toggle');
   document.getElementById("uploadingProgressMessage").style.display = "none";
   document.getElementById("submitButton").style.display = "block";
   $('#x-autoselect').trigger('click');
   $('#y-autoselect').trigger('click');
   $('#z-autoselect').trigger('click');
 }
});

function getType(previewRow){
    var results = [];
      // console.log("length!: ",previewRow);
    var nan = isNaN(Number(previewRow[1]));
    // var isfloat = /^\d*(\.|,)\d*$/;
    // var commaFloat = /^(\d{0,3}(,)?)+\.\d*$/;
    // var dotFloat = /^(\d{0,3}(\.)?)+,\d*$/;
    // var date = /^\d{0,4}(\.|\/)\d{0,4}(\.|\/)\d{0,4}$/;
    // var email = /^[A-za-z0-9._-]*@[A-za-z0-9_-]*\.[A-Za-z0-9.]*$/;
    // var phone = /^\+\d{2}\/\d{4}\/\d{6}$/g;
    // console.log(previewRow.length);
      var retval = "int"
      for (var i = 1; i < previewRow.length-3; i++) {
        if(isNaN(Number(previewRow[i]))){
          return "string"
        }
        else if (parseFloat(previewRow[i]) !== parseInt(previewRow[i])){
          return "float"
        };
      }
      return retval;

      // else if (isfloat.test(str) || commaFloat.test(str) || dotFloat.test(str)) return "float";
      // // else if (date.test(str)) return "date";


}
}
function filterUncheckAttributes(attributeList,selectedAxis){

  var returnList = [];

  $("input:checkbox[name=x-checkbox]:not(:checked)").each(function(){
    if(attributeList.includes($(this).val() + " " + "float")){
      var indexFloat = attributeList.indexOf($(this).val() + " " + "float");
      selectedAxis[indexFloat][0] = "false"
    }
    if(attributeList.includes($(this).val() + " " + "int")){
      var indexInt = attributeList.indexOf($(this).val() + " " + "int");
      selectedAxis[indexInt][0] = "false"
    }
    if(attributeList.includes($(this).val() + " " + "string")){
      var indexString = attributeList.indexOf($(this).val() + " " + "string");
      selectedAxis[indexString][0] = "false"
    }
        //  console.log("testx",x);
    })
  $("input:checkbox[name=y-checkbox]:not(:checked)").each(function(){
    if(attributeList.includes($(this).val() + " " + "float")){
      var indexFloat = attributeList.indexOf($(this).val() + " " + "float");
      selectedAxis[indexFloat][1] = "false"
    }
    if(attributeList.includes($(this).val() + " " + "int")){
      var indexInt = attributeList.indexOf($(this).val() + " " + "int");
      selectedAxis[indexInt][1] = "false"
    }
    if(attributeList.includes($(this).val() + " " + "string")){
      var indexString = attributeList.indexOf($(this).val() + " " + "string");
      selectedAxis[indexString][1] = "false"
    }
        //  console.log("testx",x);
    })
  $("input:checkbox[name=z-checkbox]:not(:checked)").each(function(){
      if(attributeList.includes($(this).val() + " " + "float")){
        var indexFloat = attributeList.indexOf($(this).val() + " " + "float");
        selectedAxis[indexFloat][2] = "false"
      }
      if(attributeList.includes($(this).val() + " " + "int")){
        var indexInt = attributeList.indexOf($(this).val() + " " + "int");
        selectedAxis[indexInt][2] = "false"
      }
      if(attributeList.includes($(this).val() + " " + "string")){
        var indexString = attributeList.indexOf($(this).val() + " " + "string");
        selectedAxis[indexString][2] = "false"
      }
          //  console.log("testx",x);
      })

      for (i = 0; i < attributeList.length; i++) {
          returnList.push(attributeList[i] + " " + selectedAxis[i][0]+ " " + selectedAxis[i][1]+ " " + selectedAxis[i][2])
      }

    return returnList;
};