app.factory('ScatterService', function () {
        const factory = {};
        var currentPolygon;
        var drawPolygonFlag = false;
        var currentRepresentativePlot;

        function enableButton(buttonId) {
            $('.' + buttonId).removeClass('disabled');
        }
        function disableButton(buttonId) {
            $('.' + buttonId).addClass('disabled');
        }


        factory.drawScatter = function ( data ) {

            var container, data, xlabel, ylabel, title;
            var scaleMultiplier = 1.05;
            var infoToReturn = {};
            //Find an element to render the chart
            container = $("#main-chart");
            $(container).empty();

            // finding actual dimensions of div
            var heightOfDiv = container.innerHeight();
            var widthOfDiv = container.innerWidth();

            // finding relative point radius
            var radius = 2;

            // Setting margins as percentages
            //var topMargin = widthOfDiv * 0.08;
            //var bottomMargin = widthOfDiv * 0.08;
            //var rightMargin = widthOfDiv * 0.08;
            //var leftMargin = widthOfDiv * 0.08;
            // Setting margins as percentages
            var topMargin = 5;
            var bottomMargin = 30;
            var rightMargin = 0;
            var leftMargin = 30;

            infoToReturn.leftMargin = leftMargin;
            infoToReturn.topMargin = topMargin;

            var margin = {
                    top: Math.ceil(topMargin),
                    right: Math.ceil(rightMargin),
                    bottom: Math.ceil(bottomMargin),
                    left: Math.ceil(leftMargin)
                },
                width = widthOfDiv - margin.left - margin.right,
                height = heightOfDiv - margin.top - margin.bottom;

            var yMax = d3.max(data, function(d) {return Math.max(d.yval); })
            var xMax = d3.max(data, function(d) {return Math.max(d.xval); })

            //setting y-scale to fit in the svg window
            var yScale = d3.scaleLinear()
                .domain([0, yMax])
                .range([height, 0]);

            //setting x-scale to fit in the svg window
            var xScale = d3.scaleLinear()
                .domain([0, xMax])
                .range([0, width]);

            infoToReturn.yScale = yScale;
            infoToReturn.xScale = xScale;
            infoToReturn.yMax = yMax;
            infoToReturn.xMax = xMax;

            // AXES:
            // to change tick-sizes: .ticksize(inner, outer) where inner are the normal ticks and outer are the end ticks
            // here we are keeping the inner ticks to the default value of 6 and the outer to negative extremes to form a box

            // var xAxis = d3.svg.axis()
            //     .scale(xScale)
            //     .orient("bottom")
            //     .tickSize(6, -height);
            var xAxis = d3.axisBottom(xScale).tickSize(6, -height);
            var yAxis = d3.axisLeft(yScale).tickSize(6, -width);

            // var yAxis = d3.svg.axis()
            //     .scale(yScale)
            //     .orient("left")
            //     .tickSize(6, -width);

            var svg = d3.select("#main-chart")
                .attr("width", 400)
                .attr("height", 200)
                .append("g") //group that will house the plot
                .attr("id", "main-area")
                .attr("transform",  "translate(40,20) scale(1.2)"); //to center the g in the svg



            //////////////////////////////////////////////////////GRID LINES
            var yAxisTickValues = yAxis.scale().ticks(yAxis.ticks());
            var xAxisTickValues = xAxis.scale().ticks(xAxis.ticks());
            var xAxisTickSize = xAxisTickValues[1] - xAxisTickValues[0];
            var yAxisTickSize = yAxisTickValues[1] - yAxisTickValues[0];
            svg.append("g")
                .attr("class", "x axis")
                .selectAll("line")
                .data(d3.range(0, xMax, xAxisTickSize / 4))
                .enter().append("line")
                .attr("x1", function (d) {
                    return xScale(d);
                })
                .attr("y1", 0)
                .attr("x2", function (d) {
                    return xScale(d);
                })
                .attr("y2", height);

            svg.append("g")
                .attr("class", "y axis")
                .selectAll("line")
                .data(d3.range(0, yMax, yAxisTickSize / 4))
                .enter().append("line")
                .attr("x1", 0)
                .attr("y1", function (d) {
                    return yScale(d);
                })
                .attr("x2", width)
                .attr("y2", function (d) {
                    return yScale(d);
                });


            // ------------------ HEX BIN PROPERTIES --------------------------------------

            var points = new Array(data.length)

            // converting to array of points
            for (var i = 0; i < data.length; i++) {
                var point = [xScale(data[i]['xval']), yScale(data[i]['yval'])]
                points[i] = point;
            }

            /*
             Clip-path is made to clip anything that goes out of the svg
             */

             /*svg.append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("class", "mesh")
                .attr("width",300)
                .attr("height", 115);*/ // This clip should not be needed


            var hexColorRed = d3.scaleLinear()
                .domain([0, data.length])
                .range(["white", "maroon"]);

            var hexbin = d3_hexbin.hexbin()
                //.size([width, height])
                .radius(10);


            var binLengths = hexbin( points ).map(function (elem) {
                return elem.length;
            });

            var hexColor = d3.scaleLinear()
                .domain([0, d3.max(binLengths)])
                .range(["lightblue", "darkblue"]);


            drawHexbin();
createcolorbar();
            // ------------------- DRAWING PLOTS FUNCTIONS -------------------------------------

            /**
             * DRAWING A HEXBIN PLOT
             */
            function drawHexbin() {
                var hexbinPlot = svg.append("g")
                        .attr("clip-path", "url(#clip)")
                        .selectAll(".hexagon")
                        .data(hexbin(points)) // returns an array of bins
                        .enter().append("path") // enter returns all fictitious elements according to number of data points
                        .attr("class", "hexagon") // the class hexagon is a custom class made to incorporate stroke and fill
                        .attr("d", hexbin.hexagon())
                        .attr("transform", function (d) {
                            return "translate(" + d.x + "," + d.y + ") scale(0.8)"; // Each bin (or d) returned by hexbin(points) is an array containing the bin’s points
                        })
                    ;

                //load the points animatedly
                //reference url for ease: https://github.com/mbostock/d3/wiki/Transitions#d3_ease

                hexbinPlot.transition()
                    .style("fill", function (d, i) {

                        return hexColor(d.length);
                        //return color[i];
                    })
                    .duration(900)
                    //.ease('sin');
            }



                                            function createcolorbar(){
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
                                                .domain([d3.max(binLengths),0]);

                                            /*var yAxis = d3.svg.axis()
                                                  .orient("left")
                                                  .ticks(5)  //Set rough # of ticks
                                                    //.tickFormat(formatPercent)
                                                  .scale(xScale);*/

                                            svg.append("g")
                                                .attr("class", "axis") //Assign "axis" class
                                                .attr("transform","translate(20,10)")
                                                .call(d3.axisLeft(yScale).ticks(5).tickSize(0));
                                            };
            // adding the axes
            svg.append("g")
                .attr("class", "y axis")
                .call(yAxis);

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);
            currentRepresentativePlot = infoToReturn;
            return infoToReturn;
        };

        factory.drawPolygon = function (){
            drawPolygonFlag = true;
            enableButton('undo');
            enableButton('green-polygon');
            enableButton('red-polygon');
            enableButton('submit');
            clickPolyPoints(d3.select("#main-chart"));
        };

        // adding double tap to  d3 events
        d3.selection.prototype.dblTap = function (callback) {
            var last = 0;
            return this.each(function () {
                d3.select(this).on("touchstart", function (e) {
                    if ((d3.event.timeStamp - last) < 300) {
                        return callback(e);
                    }
                    last = d3.event.timeStamp;
                });
            });
        };

        /**
         * Function to help draw polygons on d3 chart
         * @param svg
         */
        function clickPolyPoints(svg) {
            var polygon = svg.append('polygon').classed("userPolygon", true);
            polypoints = [];
            currentPolygon = polygon;
            //currentPolypoints = polypoints;
            if (drawPolygonFlag) {
                svg.on("click", function () {
                    polypoints.push(d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
                    // console.log(polypoints.join(" "));
                    updatePolygon(polygon, polypoints);
                });
                svg.on("mousemove", function () {
                        var tempPolypoints = polypoints.slice();
                        tempPolypoints.push(d3.mouse(this)[0] + ',' + d3.mouse(this)[1]);
                        updatePolygon(polygon, tempPolypoints);

                    }
                );
                svg.on("contextmenu", function (data, index) {
                    //handle right click
                    //stop showing browser menu
                    d3.event.preventDefault();
                    polypoints = [];
                    updatePolygon(polygon, polypoints);
                });
                svg.on("mouseout", function () {
                    updatePolygon(polygon, polypoints);
                });
                svg.on("dblclick", function () {
                    //console.log("Double click! + ",this);
                    svg.on("click", null);
                    svg.on("mousemove", null);
                    svg.on("contextmenu", null);
                    svg.on("mouseout", null);
                    disableButton('undo');
                    highlightPolygon();
                });

                svg.dblTap(function () {
                    svg.on("click", null);
                    svg.on("mousemove", null);
                    svg.on("contextmenu", null);
                    svg.on("mouseout", null);
                    disableButton('undo');
                    highlightPolygon();
                });
            }

        }

        function highlightPolygon() {
            d3.selectAll(".userPolygon")
                .on("mouseover", function () {
                    d3.event.preventDefault();
                    //console.log("On a polygon!");
                    d3.select(this).classed("highlightPolygon", true);

                    d3.select(this).on("contextmenu", function () {
                        d3.select(this).remove()
                        d3.event.preventDefault();

                    })
                })
                .on("mouseleave", function () {
                    d3.select(this).classed("highlightPolygon", false);
                })
        }

        factory.undoPolyPoints = function (){
            if (polypoints.length > 0) {
                polypoints = polypoints.slice(0, polypoints.length - 1);
                updatePolygon(currentPolygon, polypoints);
            }
            if (polypoints.length == 0) {
                disableButton('undo');
            }

        };

        factory.changePolygonColorRed = function() {
            currentPolygon.classed("red-polygon", true);
        };

        factory.changePolygonColorGreen = function() {
            currentPolygon.classed("red-polygon", false);
        };

        function changePolygonColor(color) {
            currentPolygon.attr("style", "fill: " + color);
        }
        function updatePolygon(polygon, polypoints) {
            polygon.attr('points', "");
            polygon.attr('points', polypoints);
            //console.log(polygon.attr('points'));
        }

        factory.getPolygons = function() {
            var result = [];
            var polygons = $('.userPolygon');
            for (var i = 0; i < polygons.length; i++) {
                var polygon = polygons[i];
                var newPolygon = {};
                // points
                newPolygon.points = [];
                for (var j = 0; j < polygon.points.length; j++) {
                    var xCoordinate = polygon.points[j].x;
                    var scaledXCoordinate = currentRepresentativePlot.xScale.invert(xCoordinate - currentRepresentativePlot.leftMargin);
                    var yCoordinate = polygon.points[j].y;
                    var scaledYCoordinate = currentRepresentativePlot.yScale.invert(yCoordinate - currentRepresentativePlot.topMargin);
                    var currentCoordinates = [scaledXCoordinate, scaledYCoordinate];
                    // checking for duplicate last two points
                    //if (!newPolygon.points[newPolygon.points.length - 1] == currentCoordinates)
                    newPolygon.points.push(currentCoordinates);
                }
                // type
                var polygonClasses = $(polygon).attr('class');
                if (polygonClasses.indexOf('red') > -1) {
                    newPolygon.type = 'red';
                }
                else newPolygon.type = "green";
                result.push(newPolygon);
            }
            return result;
        };

        return factory;
    });

