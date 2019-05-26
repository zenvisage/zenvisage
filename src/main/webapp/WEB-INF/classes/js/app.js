var app = angular.module('zenvisage', ['ngCookies']);
var globalDatasetInfo;
var allAxisColumns;
var login_ava;

app.controller('classCreationController', ['$scope', '$rootScope','$http', function ($scope, $rootScope, $http) {
    $scope.data = {
        option1: "default",
        option2: "default",
        option3: "default",
        option4: "default",
        numOfClass: ['', '', '', ''],
        startPoints: ['', '', '', '']
    };

    $scope.AxisInfo = [];
    $scope.$on("clearDynamicClassOptions", function() {
        $scope.data.option1 = '';
        $scope.data.option2 = '';
        $scope.data.option3 = '';
        $scope.data.option4 = '';
        $scope.data.startPoints = ['', '', '', ''];
        document.getElementById("load-dynamic-class-button").style.display = "none";
        document.getElementById("load-dynamic-class-slider-button").style.display = "none";
    });

    $scope.$watch('data', function(newValue, oldValue) {
        if (newValue.option1 !== oldValue.option1) {
            if (newValue.option1 !== 'default' && newValue.option1 !== '') {
                $scope.data.numOfClass[0] = 2;
                loadAttributeInfo(1, newValue.option1);
                loadSlider(1);
            }
            else {
                $scope.data.numOfClass[0] = '';
                removeSlider(1);
            }
        }
        if (newValue.option2 !== oldValue.option2) {
            if (newValue.option2 !== 'default' && newValue.option2 !== '') {
                $scope.data.numOfClass[1] = 2;
                loadAttributeInfo(2, newValue.option2);
                loadSlider(2);
            }
            else {
                $scope.data.numOfClass[1] = '';
                removeSlider(2);
            }
        }
        if (newValue.option3 !== oldValue.option3) {
            if (newValue.option3 !== 'default' && newValue.option3 !== '') {
                $scope.data.numOfClass[2] = 2;
                loadAttributeInfo(3, newValue.option3);
                loadSlider(3);
            }
            else {
                $scope.data.numOfClass[2] = '';
                removeSlider(3);
            }
        }
        if (newValue.option4 !== oldValue.option4) {
            if (newValue.option4 !== 'default' && newValue.option4 !== '') {
                $scope.data.numOfClass[3] = 2;
                loadAttributeInfo(4, newValue.option4);
                loadSlider(4);
            }
            else {
                $scope.data.numOfClass[3] = '';
                removeSlider(4);
            }
        }
        if (newValue.numOfClass !== oldValue.numOfClass && newValue.numOfClass !== '') {
            for(i = 0; i < 4; i++) {
                if (newValue.numOfClass[i] !== oldValue.numOfClass[i]) {
                    loadSlider(i + 1);
                    break;
                }
            }
        }
    }, true);

    $scope.switchView = function(view) {
        if(view === "slider") {
            $('#dynamic-class').modal('hide');
            $('#dynamic-class-with-slider').modal('show');
        }
        else {
            $('#dynamic-class-with-slider').modal('hide');
            $('#dynamic-class').modal('show');
        }
    }

    function removeSlider(i) {
        handlesSlider = document.getElementById("dynamic-class-slider-" + i);
        if(handlesSlider.noUiSlider) {
            handlesSlider.noUiSlider.destroy();
        }
        var id = "#legend" + i;
        // remove the old svg
        d3.select(id).select("svg").remove();
    }

    $scope.$on("loadAxisInfo", function() {
        $scope.AxisInfo = [];
        $scope.AxisInfo.push('');
        allAxisColumns = $.extend(true,globalDatasetInfo["xAxisColumns"],globalDatasetInfo["yAxisColumns"],globalDatasetInfo["zAxisColumns"]);
        for (var key in allAxisColumns) {
            if (allAxisColumns[key]["dataType"] == "float" || allAxisColumns[key]["dataType"] == "int") {
                $scope.AxisInfo.push(key);
            }
        }
        $scope.classes = [];
    });

    function valueToColor(val) {
        if(val < 0.1) return "#e6f0ff";
        if(val < 0.2) return "#b3d1ff";
        if(val < 0.3) return "#80b3ff";
        if(val < 0.4) return "#4d94ff";
        if(val < 0.5) return "#3385ff";
        if(val < 0.6) return "#1a75ff";
        if(val < 0.7) return "#0066ff";
        if(val < 0.8) return "#005ce6";
        if(val < 0.9) return "#0052cc";
        return "#0047b3";
    }

    function loadAttributeInfo(i, attr) {
        dataset = getSelectedDataset();
        $http.post('/zv/getAttributeInfo', dataset + "," + attr
        ).then(
            function (response) {
                console.log("success: getAttributeInfo");
                k = $("#dynamic-class-row-with-slider-" + i + "\ > div").find(":selected").text();
                var min = allAxisColumns[k]["min"];
                var max = allAxisColumns[k]["max"] + 1;

                var w = 310, h = 20;
                var id = "#legend" + i;
                // remove the old svg
                d3.select(id).select("svg").remove();
                var key = d3.select(id)
                    .append("svg")
                    .attr("width", w)
                    .attr("height", h);

                var legend = key.append("defs")
                    .append("svg:linearGradient")
                    .attr("id", "gradient" + i)
                    .attr("x1", "0%")
                    .attr("y1", "100%")
                    .attr("x2", "100%")
                    .attr("y2", "100%")
                    .attr("spreadMethod", "pad");

                numOfItvs = 12;
                gap = (max - min) / numOfItvs;
                startPoints = [], counts = [];
                curr_max = min + gap;
                cnt = 0, total = response.data.length;

                for(var j = 0; j < response.data.length; ) {
                    if(response.data[j] < curr_max) {
                        cnt++;
                        j++;
                    } else {
                        startPoints.push(curr_max);
                        counts.push(cnt);
                        curr_max += gap;
                        cnt = 0;
                    }
                }

                while(startPoints.length < numOfItvs) {
                    startPoints.push(curr_max);
                    counts.push(cnt);
                    curr_max += gap;
                    cnt = 0;
                }

                for(var j = 0; j < startPoints.length; j++) {
                    legend.append("stop")
                        .attr("offset", (j + 1) / startPoints.length)
                        .attr("stop-color", valueToColor(counts[j]/total))
                        .attr("stop-opacity", 1);
                }

                key.append("rect")
                    .attr("width", w)
                    .attr("height", h)
                    .style("fill", "url(#gradient" + i + ")");
            },
            function (response) {
                console.log("failed to load attribute info", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    }

    // TODO(Renxuan): merge dynamic class creation and info
    function loadSlider(i) {
        key = $("#dynamic-class-row-with-slider-" + i + "\ > div").find(":selected").text();
        numOfClass = $scope.data.numOfClass[i-1]
        if (numOfClass && key)
        {
            numOfClass = Math.max(1, $scope.data.numOfClass[i-1]);
            $scope.data.numOfClass[i-1] = numOfClass;

            var min = allAxisColumns[key]["min"];
            var max = allAxisColumns[key]["max"] + 1;
            gap = (max - min)/numOfClass;
            startPoints = [];
            startPoint = min;
            for (j = 0; j < numOfClass; j++) {
                startPoints.push(startPoint);
                startPoint += gap;
            }
            startPoints.push(max);
            $scope.data.startPoints[i - 1] = startPoints;

            id = 'dynamic-class-slider-' + i;
            handlesSlider = document.getElementById(id);
            if(handlesSlider.noUiSlider) {
                handlesSlider.noUiSlider.destroy();
            }

            noUiSlider.create(handlesSlider, {
                range: {
                    'min': min,
                    'max': max
                },
                // Handles start at ...
                start: startPoints,
                connect: false,
                behaviour: 'tap-drag',
                tooltips: true/*,
        format: {
          to: function (x) {
            return d3.format(".2g")(x);
          }, from: Number
        }*/
            });

            origins = handlesSlider.getElementsByClassName('noUi-handle');
            // change the color of min, max
            origins[0].classList.add("minmax");
            origins[origins.length-1].classList.add("minmax");

            tooltips = handlesSlider.getElementsByClassName('noUi-tooltip');
            tooltips[0].classList.add("noUi-tooltip-minmax");
            tooltips[tooltips.length-1].classList.add("noUi-tooltip-minmax");
        }
    }

    $scope.createOrModifyClasses = function() {
        var query = {};
        var classList = [];
        for (i = 1; i <= 4; i++) {
            key = $("#dynamic-class-row-with-slider-" + i + "\ > div").find(":selected").text();
            handlesSlider = document.getElementById("dynamic-class-slider-" + i).noUiSlider;

            if (key && handlesSlider)
            {
                var startPoints = [];
                tmp = handlesSlider.get()
                if(Array.isArray(tmp)) {
                    for(j = 0; j < tmp.length; j++) {
                        startPoints.push(Number(tmp[j]));
                    }
                } else {
                    startPoints.push(Number(tmp));
                }

                var val = '';
                var keyval = {};
                for(j = 0; j < startPoints.length - 1; j++) {
                    val += '[' + startPoints[j] + ',' + startPoints[j+1] + ']' + ','
                }
                // remove last ','
                val = val.substring(0, val.length - 1);
                keyval["name"] = key;
                keyval["values"] = JSON.parse("[" + val + "]");
                classList.push(keyval);
            }
        }
        query["dataset"] = getSelectedDataset();
        query["classes"] = classList;
        document.getElementById("loadingEclipse4").style.display = "inline";

        $http.post('/zv/createClasses', query
        ).then(
            function (response) {
                console.log("success: ", response);
                query = {};
                query["dataset"] = getSelectedDataset();
                $http.post('/zv/getClassInfo', query
                ).then(
                    function (response) {
                        console.log("success: ", response.data);
                        globalDatasetInfo["classes"] = response.data
                        var formattedRanges = formatRanges(response.data["classes"]);
                        $scope.classes = response.data["classes"];
                        for (var i = 0; i < response.data["classes"].length; i++){
                            response.data["classes"][i].formattedRanges = formattedRanges[i];
                        }
                        document.getElementById("load-dynamic-class-slider-button").style.display = "inline";
                    },
                    function (response) {
                        console.log("failed to get class info: ", response.data);
                        $("#errorModalText").html(response.data);
                        $("#errorModal").modal();
                    }
                );
                document.getElementById("loadingEclipse4").style.display = "none";
            },
            function (response) {
                console.log("failed to create classes", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
                document.getElementById("loadingEclipse4").style.display = "none";
            }
        );
        log.info("Dynamic Class created",JSON.stringify(classList))
    }

    $scope.createOrModifyClassesOld = function() {
        var query = {};
        var classList = [];
        for (i = 1; i < 5; i++) {
            key = $("#dynamic-class-row-" + i + "\ > div").find(":selected").text();
            val = $("#dynamic-class-row-" + i + "\ > div > input")[0].value
            if (val && key)
            {
                var keyval = {};
                var min = allAxisColumns[key]["min"]
                var max = allAxisColumns[key]["max"]
                var replacedMin = val.replace("min", min);
                var replacedMinMax = replacedMin.replace("max", max);
                keyval["name"] = key
                keyval["values"] = JSON.parse("[" + replacedMinMax + "]");
                classList.push(keyval);
            }
        }
        query["dataset"] = getSelectedDataset();
        query["classes"] = classList;
        document.getElementById("loadingEclipse3").style.display = "inline";

        $http.post('/zv/createClasses', query
        ).then(
            function (response) {
                console.log("success: ", response);
                query = {};
                query["dataset"] = getSelectedDataset();
                $http.post('/zv/getClassInfo', query
                ).then(
                    function (response) {
                        console.log("success: ", response.data);
                        globalDatasetInfo["classes"] = response.data
                        var formattedRanges = formatRanges(response.data["classes"]);
                        $scope.classes = response.data["classes"];
                        for (var i = 0; i < response.data["classes"].length; i++){
                            response.data["classes"][i].formattedRanges = formattedRanges[i];
                        }
                        document.getElementById("load-dynamic-class-button").style.display = "inline";
                    },
                    function (response) {
                        console.log("failed to get class info: ", response.data);
                        $("#errorModalText").html(response.data);
                        $("#errorModal").modal();
                    }
                );
                document.getElementById("loadingEclipse3").style.display = "none";
            },
            function (response) {
                console.log("failed to create classes", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
                document.getElementById("loadingEclipse3").style.display = "none";
            }
        );
        log.info("Dynamic Class created",JSON.stringify(classList))
    }

    $scope.$watch('classes', function(newValue, oldValue) {
        if(newValue && oldValue && newValue.length > 0 && newValue.length == oldValue.length) {
            for(i = 0; i < newValue.length; i++) {
                if (newValue[i].class_id !== oldValue[i].class_id) {
                    renameDynamicClass(newValue[i]);
                    break;
                }
            }
        }
    }, true);

    function renameDynamicClass(classToRename) {
        $http.post('/zv/renameDynamicClass', "tableName: " + classToRename.name + ", classId: " + classToRename.class_id + ", tag: " + classToRename.tag
        ).then(
            function (response) {
                console.log("rename class success");
            },
            function (response) {
                console.log("failed to rename class: ", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    }

    $scope.deleteClass = function deleteClass(classes, index) {
        var classToDelete = classes[index]
        classes.splice(index, 1)

        $http.post('/zv/deleteClass', "tableName: " + classToDelete.name + ", classId: " + classToDelete.class_id + ", tag: " + classToDelete.tag
        ).then(
            function (response) {
                console.log("delete class success");
            },
            function (response) {
                console.log("failed to get delete class: ", response.data);
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    }

    $scope.loadDynamicClass = function() {
        angular.element($("#sidebar")).scope().setDataAttributeToDynamicClass();
    }
}]);

app.controller('zqlScriptController', ['$scope', '$rootScope', '$http', 'plotResultsService', function($scope, $rootScope, $http, plotResultsService) {
    $scope.submitZQLScript = function () {
        // var test_script = "db = real_estate\n" +
        // 		"ax x1 = [year]\n" +
        // 		"ax y1 = [soldprice]\n" +
        // 		"ax z1 = [state.*]\n" +
        // 		"vc f1 = {x1, y1, z1}\n" +
        // 		"ax y2 = [listingprice]\n" +
        // 		"vc f2 = {x1, y1, z1}\n" +
        // 		"ax v1 = process(argmin={z1},k=1,DEuclidean(f1,f2))\n" +
        // 		"vc f3 = {x1, y1, v1}\n" +
        // 		"display(f3)";
        var script = document.getElementById('zqlScriptCode').value;
        $http.get('/zv/executeZQLScript', {params: {'query': script}}
        ).then(
            function (response) {
                console.log("success: ", response);
                var userQueryResults = response.data.outputCharts;
                plotResultsService.displayUserQueryResults(userQueryResults, false);
            },
            function (response) {
                console.log("failed ZQL Query", escape(response.data));
                document.getElementById("loadingEclipse").style.display = "none";
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    };
}]);


app.controller('zqlTableController', ['$scope' ,'$http', 'plotResultsService', '$compile', function ($scope, $http, plotResultsService, $compile) {

    $scope.input = {};
    $scope.queries = {};
    $scope.queries['zqlRows'] = [];

    $scope.removeRow = function ( index ) {
        $("#table-row-" + index).remove();
        console.log('remove regular row ',index,' added!');
    };

    $scope.addRow = function () {
        var table = $("#zql-table > tbody")[0];
        var rowCount = table.rows.length;
        var rowNumber = (rowCount+1).toString();
        //$("#zql-table").append
        console.log('insert regular row ',rowNumber,' added!');
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
        //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
        $compile($el)($scope);
        //tree.addLeaf(count);
        //tree.addParent(1);
    };

    $scope.$on('insertRowhelper', function(event){
        var table = $("#zql-table > tbody")[0];
        var rowCount = table.rows.length;
        var rowNumber = (rowCount+1).toString();
        //$("#zql-table").append
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
        //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
        $compile($el)($scope);
        //tree.addLeaf(count);
        //tree.addParent(1);
    });

    $scope.addProcessRow = function () {
        var table = $("#zql-table > tbody")[0];
        var rowCount = table.rows.length;
        var rowNumber = (rowCount+1).toString();
        //$("#zql-table").append
        console.log('insert process row ',rowNumber,' added!');
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler processRow\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td colspan=\"5\"><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
        //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
        $compile($el)($scope);
        //tree.addParent(count);
    };

    $scope.$on('insertProcessRowhelper', function (event) {
        var table = $("#zql-table > tbody")[0];
        var rowCount = table.rows.length;
        var rowNumber = (rowCount+1).toString();
        //$("#zql-table").append
        //        console.log('insert row ',i,' added!');
        $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler processRow\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td colspan=\"5\"><input class=\"form-control zql-table process\" type=\"text\" size=\"20\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");
        //<td><input class=\"form-control zql-table process\" type=\"text\" size=\"36\" value=\" \"></td>
        $compile($el)($scope);
        //tree.addParent(count);
    });

    $scope.$on('removeAndInsertRowshelper', function( event, args ) {
        var table = $("#zql-table > tbody")[0];
        var rowCount = table.rows.length;

        for (i = rowCount; i > 0; i--) {
            // $("#table-row-" + i).remove();
            //  console.log('row ',i-1,' removed!');
            table.deleteRow(i-1)
        }

        for (i = 1; i <= args.n; i++) {
            var rowNumber = (i).toString();
//      console.log('row ',i,' added!');
            $el = $("<tr id=\"table-row-" + rowNumber + "\"" + "class=\"tabler\"><td><a ng-click=\"removeRow(" + rowNumber + ")\"><span class=\"glyphicon glyphicon glyphicon-minus-sign\"></span></a></td><td><input class=\"form-control zql-table name\" type=\"text\" size=\"1\" value=\" \"></td><td><input class=\"form-control zql-table x-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table y-val\" type=\"text\" size=\"11\" value=\" \"></td><td><input class=\"form-control zql-table z-val\" type=\"text\" size=\"10\" value=\" \"></td><td><input class=\"form-control zql-table constraints\" type=\"text\" size=\"6\" value=\" \"></td><td></td></tr>").appendTo("#zql-table");

            $compile($el)($scope);
        }});

    // $scope.filterZQL = function () {
    //   console.log("Filter working!")
    //   // var xAxis = getSelectedXAxis();
    //   // var yAxis = getSelectedYAxis();
    //   // var zAxis = getSelectedCategory();
    //   // var constraint = $("#filter.form-control").val();
    //   // //Populate ZQL table
    //   // // $( ".tabler" ).each(function( index ) {
    //   // //   $(this).find(".name").val("*f1")
    //   // //   $(this).find(".x-val").val("x<-{'"+xAxis+"'}")
    //   // //   $(this).find(".y-val").val("y<-{'"+yAxis+"'}")
    //   // //   $(this).find(".z-val").val("z<-'"+zAxis+"'.*")
    //   // //   $(this).find(".constraints").val(constraint)
    //   // // })
    //   // // $scope.submitZQL();
    //   // $scope.getUserQueryResultsWithCallBack();
    //   // getRepresentativeTrendsWithoutCallback();
    //   // constructUserQuery();
    //   // constructRepresentativeTrendQuery();
    //   // constructOutlierTrendQuery();
    //   log.info("filter constraint: ",constraint)

    // }
    $scope.submitZQL = function () {
        $("#graph-div").empty();
        createZQLGraph( submitNodeZQL );
        clearUserQueryResultsTable();
        $scope.queries['zqlRows'] = [];
        var processRow = [];
        $( ".tabler" ).each(function( index ) {
            if ( $(this).hasClass("processRow") )
            {
                var processe = $(this).find(".process").val()
                if (processe !== undefined) {
                    processe = parseProcess(processe);
                }
                processRow.push(processe);
            }
            else
            {
                var name = $(this).find(".name").val()
                var x = $(this).find(".x-val").val()
                var y = $(this).find(".y-val").val()
                var z = $(this).find(".z-val").val()
                var constraints = $(this).find(".constraints").val()
                // var viz = $(this).find(".viz").val()
                // var processe = $(this).find(".process").val()
                // "processe": processe
                var input = { "name": name, "x": x, "y": y, "z": z, "constraints": constraints, "viz": ""};
                if (checkInput(input)) {
                    if (input.name.sketch) {
                        // if this row needs to grab data from the sketch
                        var points = [];
                        this.dataX = [];
                        this.dataY = [];
                        this.xAxis = getSelectedXAxis();
                        this.yAxis = getSelectedYAxis();
                        for(var i = 0; i < sketchpadData.length; i++){
                            var xp = sketchpadData[i]["xval"];
                            var yp = sketchpadData[i]["yval"];
                            points.push(new Point( xp, yp ));
                            this.dataX.push( xp );
                            this.dataY.push( yp );
                        }
                        input["sketchPoints"] = new SketchPoints(this.xAxis, this.yAxis, points);
                        input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x"+(index+1)};
                        input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y"+(index+1)};
                        input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z"+(index+1), expression: undefined};
                        "z"+index + "<-'"+ getSelectedCategory() +"'.*";
                    }
                    $scope.queries['zqlRows'].push(input);
                }
            }
        });

        $.each( processRow, function( index, value ) {
            $scope.queries['zqlRows'][index]["processe"] = value;
        });

        $scope.queries['db'] = getSelectedDataset();
        console.log($scope.queries);

        $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify( $scope.queries )}}
        ).then(
            function (response) {
                console.log("success: ", response);
                plotResultsService.displayUserQueryResults(response.data.outputCharts, false);
            },
            function (response) {
                console.log("failed ZQL Query", escape(response.data));
                document.getElementById("loadingEclipse").style.display = "none";
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    };

    function submitNodeZQL( d )
    {
        $scope.queries['zqlRows'] = [];
        var input = { "name": "*f1", "x": d.xval, "y": d.yval, "z": d.zval, "constraints": d.constraint, "viz": ""};
        if (checkInput(input)) {
            if (input.name.sketch) {
                // if this row needs to grab data from the sketch
                var points = [];s
                this.dataX = [];
                this.dataY = [];
                this.xAxis = getSelectedXAxis();
                this.yAxis = getSelectedYAxis();
                for(var i = 0; i < sketchpadData.length; i++){
                    var xp = sketchpadData[i]["xval"];
                    var yp = sketchpadData[i]["yval"];
                    points.push(new Point( xp, yp ));
                    this.dataX.push( xp );
                    this.dataY.push( yp );
                }
                input["sketchPoints"] = new SketchPoints(this.xAxis, this.yAxis, points);
                input["x"] = {"attributes": ["'"+ getSelectedXAxis() + "'"], "variable" : "x"+(index+1)};
                input["y"] = {"attributes": ["'"+ getSelectedYAxis() + "'"], "variable" : "y"+(index+1)};
                input["z"] = {"attribute": "'"+ getSelectedCategory() + "'", "values": ["*"], "variable" : "z"+(index+1), expression: undefined};
                "z"+index + "<-'"+ getSelectedCategory() +"'.*";
            }
            $scope.queries['zqlRows'].push(input);
        }
        $scope.queries['db'] = getSelectedDataset();
        $http.get('/zv/executeZQLComplete', {params: {'query': JSON.stringify( $scope.queries )}}
        ).then(
            function (response) {
                console.log("success: ", response);
                plotResultsService.displayUserQueryResults(response.data.outputCharts,false);
            },
            function (response) {
                console.log("failed Node ZQL Query: ", escape(response.data));
                document.getElementById("loadingEclipse").style.display = "none";
                $("#errorModalText").html(response.data);
                $("#errorModal").modal();
            }
        );
    }

}]);

// check for emput x, y and z and then check for syntax correctness
function checkInput(input) {
    for (var property in input) {
        if (input.hasOwnProperty(property)) {

            input[property] = input[property].trim();
            // do stuff
        }
    }
    var essentialColumns = input.name && input.x && input.y;
    if (essentialColumns === undefined) {
        console.error("X or Y or Z Column cannot be empty.");
        return false;
    }
    input.name = parseName(input.name);
    input.x = parseX(input.x);
    input.y = parseY(input.y);

    var constraints = null, viz = null, processe = null, z = null;
    if (input.z !== undefined) {
        input.z = parseZ(input.z);
    }
    if (input.constraints !== undefined) {
        if (0 === input.constraints.length)
        {
            input.constraints = undefined;
        }
        //input.constraints = input.constraints //parseConstraints(input.constraints);
    }
    if (input.viz !== undefined) {
        input.viz = parseViz(input.viz);
    }
    return (name && x && y && z && constraints && viz) !== undefined;
}

// checks the input process variable and converts it to proper object format to sent to backend
function checkProcessInput(input)
{
    if (input.processe !== undefined) {
        input.processe = parseProcess(input.processe);
    }
    return processe !== undefined;
}
app.controller('options-controller', [
    '$scope', '$rootScope', '$http','datasetService',  'ChartSettings', '$compile', 'sketchService',
    function($scope, $rootScope, $http,datasetService, ChartSettings, $compile , sketchService) {
        $scope.similarity = 'Euclidean';
        $scope.representative = 'kmeans';
        $scope.aggregation = 'avg';
        $scope.numResults = 50;
        $scope.clusterSize = 3;
        $scope.minDisplayThresh = 0.0;
        $scope.considerRange = true;
        $scope.showOriginalSketch = true;
        $scope.showBar = false;
        $scope.showScatterplot = false;
        $scope.flipY = false;
        $scope.outputNormalized = true;
        $scope.equation = '';
        $scope.selectedSmoothing = "none";
        $scope.zqltable = false;
        $scope.scattertable = false;
        $scope.chartSettings = ChartSettings;
        $scope.chartSettings.chartOptions = ["Line", "Bar", "Scatter"];
        $scope.chartSettings.selectedChartOption = $scope.chartSettings.chartOptions[0];
        // $scope.filter= '';

        var var_map = {
            "similarity": $scope.similarity,
            "aggregation": $scope.aggregation,
            "numResults": $scope.numResults,
            "clusterSize": $scope.clusterSize,
            "minDisplayThresh": $scope.minDisplayThresh,
            "considerRange": $scope.considerRange,
            "showOriginalSketch": $scope.showOriginalSketch,
            "showScatterplot": $scope.showScatterplot,
            "showBar": $scope.showBar,
            "flipY": $scope.flipY,
            "selectedSmoothing": $scope.selectedSmoothing
        }

        var toggleWatch = function (watchExpr, fn) {
            var watchFn;
            return function () {
                if (watchFn) {
                    watchFn();
                    watchFn = undefined;
                    console.log("Disabled " + watchExpr);
                } else {
                    watchFn = $scope.$watch(watchExpr, fn);
                    console.log("Enabled " + watchExpr);
                }
            };
        };

        var watchFunc = function (varName, funcIndex) {
            return function (newValue, oldValue) {
                if (newValue !== oldValue) {
                    log.info(varName, var_map[varName]);
                    if (funcIndex == 1) {
                        document.getElementById("loadingEclipse").style.display = "inline";
                        $scope.callGetUserQueryResults();
                    } else if (funcIndex == 2) {
                        document.getElementById("loadingEclipse").style.display = "inline";
                        $scope.callGetUserQueryResultsWithCallBack();//dont call representative trends
                    } else if (funcIndex == 3) {
                        $scope.callgetRepresentativeTrends();
                    }
                }
            }
        };

        $scope.considerRangeToggle = toggleWatch("considerRange", watchFunc("considerRange", 1));
        $scope.considerRangeToggle();
        $scope.showOriginalSketchToggle = toggleWatch("showOriginalSketch", watchFunc("showOriginalSketch", 1));
        $scope.showOriginalSketchToggle();
        $scope.showScatterplotToggle = toggleWatch("showScatterplot", watchFunc("showScatterplot", 2));
        $scope.showScatterplotToggle();
        $scope.showBarToggle = toggleWatch("showBar", watchFunc("showBar", 1));
        $scope.showBarToggle();
        $scope.flipYToggle = toggleWatch("flipY", watchFunc("flipY", 2));
        $scope.flipYToggle();
        $scope.similarityToggle = toggleWatch("similarity", watchFunc("similarity", 1));
        $scope.similarityToggle();
        $scope.aggregationToggle = toggleWatch("aggregation", watchFunc("aggregation", 2));
        $scope.aggregationToggle();
        $scope.clusterSizeToggle = toggleWatch("clusterSize", watchFunc("clusterSize", 2));
        $scope.clusterSizeToggle();
        $scope.numResultsToggle = toggleWatch("numResults", watchFunc("numResults", 1));
        $scope.numResultsToggle();
        $scope.minDisplayThreshToggle = toggleWatch("minDisplayThresh", watchFunc("minDisplayThresh", 2));
        $scope.minDisplayThreshToggle();
        $scope.outputNormalizedToggle = toggleWatch("outputNormalized", watchFunc("outputNormalized", 2));
        $scope.outputNormalizedToggle();
        $scope.selectedSmoothingToggle = toggleWatch("selectedSmoothing", watchFunc("selectedSmoothing", 2));
        $scope.selectedSmoothingToggle();

        $("#slider-range-max").slider({
            range: "max",
            min: 0,
            max: 1,
            step: 0.05,
            value: 0.5,
            slide: function (event, ui) {
                $("#amount").val(ui.value);
                log.info("smoothingcoefficient", ui.value)
                if (getSmoothingType() != "none") {
                    $scope.getUserQueryResults();
                    $scope.callgetRepresentativeTrends();
                }
            }
        });

        $scope.onFilterChange = function () {
            log.info("filter change", $("#filter.form-control").val());
            $scope.callGetUserQueryResultsWithCallBack();
        };

        $scope.onflipYChange = function () {
            document.getElementById("loadingEclipse").style.display = "inline";
            if (usingPattern == true) {
                patternLoad();
            }
            else {
                createSketchpad(sketchpadData);
            }
            ;
        }

        $scope.drawFunction = function () {
            log.info('input equation', $scope.equation)
            var xval = [];
            var plotData = [];

            for (var i = 0; i < sketchpadData.length; i++) {
                var xp = sketchpadData[i]["xval"];
                //var yp = sketchpadData[i]["yval"];
                xval.push(xp)
            }

            var scope = {
                x: xval,
            };

            var eq = $scope.equation.replace("^", ".^");
            var y = math.eval(eq, scope)
            if (eq.includes("x")) {
                for (i = 0; i < xval.length; i++) {
                    plotData.push({"xval": xval[i], "yval": y[i]})
                }
            }
            else {
                for (i = 0; i < xval.length; i++) {
                    plotData.push({"xval": xval[i], "yval": y})
                }
            }

            angular.element('#class-creation').triggerHandler('click');

            plotSketchpadNew(plotData)
            //angular.element($("#sidebar")).scope().getUserQueryResults();
        }

        // $scope.$watch('representative', function( newValue, oldValue ) {
        //   if (newValue !== oldValue)
        //   {
        //     $scope.callgetRepresentativeTrends();
        //   }
        // });


        $scope.changeZQLTableState = function () {
            // activate zqltable, and deactivate zqlscript
            if (!$scope.zqltable) {
                $scope.zqltable = true;
                $scope.zqlscript = false;
            } else {
                // deactive zqltable
                $scope.zqltable = false;
            }
        }

        $scope.changeZQLScriptState = function () {
            // activate zqlscript, deactivate zqltable
            if (!$scope.zqlscript) {
                $scope.zqlscript = true;
                $scope.zqltable = false;
            } else {
                $scope.zqlscript = false;
            }
        }


        $scope.removeAndInsertRows = function (n) {
            $scope.$broadcast('removeAndInsertRowshelper', {n});
        }

        $scope.insertRow = function () {
            $scope.$broadcast('insertRowhelper');
        }

        $scope.insertProcessRow = function () {
            $scope.$broadcast('insertProcessRowhelper');
        }

        // TOP K
        $scope.getTopK = function () {
            clearUserQueryResultsTable();
            var q = constructUserQuery(); //goes to query.js
            var data = q;

            console.log("calling getTopK");
            $http.post('/zv/findbestclass', data).success(function (response) {
                console.log("getTopK: success");
                if (response.length == 0) {
                    console.log("empty response")
                }
                plotResultsService.displayUserQueryResults(response.outputCharts, true);
            }).error(function (response) {
                console.log("getUserQueryResults: fail");
                document.getElementById("loadingEclipse").style.display = "none";
                $("#errorModalText").html(response);
                $("#errorModal").modal();
            });
        }
        $scope.onflipYChange = function () {
            document.getElementById("loadingEclipse").style.display = "inline";
            if (usingPattern == true) {
                sketchService.patternLoad();
            }
            else {
                sketchService.createSketchpadLine(sketchpadData);
            }
            ;
        }

        $scope.onSmoothingChange = function () {
            document.getElementById("loadingEclipse").style.display = "inline";
            log.info("selectedSmoothing", $scope.selectedSmoothing)
            $scope.callGetUserQueryResultsWithCallBack();
            $scope.callgetRepresentativeTrends();
        };

        $scope.onFilterChange = function () {
            log.info("filter change", $("#filter.form-control").val());
            $scope.callGetUserQueryResultsWithCallBack();
            $scope.callgetRepresentativeTrends();
        };

        $scope.clearQuery = function () {
            $scope.removeAndInsertRows(1);
            $($(".tabler")[0]).find(".name").val("")
            $($(".tabler")[0]).find(".x-val").val("")
            $($(".tabler")[0]).find(".y-val").val("")
            $($(".tabler")[0]).find(".z-val").val("")
            $($(".tabler")[0]).find(".constraints").val("")
            $($(".tabler")[0]).find(".process").val("")
        }

        $scope.populateWeatherQuery1 = function () {
            $("#dataset-form-control").val('weather');
            angular.element($("#sidebar")).scope().onDatasetChange('weather');
            $scope.removeAndInsertRows(1);

            // $scope.insertRow()
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'month'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'temperature'}")
            $($(".tabler")[0]).find(".z-val").val(" z1<-'location'.*")
            $($(".tabler")[0]).find(".constraints").val("location='Melbourne'")
            // $($( ".tabler" )[0]).find(".process").val("")
            $scope.insertRow()
            $($(".tabler")[1]).find(".name").val("f2")
            $($(".tabler")[1]).find(".x-val").val("x1<-{'month'}")
            $($(".tabler")[1]).find(".y-val").val("y1")
            $($(".tabler")[1]).find(".z-val").val(" z2<-'location'.*")
            $($(".tabler")[1]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[2]).find(".process").val("v2<-argmin_{z2}[k=5]DEuclidean(f1,f2)")

            $scope.insertRow()
            $($(".tabler")[3]).find(".name").val("*f3")
            $($(".tabler")[3]).find(".x-val").val("x1")
            $($(".tabler")[3]).find(".y-val").val("y1")
            $($(".tabler")[3]).find(".z-val").val("v2")
            $($(".tabler")[3]).find(".constraints").val("")

            $scope.insertRow()

            // $($( ".tabler" )[2]).find(".process").val("")
            //      removeZqlRow(6);  // hacky, remove extra rows
            removeZqlRow(5);
            //      removeZqlRow(4);
            //     removeZqlRow(3);
            removeZqlRow(7);


        }

        $scope.populateWeatherQuery2 = function () {
            $("#dataset-form-control").val('weather');
            angular.element($("#sidebar")).scope().onDatasetChange('weather');
            $scope.removeAndInsertRows(2);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'month'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'temperature'}")
            $($(".tabler")[0]).find(".z-val").val(" z1<-'location'.*")
            $($(".tabler")[0]).find(".constraints").val("location='Melbourne'")
            // $($( ".tabler" )[0]).find(".process").val("")

            $($(".tabler")[1]).find(".name").val("f2")
            $($(".tabler")[1]).find(".x-val").val("x1<-{'month'}")
            $($(".tabler")[1]).find(".y-val").val("y1")
            $($(".tabler")[1]).find(".z-val").val(" z2<-'location'.*")
            $($(".tabler")[1]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[2]).find(".process").val("v2<-argmax_{z2}[k=5]DEuclidean(f1,f2)")

            $scope.insertRow()
            $($(".tabler")[3]).find(".name").val("*f3")
            $($(".tabler")[3]).find(".x-val").val("x1")
            $($(".tabler")[3]).find(".y-val").val("y1")
            $($(".tabler")[3]).find(".z-val").val("v2")
            $($(".tabler")[3]).find(".constraints").val("")
            // $($( ".tabler" )[3]).find(".process").val("")

            $scope.insertRow()
            removeZqlRow(6);
            removeZqlRow(5);
            //     removeZqlRow(4);
        }

        $scope.populateWeatherQuery3 = function () {
            $("#dataset-form-control").val('weather');
            angular.element($("#sidebar")).scope().onDatasetChange('weather');
            $scope.removeAndInsertRows(1);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'year'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'temperature'}")
            $($(".tabler")[0]).find(".z-val").val(" z1<-'location'.*")
            $($(".tabler")[0]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[1]).find(".process").val("v1<-argmax_{z1}[k=5]T(f1)")

            $scope.insertRow()
            $($(".tabler")[2]).find(".name").val("*f2")
            $($(".tabler")[2]).find(".x-val").val("x1")
            $($(".tabler")[2]).find(".y-val").val("y1")
            $($(".tabler")[2]).find(".z-val").val("v1")
            $($(".tabler")[2]).find(".constraints").val("")
            $($(".tabler")[2]).find(".process").val("")
            $scope.insertRow()
            removeZqlRow(6);  // hacky, remove extra rows
            removeZqlRow(5);
            removeZqlRow(4);
            //  removeZqlRow(3);

        }

        // $scope.populateQuery1 = function() {
        //   $scope.removeAndInsertRows( 1 );
        //   $($( ".tabler" )[0]).find(".name").val("*f1")
        //   $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
        //   $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
        //   $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
        //   $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
        //   // $($( ".tabler" )[0]).find(".process").val("")
        // }

        // $scope.populateQuery2 = function() {

        //   $scope.removeAndInsertRows( 3 );
        //   $($( ".tabler" )[0]).find(".name").val("f1")
        //   $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
        //   $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
        //   $($( ".tabler" )[0]).find(".z-val").val(" z1<-'state'.*")
        //   $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
        //   $($( ".tabler" )[0]).find(".process").val("")

        //   $($( ".tabler" )[1]).find(".name").val("f2")
        //   $($( ".tabler" )[1]).find(".x-val").val("x1")
        //   $($( ".tabler" )[1]).find(".y-val").val("y1")
        //   $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.*")
        //   $($( ".tabler" )[1]).find(".constraints").val("")
        //   $($( ".tabler" )[1]).find(".process").val("v2<-argmin_{z2}[k=7]DEuclidean(f1,f2)")

        //   $($( ".tabler" )[2]).find(".name").val("*f3")
        //   $($( ".tabler" )[2]).find(".x-val").val("x1")
        //   $($( ".tabler" )[2]).find(".y-val").val("y1")
        //   $($( ".tabler" )[2]).find(".z-val").val("v2")
        //   $($( ".tabler" )[2]).find(".constraints").val("")
        //   $($( ".tabler" )[2]).find(".process").val("")
        // }

        $scope.populateQuery3 = function () {
            $("#dataset-form-control").val('real_estate');
            angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
            $scope.removeAndInsertRows(2);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'year','month'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'soldprice','listingprice'}")
            $($(".tabler")[0]).find(".z-val").val("z1<-'state'.'CA'")
            $($(".tabler")[0]).find(".constraints").val("")
            $($(".tabler")[0]).find(".process").val("")

            $($(".tabler")[1]).find(".name").val("f2")
            $($(".tabler")[1]).find(".x-val").val("x1")
            $($(".tabler")[1]).find(".y-val").val("y1")
            $($(".tabler")[1]).find(".z-val").val("z2<-'state'.'NY'")
            $($(".tabler")[1]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[2]).find(".process").val("x2,y2<-argmin_{x1,y1}[k=1]DEuclidean(f1,f2)")

            $scope.insertRow()
            $($(".tabler")[3]).find(".name").val("*f3")
            $($(".tabler")[3]).find(".x-val").val("x2")
            $($(".tabler")[3]).find(".y-val").val("y2")
            $($(".tabler")[3]).find(".z-val").val("'state'.{'CA','NY'}")
            $($(".tabler")[3]).find(".constraints").val("")
            $($(".tabler")[3]).find(".process").val("")
            removeZqlRow(6);
            removeZqlRow(5);
            //     removeZqlRow(4);
        }

        $scope.populateQuery4 = function () {
            $("#dataset-form-control").val('real_estate');
            angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
            $scope.removeAndInsertRows(2);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'year'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'soldprice'}")
            $($(".tabler")[0]).find(".z-val").val("z1<-'state'.*")
            $($(".tabler")[0]).find(".constraints").val("state='NY'")
            $($(".tabler")[0]).find(".process").val("")

            $($(".tabler")[1]).find(".name").val("f2")
            $($(".tabler")[1]).find(".x-val").val("x1")
            $($(".tabler")[1]).find(".y-val").val("y1")
            $($(".tabler")[1]).find(".z-val").val("z2<-'city'.*")
            $($(".tabler")[1]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[2]).find(".process").val("v2<-argmax_{z2}[k=3]DEuclidean(f1,f2)")

            $scope.insertRow()
            $($(".tabler")[3]).find(".name").val("*f3")
            $($(".tabler")[3]).find(".x-val").val("x1")
            $($(".tabler")[3]).find(".y-val").val("y1")
            $($(".tabler")[3]).find(".z-val").val("v2")
            $($(".tabler")[3]).find(".constraints").val("")
            $($(".tabler")[3]).find(".process").val("")

//      removeZqlRow(4);
        }

        $scope.populateQuery5 = function () {
            $("#dataset-form-control").val('real_estate');
            angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
            //Pairwise example
            $scope.removeAndInsertRows(2);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'year'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'soldprice'}")
            $($(".tabler")[0]).find(".z-val").val("z1<-'state'.*")
            $($(".tabler")[0]).find(".constraints").val("")
            $($(".tabler")[0]).find(".process").val("")

            $($(".tabler")[1]).find(".name").val("f2")
            $($(".tabler")[1]).find(".x-val").val("x1")
            $($(".tabler")[1]).find(".y-val").val("y2<-{'listingprice'}")
            $($(".tabler")[1]).find(".z-val").val("z1")
            $($(".tabler")[1]).find(".constraints").val("")

            $scope.insertProcessRow()
            $($(".tabler")[2]).find(".process").val("v1<-argmin_{z1}[k=1]DEuclidean(f1,f2)")

            $scope.insertRow()
            $($(".tabler")[3]).find(".name").val("*f3")
            $($(".tabler")[3]).find(".x-val").val("x1")
            $($(".tabler")[3]).find(".y-val").val("y3<-{'soldprice','listingprice'}")
            $($(".tabler")[3]).find(".z-val").val("v1")
            $($(".tabler")[3]).find(".constraints").val("")
            $($(".tabler")[3]).find(".process").val("")
            removeZqlRow(6);
            removeZqlRow(5);
            //    removeZqlRow(4);
        }
        $scope.populateQuery7 = function () {
            $("#dataset-form-control").val('real_estate');
            angular.element($("#sidebar")).scope().onDatasetChange('real_estate');
            //Increasing example
            $scope.removeAndInsertRows(1);
            $($(".tabler")[0]).find(".name").val("f1")
            $($(".tabler")[0]).find(".x-val").val("x1<-{'year'}")
            $($(".tabler")[0]).find(".y-val").val("y1<-{'soldprice'}")
            $($(".tabler")[0]).find(".z-val").val("z1<-'state'.*")
            $($(".tabler")[0]).find(".constraints").val("")
            $($(".tabler")[0]).find(".process").val("")

            $scope.insertProcessRow()
            $($(".tabler")[1]).find(".process").val("v1<-argmax_{z1}[k=5]Tincreasing(f1)")

            $scope.insertRow()
            $($(".tabler")[2]).find(".name").val("*f2")
            $($(".tabler")[2]).find(".x-val").val("x1")
            $($(".tabler")[2]).find(".y-val").val("y1")
            $($(".tabler")[2]).find(".z-val").val("v1")
            $($(".tabler")[2]).find(".constraints").val("")

            removeZqlRow(6);
            removeZqlRow(5);
            removeZqlRow(4);
        }

        $scope.callGetUserQueryResults = function () {
            //$rootScope.$broadcast("callGetUserQueryResults", {});
            $scope.getUserQueryResults();
        }

        $scope.callgetRepresentativeTrends = function () {
            //$rootScope.$broadcast("callgetRepresentativeTrends", {});
            $scope.getRepresentativeTrendsWithoutCallback()
        }

        $scope.callGetUserQueryResultsWithCallBack = function () {
            //$rootScope.$broadcast("callGetUserQueryResultsWithCallBack", {});
            $scope.getUserQueryResultsWithCallBack();
        }

// merged options and dataset controllers

        $scope.inittablelist = function () {
            $http.get('/zv/gettablelist'
            ).then(
                function (response) {
                    console.log("success: ", response);
                    console.log("cookies: ", userinfo);
                    var userinfo = $cookies.getObject('userinfo');

                    $http.get('zv/loginAvailable')
                        .then(
                            function (response_ava) {
                                login_ava = response_ava.data;
                                if (login_ava) {
                                    if (userinfo) {
                                        // $scope.updatetablelist(userinfo['tablelist']);
                                        document.getElementById("loginmodaltrigger").style.display = "none";
                                        document.getElementById("signoutbutton").style.display = "block";
                                        datasetService.storetablelist(userinfo['tablelist'])
                                    } else {
                                        document.getElementById("signoutbutton").style.display = "none";
                                        document.getElementById("loginmodaltrigger").style.display = "block";
                                        datasetService.storetablelist(response.data);

                                    }
                                }
                                else {
                                    document.getElementById("signoutbutton").style.display = "none";
                                    document.getElementById("loginmodaltrigger").style.display = "none";
                                    datasetService.storetablelist(response.data);
                                }
                                $scope.tablelist = Array.from(new Set(datasetService.getTablelist().reverse()));
                            }
                        )
                },
                function (response) {
                    console.log("failed to get table list: ", response);
                    $("#errorModalText").html(response);
                    $("#errorModal").modal();
                }
            );
        };
    }]);
// populates and controls the dataset attributes on the left-bar
// does not dynamically adjust to change in dataset yet
app.controller('datasetController', [
    '$scope', '$rootScope', '$http', 'datasetService', 'plotResultsService', 'ChartSettings', 'sketchService',
    function ($scope, $rootScope, $http, datasetService, plotResultsService, ChartSettings, sketchService) {
        $scope.queries = {};

        $scope.inittablelist = function () {
            $http.get('/zv/gettablelist'
            ).then(
                function (response) {
                    console.log("success: ", response);
                    // $scope.tablelist = response.data
                    datasetService.storetablelist(response)
                    $scope.tablelist = datasetService.getTablelist()
                },
                function (response) {
                    console.log("failed: ", response);
                }
            );
        };

        $scope.updatetablelist = function updatetablelist(args) {
            // datasetInfo.storetablelist(args);
            $scope.tablelist = args;
        }

        $scope.chartSettings = ChartSettings;
        function initializeSketchpadOnDataAttributeChange(xdata, ydata, zdata) {
            clearRepresentativeTable();
            clearOutlierTable();
            clearUserQueryResultsTable();

            switch ($scope.chartSettings.selectedChartOption) {
                case 'Bar':
                    break;
                case 'Scatter':
                    $scope.getPolygonQueryResults("initialize");
                    break;
                default: // Line
                    sketchService.initializeSketchpadNew(
                        xdata["min"], xdata["max"], ydata["min"], ydata["max"],
                        xdata["name"], ydata["name"], (zdata == null) ? null : zdata["name"]
                    );
                    break;
            }
        }

        $scope.callLoadAxisInfo = function () {
            $rootScope.$broadcast("loadAxisInfo");
        }
        $scope.callClearDynamicClassOptions = function () {
            clearDynamicClassModal($('#dynamic-class'));
            $rootScope.$broadcast("clearDynamicClassOptions");
        }

        $scope.callGetClassInfo = function () {
            log.info("show dynamic class info")
            $rootScope.$broadcast("callGetClassInfo", {});
        }

        $scope.scatterDatasetChangeQuery = function scatterDatasetChangeQuery() {
            $scope.queries["db"] = getSelectedDataset();
            $scope.queries['zqlRows'] = [];
            scatterDatasetChangeQueryHelper($scope.queries['zqlRows'])
        };


        $scope.getPolygonQuery = function getPolygonQuery() {
            $scope.queries["db"] = getSelectedDataset();
            this.xAxis = getSelectedXAxis();
            this.yAxis = getSelectedYAxis();
            $scope.queries['zqlRows'] = [];
            getPolygonQueryHelper($scope.queries['zqlRows']);
        }

        $scope.getScatterSimilarity = function getScatterSimilarity() {
            $scope.queries["db"] = getSelectedDataset();
            this.xAxis = getSelectedXAxis();
            this.yAxis = getSelectedYAxis();
            $scope.queries['zqlRows'] = [];
            getSimilarityQuery($scope.queries['zqlRows']);

            console.log(JSON.stringify($scope.queries));
            var data = $scope.queries;
            console.log("calling getScatterSimilarity");
            $http.post('/zv/scatterSimilarity', data).success(function (response) {
                console.log("getScatterSimilarity: success");
                if (response.length == 0) {
                    console.log("empty response")
                }
                else {
                    plotResultsService.displayUserQueryResultsScatter(response.outputCharts);
                }

            }).error(function (response) {
                console.log("scatterSimilarity: fail");
            });

        }

        $scope.getPolygonQueryResults = function getPolygonQueryResults(mode) {
            // get datasetchange query
            if (mode == "initialize") {
                $scope.scatterDatasetChangeQuery();
            }
            else {
                console.log("getsScatterResults");
                $scope.getPolygonQuery();
            }
            console.log(JSON.stringify($scope.queries));
            //$http.get('/zv/executeScatter', {params: {'query': {"db":"real_estate", "zqlRows":[{"name":{"output":true,"sketch":true,"name":"f1"},"x":{"variable":"x1","attributes":["'year'"]},"y":{"variable":"y1","attributes":["'listingprice'"]},"z":{"variable":"z1","aggregate":true,"attribute":"'state'","values":["*"]}, "viz":{"map": {"type":"scatter"}} }]}}}
            var apiCall = '/zv/executeScatter';
            var data = {params: {'query': JSON.stringify($scope.queries)}}
            $http.get(apiCall, data).then(
                function (response) {
                    if (mode == "initialize") {
                        $scope.data = response.data.outputCharts[0].points;
                    }
                    sketchService.createSketchpadScatter($scope.data);
                    plotResultsService.displayUserQueryResultsScatter(response.data.outputCharts);
                },
                function (response) {
                    console.log("failed: ", escape(response));
                }
            );


        }

        $scope.getUserQueryResultsWithCallBack = function getUserQueryResultsWithCallBack() {
            clearUserQueryResultsTable();
            var q = constructUserQuery(); //goes to query.js
            var data = q;
            console.log("calling getUserQueryResults");
            console.log(data);
            $http.post('/zv/postSimilarity', data)
                .success(function (response) {
                    console.log("getUserQueryResults: success");
                    if (response.length == 0) {
                        console.log("empty response")
                    }
                    if (data.error != null) {
                        console.log("calling getErrorResults");
                        $http.post('/zv/postSimilarity_error', data).success(function (response_error) {
                            console.log("getErrorResults: success");
                            if (response_error.length == 0) {
                                console.log("empty response")
                            }
                            console.log("merged result: ", mergejoin(response.outputCharts, response_error.outputCharts));
                            plotResultsService.displayUserQueryResults(response.outputCharts, true);
                            $scope.getRepresentativeTrendsWithoutCallback();
                        }).error(function (response_error) {
                            console.log("getUserQueryResults: fail");
                            document.getElementById("loadingEclipse").style.display = "none";
                            $("#errorModalText").html(response);
                            $("#errorModal").modal();
                        });

                    }
                    else {
                        plotResultsService.displayUserQueryResults(response.outputCharts, true);
                        if (data.groupBy == 'dynamic_class') {
                            document.getElementById("loadingEclipse2").style.display = "none";
                            document.getElementById("representative-table").style.display = "none";
                            document.getElementById("outlier-table").style.display = "none";
                        }
                        else {
                            document.getElementById("representative-table").style.display = "inline";
                            document.getElementById("outlier-table").style.display = "inline";
                            $scope.getRepresentativeTrendsWithoutCallback();
                        }
                    }
                })
                .error(function (response) {
                    console.log("getUserQueryResults: fail");
                    document.getElementById("loadingEclipse").style.display = "none";
                    document.getElementById("loadingEclipse2").style.display = "none";
                    $("#errorModalText").html(response);
                    $("#errorModal").modal();
                });
        }

        // for all other normal queries
        $scope.getUserQueryResults = function getUserQueryResults() {
            clearUserQueryResultsTable();
            var q = constructUserQuery(); //goes to query.js
            var data = q;

            console.log("calling getUserQueryResults");
            $http.post('/zv/postSimilarity', data).success(function (response) {
                console.log("getUserQueryResults: success");
                if (response.length == 0) {
                    console.log("empty response")
                }
                if (data.error != null) {
                    console.log("calling getErrorResults");
                    $http.post('/zv/postSimilarity_error', data).success(function (response_error) {
                        console.log("getErrorResults: success");
                        if (response_error.length == 0) {
                            console.log("empty response")
                        }
                        console.log("merged result: ", mergejoin(response.outputCharts, response_error.outputCharts));
                        plotResultsService.displayUserQueryResults(response.outputCharts, true);
                        //$scope.getRepresentativeTrendsWithoutCallback(); dont recompute representative
                    }).error(function (response_error) {
                        console.log("getUserQueryResults: fail");
                        document.getElementById("loadingEclipse").style.display = "none";
                        document.getElementById("loadingEclipse2").style.display = "none";
                        $("#errorModalText").html(response_error);
                        $("#errorModal").modal();
                    });

                }

                else {
                    plotResultsService.displayUserQueryResults(response.outputCharts, true);
                }


            }).error(function (response) {
                console.log("getUserQueryResults: fail");
                document.getElementById("loadingEclipse").style.display = "none";
                document.getElementById("loadingEclipse2").style.display = "none";
                $("#errorModalText").html(response);
                $("#errorModal").modal();
            });


            // Update globalDatasetInfo['classes']
            var query = {};
            query["dataset"] = getSelectedDataset();
            $http.post('/zv/getClassInfo', query
            ).then(
                function (response) {
                    console.log("success: ", response);
                    globalDatasetInfo["classes"] = response.data
                    var formattedRanges = formatRanges(response.data["classes"])
                    $scope.classes = response.data["classes"]
                    for (var i = 0; i < response.data["classes"].length; i++) {
                        response.data["classes"][i].formattedRanges = formattedRanges[i]
                    }
                },
                function (response) {
                    console.log("failed: ", response);
                }
            );

        }

        $scope.downloadResults = function downloadResults(args) {
            console.log("downloading results")
            var q = constructUserQuery(); //goes to query.js
            var data = q;
            q.download = true;
            console.log("calling downloadSimilarity");
            q.includeQuery = getIncludeQuery();
            q.yOnly = getyOnlyCheck();

            var address = '/zv/downloadSimilarity';
            if (args == 'query') {
                q.downloadThresh = getMinThresh();
                q.outlierCount = $("#num-results-download").val();
                q.downloadAll = getDownloadAll();
                address = '/zv/downloadSimilarity';
                log.info("query result download", q.outlierCount, q.yOnly, q.includeQuery, q.downloadAll, q.downloadThresh);
            } else if (args == 'representative') {
                q.kmeansClusterSize = $("#num-clusters-download").val();
                q.downloadAll = getDownloadAllRepresentative();
                address = '/zv/postRepresentative';
                log.info("representative result download", q.kmeansClusterSize, q.yOnly, q.downloadAll);
            } else if (args == 'outlier') {
                address = '/zv/downloadOutlier';
                q.downloadThresh = getMinOutlierThresh();
                q.kmeansClusterSize = $("#num-outlier-download").val();
                log.info("outlier result download", q.kmeansClusterSize, q.yOnly);
            }
            console.log("before http post")
            $http.post(address, data).success(function (response) {
                // console.log("Response:",response);
                for (var key in response) {
                    if (response.hasOwnProperty(key)) {
                        //console.log(key + " -> " + response[key]);
                        //var file = new File(["Hello, world!"], "hello world.txt", {type: "text/plain;charset=utf-8"});
                        var file = new File([response[key].join("\n")], key, {type: "text/plain;charset=utf-8"});
                        saveAs(file);
                    }
                }
                console.log("download : success");
                // alert("Sucessfully saved to zenvisage/target")
            }).error(function (response) {
                console.log("download : fail");
                $("#errorModalText").html(response);
                $("#errorModal").modal();
            });
        }
        $scope.getRepresentativeTrendsWithoutCallback = function getRepresentativeTrendsWithoutCallback() {
            getRepresentativeTrends(getOutlierTrends);
        }

        // for representative trends
        function getRepresentativeTrends(outlierCallback) {
            clearRepresentativeTable();

            var q = constructRepresentativeTrendQuery(); //goes to query.js
            var data = q;

            console.log("calling getRepresentativeTrends");
            $http.post('/zv/postRepresentative', data).success(function (response) {
                console.log("getRepresentativeTrends: success");
                if (response.length == 0) {
                    console.log("empty response")
                }
                if (data.error != null) {
                    console.log("original representative: ", response.outputCharts);
                    console.log("calling getErrorResults");
                    $http.post('/zv/postSimilarity_error', data).success(function (response_error) {
                        console.log("getErrorResults: success");
                        if (response_error.length == 0) {
                            console.log("empty response")
                        }
                        console.log("merged result in representative: ", mergejoin_representative(response.outputCharts, response_error.outputCharts));
                        plotResultsService.displayRepresentativeResults(response.outputCharts, true);
                        outlierCallback();
                    }).error(function (response_error) {
                        console.log("getRepresentativeTrends: fail");
                        document.getElementById("loadingEclipse2").style.display = "none";
                        $("#errorModalText").html(response_error);
                        $("#errorModal").modal();
                    });

                }
                else {
                    plotResultsService.displayRepresentativeResults(response.outputCharts);
                    outlierCallback();
                }
            }).error(function (response) {
                console.log("getRepresentativeTrends: fail");
                document.getElementById("loadingEclipse2").style.display = "none";
                $("#errorModalText").html(response);
                $("#errorModal").modal();
            });
        }

        function getyOnlyCheck() {
            return $("#yOnly").is(':checked');
        }

        function getIncludeQuery() {
            return $("#includeQuery").is(':checked');
        }

        function getDownloadAll() {
            return $("#downloadAll").is(':checked');
        }

        function getDownloadAllRepresentative() {
            return $("#downloadAllRepresentative").is(':checked');
        }

        function getMinThresh() {
            return $("#min-thresh-download").val();
            ;
        }

        function getMinOutlierThresh() {
            return $("#min-thresh-download-outlier").val();
            ;
        }

        function getOutlierTrends() {
            clearOutlierTable();

            var q = constructOutlierTrendQuery(); //goes to query.js
            var data = q;

            console.log("calling getOutlierTrends");
            $http.post('/zv/postOutlier', data).success(function (response) {
                console.log("getOutlierTrends: success");
                if (response.length == 0) {
                    console.log("empty response")
                }
                if (data.error != null) {
                    console.log("calling getErrorResults");
                    $http.post('/zv/postSimilarity_error', data).success(function (response_error) {
                        console.log("getErrorResults: success");
                        if (response_error.length == 0) {
                            console.log("empty response")
                        }
                        console.log("merged result: ", mergejoin(response.outputCharts, response_error.outputCharts));
                        plotResultsService.displayOutlierResults(response.outputCharts, true);
                    }).error(function (response_error) {
                        console.log("getUserQueryResults: fail");
                        document.getElementById("loadingEclipse2").style.display = "none";
                        $("#errorModalText").html(response_error);
                        $("#errorModal").modal();
                    });

                }
                else {
                    plotResultsService.displayOutlierResults(response.outputCharts)
                }
                ;
            }).error(function (response) {
                console.log("Don't worry, this outlier fail is normal. Ignoring failed outlier trends because sometimes because there are no outliers if representative takes that one up.")
                // console.log("getOutlierTrends: fail");
                // document.getElementById("loadingEclipse2").style.display = "none";
                // $("#errorModalText").html(response);
                // $("#errorModal").modal();
            });
        }

        var q = constructDatasetChangeQuery(getSelectedDataset());
        //var q = constructDatasetChangeQuery("seed2");

        var params = {
            "query": q,
        };
        var config = {
            params: params,
        };

        function clearDynamicClassModal(form) {
            $(':input', form).each(function () {
                var type = this.type;
                if (type == 'text') {
                    this.value = "";
                }
            });
            document.getElementById("load-dynamic-class-button").style.display = "none";
        };

        $scope.onDatasetChange = function (input) {
            console.log("on change,", getSelectedDataset());
            document.getElementById("loadingEclipse").style.display = "inline";
            document.getElementById("loadingEclipse2").style.display = "inline";
            log.info("dataset selected", $('#dataset-form-control').val());
            // initSettingPanel();
            clearRepresentativeTable();
            clearOutlierTable();
            clearUserQueryResultsTable();
            console.log('selected dataset', getSelectedDataset());
            if (input == 'initialize') {
                var q = constructDatasetChangeQuery('real_estate_tutorial');
            } //just for tutorial purposes
            else if (input == 'weather') {
                var q = constructDatasetChangeQuery('weather');
            }
            else if (input == 'real_estate') {
                var q = constructDatasetChangeQuery('real_estate');
            }
            else {
                var q = constructDatasetChangeQuery(getSelectedDataset());
            }

            var params = {
                "query": q,
            };
            var config = {
                params: params,
            };

            var datasetname = $('#dataset-form-control').val();

            $http.get('/zv/getformdata', config).success(function (response) {
                globalDatasetInfo = response;
                datasetService.store(response); //saves form data to datasetInfo
                $scope.categories = [];
                $scope.xAxisItems = [];
                $scope.yAxisItems = [];
                $scope.selectedCategory;
                $scope.selectedXAxis;
                $scope.selectedYAxis;
                angular.forEach(response.zAxisColumns, function (value, key) {
                    $scope.categories.push(key);
                });
                angular.forEach(response.xAxisColumns, function (value, key) {
                    $scope.xAxisItems.push(key);
                });
                angular.forEach(response.yAxisColumns, function (value, key) {
                    $scope.yAxisItems.push(key);
                });
                // hard coding default x, y axis for preloaded dataset
                if (datasetname == "breast_cancer_cells") {
                    $scope.selectedCategory = "gene";
                    $scope.selectedXAxis = "timepoint";
                    $scope.selectedYAxis = "expression";
                } else if (datasetname == "cmu") {
                    $scope.selectedCategory = "class";
                    $scope.selectedXAxis = "o2";
                    $scope.selectedYAxis = "ea";
                } else if (datasetname == "des") {
                    $scope.selectedCategory = "objid_cycle_band";
                    $scope.selectedXAxis = "mjd57000";
                    $scope.selectedYAxis = "psf_flux";
                    $scope.selectedErrorAxis = "psf_flux_err"
                } else if (datasetname == "weather") {
                    $scope.selectedCategory = "location";
                    $scope.selectedXAxis = "month";
                    $scope.selectedYAxis = "temperature";
                }
                else {
                    $scope.selectedCategory = $scope.categories[0];
                    $scope.selectedXAxis = $scope.xAxisItems[0];
                    $scope.selectedYAxis = $scope.yAxisItems[0];
                }

                log.info("initialized data attribute", getSelectedCategory(), getSelectedXAxis(), getSelectedYAxis())
                //send in first item info

                // $.when(initializeSketchpadOnDataAttributeChange(
                //       response.xAxisColumns[$scope.xAxisItems[0]],
                //       response.yAxisColumns[$scope.yAxisItems[0]],
                //       response.zAxisColumns[$scope.categories[0]]
                //     )).done(function(){
                //       getRepresentativeTrends( getOutlierTrends );
                //     });


                initializeSketchpadOnDataAttributeChange(
                    response.xAxisColumns[$scope.selectedXAxis],
                    response.yAxisColumns[$scope.selectedYAxis],
                    response.zAxisColumns[$scope.selectedCategory]
                );
                $scope.getUserQueryResultsWithCallBack();

                $scope.callLoadAxisInfo();
                $scope.callClearDynamicClassOptions();
            }).error(function (response) {
                alert('Request failed: /getformdata');
                document.getElementById("loadingEclipse").style.display = "none";
                document.getElementById("loadingEclipse2").style.display = "none";
            });
            resetSelectedErrorAxis();
        }

        // when the data selection is changed, the graphs needs to be re-initialized
        // and the rest of the graphs have to be fetched
        $scope.setDataAttributeToDynamicClass = function () {
            if ($scope.categories && !$scope.categories.includes("dynamic_class")) {
                $scope.categories.push("dynamic_class");
            }
            $scope.selectedCategory = "dynamic_class";
            $scope.onDataAttributeChange();
        }

        $scope.onDataAttributeChange = function () {
            document.getElementById("loadingEclipse").style.display = "inline";
            document.getElementById("loadingEclipse2").style.display = "inline";
            var categoryData = datasetService.getCategoryData()[getSelectedCategory()]
            var xData = datasetService.getXAxisData()[getSelectedXAxis()]
            var yData = datasetService.getYAxisData()[getSelectedYAxis()]
            log.info("data attribute changed", getSelectedCategory(), getSelectedXAxis(), getSelectedYAxis())
            // $.when(initializeSketchpadOnDataAttributeChange(xData, yData, categoryData))
            // .done(function(){
            //   getRepresentativeTrends( getOutlierTrends );
            // });
            initializeSketchpadOnDataAttributeChange(xData, yData, categoryData);
            $scope.getUserQueryResultsWithCallBack();
        };

        $scope.onErrorAttributeChange = function () {
            document.getElementById("loadingEclipse").style.display = "inline";
            document.getElementById("loadingEclipse2").style.display = "inline";
            var categoryData = datasetService.getCategoryData()[getSelectedCategory()]
            var xData = datasetService.getXAxisData()[getSelectedXAxis()]
            var yData = datasetService.getYAxisData()[getSelectedYAxis()]
            log.info("error attribute changed", getSelectedCategory(), getSelectedXAxis(), getSelectedYAxis())
            // $.when(initializeSketchpadOnDataAttributeChange(xData, yData, categoryData))
            // .done(function(){
            //   getRepresentativeTrends( getOutlierTrends );
            // });
            initializeSketchpadOnDataAttributeChange(xData, yData, categoryData);
            $scope.getUserQueryResultsWithCallBack();
        };


        $scope.$on("callGetUserQueryResultsWithCallBack", function () {
            $scope.getUserQueryResultsWithCallBack();
        });

        $scope.$on("callGetUserQueryResults", function () {
            $scope.getUserQueryResults();
        });

        $scope.$on("callgetRepresentativeTrends", function () {
            $scope.getRepresentativeTrendsWithoutCallback();
        });


        $(function () {
            $("#binning-slider").slider({
                range: "max",
                min: 0,
                max: 10,
                step: 0.5,
                value: 1.0,
                slide: function (event, ui) {
                    $("#binning-amount").val(ui.value);
                }
            });
            $("#binning-amount").val($("#binning-slider").slider("value"))
            $("#binning-slider").slider({
                change: function (event, ui) {
                    var binningcoefficient = $("#binning-slider").slider("value")
                    $scope.getPolygonQueryResults("initialize")
                }
            })
        });

        // this init is just for tutorial purpose
        var init = function () {
            $scope.onDatasetChange('initialize');
        };
        // init();
        // and fire it after definition
        $scope.$on("updateAxes", function (event, xAxis, yAxis, category) {
            $scope.selectedXAxis = xAxis;
            $scope.selectedYAxis = yAxis;
            $scope.selectedCategory = category;
        });
    }]);

app.service('ChartSettings', function () {
    return {};
})