var y_aggr_ms;

var currentFormData; // contains the current form data, only global for now to save redundant calls to backend
var existingTrends; //
var outputData; //

//For rect.js mainly
var sketchPointsList = [];
//var sketchPoints = new SketchPoints();
//var rectangles = new Rectangles();
//var circles = new Circles();
// AN error in globalvars.js means the rest of file will not compile

var circles_rem = [];
var hit = -1;
var r = 0;
var osety = 55;
var osetx = 305;

// histogram.js
var numOfBar = 6;



// from index.html, height and width of 2nd chart
var drawGraphWidth = 409;
var drawGraphHeight = 210;

var scatter = false;
//factory function to make a dictionary (an object)
//name = the keys, space seperated
function makeStruct(name){
    var names = name.split(' ');
    var count = names.length;
    function constructor(){
        for(var i = 0; i < count; i++){
            this[names[i]] = arguments[i]
        }
    }
    return constructor;
}
//cant alert here, since page has not yet loaded
/* makeStruct example
var Item = makeStruct("id speaker country");
var row = new Item(1, 'john', 'au');
// result: {'id' : 1 , 'speaker': 'john', 'country', 'au'}
alert(row)
alert(row.speaker)

*/

var magicSuggestStruct = makeStruct("id x y z constraints process");
// result: {'x' : xMagicSuggest, 'y' : yMagicSuggest, 'z',...};

var magicSuggestRows = [] //where we store a list of the above struct

// baseline constraints clear when submit
var vqlTablerows = 0;

var customQuery = false;

var tableHasSketch = false;
var tableX = "";
var tableY = "";

// Model containing all relevant information for mainChart0
// TODO ADD 'list' variable to the data structure
var chart0Information = {
    'min_X' : 0,
    'max_X' : 0,
    'min_Y' : 0,
    'max_Y' : 0,
    'chartData' : null,
    'chartName' : "mainChart", // later to be mainChart0
    'dragAndDropObject' : null, // jQuery object that wraps the DOM element
    'tools_sketchObject' : null,
    'scatterName' : "scatterplot", //
    'visualisationObject' : null,
    'svgLayerObject' : null,
    'mySvgObject' : null,
    'pathObject' : null,
    'list' : [[]], // list of points, [x][y], storing what the user drew
    'points' : [] // actual svg points
}

// Model containing all relevant information for mainChart1
var chart1Information = {
    'min_X' : 0,
    'max_X' : 0,
    'min_Y' : 0,
    'max_Y' : 0,
    'chartData' : null,
    'chartName' : "mainChart1",
    'dragAndDropObject' : null, // jQuery object that wraps the DOM element
    'tools_sketchObject' : null,
    'scatterName' : "scatterplot1", // not supported by pairwise search (yet)
    'visualisationObject' : null,
    'svgLayerObject' : null,
    'mySvgObject' : null,
    'pathObject' : null,
    'list' : [[]],
    'points' : []
}
