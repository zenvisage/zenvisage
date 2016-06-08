// imports and setups
_.mixin(_.str.exports());
$.ajaxSetup({
  timeout: 1000000,
});

function getBaselineData(query) {
	console.log(JSON.stringify(query));
	$.get('/zv/getbaseline','query='+ JSON.stringify(query), processBackEndData, 'json')
	.fail(function(){
		console.log("Failed")
		alert('Request failed: /getbaseline');
	});
}

function getSuggestData(query)
{
  $("#existing-trends").empty()
  $.get('/zv/getdata', 'query='+JSON.stringify(query), generateExistingTrends, 'json')
  .fail(function(){
    console.log("Failed")
    alert('Request failed: /getdata');
  });
}

//This method only GENERATES SCATTER Trends
function getScatterData(query){
	$("#existing-trends").empty()
	  $.get('/zv/getscatterplot','query='+ JSON.stringify(query), generateScatterTrends, 'json')
	  .fail(function(){
	    console.log("Failed")
	    alert('Request failed: /getscatterTrends');
	  });
}
//This method will get data for the OUTPUT charts
//When a person presses the "search" button, this will get called down the path
function getScatterPlot(query)
{
  console.log("sendquery", query)
  $.get('/zv/getscatterplot', 'query='+ JSON.stringify(query), returnResults, 'json')
      .fail(function(){
        console.log("Failed")
        alert('Request failed: /getscatterplot');
      });
}

function getSuggestTrends(query){
	  $("#existing-trends").empty()
	  $.get('/zv/getdata', JSON.stringify(query), generatingExistingTrending, 'json')
	  .fail(function(){
	    console.log("Failed")
	    alert('Request failed: /getdata');
	  });
}

function getZQLData(query) {
  $('div#container').empty();
  $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');

  console.log(JSON.stringify(query));
  $.get('/zv/executeZQL', 'query='+JSON.stringify(query), processBackEndData, 'json')
    .fail(function() {
      console.log("Failed")
      alert('Request failed: /executeZQL');
    });
}

function getData(query, xAxisType, yAxisType) {
  $('div#container').empty();
  $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');
  if (query.sketchPoints.length >0 && query.sketchPoints[0].points.length > 0 && query.method == "Outlier") {
	  query.method = "DissimilaritySearch";
	  console.log("Changed");
  }
  if (query.sketchPoints != []) {
	  for (var i = 0; i < 2; i++) {
		  var sketchPoints = query.sketchPoints[i];
		  if (sketchPoints == undefined) break;
		  for (var j = 0; j < sketchPoints.points.length; j++) {
			  if (sketchPoints.points[j].x > sketchPoints.maxX) {
				  alert("sketch points out of bounds");
				  return;
			  }
		  }
	  }
  }
  $.get('/zv/getdata', 'query='+JSON.stringify(query),
   function(data){
       /*function is the callback function. data is what server gives us.
         I do this so I can pass additional client info further along.
         This deals with the asyncronous nature of js
         what if users submitted again too fast before server finished first time?
         xAxisSelect0.getValue() will be updated, perhaps incorrectly!
         But the callback function relies on xAxisSelect0.getValue()! So it could use the wrong value!
       */
      processBackEndData(data, xAxisType, yAxisType);
      //update existing trends
  }, 'json')
    .fail(function() {
      console.log("Failed")
      alert('Request failed: /getdata');
    });
}

  function getInterfaceFormData(query){
	  var q = new formQuery(query);
	 //$.get('/getformdata',JSON.stringify(q),processFormData,'json')
    $.get('/zv/getformdata', 'query='+JSON.stringify(q) , processDatasetChange, 'json')
      .fail(function() {
        console.log("Failed")
        alert('Request failed: /getformdata');
      });
  }

  function getRepresentativeData(query){
	  //console.log(query);
	    $('div#container').empty();
	    $('#compare-menu').html('<li><a href="#" class="active" data-id="-1">Show All</a></li>');
	 //   console.log(JSON.stringify(query));
	    $.get('/zv/getdata','query='+JSON.stringify(query), processRepresentativeData, 'json')
	      .fail(function() {
	        console.log("Failed");
	        alert('Request failed: /getdata');
	      });
  }

  function get_sim(r,c,n) {
    if (r > c) {
      var tmp = r;
      r = c;
      c = tmp;
    }
    return r*((n-1-(r-1)) + (n-1))/2 + (c-r-1);
  }
