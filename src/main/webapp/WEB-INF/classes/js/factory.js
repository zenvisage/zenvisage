app.factory('datasetService', function() {
    var tablelist;
    var categoryData;
    var xAxisData;
    var yAxisData;
    var datasetService = {};

    datasetService.store = function( response ) {
        categoryData = response.zAxisColumns;
        xAxisData = response.xAxisColumns;
        yAxisData = response.yAxisColumns;
    };

    datasetService.storetablelist = function( response ) {
        tablelist = response.data
    };

    datasetService.getCategoryData = function()
    {
        return categoryData;
    }
    datasetService.getXAxisData = function()
    {
        return xAxisData;
    }
    datasetService.getYAxisData = function()
    {
        return yAxisData;
    }
    datasetService.getTablelist = function()
    {
        return tablelist;
    }
    return datasetService;
});

app.factory('plotResultsService', function() {
    var plottingService = {};
    plottingService.displayUserQueryResults = function displayUserQueryResults( userQueryResults,  includeSketch = true )
    {
        displayUserQueryResultsHelper( userQueryResults, includeSketch );
    }

    plottingService.displayRepresentativeResults = function displayRepresentativeResults( representativePatternResults )
    {
        displayRepresentativeResultsHelper( representativePatternResults )
    }

    plottingService.displayOutlierResults = function displayOutlierResults( outlierResults )
    {
        displayOutlierResultsHelper( outlierResults )
    }

    plottingService.displayUserQueryResultsScatter = function displayUserQueryResultsScatter( userQueryResults )
    {
        displayUserQueryResultsScatterHelper( userQueryResults )
    }

    return plottingService;
});

app.factory('sketchService', function() {
    var sketchService = {};
    sketchService.createSketchpadLine = function createSketchpad(data , flipY)
    {
        createSketchpadLineHelper(data , flipY);
    }

    sketchService.createSketchpadScatter= function createSketchpadScatter(data)
    {
        createSketchpadScatterHelper(data)
    }

    sketchService.plotSketchpadNew = function plotSketchpadNew( data )
    {
        plotSketchpadNewHelper( data  )
    }

    sketchService.initializeSketchpadNew = function initializeSketchpadNew( xmin, xmax, ymin, ymax, xlabel, ylabel, category , flipY )
    {
        initializeSketchpadNewHelper(xmin, xmax, ymin, ymax, xlabel, ylabel, category , flipY)
    }

    sketchService.patternLoad = function patternLoad()
    {
        patternLoadHelper()
    }



    return sketchService;
});