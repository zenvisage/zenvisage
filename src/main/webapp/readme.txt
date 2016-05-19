new-web file structure and details.

Main file: Index.html. This is where all the static elements of the webpage are created. In addition, the dynamic elements are initialized in index.html, which are then populated through javscript. 

vega-dynamic.html: a test page for using the visualization toolkit, vega. 
schema2.json: A sample schema of the backend DB (not used currently). 
Other files in main directory are just for libraries to use. 


/vega-master
Folder contains the code for the vega visualization library.
The examples directory could be useful for further vega work. 

/dist/
This is the directory that contains the boostrap framework being used. 

/assets/ 
Directory for more Bootstrap things, and magicsuggest. 

/magicsuggest/
The library used for making the comboboxes. 


/js/
My own js files. 

comboboxes.js: My js to dynamically fill the comboboxes and also to keep track of them (for later submit usage).

utils.js: helper for xdata.js

xdata.js: The JS that helps link the front end to back end. Links up with the nodejs server in the getData() method. 

vdb.js: File for angular.js framework needs. Currently no use of Angular in my code. 

generate-vega-json.js: 
File contains some test charts from vega (testVega(), testScatter(), createScatter(), testBackend())
and also takes the backend data and displays charts (processBackEndData(), createBarGraph(), createLineGraph())

I fill up the #viewX <div> with charts, and dynamically create mroe charts through addChart() and addGraph()


