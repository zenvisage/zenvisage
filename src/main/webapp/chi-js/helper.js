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
        ylabel: null,
      });
  }
}

// need to fix, shitty design
function displayRepresentativeAndOutlierResultsHelper( representativePatternResults, outlierResults )
{
  $("#representative-outlier-table").find('tr').not('#middle-right-headers').remove();
  var resultsDiv = $("#representative-outlier-table");
  var current = 0;
  for(var count = 0; count < 6; count++) //need to fix count
  {
    if (count%2 == 0)
    {
      var newRow = resultsDiv.append("<tr id=\"right-row-" + count.toString() + "\"></tr>")
      current = count;
    }
    $("#right-row-" + current.toString()).append("<td><div id=\"right-result-" + count.toString() + "\" style=\"width: 200px; height: 85px;\"></div></td>");
  }

  varFinalArray = []
  varFinalArray.push(representativePatternResults[0]);
  varFinalArray.push(outlierResults[0]);
  varFinalArray.push(representativePatternResults[1]);
  varFinalArray.push(outlierResults[1]);
  varFinalArray.push(representativePatternResults[2]);
  varFinalArray.push(outlierResults[2]);

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
    new Dygraph(document.getElementById("right-result-" + count.toString()), data,
      {
        valueRange: valueRange,
        xlabel: xlabel,
        ylabel: null,
      });
  }
}

