//displays user results
function displayUserQueryResultsHelper( userQueryResults )
{
  $("#results-table").empty();
  var resultsDiv = $("#results-table");
  var current = 0;
  for (var count = 0; count < userQueryResults.length; count++)
  {
    if (count%2 == 0)
    {
      var newRow = $("#results-table").append("<tr id=\"row-" + count.toString() + "\"></tr>")
      current = count;
    }
    $("#row-" + current.toString()).append("<td><div id=\"result-" + count.toString() + "\" style=\"width: 200px; height: 85px;\"></div></td>");
  }
  for (var count = 0; count < userQueryResults.length; count++)
  {
    var xData = userQueryResults[count]["xData"];
    var yData = userQueryResults[count]["yData"];

    var xlabel = userQueryResults[count]["xType"];
    var ylabel = userQueryResults[count]["yType"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ xData[i], yData[i] ] );
    }
    var valueRange = [ymin, ymax];
    new Dygraph(document.getElementById("result-" + count.toString()), data,
      {
        valueRange: valueRange,
        xlabel: xlabel,
        xLabelHeight: 11,
        axisLabelWidth: (0,0),
        axisLabelFontSize: 0,
        ylabel: null,
        showLabelsOnHighlight: false,
        highlightCircleSize: 0,
        interactionModel: {},
      });
  }
}

function displayRepresentativeResultsHelper( representativePatternResults )
{
  $("#representative-table").find('tr').not('.middle-right-headers').remove();
  var resultsDiv = $("#representative-table");
  var varFinalArray = []
  for(var count = 0; count < 4; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"representative-row-" + count.toString() + "\"></tr>")
    $("#representative-row-" + count.toString()).append("<td><div class=\"representative-results\" id=\"representative-result-" + count.toString() + "\"></div></td>");
    varFinalArray.push(representativePatternResults[count]);
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];

    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ xData[i], yData[i] ] );
    }
    var valueRange = [ymin, ymax];
    new Dygraph(document.getElementById("representative-result-" + count.toString()), data,
      {
        valueRange: valueRange,
        xlabel: xlabel,
        xLabelHeight: 11,
        axisLabelWidth: (0,0),
        axisLabelFontSize: 0,
        ylabel: null,
        showLabelsOnHighlight: false,
        highlightCircleSize: 0,
        interactionModel: {},
      });
  }
}

function displayOutlierResultsHelper( outlierResults )
{
  $("#outlier-table").find('tr').not('.middle-right-headers').remove();
  var resultsDiv = $("#outlier-table");
  var varFinalArray = [];
  for(var count = 0; count < 4; count++) //need to fix count
  {
    var newRow = resultsDiv.append("<tr id=\"outlier-row-" + count.toString() + "\"></tr>")
    $("#outlier-row-" + count.toString()).append("<td><div class=\"outlier-results\" id=\"outlier-result-" + count.toString() + "\"></div></td>");
    varFinalArray.push(outlierResults[count]);
  }

  for (var count = 0; count < varFinalArray.length; count++)
  {
    var xData = varFinalArray[count]["xData"];
    var yData = varFinalArray[count]["yData"];

    var xlabel = varFinalArray[count]["xType"];
    var ylabel = varFinalArray[count]["yType"];

    var xmin = Math.min.apply(Math, xData);
    var xmax = Math.max.apply(Math, xData);
    var ymin = Math.min.apply(Math, yData);
    var ymax = Math.max.apply(Math, yData);

    var data = [];
    var arrayLength = xData.length;
    for (var i = 0; i < arrayLength; i++ ) {
      data.push( [ xData[i], yData[i] ] );
    }
    var valueRange = [ymin, ymax];
    new Dygraph(document.getElementById("outlier-result-" + count.toString()), data,
      {
        valueRange: valueRange,
        xlabel: xlabel,
        xLabelHeight: 11,
        axisLabelWidth: (0,0),
        axisLabelFontSize: 0,
        ylabel: null,
        showLabelsOnHighlight: false,
        highlightCircleSize: 0,
        interactionModel: {},
      });
  }
}



