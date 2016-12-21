
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

        factory.initializeScatterPlot = function (xmin, xmax, ymin, ymax) {
            var container = $("#main-chart");
            $(container).empty();
            // finding actual dimensions of div
            var heightOfDiv = container.innerHeight();
            var widthOfDiv = container.innerWidth();
            // finding relative point radius
            var radius = 2;
            var topMargin = 5;
            var bottomMargin = 30;
            var rightMargin = 0;
            var leftMargin = 30;
            var margin = {
                    top: Math.ceil(topMargin),
                    right: Math.ceil(rightMargin),
                    bottom: Math.ceil(bottomMargin),
                    left: Math.ceil(leftMargin)
                },
            width = widthOfDiv - margin.left - margin.right,
            height = heightOfDiv - margin.top - margin.bottom;
            var xMax = xmax
            var yMax = ymax

            //setting y-scale to fit in the svg window
            var yScale = d3.scaleLinear()
                .domain([0, yMax])
                .range([height, 0]);

            //setting x-scale to fit in the svg window
            var xScale = d3.scaleLinear()
                .domain([0, xMax])
                .range([0, width]);

            // AXES:
            // to change tick-sizes: .ticksize(inner, outer) where inner are the normal ticks and outer are the end ticks
            // here we are keeping the inner ticks to the default value of 6 and the outer to negative extremes to form a box

            var xAxis = d3.axisBottom(xScale).tickSize(6, -height);
            var yAxis = d3.axisLeft(yScale).tickSize(6, -width);

            // ZOOM VARIABLE
            var zoom = d3.zoom()
                .scaleExtent([1, 10])
                .on("zoom", zoomed);

            var svg = d3.select("#main-chart")
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g") //group that will house the plot
                .attr("id", "main-area")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")"); //to center the g in the svg

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
            /*
             Clip-path is made to clip anything that goes out of the svg
             */
            svg.append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("class", "mesh")
                .attr("width", width)
                .attr("height", height);

            var hexbin = d3_hexbin.hexbin()
                .radius(10);

            drawHexbin();

            // ------------------- DRAWING PLOTS FUNCTIONS -------------------------------------

            /**
             * DRAWING A HEXBIN PLOT
             */
            function drawHexbin() {
                var hexbinPlot = svg.append("g")
                        .attr("clip-path", "url(#clip)")
                        .selectAll(".hexagon")
                        //.data(hexbin(points)) // returns an array of bins
                        .enter().append("path") // enter returns all fictitious elements according to number of data points
                        .attr("class", "hexagon") // the class hexagon is a custom class made to incorporate stroke and fill
                        .attr("d", hexbin.hexagon())
                        .attr("transform", function (d) {
                            return "translate(" + d.x + "," + d.y + ")"; // Each bin (or d) returned by hexbin(points) is an array containing the bin’s points
                        })
                    ;

                //load the points animatedly
                //reference url for ease: https://github.com/mbostock/d3/wiki/Transitions#d3_ease

                hexbinPlot.transition()
                    .style("fill", function (d, i) {
                        return hexColor(d.length);
                    })
                    .duration(900)
            }

            /**
             * DRAWING A SCATTERPLOT
             */
            function drawScatterPlot() {
                var scatterPlot = svg.append('g')
                        .attr("clip-path", "url(#clip)")
                        .selectAll('circle').data(data)
                        .enter().append('circle')
                        .attr('r', 0)
                        .attr('cx', function (d) {
                            return xScale(d['xval'])
                        })
                        .attr('cy', function (d) {
                            return yScale(d['y'])
                        })
                    ;

                //load the points animatedly
                //reference url for ease: https://github.com/mbostock/d3/wiki/Transitions#d3_ease
                //load the points animatedly
                scatterPlot.transition()
                    .attr('r', radius)
                    .duration(1000)
                    .ease('elastic')

            }


            // adding the axes
            svg.append("g")
                .attr("class", "y axis")
                .call(yAxis);

            svg.append("g")
                .attr("class", "x axis")
                .attr("transform", "translate(0," + height + ")")
                .call(xAxis);

            function zoomed() {
                svg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
            }

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
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g") //group that will house the plot
                .attr("id", "main-area")
                .attr("transform", "translate(" + margin.left + "," + margin.top + ")"); //to center the g in the svg

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
            svg.append("clipPath")
                .attr("id", "clip")
                .append("rect")
                .attr("class", "mesh")
                .attr("width", width)
                .attr("height", height);


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
                            return "translate(" + d.x + "," + d.y + ")"; // Each bin (or d) returned by hexbin(points) is an array containing the bin’s points
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

app.controller('scatterController', ['$scope', '$rootScope', 'ScatterService', function ($scope, $rootScope, ScatterService) {
        //var data = [{'xval': 4, 'yval': 55.5}, {'xval': 3, 'yval': 5}, {'xval': 4, 'yval': 2}, {'xval': 5, 'yval': 7}]
        //currentRepresentativePlot = ScatterService.drawScatter( data );
        $scope.scatterService = ScatterService;
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
