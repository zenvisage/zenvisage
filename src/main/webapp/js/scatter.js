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



                /*function colorBar(){
                    var orient = "right",
                    lineWidth = 40, //Function?... because that would be coooooool... not sure if it is compatible with axis.js
                    size_ = 300,
                    tickFormat = d3.format("3e"),
                    color = d3.scaleLinear().domain([0, 0.5, 1]).range(["blue", "green", "red"]), //v -> color
                    line = d3.line().curve(d3.curveBasis);
                    precision = 8,
                    points_,
                    tickSize_;

                    function component(selection){
                        selection.each(function(data,index){
                            var container = d3.select(this),
                            tickSize = tickSize_ || lineWidth,
                            n,
                            points = points_ || (((orient == "left") || (orient == "right"))?[[0,size_],[0,0]]:[[size_,0],[0,0]]),
                            quads = quad(sample(line(points),precision)),
                            size = (points)?n:size_,
                            aScale = color.copy().interpolate(d3.interpolate).domain(d3.extent(color.domain())).range([size,0]), //v -> px
                            colorExtent = d3.extent(color.domain()),
                            normScale = color.copy().domain(color.domain().map(function(d){ return (d - colorExtent[0])/ (colorExtent[1] - colorExtent[0])})),

                            //Save values for transitions
                            oldLineWidth = this.__lineWidth__ || lineWidth;
                            oldQuads = this.__quads__ || quads;
                            this.__quads__ = quads;
                            this.__lineWidth__ = lineWidth;

                            //Enters
                            var bar = container.selectAll("path.c").data(d3.range(quads.length), function(d){return d}),
                            bEnter = bar.enter().insert("path","g.axis").classed("c",true),
                            bExit = d3.transition(bar.exit()).remove(),
                            bUpdate = d3.transition(bar),
                            bTransform = function(selection,f,lw){
                                selection.style("fill", function(d) { return normScale(f(d).t); })
                                    .style("stroke", function(d) { return normScale(f(d).t); })
                                    .attr("d", function(d) { var p = f(d); return lineJoin(p[0], p[1], p[2], p[3], lw); });};

                            bEnter.call(bTransform,function(d){return oldQuads[oldQuads.length - 1]},oldLineWidth); // enter from last of oldQuad
                            bExit.call(bTransform,function(d){return quads[quads.length - 1]},lineWidth); //exit from last of quads
                            bUpdate.call(bTransform,function(d){return quads[d]},lineWidth)

                            var colorBarAxis = d3.svg.axis().scale(aScale).orient(orient)
                                .tickSize(tickSize).tickFormat(tickFormat),
                            a = container.selectAll("g.axis").data(function(d){return (aScale)?[1]:[]}), //axis container
                            aEnter = a.enter().append("g").classed("axis",true),
                            aExit = d3.transition(a.exit()).remove(),
                            aUpdate = d3.transition(a).call(colorBarAxis),
                            aTransform = function(selection,lw){
                                selection.attr("transform", "translate("
                                               + (((orient == "right") || (orient == "left"))?-lw/2:0) + ","
                                               + (((orient == "right") || (orient =="left"))?0:lw/2) + ")");};

                            aEnter.call(aTransform,oldLineWidth);
                            aExit.call(aTransform,lineWidth);
                            aUpdate.call(aTransform,lineWidth);

                            // Sample the SVG path string "d" uniformly with the specified precision.
                            function sample(d,pre) {
                                var path = document.createElementNS(d3.ns.prefix.svg, "path");
                                path.setAttribute("d", d);

                                n = path.getTotalLength();

                                var t = [0], i = 0, dt = pre;
                                while ((i += dt) < n) t.push(i);
                                t.push(n);

                                return t.map(function(t) {
                                    var p = path.getPointAtLength(t), a = [p.x, p.y];
                                    a.t = t / n;
                                    return a;
                                });

                                document.removeChild(path);
                            }

                            // Compute quads of adjacent points [p0, p1, p2, p3].
                            function quad(pts) {
                                return d3.range(pts.length - 1).map(function(i) {
                                    var a = [pts[i - 1], pts[i], pts[i + 1], pts[i + 2]];
                                    a.t = (pts[i].t + pts[i + 1].t) / 2;
                                    return a;
                                });
                            }

                            // Compute stroke outline for segment p12.
                            function lineJoin(p0, p1, p2, p3, width) {
                                var u12 = perp(p1, p2),
                                r = width / 2,
                                a = [p1[0] + u12[0] * r, p1[1] + u12[1] * r],
                                b = [p2[0] + u12[0] * r, p2[1] + u12[1] * r],
                                c = [p2[0] - u12[0] * r, p2[1] - u12[1] * r],
                                d = [p1[0] - u12[0] * r, p1[1] - u12[1] * r];

                                if (p0) { // clip ad and dc using average of u01 and u12
                                    var u01 = perp(p0, p1), e = [p1[0] + u01[0] + u12[0], p1[1] + u01[1] + u12[1]];
                                    a = lineIntersect(p1, e, a, b);
                                    d = lineIntersect(p1, e, d, c);
                                }

                                if (p3) { // clip ab and dc using average of u12 and u23
                                    var u23 = perp(p2, p3), e = [p2[0] + u23[0] + u12[0], p2[1] + u23[1] + u12[1]];
                                    b = lineIntersect(p2, e, a, b);
                                    c = lineIntersect(p2, e, d, c);
                                }

                                return "M" + a + "L" + b + " " + c + " " + d + "Z";
                            }

                            // Compute intersection of two infinite lines ab and cd.
                            function lineIntersect(a, b, c, d) {
                                var x1 = c[0], x3 = a[0], x21 = d[0] - x1, x43 = b[0] - x3,
                                y1 = c[1], y3 = a[1], y21 = d[1] - y1, y43 = b[1] - y3,
                                ua = (x43 * (y1 - y3) - y43 * (x1 - x3)) / (y43 * x21 - x43 * y21);
                                return [x1 + ua * x21, y1 + ua * y21];
                            }

                            // Compute unit vector perpendicular to p01.
                            function perp(p0, p1) {
                                var u01x = p0[1] - p1[1], u01y = p1[0] - p0[0],
                                u01d = Math.sqrt(u01x * u01x + u01y * u01y);
                                return [u01x / u01d, u01y / u01d];
                            }

                        })}

                    component.orient = function(_) {
                        if (!arguments.length) return orient;
                        orient = _;
                        return component;
                    };

                    component.lineWidth = function(_) {
                        if (!arguments.length) return lineWidth;
                        lineWidth = _;
                        return component;
                    };

                    component.size = function(_) {
                        if (!arguments.length) return size_;
                        size_ = _;
                        return component;
                    };

                    component.tickFormat = function(_) {
                        if (!arguments.length) return tickFormat;
                        tickFormat = _;
                        return component;
                    };

                    component.tickFormat = function(_) {
                        if (!arguments.length) return tickSize_;
                        tickSize_ = _;
                        return component;
                    };

                    component.color = function(_) {
                        if (!arguments.length) return color;
                        color = _;
                        return component;
                    };

                    component.precision = function(_) {
                        if (!arguments.length) return precision;
                        precision = _;
                        return component;
                    };

                    component.points = function(_) {
                        if (!arguments.length) return points_;
                        points_ = _;
                        return component;
                    };

                    component.line = function(_) {
                        if (!arguments.length) return line;
                        line = _;
                        return component;
                    };

                    return component;
                }*/



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

            //svg.append("g").attr("transform","translate(10,10)").classed("colorbar",true)
            //cb = colorBar().color(d3.scale.linear().domain([-1, 0, 1]).range(["red", "green", "blue"])).size(350).lineWidth(80).precision(4);
            //call(cb);

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
        var data = [{'xval': 4, 'yval': 55.5}, {'xval': 3.5, 'yval': 30},{'xval': 0.5, 'yval':7},
                    {'xval': 3, 'yval':15},{'xval': 3.2, 'yval':20},{'xval': 3.2, 'yval':20},
                    {'xval': 3.2, 'yval':35},{'xval': 3.2, 'yval':45},{'xval': 3.8, 'yval':50},
                    {'xval': 0.5, 'yval':7},{'xval': 0.5, 'yval':7},{'xval': 0.5, 'yval':7},
                    {'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},
                    {'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},{'xval': 3, 'yval': 5},
                    {'xval': 1.6, 'yval': 10}, {'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17},
                    {'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17},{'xval': 2.5, 'yval': 17}]
        $scope.scatterService = ScatterService.drawScatter( data );
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