app.controller('scatterController', ['$scope','$http', '$rootScope', 'ScatterService', function ($scope, $http, $rootScope, ScatterService) {
$scope.data;

/*  $http.get('/zv/executeScatter', {params: {'query': {"db":"real_estate", "zqlRows":[{"name":{"output":true,"sketch":true,"name":"f1"},"x":{"variable":"x1","attributes":["'year'"]},"y":{"variable":"y1","attributes":["'soldprice'"]},"z":{"variable":"z1","aggregate":true,"attribute":"'state'","values":["*"]}, "viz":{"map": {"type":"scatter"}} }]}}}
  ).then(
      function (response) {
          $scope.data = response.data.outputCharts[0].points;
          //$scope.scatterService = ScatterService.drawScatter( $scope.data );
          //$scope.scatterService = ScatterService.drawScatter( data2 );
          console.log("data: ", response.data.outputCharts[0].points);
          $scope.scatterService = ScatterService.drawScatter( $scope.data );
          $scope.submit;
          setTimeout(function () {
              $rootScope.shared = {value:"The input controller just changed this"};
              $rootScope.$digest();
          }, 3000);
      },
      function (response) {
      //    console.log("failed: ", escape(response));
      }
  );*/



        var data = [{'xval': 4, 'yval': 55.5}, {'xval': 3.5, 'yval': 30},{'xval': 0.5, 'yval':7},
                    {'xval': 3, 'yval':15},{'xval': 3.2, 'yval':20},{'xval': 3.2, 'yval':20},
                    {'xval': 3.2, 'yval':35},{'xval': 3.2, 'yval':45},{'xval': 3.8, 'yval':50},
                    {'xval': 0.5, 'yval':7},{'xval': 0.5, 'yval':7},{'xval': 0.5, 'yval':7},
                    {'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},
                    {'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},
                    {'xval': 1.6, 'yval': 10}, {'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17},
                    {'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17}]

        //$scope.scatterService = ScatterService.drawScatter( $scope.data );
        //currentRepresentativePlot = ScatterService.drawScatter( data );
        //drawRandomChart();
        // $scope.scatterService = ScatterService;
        $scope.submit = function (){
            var polygons = ScatterService.getPolygons();
            $rootScope.polygons = polygons;
            $rootScope.$digest();
        };
        setTimeout(function () {
            $rootScope.shared = {value:"The input controller just changed this"};
            $rootScope.$digest();
        }, 3000);
    }]);
